package com.bangunkota.bangunkota.data.repository.abstractions

import com.bangunkota.bangunkota.domain.entity.community_post.CommunityPost
import com.bangunkota.bangunkota.domain.entity.community_post.UserLikePost

interface CommunityRepository {
    // get data firestore
//    suspend fun fetchDataFromFireStoreAndSaveToRoom()

    // Menampilkan detail post berdasarkan ID
    suspend fun getPostById(postId: String): CommunityPost?

    // Menambahkan post baru
    suspend fun addPost(post: CommunityPost): Result<Unit>

    // Menambahkan post baru
    suspend fun addLikePost(post: UserLikePost): Result<Unit>

    // Memperbarui informasi post
    suspend fun updatePost(post: CommunityPost): Result<Unit>

    // Menghapus post berdasarkan ID
    suspend fun deletePost(postId: String): Result<Unit>
}