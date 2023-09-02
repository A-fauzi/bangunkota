package com.bangunkota.bangunkota.presentation.view.main

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.data.repository.abstractions.UserRepository
import com.bangunkota.bangunkota.data.repository.implementatios.UserRepositoryImpl
import com.bangunkota.bangunkota.databinding.ActivityMainBinding
import com.bangunkota.bangunkota.domain.entity.User
import com.bangunkota.bangunkota.domain.usecase.UserUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.utils.MessageHandler
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    /**
     * FIREBASE AUTH
     */
    private lateinit var auth: FirebaseAuth

    /**
     * FIREBASE USER
     */
    private var user: FirebaseUser? = null
    /**
     * MESSAGE TOAST
     */
    private lateinit var message: MessageHandler

    private lateinit var fireStoreManager: FireStoreManager
    private lateinit var userRepository: UserRepository
    private lateinit var userUseCase: UserUseCase
    private lateinit var userViewModelFactory: UserViewModelFactory
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPreferencesManager: UserPreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObject()
        setUpNavigation()
        permissionsRequest()
    }

    override fun onStart() {
        super.onStart()
        checkingUserDocument()
    }

    private fun initObject() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        message = MessageHandler(this)

        // USER CONFIG
        userPreferencesManager = UserPreferencesManager(this)
        fireStoreManager = FireStoreManager(FirebaseFirestore.getInstance())
        userRepository = UserRepositoryImpl(fireStoreManager)
        userUseCase = UserUseCase(userRepository)
        userViewModelFactory = UserViewModelFactory(userPreferencesManager, userUseCase)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]
    }

    /**
     * Ceck user and condition if exists or not exists
     */
    private fun checkingUserDocument() {
        val data = User(
            user?.uid,
            user?.displayName,
            user?.email,
            user?.photoUrl.toString(),
            Timestamp.now().toDate(),
            null,
            null
        )

        lifecycleScope.launch {
            userViewModel.createUserDocument(user?.uid.toString(), data,
                onSuccess = {
                    message.toastMsg("Data Pengguna berhasil disimpan")
                }, onFailure = {
                    message.toastMsg("Data Pengguna gagal disimpan")
                }
            )
        }
    }

    private fun setUpNavigation() {
        // Temukan NavHostFragment dan dapatkan NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Mengatur navigasi bawah dengan NavController
        val bottomNavigation = binding.bottomNav
        setupWithNavController(bottomNavigation, navController)
    }

    private fun permissionsRequest() {
        val locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                when {
                    permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                        Toast.makeText(this, "Precise location access granted", Toast.LENGTH_SHORT).show()
                    }
                    permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                        Toast.makeText(this, "Only approximate location access granted", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // No location access granted.
                        Toast.makeText(this, "Izinkan Lokasi Agar dapat masuk ke aplikasi", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

}