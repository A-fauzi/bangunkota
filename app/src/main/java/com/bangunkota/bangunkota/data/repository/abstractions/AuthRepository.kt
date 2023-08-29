package com.bangunkota.bangunkota.data.repository.abstractions

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface AuthRepository {
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<Unit>
    fun checkCurrentUser(): Boolean
}