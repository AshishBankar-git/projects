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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.ClinicViewModel

@Composable
fun AppointmentManagementScreen(
    viewModel: ClinicViewModel,
    onNavigateToBookAppointment: () -> Unit,
    onNavigateToPatientProfile: (String) -> Unit
) {
    val appointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val todayDate = viewModel.todayDateString

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Today", "Upcoming", "Completed", "Cancelled", "All")

    var searchQuery by remember { mutableStateOf("") }

    var selectedAppointmentForReschedule by remember { mutableStateOf<Appointment?>(null) }
    var rescheduleDate by remember { mutableStateOf("") }
    var rescheduleTime by remember { mutableStateOf("") }

    val filteredList = remember(appointments, selectedTabIndex, searchQuery, todayDate) {
        val tabFiltered = when (selectedTabIndex) {
            0 -> appointments.filter { it.appointmentDate == todayDate }
            1 -> appointments.filter { it.appointmentDate > todayDate && it.status != "Cancelled" }
            2 -> appointments.filter { it.status == "Completed" }
            3 -> appointments.filter { it.status == "Cancelled" }
            else -> appointments
        }

        if (searchQuery.isBlank()) {
            tabFiltered
        } else {
            val q = searchQuery.trim().lowercase()
            tabFiltered.filter {
                it.patientName.lowercase().contains(q) ||
                it.doctorName.lowercase().contains(q) ||
                it.appointmentId.lowercase().contains(q) ||
                it.patientId.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Appointment Queue",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage schedules, status transitions, and arrival checks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onNavigateToBookAppointment,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("new_booking_queue_button")
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Book")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search in Appointments
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter by patient, doctor, or APT ID...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Tabs (Today, Upcoming, Completed, Cancelled, All)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Appointments List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No appointments in this view",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.appointmentId }) { apt ->
                    AppointmentManagementItem(
                        appointment = apt,
                        onViewPatient = { onNavigateToPatientProfile(apt.patientId) },
                        onMarkWaiting = {
                            viewModel.updateAppointmentStatus(apt.appointmentId, "Waiting")
                        },
                        onMarkCompleted = {
                            viewModel.updateAppointmentStatus(apt.appointmentId, "Completed")
                        },
                        onCancel = {
                            viewModel.cancelAppointment(apt.appointmentId)
                        },
                        onReschedule = {
                            selectedAppointmentForReschedule = apt
                            rescheduleDate = apt.appointmentDate
                            rescheduleTime = apt.appointmentTime
                        }
                    )
                }
            }
        }
    }

    // Reschedule Dialog
    if (selectedAppointmentForReschedule != null) {
        val apt = selectedAppointmentForReschedule!!
        AlertDialog(
            onDismissRequest = { selectedAppointmentForReschedule = null },
            title = { Text("Reschedule Appointment") },
            text = {
                Column {
                    Text("Patient: ${apt.patientName}")
                    Text("Doctor: ${apt.doctorName}")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rescheduleDate,
                        onValueChange = { rescheduleDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rescheduleTime,
                        onValueChange = { rescheduleTime = it },
                        label = { Text("Time Slot (e.g. 11:15 AM)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rescheduleAppointment(apt.appointmentId, rescheduleDate, rescheduleTime)
                        selectedAppointmentForReschedule = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAppointmentForReschedule = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AppointmentManagementItem(
    appointment: Appointment,
    onViewPatient: () -> Unit,
    onMarkWaiting: () -> Unit,
    onMarkCompleted: () -> Unit,
    onCancel: () -> Unit,
    onReschedule: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = appointment.appointmentTime,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${appointment.appointmentDate}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.patientName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ID: ${appointment.patientId} • Dr. ${appointment.doctorName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (appointment.reason.isNotBlank()) {
                        Text(
                            text = "Reason: ${appointment.reason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action Menu
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Patient Profile") },
                            leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onViewPatient()
                            }
                        )
                        if (appointment.status == "Confirmed") {
                            DropdownMenuItem(
                                text = { Text("Mark Arrived (Waiting)") },
                                leadingIcon = { Icon(Icons.Default.HourglassTop, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onMarkWaiting()
                                }
                            )
                        }
                        if (appointment.status == "Waiting" || appointment.status == "Confirmed") {
                            DropdownMenuItem(
                                text = { Text("Mark Completed") },
                                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onMarkCompleted()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Reschedule") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onReschedule()
                            }
                        )
                        if (appointment.status != "Cancelled") {
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Cancel", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = { Icon(Icons.Default.Cancel, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    menuExpanded = false
                                    onCancel()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
