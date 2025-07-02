package com.example.astroyoumat.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astroyoumat.data.model.AppointmentModel
import com.example.astroyoumat.data.remote.sendNotification
import com.example.astroyoumat.data.repository.CounselorRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CounselorViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val repo = CounselorRepository()

    private val _appointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val appointments: StateFlow<List<AppointmentModel>> = _appointments

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun startListening(counselorId: String) {
        repo.listenToAppointments(
            counselorId = counselorId,
            onChange = {
                _appointments.value = it
            },
            onError = {
                _error.value = it
            }
        )
    }

    fun updateStatus(
        appointmentId: String,
        newStatus: String,
        userId: String
    ) {
        viewModelScope.launch {
            repo.updateStatus(
                appointmentId,
                newStatus,
                onComplete = {
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { document ->
                            val token = document.getString("fcmToken")
                            if (!token.isNullOrEmpty()) {
                                sendNotification.sendPushNotification(
                                    context = context,
                                    token = token,
                                    title = "Appointment $newStatus",
                                    message = "Your appointment was $newStatus by the counselor."
                                )
                            } else {
                                _error.value = "FCM token not found."
                            }
                        }
                        .addOnFailureListener {
                            _error.value = "Failed to fetch FCM token: ${it.localizedMessage}"
                        }
                },
                onError = { msg ->
                    _error.value = msg
                }
            )
        }
    }

    fun deleteAppointment(appointmentId: String) {
        FirebaseFirestore.getInstance()
            .collection("appointments")
            .document(appointmentId)
            .delete()
            .addOnSuccessListener {
                // Optional: Show success message or update state
            }
            .addOnFailureListener { e ->
                _error.value = "Failed to delete: ${e.localizedMessage}"
            }
    }


    override fun onCleared() {
        super.onCleared()
        repo.removeListener()
    }
}
