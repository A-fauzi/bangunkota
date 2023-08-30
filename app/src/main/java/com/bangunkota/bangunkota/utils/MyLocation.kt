package com.bangunkota.bangunkota.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest

class MyLocation(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
    ) {
    fun getLastLocation(onSuccessGetLocation: (location: Location?) -> Unit, onFailureGetLocation: (exception: Exception) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin tidak diberikan, mungkin perlu meminta izin kepada pengguna
            // Anda dapat menggunakan ActivityCompat.requestPermissions() di sini
            return
        }
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                onSuccessGetLocation(location)
            }.addOnFailureListener {
                onFailureGetLocation(it)
            }
    }
}