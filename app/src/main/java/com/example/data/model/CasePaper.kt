package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_papers")
data class CasePaper(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val casePaperId: String,
    val patientId: String,
    val doctorId: String,
    val doctorName: String,
    val visitDate: String,
    val chiefComplaint: String,
    val symptoms: String,
    val medicalHistory: String,
    val allergies: String,
    val medications: String,
    val bloodPressure: String = "",
    val pulse: String = "",
    val temperature: String = "",
    val weight: String = "",
    val height: String = "",
    val spo2: String = "",
    val doctorNotes: String = "",
    val diagnosis: String = "",
    val prescriptionJson: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
