package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.data.datasource.PagingSource
import com.bangunkota.bangunkota.domain.entity.community_post.CommunityPost
import com.bangunkota.bangunkota.domain.entity.community_post.UserLikePost
import com.bangunkota.bangunkota.domain.usecase.CommunityUseCase
import com.google.firebase.firestore.FirebaseFirestore

class CommunityViewModel(private val communityUseCase: CommunityUseCase): ViewModel() {
    private val fireStore = FirebaseFirestore.getInstance()
    private val fireStoreManager = FireStoreManager(fireStore)
    private val pageSize = 10

    val getPosts = Pager(PagingConfig(pageSize)) {
        PagingSource(fireStoreManager, "community_posts", pageSize, CommunityPost::class.java)
    }.flow.cachedIn(viewModelScope)


//    fun fetchDataAndSaveToRoom() {
//        viewModelScope.launch {
//            try {
//                communityUseCase.fetchDataAndSaveToRoom()
//            }catch (e: Exception) {
//                // Handle error
//            }
//        }
//    }


    suspend fun insertPost(data: CommunityPost): Result<Unit> {
        return communityUseCase.insertData(data)
    }

    suspend fun insertLikePost(data: UserLikePost): Result<Unit> {
        return communityUseCase.insertLikePost(data)
    }

    suspend fun updatePost(data: CommunityPost) {
        communityUseCase.updateData(data)
    }

    suspend fun deletePost(postId: String) {
        communityUseCase.deleteData(postId)
    }
}