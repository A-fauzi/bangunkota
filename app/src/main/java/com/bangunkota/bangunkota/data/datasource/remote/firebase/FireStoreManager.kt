package com.bangunkota.bangunkota.data.datasource.remote.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

class FireStoreManager<T: Any>(private val collection: String) {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getInitialPage(collectionPath: String, pageSize: Int): QuerySnapshot {
        return db.collection(collectionPath)
            .limit(pageSize.toLong())
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .await()
    }

    suspend fun getNextPage(collectionPath: String, pageSize: Int, lastDocumentSnapshot: DocumentSnapshot): QuerySnapshot {
        return db.collection(collectionPath)
            .limit(pageSize.toLong())
            .orderBy("created_at", Query.Direction.DESCENDING)
            .startAfter(lastDocumentSnapshot)
            .get()
            .await()
    }

    fun create(data: T, documentId: String): Task<Void>{
        return db.collection(collection)
            .document(documentId)
            .set(data)
    }

    fun getData(documentId: String): Task<DocumentSnapshot> {
        return db.collection(collection)
            .document(documentId)
            .get()
    }

    fun update(data: T, documentId: String): Task<Void> {
        return db.collection(collection)
            .document(documentId)
            .set(data, SetOptions.merge())
    }

    fun delete(documentId: String): Task<Void> {
        return db.collection(collection)
            .document(documentId)
            .delete()
    }
}