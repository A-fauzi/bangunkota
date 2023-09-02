package com.bangunkota.bangunkota.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.bangunkota.bangunkota.domain.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
    private val userIdFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_ID] ?: ""
    }
    private val userNameFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }
    private val userEmailFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_EMAIL] ?: ""
    }
    private val userPhotoFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_PHOTO]?: " "
    }

    val userDataFlow: Flow<User> = combine(
        userIdFlow,
        userNameFlow,
        userEmailFlow,
        userPhotoFlow
    ){ userId, userName, userEmail, userPhoto ->
        User(
            id = userId,
            name = userName,
            email = userEmail,
            photoUrl = userPhoto
        )
    }
}