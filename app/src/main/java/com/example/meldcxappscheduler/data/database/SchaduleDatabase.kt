package com.example.meldcxappscheduler.data.database

import com.example.meldcxappscheduler.data.AppScheduleStore
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Created by @author Moniruzzaman on 20/9/23. github: filelucker

// Database class represents database and contains the data
@Database(
    entities = [AppScheduleStore::class],
    version = 1,                // <- Database version
    exportSchema = true
)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        fun getDatabase(context: Context): ScheduleDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}