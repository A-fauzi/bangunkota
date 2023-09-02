package com.bangunkota.bangunkota.presentation.view.main.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.data.repository.abstractions.UserRepository
import com.bangunkota.bangunkota.data.repository.implementatios.EventRepositoryImpl
import com.bangunkota.bangunkota.data.repository.implementatios.UserRepositoryImpl
import com.bangunkota.bangunkota.databinding.FragmentHomeBinding
import com.bangunkota.bangunkota.databinding.ItemEventBinding
import com.bangunkota.bangunkota.domain.entity.CommunityEvent
import com.bangunkota.bangunkota.domain.entity.User
import com.bangunkota.bangunkota.domain.usecase.EventUseCase
import com.bangunkota.bangunkota.domain.usecase.UserUseCase
import com.bangunkota.bangunkota.presentation.adapter.AdapterPagingList
import com.bangunkota.bangunkota.presentation.adapter.LoadStateAdapter
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    private lateinit var message: MessageHandler

    private lateinit var eventAdapter: AdapterPagingList<CommunityEvent, ItemEventBinding>

    /**
     * USER PREFERENCES
     */
    private lateinit var userPreferencesManager: UserPreferencesManager

    /**
     * USER VIEWMODEL FACTORY
     */
    private lateinit var userViewModelFactory: UserViewModelFactory


    /**
     * USER USECASE
     */
    private lateinit var userUseCase: UserUseCase

    /**
     * USER REPOSITORY
     */
    private lateinit var userRepository: UserRepository

    /**
     * FIRESTORE MANAGER
     */
    private lateinit var fireStoreManager: FireStoreManager


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
        message = MessageHandler(requireActivity())
        eventAdapter = AdapterPagingList(requireActivity(), { binding, event ->
            binding.itemTitle.text = event.title
                binding.itemAddress.text = event.address
                binding.itemDate.text = event.date
                binding.itemTime.text = "${event.time} WIB"
                Glide.with(this@HomeFragment)
                    .load(event.image)
                    .error(R.drawable.img_placeholder)
                    .into(binding.itemImage)
        }, ItemEventBinding::inflate )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        myLocation = MyLocation(requireActivity(), fusedLocationClient)

        val fireStore = FirebaseFirestore.getInstance()
        fireStoreManager = FireStoreManager(fireStore)

        // USER CONFIG
        userPreferencesManager = UserPreferencesManager(requireActivity())
        userRepository = UserRepositoryImpl(fireStoreManager)
        userUseCase = UserUseCase(userRepository)
        userViewModelFactory = UserViewModelFactory(userPreferencesManager, userUseCase)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]


        val eventRepository = EventRepositoryImpl()
        val eventUseCase = EventUseCase(eventRepository)
        val viewModelFactory = EventViewModelFactory(eventUseCase)
        eventViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[EventViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            eventViewModel.getEvents.collect { pagingData ->
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
            adapter = eventAdapter.withLoadStateHeaderAndFooter(
                header = LoadStateAdapter {eventAdapter.retry()},
                footer = LoadStateAdapter {eventAdapter.retry()}
            )
        }

        eventAdapter.addLoadStateListener { combinedLoadStates ->
            val isLoading = combinedLoadStates.refresh is LoadState.Loading

            if (isLoading) {
                binding.rvEvent.visibility = View.GONE
                binding.progressbar.visibility = View.VISIBLE
            } else {
                binding.rvEvent.visibility = View.VISIBLE
                binding.progressbar.visibility = View.GONE
            }
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
        userViewModel.userData.observe(viewLifecycleOwner) {
            checkingUserDocument(it.id.toString(), it.name.toString(), it.email.toString(), it.photoUrl.toString())
            binding.appBarLayout.topAppBar.title = "Hi, ${it.name}"
            Glide.with(requireContext())
                .asBitmap()
                .load(it.photoUrl)
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

    /**
     * Ceck user and condition if exists or not exists
     */
    private fun checkingUserDocument(uid: String, name: String, email: String, photo: String) {
        val data = User(
            uid,
            name,
            email,
            photo,
            Timestamp.now().toDate(),
            null,
            null
        )

        lifecycleScope.launch {
            userViewModel.createUserDocument(uid, data,
                onSuccess = {
                    message.toastMsg("Data Pengguna berhasil disimpan")
                }, onFailure = {
                    message.toastMsg("Data Pengguna gagal disimpan")
                }
            )
        }

    }

    private fun signOutUser() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        startActivity(Intent(requireActivity(), SignInActivity::class.java))
        activity?.finish()
    }


}