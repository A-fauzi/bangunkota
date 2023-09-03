package com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.domain.entity.community_post.CommunityPost
import com.bangunkota.bangunkota.domain.usecase.CommunityUseCase
import com.bangunkota.bangunkota.domain.usecase.ExampleUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.CommunityViewModel

class CommunityViewModelFactory(private val useCase: ExampleUseCase<CommunityPost>): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CommunityViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}