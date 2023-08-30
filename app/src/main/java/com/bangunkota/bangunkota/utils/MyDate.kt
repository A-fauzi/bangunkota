package com.bangunkota.bangunkota.utils

import java.text.SimpleDateFormat
import java.util.*

object MyDate {
    fun currentDate(): String {
        val currentTime = Calendar.getInstance().time
        val desiredFormat = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH)
        return desiredFormat.format(currentTime)
    }
}