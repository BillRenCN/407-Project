package com.cs407.project.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserState(
    val userName: String = "No Name Provided",
    val userId: Int = 0,
    //val userDescription: String? = "No Description Provided"
    /*
    val rating: Float = 0f,
    val comments: List<String> = emptyList(),
    val items: List<String> = emptyList()
     */
)

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    // Expose screen UI state
    private val _userState = MutableStateFlow(UserState())
    val userState = _userState.asStateFlow()

    fun setUser(state: UserState) {
        _userState.update {
            state
        }
    }

    fun setName(name: String) {
        _userState.update {
            it.copy(userName = name)
        }
        Log.d("ProfileViewModel", "Name changed to $name")
    }

    fun setDescription(description: String) {
        _userState.update {
            it.copy(userName = description)
        }
    }
}