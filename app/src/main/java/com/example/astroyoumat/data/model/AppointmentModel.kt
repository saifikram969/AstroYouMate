package com.example.astroyoumat.data.model

data class AppointmentModel(
    val appointmentId: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val counselorId: String = "",
    val issue: String = "",
    val dateTime: String = "",
    val status: String = "pending"
)
