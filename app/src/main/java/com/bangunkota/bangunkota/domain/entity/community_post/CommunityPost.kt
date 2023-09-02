package com.bangunkota.bangunkota.domain.entity.community_post

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.*


@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey
    val id: String = "",
    val text: String? = null,
    val uid: String? = null,
    val create_at: Timestamp? = null
)
