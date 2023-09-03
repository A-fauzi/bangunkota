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
import com.bangunkota.bangunkota.domain.usecase.ExampleUseCase
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class CommunityViewModel(private val communityUseCase: ExampleUseCase<CommunityPost>): ViewModel() {
    private val fireStore = FirebaseFirestore.getInstance()
    private val fireStoreManager = FireStoreManager(fireStore)
    private val pageSize = 10

    val getPosts = Pager(PagingConfig(20)) {
        PagingSource(fireStoreManager, "community_posts", pageSize, CommunityPost::class.java)
    }.flow.cachedIn(viewModelScope)


    fun insertPost(data: CommunityPost, documentId: String): Task<Void> {
        return communityUseCase.createData(data, documentId)
    }

//    suspend fun insertLikePost(data: UserLikePost): Task<Void> {
//        return communityUseCase.insertLikePost(data)
//    }

    fun updatePost(data: CommunityPost, documentId: String): Task<Void> {
        return communityUseCase.updateData(data, documentId)
    }

    fun deletePost(postId: String): Task<Void> {
        return communityUseCase.deleteData(postId)
    }
}