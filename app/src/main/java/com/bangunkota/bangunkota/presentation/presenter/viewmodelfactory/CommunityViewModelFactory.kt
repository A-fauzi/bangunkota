package com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.domain.usecase.CommunityUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.CommunityViewModel

class CommunityViewModelFactory(private val useCase: CommunityUseCase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CommunityViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}