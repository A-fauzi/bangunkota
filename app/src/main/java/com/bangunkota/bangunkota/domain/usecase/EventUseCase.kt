package com.bangunkota.bangunkota.domain.usecase

import com.bangunkota.bangunkota.data.repository.abstractions.EventRepository
import com.bangunkota.bangunkota.domain.entity.Event

class EventUseCase(private val repository: EventRepository) {
    suspend fun insertData(data: Event): Result<Unit> {
        return repository.insertData(data)
    }

    suspend fun updateData(data: Event) {
        val firestoreData = Event(data.id, data.title, data.address, data.date, data.image)
        repository.updateData(firestoreData)
    }

    suspend fun deleteData(dataId: String) {
        repository.deleteData(dataId)
    }
}