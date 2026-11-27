package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.FeedbackEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.WastageLogEntity
import com.example.data.model.WeeklyPlanEntity

@Database(
    entities = [
        MenuItemEntity::class,
        WeeklyPlanEntity::class,
        OrderEntity::class,
        WastageLogEntity::class,
        FeedbackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FastBiteDatabase : RoomDatabase() {
    abstract fun fastBiteDao(): FastBiteDao

    companion object {
        @Volatile
        private var INSTANCE: FastBiteDatabase? = null

        fun getInstance(context: Context): FastBiteDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FastBiteDatabase::class.java,
                    "fastbite_canteen.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
