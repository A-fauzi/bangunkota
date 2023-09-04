package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangunkota.bangunkota.domain.entity.User
import com.bangunkota.bangunkota.domain.usecase.ExampleUseCase
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel(
    private val preferencesManager: UserPreferencesManager,
    private val userUseCase: ExampleUseCase<User>
) : ViewModel() {
    val userData: LiveData<User> = preferencesManager.userDataFlow
        .map { userData -> userData }
        .asLiveData()

    fun saveUserData(userId: String, username: String, userEmail: String, userPhoto: String) {
        viewModelScope.launch {
            preferencesManager.saveUserData(userId, username, userEmail, userPhoto)
        }
    }

    fun createUserDocument(data: User, documentId: String): Task<Void> {
        return userUseCase.createData(data, documentId)
    }

//    private val _data = MutableLiveData<DocumentSnapshot?>()
//    val data: LiveData<DocumentSnapshot?> = _data

    fun getDocumentUserById(id: String): Task<DocumentSnapshot> {
        return try {
            userUseCase.getData(id)
        } catch (e: Exception) {
            throw e
        }
    }
}