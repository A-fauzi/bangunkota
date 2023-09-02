package com.bangunkota.bangunkota.domain.entity.community_post

import com.google.firebase.Timestamp

data class UserLikePost(
    val id: String? = null,
    val postId: String? = null,
    val userId: String? = null,
    val createdAt: Timestamp? = null,
    val updateAt: Timestamp? = null,
)
