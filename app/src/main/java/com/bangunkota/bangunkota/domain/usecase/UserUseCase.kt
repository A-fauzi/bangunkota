package com.bangunkota.bangunkota.domain.usecase

import com.bangunkota.bangunkota.data.repository.abstractions.UserRepository
import com.bangunkota.bangunkota.domain.entity.User
import com.google.firebase.firestore.DocumentSnapshot

class UserUseCase(private val repository: UserRepository) {
    suspend fun getUserById(collectionPath: String, id: String): DocumentSnapshot {
        return repository.getUserById(collectionPath, id)
    }

    suspend fun execute(uid: String, user: User, onSuccess: () -> Unit, onFailure: () -> Unit) {
        repository.addUser(uid, user, onSuccess, onFailure)
    }
}