package com.bangunkota.bangunkota.domain.entity

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Event(
    val id: String? = null,
    val title: String? = null,
    val address: String? = null,

    @ServerTimestamp
    val date: Date? = null,
    val image: String? = null
)
