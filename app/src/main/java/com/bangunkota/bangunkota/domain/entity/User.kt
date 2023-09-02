package com.bangunkota.bangunkota.domain.entity

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val birthday: String? = null,

    @ServerTimestamp
    val dateJoin: Date? = null,
    val myEvents: List<String>? = null,
    val createEvents: List<String>? = null,

    @ServerTimestamp
    val created_at: Date? = null,
    @ServerTimestamp
    val updated_at: Date? = null,
)
