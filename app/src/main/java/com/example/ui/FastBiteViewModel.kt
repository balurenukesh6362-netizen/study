package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.ChimePlayer
import com.example.data.model.AppSettings
import com.example.data.model.CartItem
import com.example.data.model.CustomerUser
import com.example.data.model.DefaultSeedData
import com.example.data.model.FeedbackEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OwnerBankInfo
import com.example.data.model.WastageLogEntity
import com.example.data.model.WeeklyPlanEntity
import com.example.data.repository.FastBiteRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    LANDING,
    CUSTOMER_LOGIN,
    STAFF_LOGIN,
    CUSTOMER_APP,
    STAFF_APP
}

enum class CustomerTab {
    MENU,
    ORDERS,
    WEEKLY,
    INFO
}

enum class StaffTab {
    ORDERS,
    MENU,
    WEEKLY,
    PAYMENTS,
    WASTAGE_BANK
}

enum class ToastTone {
    ORANGE,
    EMERALD,
    AMBER,
    SLATE
}

data class ToastBanner(
    val id: Long,
    val title: String,
    val message: String,
    val tone: ToastTone = ToastTone.ORANGE
)

data class EphemeralUiState(
    val currentScreen: AppScreen = AppScreen.LANDING,
    val customerTab: CustomerTab = CustomerTab.MENU,
    val staffTab: StaffTab = StaffTab.ORDERS,
    val activeCategory: String = "All",
    val searchQuery: String = "",
    val selectedCalendarDay: String = "Wednesday",
    val staffSelectedDay: String = "Wednesday",
    val paymentFilter: String = "ALL",
    val cart: List<CartItem> = emptyList(),
    val scheduledTime: String = DefaultSeedData.pickupSlots[2],
    val selectedPayment: String = "Google Pay",
    val staffLoggedIn: Boolean = false,
    val staffEmail: String = "",
    val ownerUnlocked: Boolean = false,
    val latestTokenOrder: OrderEntity? = null,
    val toastBanner: ToastBanner? = null
)

data class FastBiteUiState(
    val menuItems: List<MenuItemEntity> = emptyList(),
    val weeklyPlans: List<WeeklyPlanEntity> = emptyList(),
    val orders: List<OrderEntity> = emptyList(),
    val wastageLogs: List<WastageLogEntity> = emptyList(),
    val feedbacks: List<FeedbackEntity> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val ui: EphemeralUiState = EphemeralUiState()
) {
    val cartCount: Int get() = ui.cart.sumOf { it.qty }
    val cartTotal: Int get() = ui.cart.sumOf { it.price * it.qty }
}

private data class RepoSnapshot(
    val menuItems: List<MenuItemEntity>,
    val weeklyPlans: List<WeeklyPlanEntity>,
    val orders: List<OrderEntity>,
    val wastageLogs: List<WastageLogEntity>,
    val feedbacks: List<FeedbackEntity>
)

