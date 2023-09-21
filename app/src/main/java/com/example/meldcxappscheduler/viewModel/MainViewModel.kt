package com.example.meldcxappscheduler.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.meldcxappscheduler.data.AppScheduleStore
import com.example.meldcxappscheduler.data.database.ScheduleDatabase
import com.example.meldcxappscheduler.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// Created by @author Moniruzzaman on 20/9/23. github: filelucker

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<AppScheduleStore>>
    private val repository: AppRepository

    init {
        val appDao = ScheduleDatabase.getDatabase(application).appDao()
        repository = AppRepository(appDao)
        readAllData = repository.readAllData
    }

    fun add(appScheduleStore: AppScheduleStore) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.add(appScheduleStore)
        }
    }

    fun update(appScheduleStore: AppScheduleStore) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(appScheduleStore)
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(id)
        }
    }

    fun deleteByPackageId(packageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteByPackageId(packageId)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}