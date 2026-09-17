package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appointmentId: String,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val reason: String,
    val status: String, // "Waiting", "Confirmed", "Completed", "Cancelled"
    val createdAt: Long = System.currentTimeMillis()
)
