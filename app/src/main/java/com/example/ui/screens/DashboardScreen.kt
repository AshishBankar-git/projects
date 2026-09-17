package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Appointment
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ClinicTealPrimary
import com.example.ui.viewmodel.ClinicViewModel
import java.util.Calendar

@Composable
fun DashboardScreen(
    viewModel: ClinicViewModel,
    onNavigateToNewPatient: () -> Unit,
    onNavigateToBookAppointment: () -> Unit,
    onNavigateToSearchPatient: () -> Unit,
    onNavigateToPatientProfile: (String) -> Unit
) {
    val patients by viewModel.allPatients.collectAsStateWithLifecycle()
    val appointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val staffName by viewModel.currentStaffName.collectAsStateWithLifecycle()

    val todayDate = viewModel.todayDateString
    val todayAppointments = remember(appointments, todayDate) {
        appointments.filter { it.appointmentDate == todayDate }
    }
    val upcomingAppointments = remember(appointments, todayDate) {
        appointments.filter { it.appointmentDate > todayDate && it.status != "Cancelled" }
    }
    val completedAppointments = remember(appointments) {
        appointments.filter { it.status == "Completed" }
    }

    var selectedAppointmentForAction by remember { mutableStateOf<Appointment?>(null) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var rescheduleTime by remember { mutableStateOf("") }
    var rescheduleDate by remember { mutableStateOf(todayDate) }

    // Greeting determination
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$greeting, $staffName",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Today is $todayDate • Overview of clinic queue & appointments",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Summary Metric Cards in 2x2 Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Today's Appts",
                    value = "${todayAppointments.size}",
                    icon = Icons.Default.CalendarMonth,
                    iconColor = ClinicTealPrimary,
                    iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Patients",
                    value = "${patients.size}",
                    icon = Icons.Default.People,
                    iconColor = Color(0xFF1565C0),
                    iconBgColor = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Upcoming",
                    value = "${upcomingAppointments.size}",
                    icon = Icons.Default.DateRange,
                    iconColor = Color(0xFF6A1B9A),
                    iconBgColor = Color(0xFFF3E5F5),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Completed",
                    value = "${completedAppointments.size}",
                    icon = Icons.Default.CheckCircle,
                    iconColor = Color(0xFF2E7D32),
                    iconBgColor = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Actions Section
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToNewPatient,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("quick_new_patient_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ New Patient")
                }

                Button(
                    onClick = onNavigateToBookAppointment,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.testTag("quick_book_appointment_button")
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📅 Book Appointment")
                }

                OutlinedButton(
                    onClick = onNavigateToSearchPatient,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("quick_search_patient_button")
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🔍 Search Patient")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Today's Appointments Header
        item {
            SectionHeader(
                title = "Today's Appointments (${todayAppointments.size})",
                actionText = "View All Appointments",
                onActionClick = onNavigateToBookAppointment
            )
        }

        // Today's Appointments List / Table Card
        if (todayAppointments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No appointments scheduled for today.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onNavigateToBookAppointment) {
                            Text("Book First Appointment")
                        }
                    }
                }
            }
        } else {
            items(todayAppointments, key = { it.appointmentId }) { apt ->
                TodayAppointmentItem(
                    appointment = apt,
                    onViewPatient = { onNavigateToPatientProfile(apt.patientId) },
                    onMarkCompleted = {
                        viewModel.updateAppointmentStatus(apt.appointmentId, "Completed")
                    },
                    onMarkArrived = {
                        viewModel.updateAppointmentStatus(apt.appointmentId, "Waiting")
                    },
                    onCancel = {
                        viewModel.cancelAppointment(apt.appointmentId)
                    },
                    onRescheduleClick = {
                        selectedAppointmentForAction = apt
                        rescheduleDate = apt.appointmentDate
                        rescheduleTime = apt.appointmentTime
                        showRescheduleDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // Reschedule Dialog
    if (showRescheduleDialog && selectedAppointmentForAction != null) {
        val apt = selectedAppointmentForAction!!
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = false },
            title = { Text("Reschedule Appointment") },
            text = {
                Column {
                    Text(text = "Patient: ${apt.patientName}")
                    Text(text = "Doctor: ${apt.doctorName}")
                    Spacer(modifier = Modifier.height(12.dp))
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
                        label = { Text("Time Slot (e.g. 10:30 AM)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rescheduleAppointment(apt.appointmentId, rescheduleDate, rescheduleTime)
                        showRescheduleDialog = false
                    }
                ) {
                    Text("Confirm Reschedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun TodayAppointmentItem(
    appointment: Appointment,
    onViewPatient: () -> Unit,
    onMarkCompleted: () -> Unit,
    onMarkArrived: () -> Unit,
    onCancel: () -> Unit,
    onRescheduleClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${appointment.appointmentId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onViewPatient() }
                ) {
                    Text(
                        text = appointment.patientName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ID: ${appointment.patientId} • With ${appointment.doctorName}",
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
                        Icon(Icons.Default.MoreVert, contentDescription = "Appointment actions")
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
                        if (appointment.status == "Confirmed") {
                            DropdownMenuItem(
                                text = { Text("Mark Arrived (Waiting)") },
                                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onMarkArrived()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Reschedule / Edit") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onRescheduleClick()
                            }
                        )
                        if (appointment.status != "Cancelled") {
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Cancel Appointment", color = MaterialTheme.colorScheme.error) },
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
