package com.example.meldcxappscheduler.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.meldcxappscheduler.data.AppScheduleStore

// Created by @author Moniruzzaman on 20/9/23. github: filelucker

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(appScheduleStore: AppScheduleStore)

    @Update
    suspend fun update(appScheduleStore: AppScheduleStore)

    @Query("DELETE FROM app_schedule_store_table WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM app_schedule_store_table WHERE packageId = :packageId")
    fun deleteByPackageId(packageId: String)

    @Query("DELETE FROM app_schedule_store_table")
    suspend fun deleteAll()

    @Query("SELECT * from app_schedule_store_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<AppScheduleStore>>
}