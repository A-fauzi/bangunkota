package com.bangunkota.bangunkota.data.datasource.remote.firebase

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bangunkota.bangunkota.domain.entity.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class EventPagingSource(
    private val fireStore: FirebaseFirestore
) : PagingSource<QuerySnapshot, Event>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Event>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Event> {
        return try {
            val currentPage = params.key ?: fireStore.collection("events")
                .limit(10)
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = fireStore.collection("events").limit(10)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                data = currentPage.toObjects(Event::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        }catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
