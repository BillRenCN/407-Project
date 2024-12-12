package com.cs407.project.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserState(
    val userName: String = "No name provided",
    val userId: Int = 0,
    val userDescription: String? = "No description",
    val rating: Float = 0f,
    val comments: List<String> = emptyList(),
    val items: List<String> = emptyList()
)

class ProfileViewModel : ViewModel() {

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
            it.copy(userDescription = description)
        }
    }
}