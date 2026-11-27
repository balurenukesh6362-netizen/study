package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val available: Boolean,
    val rating: Double,
    val ratingCount: Int,
    val popular: Boolean,
    val comboTag: String,
    val prepTime: String,
    val imageUrl: String,
    val description: String
)

@Entity(tableName = "weekly_plans")
data class WeeklyPlanEntity(
    @PrimaryKey val dayOfWeek: String,
    val dishIdsCsv: String
) {
    fun dishIds(): List<String> =
        dishIdsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val tokenCode: String,
    val tokenType: String, // "Permanent" | "Temporary"
    val customerName: String,
    val customerContact: String,
    val loginMethod: String, // "google" | "phone"
    val itemsJson: String,
    val totalAmount: Int,
    val paymentMethod: String, // "Google Pay" | "PhonePe" | "UPI" | "Cash on Delivery"
    val paymentMode: String, // "Online" | "COD"
    val pickupTime: String,
    val status: String, // "Order Placed" | "Preparing" | "Ready" | "Completed" | "Cancelled"
    val createdAt: String,
    val createdAtMillis: Long,
    val rated: Boolean = false,
    val ratingGiven: Int = 0,
    val ratingComment: String = "",
    val penaltyDeducted: Int = 0,
    val refundAmount: Int = 0
)

@Entity(tableName = "wastage_logs")
data class WastageLogEntity(
    @PrimaryKey val id: String,
    val dishName: String,
    val qty: Int,
    val cost: Int,
    val date: String,
    val reason: String,
    val timestamp: Long
)

@Entity(tableName = "feedbacks")
data class FeedbackEntity(
    @PrimaryKey val id: String,
    val userName: String,
    val type: String, // "Compliment" | "Suggestion" | "Issue"
    val message: String,
    val time: String,
    val timestamp: Long
)

data class OrderLineItem(
    val id: String,
    val name: String,
    val price: Int,
    val qty: Int
)

data class CartItem(
    val dishId: String,
    val name: String,
    val price: Int,
    val qty: Int,
    val imageUrl: String,
    val category: String
)

data class CustomerUser(
    val name: String,
    val contact: String,
    val method: String // "google" | "phone"
)

data class OwnerBankInfo(
    val accountHolder: String = "FastBite Hospitality Pvt Ltd",
    val bankName: String = "HDFC Bank Ltd",
    val accountNumber: String = "50200084920144",
    val ifsc: String = "HDFC0001204",
    val upiId: String = "fastbite.settlement@okhdfcbank"
)

data class AppSettings(
    val customerUser: CustomerUser? = null,
    val soundEnabled: Boolean = true,
    val penaltyPercent: Int = 15,
    val ownerBank: OwnerBankInfo = OwnerBankInfo()
)

