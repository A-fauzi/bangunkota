package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserViewModel(private val preferencesManager: UserPreferencesManager): ViewModel() {
    val userId: LiveData<String> = preferencesManager.userIdFlow.map { userId -> userId }.asLiveData()
    val userName: LiveData<String> = preferencesManager.userNameFlow.map { username -> username }.asLiveData()
    val userEmail: LiveData<String> = preferencesManager.userEmailFlow.map { userEmail -> userEmail }.asLiveData()
    val userPhoto: LiveData<String> = preferencesManager.userPhotoFlow.map { userPhoto -> userPhoto }.asLiveData()

    fun saveUserData(userId: String, username: String, userEmail: String, userPhoto: String) {
        viewModelScope.launch {
            preferencesManager.saveUserData(userId, username, userEmail, userPhoto)
        }
    }
}