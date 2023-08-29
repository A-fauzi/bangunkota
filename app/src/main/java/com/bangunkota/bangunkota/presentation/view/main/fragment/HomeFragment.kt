package com.bangunkota.bangunkota.presentation.view.main.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.databinding.FragmentHomeBinding
import com.bangunkota.bangunkota.presentation.adapter.EventPagingAdapter
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.EventViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.EventViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okio.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var eventViewModel: EventViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        val userPreferencesManager = UserPreferencesManager(requireActivity())
        val userViewModelFactory = UserViewModelFactory(userPreferencesManager)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        val viewModelFactory = EventViewModelFactory()
        eventViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[EventViewModel::class.java]

        val eventAdapter = EventPagingAdapter(requireActivity())
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = eventAdapter
        }

        lifecycleScope.launch {
            eventViewModel.flow.collect() { pagingData ->
                eventAdapter.submitData(pagingData)
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        getLastLocation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currentDate.text = currentDate()

        userViewModel.userName.observe(viewLifecycleOwner){
            binding.topAppBar.title = it
        }

        userViewModel.userPhoto.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .asBitmap()
                .load(it)
                .apply(RequestOptions.circleCropTransform())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        // Konversi Bitmap menjadi Drawable
                        val iconDrawable = BitmapDrawable(resources, resource)
                        binding.topAppBar.menu.findItem(R.id.account).icon = iconDrawable
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle jika gambar gagal dimuat atau dihapus
                        Toast.makeText(requireActivity(), "Glide Gagal Upload Image Profile", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(requireActivity(), "Menu Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.account -> {
                    Toast.makeText(requireActivity(), "User Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }


    private fun currentDate(): String {
        val currentTime = Calendar.getInstance().time
        val desiredFormat = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH)
        return desiredFormat.format(currentTime)
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            // Jika izin tidak diberikan, mungkin perlu meminta izin kepada pengguna
            // Anda dapat menggunakan ActivityCompat.requestPermissions() di sini
            return
        }
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val long = location.longitude
                    try {
                        val addresses = geocoder.getFromLocation(lat, long, 1)
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val cityName = address.locality
                            val countryName = address.countryName
                            val currentAddress = "$cityName, $countryName"
                            binding.currentAddress.text = currentAddress
                        } else {
                            binding.currentAddress.text = "Address IsEmpty!"
                        }
                    }catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    binding.currentAddress.text = "Address null!"
                }
            }.addOnFailureListener {
                binding.currentAddress.text = "Failure Get Address ${it.localizedMessage}"
            }
    }
}