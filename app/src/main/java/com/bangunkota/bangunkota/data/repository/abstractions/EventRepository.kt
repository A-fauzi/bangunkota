package com.bangunkota.bangunkota.data.repository.abstractions

import com.bangunkota.bangunkota.domain.entity.CommunityEvent

interface EventRepository {
    // Menampilkan detail event berdasarkan ID
    suspend fun getEventById(eventId: String): CommunityEvent?

    // Menambahkan event baru
    suspend fun addEvent(event: CommunityEvent): Result<Unit>

    // Memperbarui informasi event
    suspend fun updateEvent(event: CommunityEvent): Result<Unit>

    // Menghapus event berdasarkan ID
    suspend fun deleteEvent(eventId: String): Result<Unit>
}