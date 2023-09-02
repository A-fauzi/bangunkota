package com.bangunkota.bangunkota.data.datasource.remote.firebase

import com.bangunkota.bangunkota.domain.entity.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FireStoreManager(private val firestore: FirebaseFirestore) {
    suspend fun getInitialPage(collectionPath: String, pageSize: Int): QuerySnapshot {
        return firestore.collection(collectionPath)
            .limit(pageSize.toLong())
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .await()
    }

    suspend fun getNextPage(collectionPath: String, pageSize: Int, lastDocumentSnapshot: DocumentSnapshot): QuerySnapshot {
        return firestore.collection(collectionPath)
            .limit(pageSize.toLong())
            .orderBy("created_at", Query.Direction.DESCENDING)
            .startAfter(lastDocumentSnapshot)
            .get()
            .await()
    }

    suspend fun getDocumentById(collectionPath: String, id: String): DocumentSnapshot {
        return firestore.collection(collectionPath)
            .document(id)
            .get()
            .await()
    }

    fun createUserDocument(uid: String, user: User, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userRef = firestore.collection("users").document(uid)

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (!document.exists()) {
                    // Dokumen pengguna belum ada, maka Anda bisa memasukkan datanya ke Firestore
                    userRef.set(user)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure()
                        }
                } else {
                    // Jika pengguna sudah ada di database
                }
            } else {
                onFailure()
            }
        }
    }
}