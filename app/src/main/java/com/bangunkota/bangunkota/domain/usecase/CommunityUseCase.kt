package com.bangunkota.bangunkota.domain.usecase

import androidx.lifecycle.LiveData
import com.bangunkota.bangunkota.data.repository.abstractions.CommunityRepository
import com.bangunkota.bangunkota.domain.entity.CommunityPost

class CommunityUseCase(private val repository: CommunityRepository) {

//    suspend fun fetchDataAndSaveToRoom() {
//        return repository.fetchDataFromFireStoreAndSaveToRoom()
//    }


    suspend fun insertData(data: CommunityPost): Result<Unit> {
        return repository.addPost(data)
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