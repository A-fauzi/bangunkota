package com.bangunkota.bangunkota.utils

import android.content.Context
import android.widget.Toast

class MessageHandler(private val context: Context) {
    fun toastMsg(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}