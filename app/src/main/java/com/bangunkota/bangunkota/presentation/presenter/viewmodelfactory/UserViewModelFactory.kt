package com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.domain.usecase.SignInUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.SignInViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.utils.UserPreferencesManager

class UserViewModelFactory(private val preferencesManager: UserPreferencesManager): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(preferencesManager) as T
        }
        throw IllegalArgumentException("Unknwon ViewModel Class")
    }
}