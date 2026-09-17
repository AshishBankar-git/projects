package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Appointment
import com.example.data.model.CasePaper
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.data.model.PrescriptionItem
import com.example.data.model.PrescriptionParser
import com.example.data.repository.ClinicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ClinicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClinicRepository
    val todayDateString: String

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ClinicRepository(
            patientDao = db.patientDao(),
            doctorDao = db.doctorDao(),
            casePaperDao = db.casePaperDao(),
            appointmentDao = db.appointmentDao()
        )
        todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    // Authentication & Receptionist State
    private val _isLoggedIn = MutableStateFlow(true) // Ready to test, allows logout/login
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentStaffName = MutableStateFlow("Receptionist")
    val currentStaffName: StateFlow<String> = _currentStaffName.asStateFlow()

    // Snackbar notifications
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun login(email: String, role: String = "Receptionist") {
        _currentStaffName.value = if (email.contains("@")) email.substringBefore("@").replaceFirstChar { it.uppercase() } else email
        _isLoggedIn.value = true
        showMessage("Welcome, ${_currentStaffName.value}!")
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // Reactive Data Sources
    val allPatients: StateFlow<List<Patient>> = repository.allPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDoctors: StateFlow<List<Doctor>> = repository.allDoctors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCasePapers: StateFlow<List<CasePaper>> = repository.allCasePapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAppointments: StateFlow<List<Appointment>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search Patients
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Patient>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.allPatients
        } else {
            repository.searchPatients(query.trim())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Selected Patient / Profile
    private val _selectedPatientId = MutableStateFlow<String?>(null)
    val selectedPatientId: StateFlow<String?> = _selectedPatientId.asStateFlow()

    fun selectPatient(patientId: String?) {
        _selectedPatientId.value = patientId
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPatientCasePapers: StateFlow<List<CasePaper>> = _selectedPatientId.flatMapLatest { pId ->
        if (pId == null) MutableStateFlow(emptyList())
        else repository.getCasePapersForPatient(pId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPatientAppointments: StateFlow<List<Appointment>> = _selectedPatientId.flatMapLatest { pId ->
        if (pId == null) MutableStateFlow(emptyList())
        else repository.getAppointmentsForPatient(pId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Print Case Paper Dialog Target
    private val _previewCasePaper = MutableStateFlow<CasePaper?>(null)
    val previewCasePaper: StateFlow<CasePaper?> = _previewCasePaper.asStateFlow()

    fun openCasePaperPreview(casePaper: CasePaper?) {
        _previewCasePaper.value = casePaper
    }

    // Patient Registration logic
    fun generateNextPatientId(existingList: List<Patient>): String {
        val maxNumber = existingList.mapNotNull { patient ->
            val numStr = patient.patientId.removePrefix("PAT-").trim()
            numStr.toIntOrNull()
        }.maxOrNull() ?: 0
        return String.format(Locale.US, "PAT-%04d", maxNumber + 1)
    }

    fun calculateAgeFromDob(dobString: String): Int? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val birthDate = sdf.parse(dobString) ?: return null
            val dobCalendar = Calendar.getInstance().apply { time = birthDate }
            val today = Calendar.getInstance()

            var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            if (age in 0..130) age else null
        } catch (_: Exception) {
            null
        }
    }

    fun registerPatient(
        fullName: String,
        dob: String,
        age: Int,
        gender: String,
        mobile: String,
        email: String,
        address: String,
        bloodGroup: String,
        emergencyContact: String,
        allergies: String,
        medicalConditions: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val nextId = generateNextPatientId(allPatients.value)
            val newPatient = Patient(
                patientId = nextId,
                fullName = fullName.trim(),
                dateOfBirth = dob.trim(),
                age = age,
                gender = gender,
                mobile = mobile.trim(),
                email = email.trim(),
                address = address.trim(),
                bloodGroup = bloodGroup,
                emergencyContact = emergencyContact.trim(),
                allergies = allergies.trim(),
                medicalConditions = medicalConditions.trim()
            )
            repository.insertPatient(newPatient)
            selectPatient(nextId)
            showMessage("Patient $nextId ($fullName) registered successfully.")
            onSuccess(nextId)
        }
    }

    // Digital Case Paper Creation
    fun generateNextCasePaperId(existingList: List<CasePaper>): String {
        val maxNumber = existingList.mapNotNull { cp ->
            val numStr = cp.casePaperId.removePrefix("CP-").trim()
            numStr.toIntOrNull()
        }.maxOrNull() ?: 1000
        return String.format(Locale.US, "CP-%04d", maxNumber + 1)
    }

    fun saveCasePaper(
        patientId: String,
        doctorId: String,
        doctorName: String,
        visitDate: String,
        chiefComplaint: String,
        symptoms: String,
        medicalHistory: String,
        allergies: String,
        medications: String,
        bp: String,
        pulse: String,
        temp: String,
        weight: String,
        height: String,
        spo2: String,
        notes: String,
        diagnosis: String,
        prescriptionList: List<PrescriptionItem>,
        onSuccess: (CasePaper) -> Unit
    ) {
        viewModelScope.launch {
            val nextCpId = generateNextCasePaperId(allCasePapers.value)
            val casePaper = CasePaper(
                casePaperId = nextCpId,
                patientId = patientId,
                doctorId = doctorId,
                doctorName = doctorName,
                visitDate = visitDate.ifBlank { todayDateString },
                chiefComplaint = chiefComplaint,
                symptoms = symptoms,
                medicalHistory = medicalHistory,
                allergies = allergies,
                medications = medications,
                bloodPressure = bp,
                pulse = pulse,
                temperature = temp,
                weight = weight,
                height = height,
                spo2 = spo2,
                doctorNotes = notes,
                diagnosis = diagnosis,
                prescriptionJson = PrescriptionParser.serialize(prescriptionList)
            )
            repository.insertCasePaper(casePaper)
            showMessage("Digital Case Paper $nextCpId saved & linked to patient.")
            onSuccess(casePaper)
        }
    }

    // Appointment Booking Logic
    fun generateNextAppointmentId(existingList: List<Appointment>): String {
        val maxNumber = existingList.mapNotNull { apt ->
            val numStr = apt.appointmentId.removePrefix("APT-").trim()
            numStr.toIntOrNull()
        }.maxOrNull() ?: 10240
        return String.format(Locale.US, "APT-%05d", maxNumber + 1)
    }

    // Generate Standard Slot Times for Doctor
    fun getStandardDoctorSlots(doctor: Doctor?): List<String> {
        if (doctor == null) return emptyList()
        // Provide standard clinic slot intervals based on doctor's shift
        return when (doctor.doctorId) {
            "DOC-001" -> listOf(
                "09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM",
                "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM",
                "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM",
                "12:00 PM", "12:15 PM", "12:30 PM", "12:45 PM"
            )
            "DOC-002" -> listOf(
                "10:00 AM", "10:20 AM", "10:40 AM",
                "11:00 AM", "11:20 AM", "11:40 AM",
                "12:00 PM", "12:20 PM", "12:40 PM",
                "01:00 PM", "01:20 PM", "01:40 PM"
            )
            "DOC-003" -> listOf(
                "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM",
                "05:00 PM", "05:30 PM", "06:00 PM", "06:30 PM"
            )
            "DOC-004" -> listOf(
                "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM",
                "01:00 PM", "01:15 PM", "01:30 PM", "01:45 PM",
                "02:00 PM", "02:15 PM", "02:30 PM", "02:45 PM",
                "03:00 PM", "03:15 PM", "03:30 PM", "03:45 PM"
            )
            else -> listOf(
                "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM"
            )
        }
    }

    fun isSlotBooked(doctorId: String, date: String, timeSlot: String): Boolean {
        return allAppointments.value.any { apt ->
            apt.doctorId == doctorId &&
            apt.appointmentDate == date &&
            apt.appointmentTime.equals(timeSlot, ignoreCase = true) &&
            apt.status != "Cancelled"
        }
    }

    fun bookAppointment(
        patientId: String,
        patientName: String,
        doctorId: String,
        doctorName: String,
        date: String,
        time: String,
        reason: String,
        onSuccess: (Appointment) -> Unit
    ) {
        if (isSlotBooked(doctorId, date, time)) {
            showMessage("Slot $time on $date is already booked! Please select another slot.")
            return
        }

        viewModelScope.launch {
            val nextAptId = generateNextAppointmentId(allAppointments.value)
            val appointment = Appointment(
                appointmentId = nextAptId,
                patientId = patientId,
                patientName = patientName,
                doctorId = doctorId,
                doctorName = doctorName,
                appointmentDate = date,
                appointmentTime = time,
                reason = reason.ifBlank { "General Consultation" },
                status = "Confirmed"
            )
            repository.insertAppointment(appointment)
            showMessage("Appointment $nextAptId booked successfully.")
            onSuccess(appointment)
        }
    }

    fun updateAppointmentStatus(appointmentId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointmentId, newStatus)
            showMessage("Appointment $appointmentId status updated to $newStatus.")
        }
    }

    fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        viewModelScope.launch {
            val current = allAppointments.value.find { it.appointmentId == appointmentId }
            if (current != null) {
                if (isSlotBooked(current.doctorId, newDate, newTime)) {
                    showMessage("Selected slot $newTime is already booked!")
                    return@launch
                }
                repository.updateAppointment(
                    current.copy(
                        appointmentDate = newDate,
                        appointmentTime = newTime,
                        status = "Confirmed"
                    )
                )
                showMessage("Appointment $appointmentId rescheduled to $newDate at $newTime.")
            }
        }
    }

    fun cancelAppointment(appointmentId: String) {
        updateAppointmentStatus(appointmentId, "Cancelled")
    }

    // Doctor Management
    fun addDoctor(
        name: String,
        specialization: String,
        phone: String,
        fee: Double,
        availableDays: String,
        startTime: String,
        endTime: String,
        duration: Int
    ) {
        viewModelScope.launch {
            val count = allDoctors.value.size
            val nextDocId = String.format(Locale.US, "DOC-%03d", count + 1)
            val doctor = Doctor(
                doctorId = nextDocId,
                name = name.trim(),
                specialization = specialization.trim(),
                phone = phone.trim(),
                consultationFee = fee,
                availableDays = availableDays.trim(),
                startTime = startTime.trim(),
                endTime = endTime.trim(),
                appointmentDuration = duration
            )
            repository.insertDoctor(doctor)
            showMessage("Doctor ${doctor.name} added successfully.")
        }
    }

    fun updateDoctor(doctor: Doctor) {
        viewModelScope.launch {
            repository.updateDoctor(doctor)
            showMessage("Doctor ${doctor.name} updated.")
        }
    }

    fun deleteDoctor(doctor: Doctor) {
        viewModelScope.launch {
            repository.deleteDoctor(doctor)
            showMessage("Doctor ${doctor.name} removed.")
        }
    }

    // Reset database to demo data
    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetToDemoData()
            showMessage("Database reset to demo state with 10 patients & 4 doctors.")
        }
    }
}
