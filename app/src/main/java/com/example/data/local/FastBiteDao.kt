package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FeedbackEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.WastageLogEntity
import com.example.data.model.WeeklyPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FastBiteDao {

    @Query("SELECT * FROM menu_items ORDER BY popular DESC, name ASC")
    fun observeMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT COUNT(*) FROM menu_items")
    suspend fun getMenuCount(): Int

    @Query("SELECT * FROM menu_items WHERE id = :id LIMIT 1")
    suspend fun getMenuItemById(id: String): MenuItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMenuItem(item: MenuItemEntity)

    @Query("DELETE FROM menu_items WHERE id = :id")
    suspend fun deleteMenuItemById(id: String)

    @Query("SELECT * FROM weekly_plans")
    fun observeWeeklyPlans(): Flow<List<WeeklyPlanEntity>>

    @Query("SELECT * FROM weekly_plans WHERE dayOfWeek = :day LIMIT 1")
    suspend fun getWeeklyPlanForDay(day: String): WeeklyPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyPlans(plans: List<WeeklyPlanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeeklyPlan(plan: WeeklyPlanEntity)

    @Query("SELECT * FROM orders ORDER BY createdAtMillis DESC")
    fun observeOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("SELECT * FROM wastage_logs ORDER BY timestamp DESC")
    fun observeWastageLogs(): Flow<List<WastageLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWastageLogs(logs: List<WastageLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWastageLog(log: WastageLogEntity)

    @Query("SELECT * FROM feedbacks ORDER BY timestamp DESC")
    fun observeFeedbacks(): Flow<List<FeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedbacks(items: List<FeedbackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(item: FeedbackEntity)
}
