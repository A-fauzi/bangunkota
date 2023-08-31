package com.bangunkota.bangunkota.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

// Note: This is at the top level of the file, outside of any classes.
private val Context.dataStore by preferencesDataStore("user_preferences")

class UserPreferencesManager(context: Context) {
    private val dataStore = context.dataStore

    suspend fun saveUserData(userId: String, username: String, email: String, photoUrl: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.USER_NAME] = username
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_PHOTO] = photoUrl
        }
    }
    val userIdFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_ID] ?: ""
    }
    val userNameFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }
    val userEmailFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_EMAIL] ?: ""
    }
    val userPhotoFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_PHOTO]?: " "
    }
}