package com.bangunkota.bangunkota.domain.usecase

import com.bangunkota.bangunkota.data.repository.abstractions.UserRepository
import com.google.firebase.firestore.DocumentSnapshot

class UserUseCase(private val repository: UserRepository) {
    suspend fun getUserById(collectionPath: String, id: String): DocumentSnapshot {
        return repository.getUserById(collectionPath, id)
    }
}