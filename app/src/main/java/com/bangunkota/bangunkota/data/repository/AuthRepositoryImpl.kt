package com.bangunkota.bangunkota.data.repository

import com.bangunkota.bangunkota.data.repository.abstractions.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuth) : AuthRepository {
    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<Unit> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return try {
            firebaseAuth.signInWithCredential(credential)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun checkCurrentUser(): Boolean {
        return firebaseAuth.currentUser != null
    }
}