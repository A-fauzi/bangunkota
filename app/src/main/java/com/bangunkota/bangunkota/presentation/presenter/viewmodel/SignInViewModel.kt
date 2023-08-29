package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangunkota.bangunkota.domain.usecase.SignInUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

class SignInViewModel(private val signInUseCase: SignInUseCase): ViewModel() {
    private val _signInResult = MutableLiveData<Result<Unit>>()
    val signInResult: LiveData<Result<Unit>> = _signInResult

    fun signIn(account: GoogleSignInAccount) {
        viewModelScope.launch {
            val result = signInUseCase.signInWithGoogle(account)
            _signInResult.value = result
        }
    }

    private val _isUserSignedIn = MutableLiveData<Boolean>()
    val isUserSignedIn: LiveData<Boolean> = _isUserSignedIn

    fun checkCurrentUser() {
        val isSignedIn = signInUseCase.checkCurrentUser()
        _isUserSignedIn.value = isSignedIn
    }
}