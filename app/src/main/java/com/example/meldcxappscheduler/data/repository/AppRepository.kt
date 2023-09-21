package com.example.meldcxappscheduler.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.example.meldcxappscheduler.data.AppScheduleStore
import com.example.meldcxappscheduler.data.database.AppDao

// Created by @author Moniruzzaman on 20/9/23. github: filelucker

class AppRepository(private val dao: AppDao) {
    val readAllData: LiveData<List<AppScheduleStore>> = dao.readAllData()

    suspend fun add(appScheduleStore: AppScheduleStore) {
        dao.add(appScheduleStore)
    }

    suspend fun update(appScheduleStore: AppScheduleStore) {
        dao.update(appScheduleStore)
    }

    suspend fun delete(id: Int) {
        dao.delete(id)
    }

    suspend fun deleteByPackageId(packageId: String) {
        dao.deleteByPackageId(packageId)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}