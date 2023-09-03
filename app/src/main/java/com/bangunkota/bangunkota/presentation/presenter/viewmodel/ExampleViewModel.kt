package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.ViewModel
import com.bangunkota.bangunkota.domain.entity.User
import com.bangunkota.bangunkota.domain.usecase.ExampleUseCase
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class ExampleViewModel(private val useCase: ExampleUseCase<User>): ViewModel() {
    fun createDataToFireStore(data: User, documentId: String){
        useCase.createData(data, documentId)
    }
    fun getDataFromFireStore(documentId: String): Task<DocumentSnapshot>{
        return useCase.getData(documentId)
    }
    fun updateDataInFireStore(data: User, documentId: String){
        useCase.updateData(data, documentId)
    }
    fun deleteDataFromFireStore(documentId: String){
        useCase.deleteData(documentId)
    }
}