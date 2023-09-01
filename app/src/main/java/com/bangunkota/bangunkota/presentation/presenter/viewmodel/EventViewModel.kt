package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStorePagingManager
import com.bangunkota.bangunkota.data.datasource.PagingSource
import com.bangunkota.bangunkota.domain.entity.CommunityEvent
import com.bangunkota.bangunkota.domain.usecase.EventUseCase
import com.google.firebase.firestore.FirebaseFirestore

class EventViewModel(private val eventUseCase: EventUseCase): ViewModel() {
    private val fireStore = FirebaseFirestore.getInstance()
    private val fireStoreManager = FireStorePagingManager(fireStore)
    private val pageSize = 10

    val getEvents = Pager(PagingConfig(pageSize)) {
        PagingSource(fireStoreManager, "events", pageSize, CommunityEvent::class.java)
    }.flow.cachedIn(viewModelScope)



    suspend fun insertEvent(data: CommunityEvent): Result<Unit> {
        return eventUseCase.insertData(data)
    }

    suspend fun updateEvent(event: CommunityEvent) {
        eventUseCase.updateData(event)
    }

    suspend fun deleteData(eventId: String) {
        eventUseCase.deleteData(eventId)
    }
}