class FastBiteViewModel(
    private val repository: FastBiteRepository
) : ViewModel() {

    private val ephemeral = MutableStateFlow(EphemeralUiState())

    private val repoSnapshot = combine(
        repository.menuItems,
        repository.weeklyPlans,
        repository.orders,
        repository.wastageLogs,
        repository.feedbacks
    ) { menu, plans, orders, wastage, feedbacks ->
        RepoSnapshot(menu, plans, orders, wastage, feedbacks)
    }

    val uiState: StateFlow<FastBiteUiState> = combine(
        repoSnapshot,
        repository.settings,
        ephemeral
    ) { snap, settings, ui ->
        FastBiteUiState(
            menuItems = snap.menuItems,
            weeklyPlans = snap.weeklyPlans,
            orders = snap.orders,
            wastageLogs = snap.wastageLogs,
            feedbacks = snap.feedbacks,
            settings = settings,
            ui = ui
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FastBiteUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun showToast(title: String, message: String, tone: ToastTone = ToastTone.ORANGE) {
        val toastId = System.currentTimeMillis()
        ephemeral.update { it.copy(toastBanner = ToastBanner(toastId, title, message, tone)) }
        viewModelScope.launch {
            delay(4_200)
            ephemeral.update { state ->
                if (state.toastBanner?.id == toastId) state.copy(toastBanner = null) else state
            }
        }
    }

    fun dismissToast() {
        ephemeral.update { it.copy(toastBanner = null) }
    }

    fun openCustomerPortalEntry() {
        val existing = uiState.value.settings.customerUser
        ephemeral.update {
            it.copy(
                currentScreen = if (existing != null) AppScreen.CUSTOMER_APP else AppScreen.CUSTOMER_LOGIN
            )
        }
    }

    fun openStaffPortalEntry() {
        ephemeral.update {
            it.copy(
                currentScreen = if (it.staffLoggedIn) AppScreen.STAFF_APP else AppScreen.STAFF_LOGIN
            )
        }
    }

    fun navigateTo(screen: AppScreen) {
        ephemeral.update { it.copy(currentScreen = screen) }
    }

    fun selectCustomerTab(tab: CustomerTab) {
        ephemeral.update { it.copy(customerTab = tab) }
    }

    fun selectStaffTab(tab: StaffTab) {
        ephemeral.update { it.copy(staffTab = tab) }
    }

    fun setCategory(category: String) {
        ephemeral.update { it.copy(activeCategory = category) }
    }

    fun setSearchQuery(query: String) {
        ephemeral.update { it.copy(searchQuery = query) }
    }

    fun setCustomerCalendarDay(day: String) {
        ephemeral.update { it.copy(selectedCalendarDay = day) }
    }

    fun setStaffCalendarDay(day: String) {
        ephemeral.update { it.copy(staffSelectedDay = day) }
    }

    fun setPaymentFilter(filter: String) {
        ephemeral.update { it.copy(paymentFilter = filter) }
    }

    fun setScheduledTime(time: String) {
        ephemeral.update { it.copy(scheduledTime = time) }
    }

    fun setSelectedPayment(method: String) {
        ephemeral.update { it.copy(selectedPayment = method) }
    }

    fun signInCustomer(name: String, contact: String, method: String) {
        if (name.isBlank() || contact.isBlank()) {
            showToast("Missing Details", "Please enter your name and email or phone number.", ToastTone.AMBER)
            return
        }
        viewModelScope.launch {
            val user = repository.signInCustomer(name, contact, method)
            ephemeral.update {
                it.copy(
                    currentScreen = AppScreen.CUSTOMER_APP,
                    customerTab = CustomerTab.MENU
                )
            }
            val tokenKind = if (user.method == "google") "Permanent Token" else "Temporary Token"
            showToast("Welcome, ${user.name}!", "Signed in with $tokenKind access.", ToastTone.EMERALD)
        }
    }

    fun signOutCustomer() {
        viewModelScope.launch {
            repository.signOutCustomer()
            ephemeral.update {
                it.copy(
                    currentScreen = AppScreen.LANDING,
                    cart = emptyList()
                )
            }
        }
    }

    fun signInStaff(email: String, password: String): Boolean {
        val cleanEmail = email.trim()
        return if (cleanEmail.isNotEmpty() && (password == "chef123" || password == "staff123" || password == "admin123")) {
            ephemeral.update {
                it.copy(
                    staffLoggedIn = true,
                    staffEmail = cleanEmail,
                    currentScreen = AppScreen.STAFF_APP,
                    staffTab = StaffTab.ORDERS
                )
            }
            showToast("Kitchen Console Active", "Signed in as $cleanEmail", ToastTone.EMERALD)
            true
        } else {
            showToast("Invalid Credentials", "Use demo credentials kitchen@fastbite.in / chef123.", ToastTone.AMBER)
            false
        }
    }

    fun signOutStaff() {
        ephemeral.update {
            it.copy(
                staffLoggedIn = false,
                staffEmail = "",
                ownerUnlocked = false,
                currentScreen = AppScreen.LANDING
            )
        }
    }

    fun unlockOwnerVault(pin: String): Boolean {
        return if (pin.trim() == "owner123" || pin.trim() == "1234") {
            ephemeral.update { it.copy(ownerUnlocked = true) }
            showToast("Owner Vault Unlocked", "Bank settlement & penalty controls enabled.", ToastTone.EMERALD)
            true
        } else {
            showToast("Wrong Owner PIN", "Enter owner123 or 1234 to unlock.", ToastTone.AMBER)
            false
        }
    }

    fun lockOwnerVault() {
        ephemeral.update { it.copy(ownerUnlocked = false) }
    }

    fun addToCart(dish: MenuItemEntity, qtyToAdd: Int = 1) {
        if (!dish.available) {
            showToast("Sold Out", "${dish.name} is currently unavailable.", ToastTone.AMBER)
            return
        }
        ephemeral.update { state ->
            val mutable = state.cart.toMutableList()
            val idx = mutable.indexOfFirst { it.dishId == dish.id }
            if (idx >= 0) {
                val existing = mutable[idx]
                mutable[idx] = existing.copy(qty = (existing.qty + qtyToAdd).coerceAtMost(20))
            } else {
                mutable.add(
                    CartItem(
                        dishId = dish.id,
                        name = dish.name,
                        price = dish.price,
                        qty = qtyToAdd.coerceIn(1, 20),
                        imageUrl = dish.imageUrl,
                        category = dish.category
                    )
                )
            }
            state.copy(cart = mutable)
        }
        showToast("Added to Tray", "${qtyToAdd}× ${dish.name}", ToastTone.ORANGE)
    }

    fun updateCartQty(dishId: String, delta: Int) {
        ephemeral.update { state ->
            val mutable = state.cart.toMutableList()
            val idx = mutable.indexOfFirst { it.dishId == dishId }
            if (idx >= 0) {
                val nextQty = mutable[idx].qty + delta
                if (nextQty <= 0) {
                    mutable.removeAt(idx)
                } else {
                    mutable[idx] = mutable[idx].copy(qty = nextQty.coerceAtMost(20))
                }
            }
            state.copy(cart = mutable)
        }
    }

    fun clearCart() {
        ephemeral.update { it.copy(cart = emptyList()) }
    }

    fun placeOrder() {
        val current = uiState.value
        val user = current.settings.customerUser ?: CustomerUser("Guest Diner", "guest@fastbite.in", "google")
        val cart = current.ui.cart
        if (cart.isEmpty()) return

        viewModelScope.launch {
            val order = repository.placeOrder(
                customer = user,
                cart = cart,
                pickupTime = current.ui.scheduledTime,
                paymentMethod = current.ui.selectedPayment
            )
            ChimePlayer.playChime("order", current.settings.soundEnabled)
            ephemeral.update {
                it.copy(
                    cart = emptyList(),
                    latestTokenOrder = order,
                    customerTab = CustomerTab.ORDERS
                )
            }
            showToast(
                "Order Confirmed • ${order.id}",
                "Token ${order.tokenCode} generated for ${order.pickupTime}.",
                ToastTone.EMERALD
            )
        }
    }

    fun dismissLatestToken() {
        ephemeral.update { it.copy(latestTokenOrder = null) }
    }

    fun cancelOrder(orderId: String) {
        val penaltyPercent = uiState.value.settings.penaltyPercent
        viewModelScope.launch {
            val updated = repository.cancelOrder(orderId, penaltyPercent) ?: return@launch
            val msg = if (updated.paymentMode == "Online") {
                "₹${updated.refundAmount} refund initiated (₹${updated.penaltyDeducted} penalty applied)."
            } else {
                "COD order cancelled with zero fee."
            }
            showToast("Order ${updated.id} Cancelled", msg, ToastTone.AMBER)
        }
    }

    fun rateOrder(orderId: String, stars: Int, comment: String) {
        viewModelScope.launch {
            repository.rateOrder(orderId, stars, comment)
            showToast("Thanks for Rating!", "Your $stars★ feedback updated our kitchen score.", ToastTone.EMERALD)
        }
    }

    fun advanceOrderStatus(order: OrderEntity, nextStatus: String) {
        val soundOn = uiState.value.settings.soundEnabled
        viewModelScope.launch {
            val updated = repository.advanceOrderStatus(order.id, nextStatus) ?: return@launch
            if (nextStatus == "Ready") {
                ChimePlayer.playChime("ready", soundOn)
                showToast(
                    "Order ${updated.id} is READY!",
                    "Token ${updated.tokenCode} • ${updated.customerName} notified for Counter #1 pickup.",
                    ToastTone.EMERALD
                )
            } else {
                showToast(
                    "Order ${updated.id} Updated",
                    "Status moved to $nextStatus.",
                    ToastTone.ORANGE
                )
            }
        }
    }

    fun toggleDishAvailability(dishId: String) {
        viewModelScope.launch {
            repository.toggleDishAvailability(dishId)
        }
    }

    fun upsertDish(
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
        viewModelScope.launch {
            repository.upsertDish(id, name, category, price, prepTime, comboTag, description, popular, available)
            showToast(
                if (id == null) "Dish Added" else "Dish Updated",
                "$name is now live in $category.",
                ToastTone.EMERALD
            )
        }
    }

    fun deleteDish(dishId: String, name: String) {
        viewModelScope.launch {
            repository.deleteDish(dishId)
            showToast("Dish Removed", "$name deleted from menu.", ToastTone.SLATE)
        }
    }

    fun toggleWeeklyDish(day: String, dishId: String) {
        viewModelScope.launch {
            repository.toggleWeeklyDish(day, dishId)
        }
    }

    fun logWastage(dishName: String, qty: Int, unitPrice: Int, reason: String) {
        viewModelScope.launch {
            repository.logWastage(dishName, qty, unitPrice, reason)
            showToast("Wastage Logged", "$qty× $dishName recorded.", ToastTone.AMBER)
        }
    }

    fun submitFeedback(type: String, message: String) {
        if (message.isBlank()) return
        val user = uiState.value.settings.customerUser?.name ?: "Student Diner"
        viewModelScope.launch {
            repository.submitFeedback(user, type, message)
            showToast("Feedback Sent", "Thank you for helping FastBite improve!", ToastTone.EMERALD)
        }
    }

    fun toggleSound() {
        val next = !uiState.value.settings.soundEnabled
        viewModelScope.launch {
            repository.setSoundEnabled(next)
            if (next) ChimePlayer.playChime("order", true)
            showToast("Sound Chimes ${if (next) "On" else "Muted"}", "Kitchen & token alerts updated.", ToastTone.SLATE)
        }
    }

    fun updateOwnerSettings(bank: OwnerBankInfo, penaltyPercent: Int) {
        viewModelScope.launch {
            repository.updateOwnerBank(bank)
            repository.updatePenaltyPercent(penaltyPercent)
            showToast("Settlement Policy Saved", "Bank details & $penaltyPercent% cancellation fee updated.", ToastTone.EMERALD)
        }
    }

    class Factory(private val repository: FastBiteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FastBiteViewModel(repository) as T
        }
    }
}
