package com.bangunkota.bangunkota.data.datasource.remote.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreManagerV2<T: Any>(private val collection: String) {
    private val db = FirebaseFirestore.getInstance()

    fun create(data: T, documentId: String) {
        db.collection(collection)
            .document(documentId)
            .set(data)
    }

    fun getData(documentId: String): Task<DocumentSnapshot> {
        return db.collection(collection)
            .document(documentId)
            .get()
    }

    fun update(data: T, documentId: String) {
        db.collection(collection)
            .document(documentId)
            .set(data, SetOptions.merge())
    }

    fun delete(documentId: String) {
        db.collection(collection)
            .document(documentId)
            .delete()
    }
}