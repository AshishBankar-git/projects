package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.ui.components.InfoRow
import com.example.ui.viewmodel.ClinicViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppointmentBookingScreen(
    preselectedPatientId: String?,
    viewModel: ClinicViewModel,
    onBack: () -> Unit,
    onAppointmentConfirmed: () -> Unit
) {
    val allPatients by viewModel.allPatients.collectAsStateWithLifecycle()
    val allDoctors by viewModel.allDoctors.collectAsStateWithLifecycle()
    val allAppointments by viewModel.allAppointments.collectAsStateWithLifecycle()

    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }

    var patientDropdownExpanded by remember { mutableStateOf(false) }
    var doctorDropdownExpanded by remember { mutableStateOf(false) }

    // Prefill patient if passed
    LaunchedEffect(preselectedPatientId, allPatients) {
        if (!preselectedPatientId.isNullOrBlank()) {
            selectedPatient = allPatients.find { it.patientId == preselectedPatientId }
        } else if (selectedPatient == null && allPatients.isNotEmpty()) {
            selectedPatient = allPatients.first()
        }
    }

    LaunchedEffect(allDoctors) {
        if (selectedDoctor == null && allDoctors.isNotEmpty()) {
            selectedDoctor = allDoctors.first()
        }
    }

    // Step 3: Date selection (Generate today + next 6 days)
    val availableDates = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (i in 0..6) {
            list.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }
    var selectedDate by remember { mutableStateOf(availableDates.first()) }

    // Step 4: Time Slot selection
    val doctorSlots = remember(selectedDoctor) {
        viewModel.getStandardDoctorSlots(selectedDoctor)
    }
    var selectedSlot by remember { mutableStateOf("") }

    // Step 5: Reason
    val standardReasons = listOf(
        "General Consultation",
        "Follow-up Visit",
        "Routine Checkup",
        "Report Review",
        "Prescription Refill",
        "Emergency / Acute Pain"
    )
    var selectedReason by remember { mutableStateOf(standardReasons.first()) }
    var customReason by remember { mutableStateOf("") }

    // Step 6: Confirmation Dialog State
    var confirmedAppointment by remember { mutableStateOf<Appointment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Book Appointment",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "6-Step simple clinic scheduling workflow",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 1: Patient Selection Card
        BookingStepCard(stepNumber = "1", title = "Select Patient") {
            ExposedDropdownMenuBox(
                expanded = patientDropdownExpanded,
                onExpandedChange = { patientDropdownExpanded = !patientDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedPatient?.let { "${it.fullName} (${it.patientId}) - ${it.mobile}" } ?: "Select Patient",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Patient *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor().testTag("booking_patient_select")
                )
                ExposedDropdownMenu(
                    expanded = patientDropdownExpanded,
                    onDismissRequest = { patientDropdownExpanded = false }
                ) {
                    allPatients.forEach { p ->
                        DropdownMenuItem(
                            text = { Text("${p.fullName} (${p.patientId}) • ${p.gender}, ${p.age}y") },
                            onClick = {
                                selectedPatient = p
                                patientDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step 2: Doctor Selection Card
        BookingStepCard(stepNumber = "2", title = "Select Doctor") {
            ExposedDropdownMenuBox(
                expanded = doctorDropdownExpanded,
                onExpandedChange = { doctorDropdownExpanded = !doctorDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedDoctor?.let { "${it.name} (${it.specialization}) - Fee: $${it.consultationFee.toInt()}" } ?: "Select Doctor",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Consulting Doctor *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor().testTag("booking_doctor_select")
                )
                ExposedDropdownMenu(
                    expanded = doctorDropdownExpanded,
                    onDismissRequest = { doctorDropdownExpanded = false }
                ) {
                    allDoctors.forEach { d ->
                        DropdownMenuItem(
                            text = { Text("${d.name} • ${d.specialization} (${d.availableDays})") },
                            onClick = {
                                selectedDoctor = d
                                selectedSlot = "" // reset slot on doctor change
                                doctorDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            if (selectedDoctor != null) {
                Spacer(modifier = Modifier.height(6.dp))
                val d = selectedDoctor!!
                Text(
                    text = "Hours: ${d.startTime} – ${d.endTime} • Slot duration: ${d.appointmentDuration} min • Days: ${d.availableDays}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step 3: Date Selection Card
        BookingStepCard(stepNumber = "3", title = "Select Appointment Date") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableDates.forEach { dateStr ->
                    val isSelected = selectedDate == dateStr
                    val isToday = dateStr == availableDates.first()

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedDate = dateStr
                            selectedSlot = "" // reset slot when date changes
                        },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = dateStr, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                if (isToday) {
                                    Text(text = "(Today)", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                        modifier = Modifier.testTag("date_chip_$dateStr")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step 4: Available Time Slots
        BookingStepCard(
            stepNumber = "4",
            title = "Select Available Time Slot",
            subtitle = "Occupied slots are marked as Booked and cannot be selected."
        ) {
            if (doctorSlots.isEmpty()) {
                Text(
                    text = "No slot schedule defined for the selected doctor.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currentDocId = selectedDoctor?.doctorId ?: ""
                    doctorSlots.forEach { slot ->
                        val isBooked = viewModel.isSlotBooked(currentDocId, selectedDate, slot)
                        val isSelected = selectedSlot == slot

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isBooked -> Color(0xFFEEEEEE)
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        isBooked -> Color(0xFFCCCCCC)
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.outlineVariant
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = !isBooked) {
                                    selectedSlot = slot
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("slot_chip_$slot")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = slot,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        isBooked -> Color(0xFF9E9E9E)
                                        isSelected -> Color.White
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (isBooked) {
                                    Text(
                                        text = "• Booked",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F)
                                    )
                                } else {
                                    Text(
                                        text = "• Open",
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step 5: Reason Selection Card
        BookingStepCard(stepNumber = "5", title = "Appointment Reason / Type") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                standardReasons.forEach { reason ->
                    FilterChip(
                        selected = selectedReason == reason,
                        onClick = { selectedReason = reason },
                        label = { Text(reason) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customReason,
                onValueChange = { customReason = it },
                label = { Text("Additional Symptoms / Notes (Optional)") },
                placeholder = { Text("e.g. Mild chest pain, routine follow up report review") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("appointment_custom_reason")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 6: Confirmation Button
        Button(
            onClick = {
                val p = selectedPatient
                val d = selectedDoctor
                if (p == null) {
                    viewModel.showMessage("Please select a patient.")
                    return@Button
                }
                if (d == null) {
                    viewModel.showMessage("Please select a doctor.")
                    return@Button
                }
                if (selectedSlot.isBlank()) {
                    viewModel.showMessage("Please pick an available time slot.")
                    return@Button
                }

                val finalReason = if (customReason.isNotBlank()) "$selectedReason: $customReason" else selectedReason

                viewModel.bookAppointment(
                    patientId = p.patientId,
                    patientName = p.fullName,
                    doctorId = d.doctorId,
                    doctorName = d.name,
                    date = selectedDate,
                    time = selectedSlot,
                    reason = finalReason
                ) { bookedApt ->
                    confirmedAppointment = bookedApt
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("confirm_booking_button")
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Step 6: Confirm & Book Appointment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Booking Success Confirmation Dialog
    if (confirmedAppointment != null) {
        val apt = confirmedAppointment!!
        AlertDialog(
            onDismissRequest = {
                confirmedAppointment = null
                onAppointmentConfirmed()
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text("Appointment Booked Successfully", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Booking Reference: ${apt.appointmentId}",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    InfoRow(label = "Patient", value = apt.patientName)
                    InfoRow(label = "Patient ID", value = apt.patientId)
                    InfoRow(label = "Doctor", value = apt.doctorName)
                    InfoRow(label = "Date", value = apt.appointmentDate)
                    InfoRow(label = "Time Slot", value = apt.appointmentTime)
                    InfoRow(label = "Status", value = apt.status)
                    InfoRow(label = "Reason", value = apt.reason)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        confirmedAppointment = null
                        onAppointmentConfirmed()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("booking_success_dismiss_button")
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun BookingStepCard(
    stepNumber: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            content()
        }
    }
}
