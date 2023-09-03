package com.bangunkota.bangunkota.data.repository.abstractions

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

interface ExampleRepositoryFireStore<T> {
    fun createData(data: T, documentId: String): Task<Void>
    fun getData(id: String): Task<DocumentSnapshot>
    fun updateData(data: T, documentId: String): Task<Void>
    fun deleteData(id: String): Task<Void>
}