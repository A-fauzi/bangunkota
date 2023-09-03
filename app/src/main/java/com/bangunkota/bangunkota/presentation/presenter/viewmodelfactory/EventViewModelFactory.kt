package com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.domain.entity.CommunityEvent
import com.bangunkota.bangunkota.domain.usecase.ExampleUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.EventViewModel

class EventViewModelFactory(private val eventUseCase: ExampleUseCase<CommunityEvent>): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(eventUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}