package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangunkota.bangunkota.domain.entity.User
import com.bangunkota.bangunkota.domain.usecase.UserUseCase
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserViewModel(private val preferencesManager: UserPreferencesManager, private val userUseCase: UserUseCase): ViewModel() {
    val userData: LiveData<User> = preferencesManager.userDataFlow
        .map { userData -> userData }
        .asLiveData()

    fun saveUserData(userId: String, username: String, userEmail: String, userPhoto: String) {
        viewModelScope.launch {
            preferencesManager.saveUserData(userId, username, userEmail, userPhoto)
        }
    }

    suspend fun createUserDocument(uid: String, user: User, onSuccess: () -> Unit, onFailure: () -> Unit) {
        userUseCase.execute(uid, user, onSuccess, onFailure)
    }

    private val _data = MutableLiveData<DocumentSnapshot?>()
    val data: LiveData<DocumentSnapshot?> = _data

    fun getDocumentUserById(collectionPath: String, id: String) {
        viewModelScope.launch {
            val result = userUseCase.getUserById(collectionPath, id)
            _data.postValue(result)
        }
    }
}