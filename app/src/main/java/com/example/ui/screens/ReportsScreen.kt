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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.InfoRow
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.ClinicTealPrimary
import com.example.ui.viewmodel.ClinicViewModel

@Composable
fun ReportsScreen(
    viewModel: ClinicViewModel
) {
    val patients by viewModel.allPatients.collectAsStateWithLifecycle()
    val appointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val doctors by viewModel.allDoctors.collectAsStateWithLifecycle()
    val casePapers by viewModel.allCasePapers.collectAsStateWithLifecycle()

    val todayDate = viewModel.todayDateString

    val todayAppointmentsCount = remember(appointments, todayDate) {
        appointments.count { it.appointmentDate == todayDate }
    }
    val completedCount = remember(appointments) {
        appointments.count { it.status == "Completed" }
    }
    val cancelledCount = remember(appointments) {
        appointments.count { it.status == "Cancelled" }
    }
    val waitingCount = remember(appointments) {
        appointments.count { it.status == "Waiting" }
    }
    val confirmedCount = remember(appointments) {
        appointments.count { it.status == "Confirmed" }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Clinic Operational Analytics",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Patient inflow, completion metrics, and doctor load distributions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Top 4 Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Total Patients",
                    value = "${patients.size}",
                    icon = Icons.Default.People,
                    iconColor = ClinicTealPrimary,
                    iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Today's Appts",
                    value = "$todayAppointmentsCount",
                    icon = Icons.Default.CalendarMonth,
                    iconColor = Color(0xFF1565C0),
                    iconBgColor = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Completed",
                    value = "$completedCount",
                    icon = Icons.Default.CheckCircle,
                    iconColor = Color(0xFF2E7D32),
                    iconBgColor = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Cancelled",
                    value = "$cancelledCount",
                    icon = Icons.Default.Cancel,
                    iconColor = Color(0xFFC62828),
                    iconBgColor = Color(0xFFFFEBEE),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Secondary Metrics & Case Paper Stats
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Appointment Status Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val totalApts = if (appointments.isEmpty()) 1 else appointments.size

                    StatusDistributionRow(
                        label = "Completed",
                        count = completedCount,
                        total = totalApts,
                        color = Color(0xFF2E7D32)
                    )
                    StatusDistributionRow(
                        label = "Confirmed (Upcoming)",
                        count = confirmedCount,
                        total = totalApts,
                        color = Color(0xFF0277BD)
                    )
                    StatusDistributionRow(
                        label = "Waiting / In-Clinic",
                        count = waitingCount,
                        total = totalApts,
                        color = Color(0xFFE65100)
                    )
                    StatusDistributionRow(
                        label = "Cancelled",
                        count = cancelledCount,
                        total = totalApts,
                        color = Color(0xFFC62828)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(label = "Total Case Papers Documented", value = "${casePapers.size}")
                    InfoRow(label = "Active Consulting Doctors", value = "${doctors.size}")
                }
            }
        }

        // Doctor-wise Appointment Breakdown
        item {
            SectionHeader(title = "Doctor-Wise Appointment Distribution")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (doctors.isEmpty()) {
                        Text("No doctors registered.")
                    } else {
                        val maxPerDoctor = doctors.maxOfOrNull { doc ->
                            appointments.count { it.doctorId == doc.doctorId }
                        }?.coerceAtLeast(1) ?: 1

                        doctors.forEach { doc ->
                            val docApts = appointments.filter { it.doctorId == doc.doctorId }
                            val docCompleted = docApts.count { it.status == "Completed" }

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = doc.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = doc.specialization,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${docApts.size} appts (${docCompleted} completed)",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { docApts.size.toFloat() / maxPerDoctor },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusDistributionRow(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val fraction = if (total > 0) count.toFloat() / total else 0f
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = "$count (${(fraction * 100).toInt()}%)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
