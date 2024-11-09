package com.cs407.project.ui.listing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is listing Fragment"
    }
    val text: LiveData<String> = _text
}