package com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.MyLocationViewModel
import com.bangunkota.bangunkota.utils.MyLocation

class MyLocationViewModelFactory(private val myLocation: MyLocation) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyLocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyLocationViewModel(myLocation) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
