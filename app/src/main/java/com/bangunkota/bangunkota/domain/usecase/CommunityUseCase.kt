package com.bangunkota.bangunkota.domain.usecase

import com.bangunkota.bangunkota.data.repository.abstractions.CommunityRepository
import com.bangunkota.bangunkota.domain.entity.community_post.CommunityPost
import com.bangunkota.bangunkota.domain.entity.community_post.UserLikePost

class CommunityUseCase(private val repository: CommunityRepository) {

//    suspend fun fetchDataAndSaveToRoom() {
//        return repository.fetchDataFromFireStoreAndSaveToRoom()
//    }


    suspend fun insertData(data: CommunityPost): Result<Unit> {
        return repository.addPost(data)
    }

    suspend fun insertLikePost(data: UserLikePost): Result<Unit> {
        return repository.addLikePost(data)
    }

    suspend fun updateData(data: CommunityPost) {
        val firestoreData = CommunityPost(
            id = data.id,
            text = data.text,
            uid = data.uid
        )
        repository.updatePost(firestoreData)
    }

    suspend fun deleteData(dataId: String) {
        repository.deletePost(dataId)
    }
}