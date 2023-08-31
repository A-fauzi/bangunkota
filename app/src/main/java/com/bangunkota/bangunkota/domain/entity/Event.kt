package com.bangunkota.bangunkota.domain.entity

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Event(
    val id: String? = null,
    val title: String? = null,
    val address: String? = null,
    val image: String? = null,
    val date: String? = null,
    val time: String? = null,
    val peopleJoin: List<User>? = null,
    val createdBy: String? = null,


    @ServerTimestamp
    val create_at: Date? = null,
    @ServerTimestamp
    val update_at: Date? = null,
)
