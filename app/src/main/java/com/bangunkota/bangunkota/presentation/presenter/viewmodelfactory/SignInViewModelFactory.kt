package com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.domain.usecase.SignInUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.SignInViewModel

class SignInViewModelFactory(private val signInUseCase: SignInUseCase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignInViewModel(signInUseCase) as T
        }
        throw IllegalArgumentException("Unknwon ViewModel Class")
    }
}