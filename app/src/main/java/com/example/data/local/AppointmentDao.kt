package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY appointmentDate DESC, appointmentTime ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE appointmentDate = :date ORDER BY appointmentTime ASC")
    fun getAppointmentsByDate(date: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY appointmentDate DESC, appointmentTime ASC")
    fun getAppointmentsForPatient(patientId: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId AND appointmentDate = :date AND status != 'Cancelled'")
    fun getActiveBookingsForDoctorOnDate(doctorId: String, date: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY appointmentDate DESC, appointmentTime ASC")
    fun getAppointmentsByStatus(status: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE appointmentId = :appointmentId LIMIT 1")
    fun getAppointmentById(appointmentId: String): Flow<Appointment?>

    @Query("SELECT COUNT(*) FROM appointments WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM appointments WHERE appointmentDate = :date")
    fun getCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM appointments")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun getTotalCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(appointments: List<Appointment>)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Query("UPDATE appointments SET status = :newStatus WHERE appointmentId = :appointmentId")
    suspend fun updateStatus(appointmentId: String, newStatus: String)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("DELETE FROM appointments")
    suspend fun clearAll()
}
