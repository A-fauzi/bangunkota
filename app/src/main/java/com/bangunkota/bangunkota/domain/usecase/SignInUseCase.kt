package com.bangunkota.bangunkota.domain.usecase

import com.bangunkota.bangunkota.data.repository.abstractions.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class SignInUseCase(private val authRepository: AuthRepository) {
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<Unit> {
        return authRepository.signInWithGoogle(account)
    }

    fun checkCurrentUser(): Boolean {
        return authRepository.checkCurrentUser()
    }
}