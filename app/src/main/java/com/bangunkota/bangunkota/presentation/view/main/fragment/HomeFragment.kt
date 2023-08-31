package com.bangunkota.bangunkota.presentation.view.main.fragment

import android.content.Intent
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.data.repository.implementatios.EventRepositoryImpl
import com.bangunkota.bangunkota.databinding.FragmentHomeBinding
import com.bangunkota.bangunkota.domain.entity.Event
import com.bangunkota.bangunkota.domain.usecase.EventUseCase
import com.bangunkota.bangunkota.presentation.adapter.EventPagingAdapter
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.EventViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.MyLocationViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.EventViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.MyLocationViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.presentation.view.CreateEventActivity
import com.bangunkota.bangunkota.presentation.view.SignInActivity
import com.bangunkota.bangunkota.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import okio.IOException
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var eventViewModel: EventViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var myLocation: MyLocation

    private lateinit var eventAdapter: EventPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        initObj()
        setUpRecyclerView()
        getLastLocation()

        return binding.root
    }

    private fun initObj() {
        eventAdapter = EventPagingAdapter(requireActivity())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        myLocation = MyLocation(requireActivity(), fusedLocationClient)

        val userPreferencesManager = UserPreferencesManager(requireActivity())
        val userViewModelFactory = UserViewModelFactory(userPreferencesManager)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        val eventRepository = EventRepositoryImpl()
        val eventUseCase = EventUseCase(eventRepository)
        val viewModelFactory = EventViewModelFactory(eventUseCase)
        eventViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[EventViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            eventViewModel.flow.collect { pagingData ->
                eventAdapter.submitData(pagingData)
            }
        }

        binding.currentDate.text = MyDate.currentDate()

        binding.fabCreateEvent.setOnClickListener {
            startActivity(Intent(requireActivity(), CreateEventActivity::class.java))
        }

        topAppBarBehaviour()
    }

    private fun setUpRecyclerView() {
        binding.rvEvent.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = eventAdapter
        }
    }

    private fun getLastLocation() {

        val myLocation = MyLocation(requireActivity(), fusedLocationClient)
        val viewModel = ViewModelProvider(
            this,
            MyLocationViewModelFactory(myLocation)
        )[MyLocationViewModel::class.java]

        // Observasi LiveData untuk mendapatkan perubahan lokasi
        viewModel.locationLiveData.observe(viewLifecycleOwner) { location ->
            if (location != null) {
                // Lokasi ditemukan, lakukan sesuatu
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
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                // Izin tidak diberikan atau lokasi tidak ditemukan
                binding.currentAddress.text = "Address Is Null!"
            }
        }

        // Panggil fungsi dalam ViewModel untuk mendapatkan lokasi
        viewModel.getLastLocation()

    }

    private fun topAppBarBehaviour() {
        userViewModel.userName.observe(viewLifecycleOwner) {
            binding.appBarLayout.topAppBar.title = "Hi, $it"
        }

        userViewModel.userPhoto.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .asBitmap()
                .load(it)
                .apply(RequestOptions.circleCropTransform())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // Konversi Bitmap menjadi Drawable
                        val iconDrawable = BitmapDrawable(resources, resource)
                        binding.appBarLayout.topAppBar.menu.findItem(R.id.account).icon =
                            iconDrawable
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle jika gambar gagal dimuat atau dihapus
                        Toast.makeText(
                            requireActivity(),
                            "Glide Gagal Upload Image Profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            Toast.makeText(requireActivity(), "Menu Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.appBarLayout.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.account -> {
                    signOutUser()
                    true
                }
                else -> false
            }
        }
    }

    private fun signOutUser() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        startActivity(Intent(requireActivity(), SignInActivity::class.java))
        activity?.finish()
    }


}