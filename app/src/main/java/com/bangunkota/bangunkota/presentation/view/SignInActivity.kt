package com.bangunkota.bangunkota.presentation.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.data.repository.abstractions.UserRepository
import com.bangunkota.bangunkota.data.repository.implementatios.AuthRepositoryImpl
import com.bangunkota.bangunkota.data.repository.implementatios.UserRepositoryImpl
import com.bangunkota.bangunkota.databinding.ActivitySignInBinding
import com.bangunkota.bangunkota.domain.usecase.SignInUseCase
import com.bangunkota.bangunkota.domain.usecase.UserUseCase
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.SignInViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.SignInViewModelFactory
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.presentation.view.main.MainActivity
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN: Int = 1
    }

    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel: SignInViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions

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


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createRequest()

        val firebaseAuth = FirebaseAuth.getInstance()
        val authRepository = AuthRepositoryImpl(firebaseAuth)
        val signInUseCase = SignInUseCase(authRepository)
        val signInViewModelFactory = SignInViewModelFactory(signInUseCase)
        val fireStore = FirebaseFirestore.getInstance()
        fireStoreManager = FireStoreManager(fireStore)

        viewModel = ViewModelProvider(this, signInViewModelFactory)[SignInViewModel::class.java]

        // USER CONFIG
        userPreferencesManager = UserPreferencesManager(this)
        userRepository = UserRepositoryImpl(fireStoreManager)
        userUseCase = UserUseCase(userRepository)
        userViewModelFactory = UserViewModelFactory(userPreferencesManager, userUseCase)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]


        binding.signInButton.setOnClickListener {
            signIn()
        }

        viewModel.signInResult.observe(this) { result ->

            if (result.isSuccess) {
                // Sign-in successful, navigate to MainActivity
                Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Sign-in failed, show error message
                val exception = result.exceptionOrNull()
                val errorMessage = exception?.localizedMessage ?: "Sign-in failed"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

                binding.progressbar.visibility = View.GONE
                binding.signInButton.visibility = View.VISIBLE

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        viewModel.isUserSignedIn.observe(this) { isLogged ->
            if (isLogged) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Sign in dulu!", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.checkCurrentUser()
    }

    private fun createRequest() {
        // config google sign in
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("183422851073-mlps2itoq10up3h96qtaicpi2dp8faef.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        binding.progressbar.visibility = View.VISIBLE
        binding.signInButton.visibility = View.GONE

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                userViewModel.saveUserData(account.id.toString(), account.displayName.toString(), account.email.toString(), account.photoUrl.toString())
                viewModel.signIn(account)

            }catch (e: ApiException) {
                binding.progressbar.visibility = View.GONE
                binding.signInButton.visibility = View.VISIBLE

                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}