package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Appointment
import com.example.data.model.CasePaper
import com.example.data.model.Doctor
import com.example.data.model.Patient

@Database(
    entities = [
        Patient::class,
        Doctor::class,
        CasePaper::class,
        Appointment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun doctorDao(): DoctorDao
    abstract fun casePaperDao(): CasePaperDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clinic_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
