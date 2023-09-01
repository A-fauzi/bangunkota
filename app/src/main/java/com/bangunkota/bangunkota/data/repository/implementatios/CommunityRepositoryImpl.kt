package com.bangunkota.bangunkota.data.repository.implementatios

import android.util.Log
import com.bangunkota.bangunkota.data.repository.abstractions.CommunityRepository
import com.bangunkota.bangunkota.domain.entity.CommunityPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CommunityRepositoryImpl(
    private val firestore: FirebaseFirestore,
//    private val communityPostDao: CommunityPostDao
    ): CommunityRepository {
//    override suspend fun fetchDataFromFireStoreAndSaveToRoom() {
//        try {
//            val querySnapshot = firestore.collection("community_posts").get().await()
//
//            val postList = mutableListOf<CommunityPost>()
//            for (document in querySnapshot) {
//                val userData = document.toObject(CommunityPost::class.java)
//                postList.add(userData)
//            }
//
//            Log.d("CommunityRepositoryImpl", postList.toString())
//            communityPostDao.insertPost(postList)
//        }catch (e: Exception) {
//            throw e
//        }
//    }


    override suspend fun getPostById(postId: String): CommunityPost? {
        return try {
            val documentSnapshot = firestore.collection("community_posts").document(postId).get().await()
            documentSnapshot.toObject(CommunityPost::class.java)
        } catch (e: Exception) {
            // Handle error
            null // Atau Anda bisa melempar Exception sesuai kebutuhan
        }
    }

    override suspend fun addPost(post: CommunityPost): Result<Unit> {
        return try {
            val document = firestore.collection("community_posts").document(post.id.toString())
            document
                .set(post)
                .await() // Tunggu hingga operasi selesai

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePost(post: CommunityPost): Result<Unit> {
        return try {
            val document = firestore.collection("community_posts").document(post.id.toString())
            document
                .set(post)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            val document = firestore.collection("community_posts").document(postId)
            document.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}