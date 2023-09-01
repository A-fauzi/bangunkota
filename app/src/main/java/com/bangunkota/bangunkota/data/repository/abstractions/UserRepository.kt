package com.bangunkota.bangunkota.data.repository.abstractions

import com.bangunkota.bangunkota.domain.entity.CommunityPost
import com.bangunkota.bangunkota.domain.entity.User

interface UserRepository {
    // Menambahkan user baru ke database
    suspend fun addUser(user: User): Result<Unit>
}