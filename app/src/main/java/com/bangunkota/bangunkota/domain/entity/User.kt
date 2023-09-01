package com.bangunkota.bangunkota.domain.entity

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,

    @ServerTimestamp
    val dateJoin: Date? = null,
    val myEvents: List<CommunityEvent>? = null,
    val createEvents: List<CommunityEvent>? = null,
)
