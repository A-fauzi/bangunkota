package com.bangunkota.bangunkota.data.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bangunkota.bangunkota.data.datasource.remote.firebase.FireStoreManager
import com.google.firebase.firestore.QuerySnapshot

class PagingSource<T: Any>(
    private val fireStoreManager: FireStoreManager,
    private val collectionPath: String,
    private val pageSize: Int,
    private val itemClass: Class<T>
) : PagingSource<QuerySnapshot, T>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, T>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, T> {
        return try {

            val currentPage = params.key ?: fireStoreManager.getInitialPage(collectionPath, pageSize)

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = fireStoreManager.getNextPage(collectionPath, pageSize, lastDocumentSnapshot)

            LoadResult.Page(
                data = currentPage.toObjects(itemClass),
                prevKey = null,
                nextKey = nextPage
            )

        }catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
