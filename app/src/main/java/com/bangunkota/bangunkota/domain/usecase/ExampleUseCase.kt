package com.bangunkota.bangunkota.domain.usecase

import com.bangunkota.bangunkota.data.repository.abstractions.ExampleRepositoryFireStore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.auth.User

class ExampleUseCase<T>(private val repositoryFireStore: ExampleRepositoryFireStore<T>) {
    fun createData(data: T, documentId: String){
        repositoryFireStore.createData(data, documentId)
    }
    fun getData(id: String): Task<DocumentSnapshot>{
        return repositoryFireStore.getData(id)
    }
    fun updateData(data: T, documentId: String){
        repositoryFireStore.updateData(data, documentId)
    }
    fun deleteData(id: String){
        repositoryFireStore.deleteData(id)
    }
}