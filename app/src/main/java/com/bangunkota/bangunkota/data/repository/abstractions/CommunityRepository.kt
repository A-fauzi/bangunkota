package com.bangunkota.bangunkota.data.repository.abstractions

import androidx.lifecycle.LiveData
import com.bangunkota.bangunkota.domain.entity.CommunityPost

interface CommunityRepository {
    // get data firestore
//    suspend fun fetchDataFromFireStoreAndSaveToRoom()

    // Menampilkan detail post berdasarkan ID
    suspend fun getPostById(postId: String): CommunityPost?

    // Menambahkan post baru
    suspend fun addPost(post: CommunityPost): Result<Unit>

    // Memperbarui informasi post
    suspend fun updatePost(post: CommunityPost): Result<Unit>

    // Menghapus post berdasarkan ID
    suspend fun deletePost(postId: String): Result<Unit>
}