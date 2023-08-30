package com.bangunkota.bangunkota.data.repository.abstractions

import com.bangunkota.bangunkota.domain.entity.Event

interface EventRepository {
    suspend fun insertData(data: Event): Result<Unit>
    suspend fun updateData(data: Event)
    suspend fun deleteData(dataId: String)
//    suspend fun getData(): List<YourDataModel>  by id harus nya
}