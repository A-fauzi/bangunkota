package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.bangunkota.bangunkota.data.datasource.PagingSource
import com.bangunkota.bangunkota.domain.entity.CommunityEvent
import com.bangunkota.bangunkota.domain.usecase.ExampleUseCase
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class EventViewModel(private val eventUseCase: ExampleUseCase<CommunityEvent>) : ViewModel() {
    private val fireStore = FirebaseFirestore.getInstance()
    private val fireStoreManager = FireStoreManager(fireStore)
    private val pageSize = 10

    
    val getEvents = Pager(PagingConfig(20)) {
        PagingSource(fireStoreManager, "events", pageSize, CommunityEvent::class.java)
    }.flow.cachedIn(viewModelScope)



    fun insertEvent(data: CommunityEvent, documentId: String): Task<Void> {
        return eventUseCase.createData(data, documentId)
    }

    fun updateEvent(event: CommunityEvent, documentId: String): Task<Void> {
        return eventUseCase.updateData(event, documentId)
    }

    fun deleteData(eventId: String): Task<Void> {
        return eventUseCase.deleteData(eventId)
    }
}