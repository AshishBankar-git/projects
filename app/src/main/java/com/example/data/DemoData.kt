package com.example.data

import com.example.data.model.Appointment
import com.example.data.model.CasePaper
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.data.model.PrescriptionItem
import com.example.data.model.PrescriptionParser
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DemoData {

    private fun getFormattedDate(daysOffset: Int = 0): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }

    val sampleDoctors = listOf(
        Doctor(
            id = 1,
            doctorId = "DOC-001",
            name = "Dr. Amit Sharma",
            specialization = "General Physician",
            phone = "+91 98765 43210",
            consultationFee = 500.0,
            availableDays = "Mon, Tue, Wed, Thu, Fri, Sat",
            startTime = "09:00 AM",
            endTime = "01:00 PM",
            appointmentDuration = 15
        ),
        Doctor(
            id = 2,
            doctorId = "DOC-002",
            name = "Dr. Priya Patel",
            specialization = "Pediatrician",
            phone = "+91 98234 56789",
            consultationFee = 600.0,
            availableDays = "Mon, Tue, Wed, Thu, Fri",
            startTime = "10:00 AM",
            endTime = "02:00 PM",
            appointmentDuration = 20
        ),
        Doctor(
            id = 3,
            doctorId = "DOC-003",
            name = "Dr. Rajesh Kulkarni",
            specialization = "Cardiologist",
            phone = "+91 97654 32109",
            consultationFee = 900.0,
            availableDays = "Tue, Thu, Sat",
            startTime = "03:00 PM",
            endTime = "07:00 PM",
            appointmentDuration = 30
        ),
        Doctor(
            id = 4,
            doctorId = "DOC-004",
            name = "Dr. Ananya Sen",
            specialization = "Dermatologist",
            phone = "+91 99887 76655",
            consultationFee = 700.0,
            availableDays = "Mon, Wed, Fri",
            startTime = "11:00 AM",
            endTime = "04:00 PM",
            appointmentDuration = 15
        )
    )

    val samplePatients = listOf(
        Patient(
            id = 1,
            patientId = "PAT-0001",
            fullName = "Rahul Sharma",
            dateOfBirth = "2002-04-12",
            age = 24,
            gender = "Male",
            mobile = "98201 11223",
            email = "rahul.sharma@example.com",
            address = "402 Silver Oak, MG Road, Mumbai",
            bloodGroup = "B+",
            emergencyContact = "Sunil Sharma (Father) - 98201 11220",
            allergies = "Penicillin",
            medicalConditions = "Mild seasonal asthma"
        ),
        Patient(
            id = 2,
            patientId = "PAT-0002",
            fullName = "Sneha Deshmukh",
            dateOfBirth = "1995-08-23",
            age = 31,
            gender = "Female",
            mobile = "98192 33445",
            email = "sneha.deshmukh@example.com",
            address = "Flat 12, Sunrise Apts, Bandra West",
            bloodGroup = "O+",
            emergencyContact = "Rohan Deshmukh (Husband) - 98192 33440",
            allergies = "None reported",
            medicalConditions = "Thyroid disorder"
        ),
        Patient(
            id = 3,
            patientId = "PAT-0003",
            fullName = "Arjun Nair",
            dateOfBirth = "1988-11-05",
            age = 37,
            gender = "Male",
            mobile = "97401 55667",
            email = "arjun.nair@example.com",
            address = "7B Green Glen Layout, Indiranagar",
            bloodGroup = "A+",
            emergencyContact = "Meera Nair (Wife) - 97401 55660",
            allergies = "Sulfa drugs",
            medicalConditions = "Hypertension"
        ),
        Patient(
            id = 4,
            patientId = "PAT-0004",
            fullName = "Kavita Rao",
            dateOfBirth = "2018-06-14",
            age = 8,
            gender = "Female",
            mobile = "99302 77889",
            email = "parent.kavita@example.com",
            address = "Plot 45, Sector 17, Vashi",
            bloodGroup = "AB+",
            emergencyContact = "Deepak Rao (Father) - 99302 77880",
            allergies = "Peanuts",
            medicalConditions = "None"
        ),
        Patient(
            id = 5,
            patientId = "PAT-0005",
            fullName = "Vikram Malhotra",
            dateOfBirth = "1975-02-18",
            age = 51,
            gender = "Male",
            mobile = "98711 22334",
            email = "vikram.m@example.com",
            address = "Villa 8, Palm Meadows, Whitefield",
            bloodGroup = "O-",
            emergencyContact = "Rita Malhotra (Wife) - 98711 22330",
            allergies = "Aspirin",
            medicalConditions = "Type 2 Diabetes, High Cholesterol"
        ),
        Patient(
            id = 6,
            patientId = "PAT-0006",
            fullName = "Meera Joshi",
            dateOfBirth = "1999-10-30",
            age = 26,
            gender = "Female",
            mobile = "98230 44556",
            email = "meera.j@example.com",
            address = "201 Kothrud Heights, Pune",
            bloodGroup = "A-",
            emergencyContact = "Anoop Joshi (Brother) - 98230 44550",
            allergies = "Dust, pollen",
            medicalConditions = "Migraine"
        ),
        Patient(
            id = 7,
            patientId = "PAT-0007",
            fullName = "Sanjay Verma",
            dateOfBirth = "1963-07-09",
            age = 63,
            gender = "Male",
            mobile = "98100 88990",
            email = "sanjay.verma@example.com",
            address = "C-44 Greater Kailash 1, New Delhi",
            bloodGroup = "B+",
            emergencyContact = "Abhishek Verma (Son) - 98100 88991",
            allergies = "None reported",
            medicalConditions = "Arthritis, Mild BP"
        ),
        Patient(
            id = 8,
            patientId = "PAT-0008",
            fullName = "Fatima Khan",
            dateOfBirth = "2000-12-01",
            age = 25,
            gender = "Female",
            mobile = "98901 99887",
            email = "fatima.k@example.com",
            address = "303 Al-Noor Manzil, Byculla",
            bloodGroup = "O+",
            emergencyContact = "Zainab Khan (Mother) - 98901 99880",
            allergies = "Ciprofloxacin",
            medicalConditions = "Polycystic Ovary Syndrome"
        ),
        Patient(
            id = 9,
            patientId = "PAT-0009",
            fullName = "Aditya Chatterjee",
            dateOfBirth = "1992-03-25",
            age = 34,
            gender = "Male",
            mobile = "98310 12345",
            email = "aditya.c@example.com",
            address = "14/2 Lake Gardens, Kolkata",
            bloodGroup = "AB-",
            emergencyContact = "Pooja Chatterjee (Sister) - 98310 12340",
            allergies = "None reported",
            medicalConditions = "Acid reflux"
        ),
        Patient(
            id = 10,
            patientId = "PAT-0010",
            fullName = "Pooja Hegde",
            dateOfBirth = "1997-05-19",
            age = 29,
            gender = "Female",
            mobile = "98800 67890",
            email = "pooja.hegde@example.com",
            address = "105 Malleshwaram 8th Cross, Bengaluru",
            bloodGroup = "B-",
            emergencyContact = "Ramesh Hegde (Father) - 98800 67891",
            allergies = "Latex",
            medicalConditions = "Eczema"
        )
    )

    fun getSampleAppointments(): List<Appointment> {
        val today = getFormattedDate(0)
        val yesterday = getFormattedDate(-1)
        val tomorrow = getFormattedDate(1)
        val nextWeek = getFormattedDate(5)

        return listOf(
            // Today's appointments
            Appointment(
                id = 1,
                appointmentId = "APT-1001",
                patientId = "PAT-0001",
                patientName = "Rahul Sharma",
                doctorId = "DOC-001",
                doctorName = "Dr. Amit Sharma",
                appointmentDate = today,
                appointmentTime = "09:30 AM",
                reason = "Fever and chills for 2 days",
                status = "Waiting"
            ),
            Appointment(
                id = 2,
                appointmentId = "APT-1002",
                patientId = "PAT-0002",
                patientName = "Sneha Deshmukh",
                doctorId = "DOC-004",
                doctorName = "Dr. Ananya Sen",
                appointmentDate = today,
                appointmentTime = "11:15 AM",
                reason = "Skin rash consultation",
                status = "Confirmed"
            ),
            Appointment(
                id = 3,
                appointmentId = "APT-1003",
                patientId = "PAT-0004",
                patientName = "Kavita Rao",
                doctorId = "DOC-002",
                doctorName = "Dr. Priya Patel",
                appointmentDate = today,
                appointmentTime = "10:20 AM",
                reason = "Routine child immunization review",
                status = "Completed"
            ),
            Appointment(
                id = 4,
                appointmentId = "APT-1004",
                patientId = "PAT-0005",
                patientName = "Vikram Malhotra",
                doctorId = "DOC-003",
                doctorName = "Dr. Rajesh Kulkarni",
                appointmentDate = today,
                appointmentTime = "03:30 PM",
                reason = "Cardio follow-up & BP check",
                status = "Confirmed"
            ),
            Appointment(
                id = 5,
                appointmentId = "APT-1005",
                patientId = "PAT-0006",
                patientName = "Meera Joshi",
                doctorId = "DOC-001",
                doctorName = "Dr. Amit Sharma",
                appointmentDate = today,
                appointmentTime = "12:00 PM",
                reason = "Severe headache and nausea",
                status = "Waiting"
            ),
            // Upcoming appointments
            Appointment(
                id = 6,
                appointmentId = "APT-1006",
                patientId = "PAT-0003",
                patientName = "Arjun Nair",
                doctorId = "DOC-001",
                doctorName = "Dr. Amit Sharma",
                appointmentDate = tomorrow,
                appointmentTime = "10:00 AM",
                reason = "Regular health checkup",
                status = "Confirmed"
            ),
            Appointment(
                id = 7,
                appointmentId = "APT-1007",
                patientId = "PAT-0008",
                patientName = "Fatima Khan",
                doctorId = "DOC-004",
                doctorName = "Dr. Ananya Sen",
                appointmentDate = tomorrow,
                appointmentTime = "11:45 AM",
                reason = "Acne treatment follow up",
                status = "Confirmed"
            ),
            Appointment(
                id = 8,
                appointmentId = "APT-1008",
                patientId = "PAT-0009",
                patientName = "Aditya Chatterjee",
                doctorId = "DOC-001",
                doctorName = "Dr. Amit Sharma",
                appointmentDate = nextWeek,
                appointmentTime = "10:30 AM",
                reason = "Gastric pain evaluation",
                status = "Confirmed"
            ),
            // Completed appointments
            Appointment(
                id = 9,
                appointmentId = "APT-1009",
                patientId = "PAT-0007",
                patientName = "Sanjay Verma",
                doctorId = "DOC-003",
                doctorName = "Dr. Rajesh Kulkarni",
                appointmentDate = yesterday,
                appointmentTime = "04:00 PM",
                reason = "Chest discomfort follow-up",
                status = "Completed"
            ),
            // Cancelled appointments
            Appointment(
                id = 10,
                appointmentId = "APT-1010",
                patientId = "PAT-0010",
                patientName = "Pooja Hegde",
                doctorId = "DOC-004",
                doctorName = "Dr. Ananya Sen",
                appointmentDate = today,
                appointmentTime = "01:30 PM",
                reason = "Cancelled by patient due to travel",
                status = "Cancelled"
            )
        )
    }

    fun getSampleCasePapers(): List<CasePaper> {
        val today = getFormattedDate(0)
        val yesterday = getFormattedDate(-1)
        val lastMonth = getFormattedDate(-25)

        val rx1 = listOf(
            PrescriptionItem("Tab Paracetamol", "650mg", "TDS (3 times/day)", "3 days", "Take after food"),
            PrescriptionItem("Tab Cetirizine", "10mg", "OD (Night)", "5 days", "May cause drowsiness"),
            PrescriptionItem("Syr Ambroxol", "10ml", "BD (2 times/day)", "5 days", "Shake well before use")
        )

        val rx2 = listOf(
            PrescriptionItem("Tab Telmisartan", "40mg", "OD (Morning)", "30 days", "Take empty stomach"),
            PrescriptionItem("Tab Atorvastatin", "10mg", "OD (Night)", "30 days", "Post dinner"),
            PrescriptionItem("Tab Ecosprin", "75mg", "OD (Lunch)", "30 days", "After lunch")
        )

        val rx3 = listOf(
            PrescriptionItem("Mupirocin Ointment 2%", "Topical", "BD (Twice daily)", "7 days", "Apply thin layer on clean skin"),
            PrescriptionItem("Tab Fexofenadine", "120mg", "OD", "10 days", "Before bedtime")
        )

        return listOf(
            CasePaper(
                id = 1,
                casePaperId = "CP-1001",
                patientId = "PAT-0001",
                doctorId = "DOC-001",
                doctorName = "Dr. Amit Sharma",
                visitDate = today,
                chiefComplaint = "High fever, body ache, and mild dry cough for the last 48 hours.",
                symptoms = "Chills, fatigue, throat soreness, headache.",
                medicalHistory = "Mild seasonal asthma. No prior hospitalizations.",
                allergies = "Penicillin (rash).",
                medications = "Salbutamol inhaler PRN.",
                bloodPressure = "118/76 mmHg",
                pulse = "88 bpm",
                temperature = "101.4 °F",
                weight = "68 kg",
                height = "175 cm",
                spo2 = "98%",
                doctorNotes = "Throat mildly congested. Lungs clear to auscultation bilaterally. Hydration advised.",
                diagnosis = "Acute Upper Respiratory Tract Infection (Viral Pharyngitis)",
                prescriptionJson = PrescriptionParser.serialize(rx1)
            ),
            CasePaper(
                id = 2,
                casePaperId = "CP-1002",
                patientId = "PAT-0005",
                doctorId = "DOC-003",
                doctorName = "Dr. Rajesh Kulkarni",
                visitDate = lastMonth,
                chiefComplaint = "Routine hypertension and cholesterol review.",
                symptoms = "Occasional morning dizziness, mild fatigue.",
                medicalHistory = "Type 2 Diabetes mellitus for 6 years, Stage 1 essential hypertension.",
                allergies = "Aspirin (triggers asthma).",
                medications = "Metformin 500mg BD, Telmisartan 40mg.",
                bloodPressure = "136/88 mmHg",
                pulse = "74 bpm",
                temperature = "98.6 °F",
                weight = "82 kg",
                height = "172 cm",
                spo2 = "97%",
                doctorNotes = "S1 and S2 normal, no murmurs. Advised 30 mins brisk walking daily and low sodium diet.",
                diagnosis = "Essential Hypertension & Dyslipidemia - stable",
                prescriptionJson = PrescriptionParser.serialize(rx2)
            ),
            CasePaper(
                id = 3,
                casePaperId = "CP-1003",
                patientId = "PAT-0002",
                doctorId = "DOC-004",
                doctorName = "Dr. Ananya Sen",
                visitDate = yesterday,
                chiefComplaint = "Pruritic erythematous rash on forearms and neck.",
                symptoms = "Intense itching, especially at night.",
                medicalHistory = "Hypothyroidism on Thyronorm 50mcg.",
                allergies = "None reported.",
                medications = "Thyronorm 50mcg daily.",
                bloodPressure = "112/70 mmHg",
                pulse = "72 bpm",
                temperature = "98.2 °F",
                weight = "59 kg",
                height = "162 cm",
                spo2 = "99%",
                doctorNotes = "Well-demarcated eczematous patches. Suspected contact dermatitis from new detergent.",
                diagnosis = "Allergic Contact Dermatitis",
                prescriptionJson = PrescriptionParser.serialize(rx3)
            )
        )
    }
}
