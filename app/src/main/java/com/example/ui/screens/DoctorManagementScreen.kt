package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Doctor
import com.example.ui.components.InfoRow
import com.example.ui.viewmodel.ClinicViewModel

@Composable
fun DoctorManagementScreen(
    viewModel: ClinicViewModel
) {
    val doctors by viewModel.allDoctors.collectAsStateWithLifecycle()

    var showAddDoctorDialog by remember { mutableStateOf(false) }
    var doctorToEdit by remember { mutableStateOf<Doctor?>(null) }
    var doctorToDelete by remember { mutableStateOf<Doctor?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Doctor Management",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage clinic doctors, specializations, fees & time slots",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showAddDoctorDialog = true },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_doctor_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Doctor")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Doctor List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(doctors, key = { it.doctorId }) { doctor ->
                DoctorCard(
                    doctor = doctor,
                    onEdit = { doctorToEdit = doctor },
                    onDelete = { doctorToDelete = doctor }
                )
            }
        }
    }

    // Add Doctor Dialog
    if (showAddDoctorDialog) {
        DoctorFormDialog(
            title = "Add New Clinic Doctor",
            initialDoctor = null,
            onDismiss = { showAddDoctorDialog = false },
            onSave = { name, spec, phone, fee, days, start, end, dur ->
                viewModel.addDoctor(name, spec, phone, fee, days, start, end, dur)
                showAddDoctorDialog = false
            }
        )
    }

    // Edit Doctor Dialog
    if (doctorToEdit != null) {
        DoctorFormDialog(
            title = "Edit Doctor Details",
            initialDoctor = doctorToEdit,
            onDismiss = { doctorToEdit = null },
            onSave = { name, spec, phone, fee, days, start, end, dur ->
                viewModel.updateDoctor(
                    doctorToEdit!!.copy(
                        name = name,
                        specialization = spec,
                        phone = phone,
                        consultationFee = fee,
                        availableDays = days,
                        startTime = start,
                        endTime = end,
                        appointmentDuration = dur
                    )
                )
                doctorToEdit = null
            }
        )
    }

    // Delete Doctor Confirmation
    if (doctorToDelete != null) {
        val d = doctorToDelete!!
        AlertDialog(
            onDismissRequest = { doctorToDelete = null },
            title = { Text("Delete Doctor") },
            text = { Text("Are you sure you want to remove ${d.name} (${d.specialization}) from the active doctor roster?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDoctor(d)
                        doctorToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { doctorToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DoctorCard(
    doctor: Doctor,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = doctor.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${doctor.specialization} • ${doctor.doctorId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Consultation Fee Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$${doctor.consultationFee.toInt()} Fee",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Available Days", value = doctor.availableDays)
            InfoRow(label = "Shift Hours", value = "${doctor.startTime} – ${doctor.endTime}")
            InfoRow(label = "Slot Duration", value = "${doctor.appointmentDuration} minutes")
            InfoRow(label = "Contact Phone", value = doctor.phone)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Doctor", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Doctor", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun DoctorFormDialog(
    title: String,
    initialDoctor: Doctor?,
    onDismiss: () -> Unit,
    onSave: (name: String, spec: String, phone: String, fee: Double, days: String, start: String, end: String, dur: Int) -> Unit
) {
    var name by remember { mutableStateOf(initialDoctor?.name ?: "") }
    var spec by remember { mutableStateOf(initialDoctor?.specialization ?: "") }
    var phone by remember { mutableStateOf(initialDoctor?.phone ?: "") }
    var feeText by remember { mutableStateOf(initialDoctor?.consultationFee?.toInt()?.toString() ?: "50") }
    var days by remember { mutableStateOf(initialDoctor?.availableDays ?: "Mon, Tue, Wed, Thu, Fri") }
    var startTime by remember { mutableStateOf(initialDoctor?.startTime ?: "09:00 AM") }
    var endTime by remember { mutableStateOf(initialDoctor?.endTime ?: "01:00 PM") }
    var durationText by remember { mutableStateOf(initialDoctor?.appointmentDuration?.toString() ?: "15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Doctor Name *") },
                    placeholder = { Text("Dr. John Doe") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = spec,
                    onValueChange = { spec = it },
                    label = { Text("Specialization *") },
                    placeholder = { Text("Cardiology / General Medicine") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = feeText,
                        onValueChange = { feeText = it },
                        label = { Text("Fee ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(0.7f)
                    )
                }
                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Available Days") },
                    placeholder = { Text("Mon - Fri / Mon, Wed, Fri") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        placeholder = { Text("09:00 AM") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        placeholder = { Text("01:00 PM") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Slot Duration (mins)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && spec.isNotBlank()) {
                        val fee = feeText.toDoubleOrNull() ?: 50.0
                        val dur = durationText.toIntOrNull() ?: 15
                        onSave(name, spec, phone, fee, days, startTime, endTime, dur)
                    }
                }
            ) {
                Text("Save Doctor")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
