package com.bangunkota.bangunkota.data.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangunkota.bangunkota.domain.entity.CommunityPost


//@Dao
//interface CommunityPostDao {
////    @Query("SELECT * FROM community_posts")
////    suspend fun getAllPost(): LiveData<List<CommunityPost>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertPost(communityPost: List<CommunityPost>)
//
//    @Delete
//    suspend fun deletePost(communityPost: CommunityPost)
//}