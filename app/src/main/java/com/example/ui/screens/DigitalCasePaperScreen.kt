package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.CasePaper
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.data.model.PrescriptionItem
import com.example.ui.components.PrintCasePaperDialog
import com.example.ui.viewmodel.ClinicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalCasePaperScreen(
    patientId: String?,
    viewModel: ClinicViewModel,
    onBack: () -> Unit,
    onCasePaperSaved: (String) -> Unit
) {
    val allPatients by viewModel.allPatients.collectAsStateWithLifecycle()
    val allDoctors by viewModel.allDoctors.collectAsStateWithLifecycle()

    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }

    var patientDropdownExpanded by remember { mutableStateOf(false) }
    var doctorDropdownExpanded by remember { mutableStateOf(false) }

    // Prefill patient if patientId passed
    LaunchedEffect(patientId, allPatients) {
        if (!patientId.isNullOrBlank()) {
            selectedPatient = allPatients.find { it.patientId == patientId }
        } else if (selectedPatient == null && allPatients.isNotEmpty()) {
            selectedPatient = allPatients.first()
        }
    }

    LaunchedEffect(allDoctors) {
        if (selectedDoctor == null && allDoctors.isNotEmpty()) {
            selectedDoctor = allDoctors.first()
        }
    }

    val visitDate = viewModel.todayDateString

    var chiefComplaint by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var currentMedications by remember { mutableStateOf("") }

    // When patient changes, prefill existing known allergies & conditions
    LaunchedEffect(selectedPatient) {
        selectedPatient?.let { p ->
            if (allergies.isBlank()) allergies = p.allergies
            if (medicalHistory.isBlank()) medicalHistory = p.medicalConditions
        }
    }

    // Vitals
    var bp by remember { mutableStateOf("120/80") }
    var pulse by remember { mutableStateOf("72") }
    var temp by remember { mutableStateOf("98.6") }
    var weight by remember { mutableStateOf("70") }
    var height by remember { mutableStateOf("170") }
    var spo2 by remember { mutableStateOf("99") }

    // Doctor Notes & Diagnosis
    var doctorNotes by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }

    // Prescription table items
    val prescriptionItems = remember {
        mutableStateListOf(
            PrescriptionItem(
                medicineName = "Tab Paracetamol",
                dosage = "650mg",
                frequency = "TDS (3 times a day)",
                duration = "3 days",
                instructions = "After food"
            )
        )
    }

    var previewDialogPaper by remember { mutableStateOf<CasePaper?>(null) }

    fun buildTransientCasePaper(): CasePaper {
        return CasePaper(
            casePaperId = "PREVIEW-CP",
            patientId = selectedPatient?.patientId ?: "PAT-UNKNOWN",
            doctorId = selectedDoctor?.doctorId ?: "DOC-UNKNOWN",
            doctorName = selectedDoctor?.name ?: "Dr. Unassigned",
            visitDate = visitDate,
            chiefComplaint = chiefComplaint,
            symptoms = symptoms,
            medicalHistory = medicalHistory,
            allergies = allergies,
            medications = currentMedications,
            bloodPressure = "$bp mmHg",
            pulse = "$pulse bpm",
            temperature = "$temp °F",
            weight = "$weight kg",
            height = "$height cm",
            spo2 = "$spo2%",
            doctorNotes = doctorNotes,
            diagnosis = diagnosis,
            prescriptionJson = com.example.data.model.PrescriptionParser.serialize(prescriptionItems)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Navigation Header
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
                    text = "APEX HEALTHCARE CLINIC",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Digital Case Paper Entry",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header Patient & Doctor Card
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
                // Patient Selector
                ExposedDropdownMenuBox(
                    expanded = patientDropdownExpanded,
                    onExpandedChange = { patientDropdownExpanded = !patientDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPatient?.let { "${it.fullName} (${it.patientId})" } ?: "Select Patient",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selected Patient *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("case_paper_patient_select")
                    )
                    ExposedDropdownMenu(
                        expanded = patientDropdownExpanded,
                        onDismissRequest = { patientDropdownExpanded = false }
                    ) {
                        allPatients.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.fullName} (${p.patientId}) - ${p.gender}, ${p.age}y") },
                                onClick = {
                                    selectedPatient = p
                                    patientDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Doctor Selector
                ExposedDropdownMenuBox(
                    expanded = doctorDropdownExpanded,
                    onExpandedChange = { doctorDropdownExpanded = !doctorDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedDoctor?.let { "${it.name} - ${it.specialization}" } ?: "Select Doctor",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Consulting Doctor *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("case_paper_doctor_select")
                    )
                    ExposedDropdownMenu(
                        expanded = doctorDropdownExpanded,
                        onDismissRequest = { doctorDropdownExpanded = false }
                    ) {
                        allDoctors.forEach { d ->
                            DropdownMenuItem(
                                text = { Text("${d.name} (${d.specialization})") },
                                onClick = {
                                    selectedDoctor = d
                                    doctorDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Auto populated info chips
                if (selectedPatient != null) {
                    val p = selectedPatient!!
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Age/Sex: ${p.age}y / ${p.gender}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Mobile: ${p.mobile}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Date: $visitDate", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clinical Details Card
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Clinical Presentation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Chief Complaint
                OutlinedTextField(
                    value = chiefComplaint,
                    onValueChange = { chiefComplaint = it },
                    label = { Text("Chief Complaint *") },
                    placeholder = { Text("Primary issue reported by patient, e.g. High grade fever with body pain") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("case_paper_complaint_input")
                )

                // Symptoms
                OutlinedTextField(
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    label = { Text("Symptoms") },
                    placeholder = { Text("e.g. Headache, chills, sore throat, nausea") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Medical History & Allergies
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = medicalHistory,
                        onValueChange = { medicalHistory = it },
                        label = { Text("Medical History") },
                        placeholder = { Text("Past illnesses") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Allergies") },
                        placeholder = { Text("Drug/food allergies") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Current Medications
                OutlinedTextField(
                    value = currentMedications,
                    onValueChange = { currentMedications = it },
                    label = { Text("Current Medications") },
                    placeholder = { Text("Drugs patient is already taking") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vitals Section
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
                Text(
                    text = "Patient Vitals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = bp,
                        onValueChange = { bp = it },
                        label = { Text("BP (mmHg)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pulse,
                        onValueChange = { pulse = it },
                        label = { Text("Pulse (bpm)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = temp,
                        onValueChange = { temp = it },
                        label = { Text("Temp (°F)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = spo2,
                        onValueChange = { spo2 = it },
                        label = { Text("SpO2 (%)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Assessment & Notes
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
                Text(
                    text = "Diagnosis & Clinical Notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("Diagnosis *") },
                    placeholder = { Text("e.g. Acute Viral Bronchitis, Type 2 Diabetes") },
                    modifier = Modifier.fillMaxWidth().testTag("case_paper_diagnosis_input")
                )

                OutlinedTextField(
                    value = doctorNotes,
                    onValueChange = { doctorNotes = it },
                    label = { Text("Doctor's Notes & Advice") },
                    placeholder = { Text("Observations, rest instructions, dietary guidance") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prescription Section (Multiple Medicine Entries)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prescription (Rx)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Button(
                        onClick = {
                            prescriptionItems.add(PrescriptionItem())
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.testTag("add_medicine_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Medicine")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                prescriptionItems.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Medicine #${index + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                if (prescriptionItems.size > 1) {
                                    IconButton(
                                        onClick = { prescriptionItems.removeAt(index) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove medicine",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Medicine Name & Dosage
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = item.medicineName,
                                    onValueChange = { newName ->
                                        prescriptionItems[index] = item.copy(medicineName = newName)
                                    },
                                    label = { Text("Medicine Name") },
                                    placeholder = { Text("e.g. Amoxicillin") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1.4f)
                                )

                                OutlinedTextField(
                                    value = item.dosage,
                                    onValueChange = { newDosage ->
                                        prescriptionItems[index] = item.copy(dosage = newDosage)
                                    },
                                    label = { Text("Dosage") },
                                    placeholder = { Text("500mg") },
                                    singleLine = true,
                                    modifier = Modifier.weight(0.8f)
                                )
                            }

                            // Frequency, Duration, Instructions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = item.frequency,
                                    onValueChange = { newFreq ->
                                        prescriptionItems[index] = item.copy(frequency = newFreq)
                                    },
                                    label = { Text("Frequency") },
                                    placeholder = { Text("BD / TDS / OD") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = item.duration,
                                    onValueChange = { newDur ->
                                        prescriptionItems[index] = item.copy(duration = newDur)
                                    },
                                    label = { Text("Duration") },
                                    placeholder = { Text("5 days") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = item.instructions,
                                onValueChange = { newInst ->
                                    prescriptionItems[index] = item.copy(instructions = newInst)
                                },
                                label = { Text("Special Instructions") },
                                placeholder = { Text("e.g. Take after food with warm water") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons: Save Case Paper, Print Case Paper, Download PDF
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val p = selectedPatient
                    val d = selectedDoctor
                    if (p == null) {
                        viewModel.showMessage("Please select a patient first.")
                        return@Button
                    }
                    if (d == null) {
                        viewModel.showMessage("Please select a doctor.")
                        return@Button
                    }

                    viewModel.saveCasePaper(
                        patientId = p.patientId,
                        doctorId = d.doctorId,
                        doctorName = d.name,
                        visitDate = visitDate,
                        chiefComplaint = chiefComplaint,
                        symptoms = symptoms,
                        medicalHistory = medicalHistory,
                        allergies = allergies,
                        medications = currentMedications,
                        bp = bp,
                        pulse = pulse,
                        temp = temp,
                        weight = weight,
                        height = height,
                        spo2 = spo2,
                        notes = doctorNotes,
                        diagnosis = diagnosis,
                        prescriptionList = prescriptionItems.toList()
                    ) { saved ->
                        onCasePaperSaved(saved.patientId)
                    }
                },
                modifier = Modifier
                    .weight(1.3f)
                    .height(50.dp)
                    .testTag("save_case_paper_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Case Paper", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    previewDialogPaper = buildTransientCasePaper()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("print_preview_case_paper_button")
            ) {
                Icon(Icons.Default.LocalPrintshop, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Print / PDF")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Print / PDF Preview Dialog
    if (previewDialogPaper != null) {
        PrintCasePaperDialog(
            casePaper = previewDialogPaper!!,
            patient = selectedPatient,
            onDismiss = { previewDialogPaper = null },
            onPrintSuccessNotice = { msg ->
                viewModel.showMessage(msg)
            }
        )
    }
}
