package com.bangunkota.bangunkota.domain.entity.community_post

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp


@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey
    val id: String = "",
    val text: String? = null,
    val uid: String? = null,
    val appreciate: List<String>? = null,
    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null
)
