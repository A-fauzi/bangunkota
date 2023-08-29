package com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.EventViewModel

class EventViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)){
            return EventViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}