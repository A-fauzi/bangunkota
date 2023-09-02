package com.bangunkota.bangunkota.data.repository.implementatios

import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.data.repository.abstractions.UserRepository
import com.bangunkota.bangunkota.domain.entity.User
import com.google.firebase.firestore.DocumentSnapshot

class UserRepositoryImpl(private val fireStoreManager: FireStoreManager): UserRepository {
    override suspend fun addUser(uid: String, user: User, onSuccess: () -> Unit, onFailure: () -> Unit) {
        fireStoreManager.createUserDocument(uid, user, onSuccess, onFailure)
    }

    override suspend fun getUserById(collectionPath: String, id: String): DocumentSnapshot {
        return fireStoreManager.getDocumentById(collectionPath, id)
    }
}