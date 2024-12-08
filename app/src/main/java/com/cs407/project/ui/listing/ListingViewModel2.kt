package com.cs407.project.ui.listing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import com.cs407.project.data.SharedPreferences
import kotlinx.coroutines.launch

class ListingViewModel2(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val _allListings = MutableLiveData<List<Item>>()
    val allListings: LiveData<List<Item>> get() = _allListings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _filteredListings = MutableLiveData<List<Item>>()
    val filteredListings: LiveData<List<Item>> get() = _filteredListings

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterListings(query)
    }

    fun filterListings(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                _filteredListings.postValue(_allListings.value)
                return@launch
            }
            val items = database.itemDao().searchItemsByTitle(query)
            _filteredListings.postValue(items)
        }
    }

    init {
        // We do not initialize the listings here, it's done in ListingFragment2
    }

    fun refreshListings(userId: Int) {
        _isLoading.postValue(true)
        _searchQuery.value = ""
        viewModelScope.launch {
            val items = database.itemDao().getItemsByUserId(userId)
            _allListings.postValue(items)
            _filteredListings.postValue(items)
            _isLoading.postValue(false)
        }
    }
}