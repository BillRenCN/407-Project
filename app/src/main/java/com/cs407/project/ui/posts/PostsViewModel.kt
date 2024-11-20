package com.cs407.project.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is posts Fragment"
    }
    val text: LiveData<String> = _text
}