package com.bangunkota.bangunkota.data.repository.implementatios

import com.bangunkota.bangunkota.data.repository.abstractions.EventRepository
import com.bangunkota.bangunkota.domain.entity.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRepositoryImpl: EventRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun insertData(data: HashMap<String, out Any>): Result<Unit> {
        return try {
            val document = firestore.collection("events")
            document
                .add(data)
                .await() // Tunggu hingga operasi selesai

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun updateData(data: Event) {
        val document = firestore.collection("event").document(data.id.toString())
        document.set(data)
    }

    override suspend fun deleteData(dataId: String) {
        val document = firestore.collection("event").document(dataId)
        document.set(dataId)
    }
}