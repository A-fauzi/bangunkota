package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangunkota.bangunkota.utils.MyLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyLocationViewModel(private val myLocation: MyLocation) : ViewModel() {
    // Fungsi untuk mendapatkan lokasi terakhir
    private val _locationLiveData = MutableLiveData<Location?>()
    val locationLiveData: LiveData<Location?> = _locationLiveData

    fun getLastLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val location = myLocation.getLastLocation()
                _locationLiveData.postValue(location)
            } catch (e: Exception) {
                // Tangani kesalahan
                Log.d("MyLocationViewModel", "Error: ${e.localizedMessage}")
            }
        }
    }
}