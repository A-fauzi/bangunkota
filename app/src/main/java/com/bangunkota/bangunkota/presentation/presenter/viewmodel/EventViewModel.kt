package com.bangunkota.bangunkota.presentation.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangunkota.bangunkota.data.datasource.remote.firebase.EventPagingSource
import com.bangunkota.bangunkota.domain.entity.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class EventViewModel: ViewModel() {
    val flow = Pager(PagingConfig(20)) {
        EventPagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)
}