object OrderItemCodec {
    fun encode(items: List<OrderLineItem>): String {
        val arr = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("price", item.price)
            obj.put("qty", item.qty)
            arr.put(obj)
        }
        return arr.toString()
    }

    fun decode(json: String): List<OrderLineItem> {
        if (json.isBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(json)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        OrderLineItem(
                            id = obj.optString("id"),
                            name = obj.optString("name"),
                            price = obj.optInt("price"),
                            qty = obj.optInt("qty", 1)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }
}

object DefaultSeedData {
    val daysOfWeek = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    )

    val pickupSlots = listOf(
        "12:45 PM", "1:00 PM", "1:15 PM", "1:30 PM", "1:45 PM", "4:30 PM", "5:00 PM"
    )

    val menuItems = listOf(
        MenuItemEntity(
            id = "m1",
            name = "Masala Dosa & Sambar",
            category = "Breakfast",
            price = 65,
            available = true,
            rating = 4.8,
            ratingCount = 142,
            popular = true,
            comboTag = "Morning Bestseller",
            prepTime = "8 mins",
            imageUrl = "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=600&q=80",
            description = "Crispy golden fermented crepe stuffed with spiced potato masala, coconut chutney & hot sambar."
        ),
        MenuItemEntity(
            id = "m2",
            name = "Steamed Idli Vada Combo",
            category = "Breakfast",
            price = 50,
            available = true,
            rating = 4.6,
            ratingCount = 98,
            popular = false,
            comboTag = "Light & Healthy",
            prepTime = "5 mins",
            imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=600&q=80",
            description = "Two fluffy steamed rice cakes paired with crispy medu vada and trio of chutneys."
        ),
        MenuItemEntity(
            id = "m3",
            name = "Aloo Paratha with Curd",
            category = "Breakfast",
            price = 60,
            available = true,
            rating = 4.7,
            ratingCount = 84,
            popular = true,
            comboTag = "Chef Special",
            prepTime = "10 mins",
            imageUrl = "https://images.unsplash.com/photo-1626132647523-66f5bf380027?auto=format&fit=crop&w=600&q=80",
            description = "Whole wheat flatbread stuffed with seasoned potatoes, served with chilled curd & pickle."
        ),
        MenuItemEntity(
            id = "m4",
            name = "Filter Coffee & Poha",
            category = "Breakfast",
            price = 45,
            available = true,
            rating = 4.5,
            ratingCount = 64,
            popular = false,
            comboTag = "Quick Bite",
            prepTime = "4 mins",
            imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=600&q=80",
            description = "Flattened rice tempered with mustard, peanuts & curry leaves alongside authentic frothy filter coffee."
        ),
        MenuItemEntity(
            id = "m5",
            name = "Executive Veg Thali",
            category = "Lunch",
            price = 110,
            available = true,
            rating = 4.9,
            ratingCount = 215,
            popular = true,
            comboTag = "House Top Pick • Full Meal",
            prepTime = "12 mins",
            imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=600&q=80",
            description = "Paneer butter masala, dal tadka, seasonal veg, 3 chapatis, jeera rice, salad & gulab jamun."
        ),
        MenuItemEntity(
            id = "m6",
            name = "Hyderabadi Paneer Biryani",
            category = "Lunch",
            price = 120,
            available = true,
            rating = 4.8,
            ratingCount = 178,
            popular = true,
            comboTag = "Trending Now",
            prepTime = "10 mins",
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80",
            description = "Aromatic basmati rice slow-cooked with marinated cottage cheese, saffron, mint & cooling raita."
        ),
        MenuItemEntity(
            id = "m7",
            name = "Rajma Chawal Bowl",
            category = "Lunch",
            price = 85,
            available = true,
            rating = 4.6,
            ratingCount = 112,
            popular = false,
            comboTag = "Comfort Classic",
            prepTime = "7 mins",
            imageUrl = "https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&w=600&q=80",
            description = "Slow-simmered red kidney beans in rich onion-tomato gravy served over steamed basmati rice."
        ),
        MenuItemEntity(
            id = "m8",
            name = "Curd Rice & Pickle",
            category = "Lunch",
            price = 55,
            available = false,
            rating = 4.4,
            ratingCount = 51,
            popular = false,
            comboTag = "Cooling Meal",
            prepTime = "5 mins",
            imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=600&q=80",
            description = "Creamy tempered yogurt rice garnished with pomegranate seeds, curry leaves and mango pickle."
        ),
        MenuItemEntity(
            id = "m9",
            name = "Punjabi Samosa (2 Pcs)",
            category = "Snacks",
            price = 35,
            available = true,
            rating = 4.9,
            ratingCount = 260,
            popular = true,
            comboTag = "Evening Favorite",
            prepTime = "4 mins",
            imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=600&q=80",
            description = "Crispy flaky pastry triangles filled with spiced potatoes & green peas, served with tamarind & mint chutney."
        ),
        MenuItemEntity(
            id = "m10",
            name = "Grilled Cheese Sandwich",
            category = "Snacks",
            price = 70,
            available = true,
            rating = 4.7,
            ratingCount = 134,
            popular = true,
            comboTag = "Combo Deal",
            prepTime = "8 mins",
            imageUrl = "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=600&q=80",
            description = "Triple-layer toasted bread loaded with melted mozzarella, sweet corn, bell peppers & mint mayo."
        ),
        MenuItemEntity(
            id = "m11",
            name = "Vada Pav & Masala Chai",
            category = "Snacks",
            price = 45,
            available = true,
            rating = 4.8,
            ratingCount = 190,
            popular = false,
            comboTag = "Student Saver",
            prepTime = "5 mins",
            imageUrl = "https://images.unsplash.com/photo-1606491956689-2ea866880c84?auto=format&fit=crop&w=600&q=80",
            description = "Mumbai street-style spiced potato fritter slider with dry garlic chutney and hot ginger cardamom tea."
        ),
        MenuItemEntity(
            id = "m12",
            name = "Crispy Veg Hakka Noodles",
            category = "Snacks",
            price = 80,
            available = true,
            rating = 4.5,
            ratingCount = 89,
            popular = false,
            comboTag = "Spicy Pick",
            prepTime = "9 mins",
            imageUrl = "https://images.unsplash.com/photo-1585032226651-759b368d7246?auto=format&fit=crop&w=600&q=80",
            description = "Wok-tossed noodles with crunchy julienned vegetables, soy-garlic glaze and spring onions."
        )
    )

    val weeklyPlans = listOf(
        WeeklyPlanEntity("Monday", "m1,m5,m9,m11"),
        WeeklyPlanEntity("Tuesday", "m2,m6,m10,m12"),
        WeeklyPlanEntity("Wednesday", "m3,m5,m7,m9"),
        WeeklyPlanEntity("Thursday", "m1,m6,m8,m10"),
        WeeklyPlanEntity("Friday", "m4,m5,m6,m11"),
        WeeklyPlanEntity("Saturday", "m2,m7,m9,m12"),
        WeeklyPlanEntity("Sunday", "m3,m6,m10,m11")
    )

    val initialOrders: List<OrderEntity> by lazy {
        listOf(
            OrderEntity(
                id = "ORD-9041",
                tokenCode = "PERM-FB-8821",
                tokenType = "Permanent",
                customerName = "Aarav Sharma",
                customerContact = "aarav.sharma@gmail.com",
                loginMethod = "google",
                itemsJson = OrderItemCodec.encode(
                    listOf(
                        OrderLineItem("m5", "Executive Veg Thali", 110, 1),
                        OrderLineItem("m9", "Punjabi Samosa (2 Pcs)", 35, 1)
                    )
                ),
                totalAmount = 145,
                paymentMethod = "Google Pay",
                paymentMode = "Online",
                pickupTime = "1:15 PM",
                status = "Ready",
                createdAt = "Today, 12:48 PM",
                createdAtMillis = System.currentTimeMillis() - 600_000L
            ),
            OrderEntity(
                id = "ORD-9040",
                tokenCode = "TEMP-FB-4410",
                tokenType = "Temporary",
                customerName = "Priya Nair",
                customerContact = "+91 98765 43210",
                loginMethod = "phone",
                itemsJson = OrderItemCodec.encode(
                    listOf(OrderLineItem("m1", "Masala Dosa & Sambar", 65, 2))
                ),
                totalAmount = 130,
                paymentMethod = "Cash on Delivery",
                paymentMode = "COD",
                pickupTime = "1:30 PM",
                status = "Preparing",
                createdAt = "Today, 12:55 PM",
                createdAtMillis = System.currentTimeMillis() - 300_000L
            ),
            OrderEntity(
                id = "ORD-9035",
                tokenCode = "PERM-FB-7719",
                tokenType = "Permanent",
                customerName = "Rohan Verma",
                customerContact = "rohan.v@gmail.com",
                loginMethod = "google",
                itemsJson = OrderItemCodec.encode(
                    listOf(OrderLineItem("m6", "Hyderabadi Paneer Biryani", 120, 1))
                ),
                totalAmount = 120,
                paymentMethod = "PhonePe",
                paymentMode = "Online",
                pickupTime = "12:30 PM",
                status = "Completed",
                createdAt = "Yesterday, 12:10 PM",
                createdAtMillis = System.currentTimeMillis() - 86_400_000L,
                rated = true,
                ratingGiven = 5,
                ratingComment = "Super aromatic and hot!"
            )
        )
    }

    val initialWastage = listOf(
        WastageLogEntity(
            id = "w1",
            dishName = "Curd Rice & Pickle",
            qty = 3,
            cost = 165,
            date = "Yesterday",
            reason = "Late afternoon surplus",
            timestamp = System.currentTimeMillis() - 86_400_000L
        ),
        WastageLogEntity(
            id = "w2",
            dishName = "Steamed Idli Vada Combo",
            qty = 2,
            cost = 100,
            date = "2 days ago",
            reason = "Breakfast batch over-prep",
            timestamp = System.currentTimeMillis() - 172_800_000L
        )
    )

    val initialFeedbacks = listOf(
        FeedbackEntity(
            id = "f1",
            userName = "Rohan Verma",
            type = "Compliment",
            message = "Hyderabadi Paneer Biryani was super fresh and hot!",
            time = "Yesterday",
            timestamp = System.currentTimeMillis() - 86_400_000L
        )
    )
}
