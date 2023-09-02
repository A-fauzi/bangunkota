package com.bangunkota.bangunkota.data.repository.abstractions

import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.domain.entity.User
import com.google.firebase.firestore.DocumentSnapshot

interface UserRepository {
    suspend fun addUser(user: User): Result<Unit>

    suspend fun getUserById(collectionPath: String, id: String): DocumentSnapshot
}