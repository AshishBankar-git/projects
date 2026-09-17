package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class Doctor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val doctorId: String,
    val name: String,
    val specialization: String,
    val phone: String,
    val consultationFee: Double,
    val availableDays: String,
    val startTime: String,
    val endTime: String,
    val appointmentDuration: Int = 15
)
