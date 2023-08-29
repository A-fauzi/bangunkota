package com.bangunkota.bangunkota.presentation.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangunkota.bangunkota.data.repository.AuthRepositoryImpl
import com.bangunkota.bangunkota.databinding.ActivitySignInBinding
import com.bangunkota.bangunkota.domain.usecase.SignInUseCase
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

class SignInActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN: Int = 1
    }

    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel: SignInViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createRequest()

        val firebaseAuth = FirebaseAuth.getInstance()
        val authRepository = AuthRepositoryImpl(firebaseAuth)
        val signInUseCase = SignInUseCase(authRepository)
        val signInViewModelFactory = SignInViewModelFactory(signInUseCase)

        viewModel = ViewModelProvider(this, signInViewModelFactory)[SignInViewModel::class.java]

        val userPreferencesManager = UserPreferencesManager(this)
        val userViewModelFactory = UserViewModelFactory(userPreferencesManager)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        binding.signInButton.setOnClickListener {
            signIn()
        }

        viewModel.signInResult.observe(this, Observer { result ->
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

            }
        })
    }

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
                userViewModel.saveUserData(account.displayName.toString(), account.email.toString(), account.photoUrl.toString())
                viewModel.signIn(account)

            }catch (e: ApiException) {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}