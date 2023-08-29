package com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.domain.usecase.EventUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.EventViewModel

class EventViewModelFactory(private val eventUseCase: EventUseCase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)){
            return EventViewModel(eventUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}