package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val fullName: String,
    val dateOfBirth: String,
    val age: Int,
    val gender: String,
    val mobile: String,
    val email: String,
    val address: String,
    val bloodGroup: String,
    val emergencyContact: String,
    val allergies: String,
    val medicalConditions: String,
    val createdAt: Long = System.currentTimeMillis()
)
