package com.example.data.repository

import com.example.data.DemoData
import com.example.data.local.AppointmentDao
import com.example.data.local.CasePaperDao
import com.example.data.local.DoctorDao
import com.example.data.local.PatientDao
import com.example.data.model.Appointment
import com.example.data.model.CasePaper
import com.example.data.model.Doctor
import com.example.data.model.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ClinicRepository(
    private val patientDao: PatientDao,
    private val doctorDao: DoctorDao,
    private val casePaperDao: CasePaperDao,
    private val appointmentDao: AppointmentDao
) {
    // Patients
    val allPatients: Flow<List<Patient>> = patientDao.getAllPatients()
    val patientCount: Flow<Int> = patientDao.getPatientCount()

    fun searchPatients(query: String): Flow<List<Patient>> = patientDao.searchPatients(query)
    fun getPatientByPatientId(patientId: String): Flow<Patient?> = patientDao.getPatientByPatientId(patientId)

    suspend fun insertPatient(patient: Patient): Long = withContext(Dispatchers.IO) {
        patientDao.insertPatient(patient)
    }

    suspend fun updatePatient(patient: Patient) = withContext(Dispatchers.IO) {
        patientDao.updatePatient(patient)
    }

    suspend fun deletePatient(patient: Patient) = withContext(Dispatchers.IO) {
        patientDao.deletePatient(patient)
    }

    // Doctors
    val allDoctors: Flow<List<Doctor>> = doctorDao.getAllDoctors()
    val doctorCount: Flow<Int> = doctorDao.getDoctorCount()

    fun getDoctorById(doctorId: String): Flow<Doctor?> = doctorDao.getDoctorByDoctorId(doctorId)

    suspend fun insertDoctor(doctor: Doctor): Long = withContext(Dispatchers.IO) {
        doctorDao.insertDoctor(doctor)
    }

    suspend fun updateDoctor(doctor: Doctor) = withContext(Dispatchers.IO) {
        doctorDao.updateDoctor(doctor)
    }

    suspend fun deleteDoctor(doctor: Doctor) = withContext(Dispatchers.IO) {
        doctorDao.deleteDoctor(doctor)
    }

    // Case Papers
    val allCasePapers: Flow<List<CasePaper>> = casePaperDao.getAllCasePapers()
    val casePaperCount: Flow<Int> = casePaperDao.getCasePaperCount()

    fun getCasePapersForPatient(patientId: String): Flow<List<CasePaper>> =
        casePaperDao.getCasePapersForPatient(patientId)

    fun getCasePaperById(casePaperId: String): Flow<CasePaper?> =
        casePaperDao.getCasePaperById(casePaperId)

    suspend fun insertCasePaper(casePaper: CasePaper): Long = withContext(Dispatchers.IO) {
        casePaperDao.insertCasePaper(casePaper)
    }

    suspend fun deleteCasePaper(casePaper: CasePaper) = withContext(Dispatchers.IO) {
        casePaperDao.deleteCasePaper(casePaper)
    }

    // Appointments
    val allAppointments: Flow<List<Appointment>> = appointmentDao.getAllAppointments()
    val totalAppointmentsCount: Flow<Int> = appointmentDao.getTotalCount()

    fun getAppointmentsByDate(date: String): Flow<List<Appointment>> =
        appointmentDao.getAppointmentsByDate(date)

    fun getAppointmentsForPatient(patientId: String): Flow<List<Appointment>> =
        appointmentDao.getAppointmentsForPatient(patientId)

    fun getActiveBookingsForDoctorOnDate(doctorId: String, date: String): Flow<List<Appointment>> =
        appointmentDao.getActiveBookingsForDoctorOnDate(doctorId, date)

    fun getAppointmentsByStatus(status: String): Flow<List<Appointment>> =
        appointmentDao.getAppointmentsByStatus(status)

    fun getAppointmentsCountByStatus(status: String): Flow<Int> =
        appointmentDao.getCountByStatus(status)

    fun getAppointmentsCountForDate(date: String): Flow<Int> =
        appointmentDao.getCountForDate(date)

    suspend fun insertAppointment(appointment: Appointment): Long = withContext(Dispatchers.IO) {
        appointmentDao.insertAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: Appointment) = withContext(Dispatchers.IO) {
        appointmentDao.updateAppointment(appointment)
    }

    suspend fun updateAppointmentStatus(appointmentId: String, status: String) = withContext(Dispatchers.IO) {
        appointmentDao.updateStatus(appointmentId, status)
    }

    suspend fun deleteAppointment(appointment: Appointment) = withContext(Dispatchers.IO) {
        appointmentDao.deleteAppointment(appointment)
    }

    // Seeding & Resetting Demo Data
    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val patientCount = patientDao.getPatientCountDirect()
        if (patientCount == 0) {
            seedDemoData()
        }
    }

    suspend fun resetToDemoData() = withContext(Dispatchers.IO) {
        patientDao.clearAll()
        doctorDao.clearAll()
        casePaperDao.clearAll()
        appointmentDao.clearAll()
        seedDemoData()
    }

    private suspend fun seedDemoData() {
        patientDao.insertAll(DemoData.samplePatients)
        doctorDao.insertAll(DemoData.sampleDoctors)
        casePaperDao.insertAll(DemoData.getSampleCasePapers())
        appointmentDao.insertAll(DemoData.getSampleAppointments())
    }
}
