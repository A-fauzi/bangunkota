package com.bangunkota.bangunkota.data.repository.implementatios

import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManagerV2
import com.bangunkota.bangunkota.data.repository.abstractions.ExampleRepositoryFireStore
import com.bangunkota.bangunkota.domain.entity.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class ExampleRepositoryFireStoreImpl<T: Any>(private val fireStoreManagerV2: FireStoreManagerV2<T>): ExampleRepositoryFireStore<T> {
    override fun createData(data: T, documentId: String) {
        fireStoreManagerV2.create(data, documentId)
    }

    override fun getData(id: String): Task<DocumentSnapshot> {
        return fireStoreManagerV2.getData(id)
    }

    override fun updateData(data: T, documentId: String) {
        fireStoreManagerV2.update(data, documentId)
    }

    override fun deleteData(id: String) {
        fireStoreManagerV2.delete(id)
    }
}