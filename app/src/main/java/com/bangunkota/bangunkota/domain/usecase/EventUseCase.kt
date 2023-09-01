package com.bangunkota.bangunkota.domain.usecase

import com.bangunkota.bangunkota.data.repository.abstractions.EventRepository
import com.bangunkota.bangunkota.domain.entity.CommunityEvent

class EventUseCase(private val repository: EventRepository) {
    suspend fun insertData(data: CommunityEvent): Result<Unit> {
        return repository.addEvent(data)
    }

    suspend fun updateData(data: CommunityEvent) {
        val firestoreData = CommunityEvent(data.id, data.title, data.address, data.image, data.date, data.time)
        repository.updateEvent(firestoreData)
    }

    suspend fun deleteData(dataId: String) {
        repository.deleteEvent(dataId)
    }
}