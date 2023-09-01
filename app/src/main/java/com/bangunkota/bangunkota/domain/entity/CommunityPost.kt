package com.bangunkota.bangunkota.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey
    val id: String = "",
    val text: String? = null,
    val uid: String? = null,
)
