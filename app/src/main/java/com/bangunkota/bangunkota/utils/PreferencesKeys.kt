package com.bangunkota.bangunkota.utils

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PHOTO = stringPreferencesKey("user_photo")
}