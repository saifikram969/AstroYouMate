package com.example.astroyoumat.data.repository

import com.example.astroyoumat.data.model.AppointmentModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CounselorRepository {

    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    fun listenToAppointments(
        counselorId: String,
        onChange: (List<AppointmentModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        listener = db.collection("appointments")
            .whereEqualTo("counselorId", counselorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "Unknown error")
                    return@addSnapshotListener
                }

                val appointments = snapshot?.documents?.mapNotNull { it.toObject(AppointmentModel::class.java) }
                onChange(appointments ?: emptyList())
            }
    }

    fun updateStatus(
        appointmentId: String,
        newStatus: String,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("appointments")
            .document(appointmentId)
            .update("status", newStatus)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { onError(it.message ?: "Update failed") }
    }

    fun removeListener() {
        listener?.remove()
    }
}

