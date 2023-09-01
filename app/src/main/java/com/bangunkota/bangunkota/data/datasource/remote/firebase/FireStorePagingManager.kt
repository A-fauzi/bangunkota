package com.bangunkota.bangunkota.data.datasource.remote.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class FireStorePagingManager(private val firestore: FirebaseFirestore) {
    suspend fun getInitialPage(collectionPath: String, pageSize: Int): QuerySnapshot {
        return firestore.collection(collectionPath)
            .limit(pageSize.toLong())
            .orderBy("create_at", Query.Direction.DESCENDING)
            .get()
            .await()
    }

    suspend fun getNextPage(collectionPath: String, pageSize: Int, lastDocumentSnapshot: DocumentSnapshot): QuerySnapshot {
        return firestore.collection(collectionPath)
            .limit(pageSize.toLong())
            .orderBy("create_at", Query.Direction.DESCENDING)
            .startAfter(lastDocumentSnapshot)
            .get()
            .await()
    }
}