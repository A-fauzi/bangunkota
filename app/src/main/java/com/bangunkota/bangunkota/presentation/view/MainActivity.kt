package com.bangunkota.bangunkota.presentation.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.presentation.presenter.viewmodel.UserViewModel
import com.bangunkota.bangunkota.presentation.presenter.viewmodelfactory.UserViewModelFactory
import com.bangunkota.bangunkota.utils.UserPreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userPreferencesManager = UserPreferencesManager(this)
        val userViewModelFactory = UserViewModelFactory(userPreferencesManager)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        userViewModel.userName.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

    }
}