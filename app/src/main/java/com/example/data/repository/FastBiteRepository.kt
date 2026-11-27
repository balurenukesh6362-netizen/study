package com.example.data.repository

import com.example.data.local.FastBiteDao
import com.example.data.local.SettingsStore
import com.example.data.model.AppSettings
import com.example.data.model.CartItem
import com.example.data.model.CustomerUser
import com.example.data.model.DefaultSeedData
import com.example.data.model.FeedbackEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemCodec
import com.example.data.model.OrderLineItem
import com.example.data.model.OwnerBankInfo
import com.example.data.model.WastageLogEntity
import com.example.data.model.WeeklyPlanEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

class FastBiteRepository(
    private val dao: FastBiteDao,
    private val settingsStore: SettingsStore
) {
    private val seedMutex = Mutex()

    val menuItems: Flow<List<MenuItemEntity>> = dao.observeMenuItems()
    val weeklyPlans: Flow<List<WeeklyPlanEntity>> = dao.observeWeeklyPlans()
    val orders: Flow<List<OrderEntity>> = dao.observeOrders()
    val wastageLogs: Flow<List<WastageLogEntity>> = dao.observeWastageLogs()
    val feedbacks: Flow<List<FeedbackEntity>> = dao.observeFeedbacks()
    val settings: Flow<AppSettings> = settingsStore.settingsFlow

    suspend fun ensureSeeded() = seedMutex.withLock {
        if (dao.getMenuCount() == 0) {
            dao.insertMenuItems(DefaultSeedData.menuItems)
            dao.insertWeeklyPlans(DefaultSeedData.weeklyPlans)
            dao.insertOrders(DefaultSeedData.initialOrders)
            dao.insertWastageLogs(DefaultSeedData.initialWastage)
            dao.insertFeedbacks(DefaultSeedData.initialFeedbacks)
        }
    }

    suspend fun signInCustomer(name: String, contact: String, method: String): CustomerUser {
        val user = CustomerUser(
            name = name.trim(),
            contact = contact.trim(),
            method = method
        )
        settingsStore.saveCustomerUser(user)
        return user
    }

    suspend fun signOutCustomer() {
        settingsStore.saveCustomerUser(null)
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        settingsStore.setSoundEnabled(enabled)
    }

    suspend fun updatePenaltyPercent(percent: Int) {
        settingsStore.setPenaltyPercent(percent)
    }

    suspend fun updateOwnerBank(bank: OwnerBankInfo) {
        settingsStore.saveOwnerBank(bank)
    }

    suspend fun placeOrder(
        customer: CustomerUser,
        cart: List<CartItem>,
        pickupTime: String,
        paymentMethod: String
    ): OrderEntity {
        val now = System.currentTimeMillis()
        val orderNum = Random.nextInt(9050, 9999)
        val tokenDigits = Random.nextInt(1000, 9999)
        val isPermanent = customer.method == "google"
        val tokenPrefix = if (isPermanent) "PERM-FB" else "TEMP-FB"
        val paymentMode = if (paymentMethod == "Cash on Delivery") "COD" else "Online"
        val lineItems = cart.map { OrderLineItem(it.dishId, it.name, it.price, it.qty) }
        val total = lineItems.sumOf { it.price * it.qty }
        val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))

        val order = OrderEntity(
            id = "ORD-$orderNum",
            tokenCode = "$tokenPrefix-$tokenDigits",
            tokenType = if (isPermanent) "Permanent" else "Temporary",
            customerName = customer.name,
            customerContact = customer.contact,
            loginMethod = customer.method,
            itemsJson = OrderItemCodec.encode(lineItems),
            totalAmount = total,
            paymentMethod = paymentMethod,
            paymentMode = paymentMode,
            pickupTime = pickupTime,
            status = "Order Placed",
            createdAt = "Today, $timeFmt",
            createdAtMillis = now
        )
        dao.upsertOrder(order)
        return order
    }

    suspend fun cancelOrder(orderId: String, penaltyPercent: Int): OrderEntity? {
        val existing = dao.getOrderById(orderId) ?: return null
        if (existing.status == "Completed" || existing.status == "Cancelled") return existing

        val penalty = if (existing.paymentMode == "Online") {
            ((existing.totalAmount * penaltyPercent) / 100.0).roundToInt()
        } else {
            0
        }
        val refund = if (existing.paymentMode == "Online") {
            (existing.totalAmount - penalty).coerceAtLeast(0)
        } else {
            0
        }
        val updated = existing.copy(
            status = "Cancelled",
            penaltyDeducted = penalty,
            refundAmount = refund
        )
        dao.updateOrder(updated)
        return updated
    }

    suspend fun rateOrder(orderId: String, stars: Int, comment: String) {
        val existing = dao.getOrderById(orderId) ?: return
        val clampedStars = stars.coerceIn(1, 5)
        dao.updateOrder(
            existing.copy(
                rated = true,
                ratingGiven = clampedStars,
                ratingComment = comment.trim()
            )
        )
        val lineItems = OrderItemCodec.decode(existing.itemsJson)
        lineItems.forEach { line ->
            val dish = dao.getMenuItemById(line.id)
            if (dish != null) {
                val newCount = dish.ratingCount + 1
                val newRating = ((dish.rating * dish.ratingCount) + clampedStars) / newCount.toDouble()
                val rounded = ((newRating * 10.0).roundToInt()) / 10.0
                dao.upsertMenuItem(dish.copy(rating = rounded, ratingCount = newCount))
            }
        }
    }

    suspend fun advanceOrderStatus(orderId: String, nextStatus: String): OrderEntity? {
        val existing = dao.getOrderById(orderId) ?: return null
        val updated = existing.copy(status = nextStatus)
        dao.updateOrder(updated)
        return updated
    }

    suspend fun toggleDishAvailability(dishId: String) {
        val existing = dao.getMenuItemById(dishId) ?: return
        dao.upsertMenuItem(existing.copy(available = !existing.available))
    }

    suspend fun upsertDish(
        id: String?,
        name: String,
        category: String,
        price: Int,
        prepTime: String,
        comboTag: String,
        description: String,
        popular: Boolean,
        available: Boolean
    ) {
        val existing = if (id != null) dao.getMenuItemById(id) else null
        val dishId = existing?.id ?: "m${System.currentTimeMillis() % 100000}"
        val fallbackImg = when (category) {
            "Breakfast" -> DefaultSeedData.menuItems[0].imageUrl
            "Lunch" -> DefaultSeedData.menuItems[4].imageUrl
            else -> DefaultSeedData.menuItems[8].imageUrl
        }
        val item = MenuItemEntity(
            id = dishId,
            name = name.trim(),
            category = category,
            price = price.coerceAtLeast(1),
            available = available,
            rating = existing?.rating ?: 4.7,
            ratingCount = existing?.ratingCount ?: 12,
            popular = popular,
            comboTag = comboTag.trim().ifEmpty { "Chef Pick" },
            prepTime = prepTime.trim().ifEmpty { "8 mins" },
            imageUrl = existing?.imageUrl ?: fallbackImg,
            description = description.trim().ifEmpty { "Freshly prepared in the FastBite smart kitchen." }
        )
        dao.upsertMenuItem(item)
    }

    suspend fun deleteDish(dishId: String) {
        dao.deleteMenuItemById(dishId)
    }

    suspend fun toggleWeeklyDish(day: String, dishId: String) {
        val existing = dao.getWeeklyPlanForDay(day)
        val currentIds = existing?.dishIds()?.toMutableList() ?: mutableListOf()
        if (currentIds.contains(dishId)) {
            currentIds.remove(dishId)
        } else {
            currentIds.add(dishId)
        }
        dao.upsertWeeklyPlan(WeeklyPlanEntity(dayOfWeek = day, dishIdsCsv = currentIds.joinToString(",")))
    }

    suspend fun logWastage(dishName: String, qty: Int, unitPrice: Int, reason: String) {
        val now = System.currentTimeMillis()
        val timeFmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(now))
        val entry = WastageLogEntity(
            id = "w${now % 1000000}",
            dishName = dishName,
            qty = qty.coerceAtLeast(1),
            cost = qty.coerceAtLeast(1) * unitPrice.coerceAtLeast(10),
            date = timeFmt,
            reason = reason.trim().ifEmpty { "End-of-service surplus" },
            timestamp = now
        )
        dao.insertWastageLog(entry)
    }

    suspend fun submitFeedback(userName: String, type: String, message: String) {
        val now = System.currentTimeMillis()
        val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
        dao.insertFeedback(
            FeedbackEntity(
                id = "f${now % 1000000}",
                userName = userName,
                type = type,
                message = message.trim(),
                time = "Today, $timeFmt",
                timestamp = now
            )
        )
    }
}
