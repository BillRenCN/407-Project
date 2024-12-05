package com.cs407.project.ui.listing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ListingViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val _allListings = MutableLiveData<List<Item>>()
    val allListings: LiveData<List<Item>> get() = _allListings

    init {
        refreshListings() // Load data initially
    }

    fun refreshListings() {
        viewModelScope.launch {
            val items = database.itemDao().getAllItems()
            _allListings.postValue(items)
        }
    }
}
