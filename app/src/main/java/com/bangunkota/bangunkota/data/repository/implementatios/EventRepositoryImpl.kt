package com.bangunkota.bangunkota.data.repository.implementatios

import com.bangunkota.bangunkota.data.repository.abstractions.EventRepository
import com.bangunkota.bangunkota.domain.entity.CommunityEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRepositoryImpl: EventRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun getEventById(eventId: String): CommunityEvent? {
        return try {
            val documentSnapshot = firestore.collection("events").document(eventId).get().await()
            documentSnapshot.toObject(CommunityEvent::class.java)
        } catch (e: Exception) {
            // Handle error
            null // Atau Anda bisa melempar Exception sesuai kebutuhan
        }
    }

    override suspend fun addEvent(event: CommunityEvent): Result<Unit> {
        return try {
            val document = firestore.collection("events").document(event.id.toString())
            document
                .set(event)
                .await() // Tunggu hingga operasi selesai

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEvent(event: CommunityEvent): Result<Unit> {
        return try {
            val document = firestore.collection("events").document(event.id.toString())
            document
                .set(event)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            val document = firestore.collection("events").document(eventId)
            document.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}