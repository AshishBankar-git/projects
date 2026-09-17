package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AppointmentBookingScreen
import com.example.ui.screens.AppointmentManagementScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DigitalCasePaperScreen
import com.example.ui.screens.DoctorManagementScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PatientListScreen
import com.example.ui.screens.PatientProfileScreen
import com.example.ui.screens.PatientRegisterScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ClinicViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Login : Screen("login", "Login")
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Patients : Screen("patients", "Patients", Icons.Default.People)
    object Appointments : Screen("appointments", "Appointments", Icons.Default.CalendarMonth)
    object Doctors : Screen("doctors", "Doctors", Icons.Default.LocalHospital)
    object Reports : Screen("reports", "Reports", Icons.Default.BarChart)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    // Sub-screens
    object NewPatient : Screen("new_patient", "New Patient")
    object PatientProfile : Screen("patient_profile/{patientId}", "Patient Profile") {
        fun createRoute(patientId: String) = "patient_profile/$patientId"
    }
    object DigitalCasePaper : Screen("digital_case_paper?patientId={patientId}", "Digital Case Paper") {
        fun createRoute(patientId: String? = null) = if (patientId != null) "digital_case_paper?patientId=$patientId" else "digital_case_paper"
    }
    object BookAppointment : Screen("book_appointment?patientId={patientId}", "Book Appointment") {
        fun createRoute(patientId: String? = null) = if (patientId != null) "book_appointment?patientId=$patientId" else "book_appointment"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ClinicApp()
            }
        }
    }
}

@Composable
fun ClinicApp() {
    val navController = rememberNavController()
    val viewModel: ClinicViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbarMessage()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Patients,
        Screen.Appointments,
        Screen.Doctors,
        Screen.Reports,
        Screen.Settings
    )

    // Show bottom bar on primary screens when logged in
    val showBottomBar = isLoggedIn && bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(it, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title, fontSize = 11.sp) },
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.testTag("nav_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Dashboard Screen
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToNewPatient = { navController.navigate(Screen.NewPatient.route) },
                    onNavigateToBookAppointment = { navController.navigate(Screen.BookAppointment.createRoute()) },
                    onNavigateToSearchPatient = { navController.navigate(Screen.Patients.route) },
                    onNavigateToPatientProfile = { patientId ->
                        viewModel.selectPatient(patientId)
                        navController.navigate(Screen.PatientProfile.createRoute(patientId))
                    }
                )
            }

            // Patients List / Search Screen
            composable(Screen.Patients.route) {
                PatientListScreen(
                    viewModel = viewModel,
                    onNavigateToNewPatient = { navController.navigate(Screen.NewPatient.route) },
                    onNavigateToPatientProfile = { patientId ->
                        viewModel.selectPatient(patientId)
                        navController.navigate(Screen.PatientProfile.createRoute(patientId))
                    },
                    onNavigateToNewCasePaper = { patientId ->
                        viewModel.selectPatient(patientId)
                        navController.navigate(Screen.DigitalCasePaper.createRoute(patientId))
                    },
                    onNavigateToBookAppointment = { patientId ->
                        navController.navigate(Screen.BookAppointment.createRoute(patientId))
                    }
                )
            }

            // Register New Patient Screen
            composable(Screen.NewPatient.route) {
                PatientRegisterScreen(
                    viewModel = viewModel,
                    onNavigateToCreateCasePaper = { patientId ->
                        viewModel.selectPatient(patientId)
                        navController.navigate(Screen.DigitalCasePaper.createRoute(patientId))
                    },
                    onNavigateToBookAppointment = { patientId ->
                        navController.navigate(Screen.BookAppointment.createRoute(patientId))
                    },
                    onNavigateToPatientProfile = { patientId ->
                        viewModel.selectPatient(patientId)
                        navController.navigate(Screen.PatientProfile.createRoute(patientId))
                    }
                )
            }

            // Patient Profile Screen
            composable(
                route = Screen.PatientProfile.route,
                arguments = listOf(navArgument("patientId") { type = NavType.StringType })
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                LaunchedEffect(patientId) {
                    viewModel.selectPatient(patientId)
                }
                PatientProfileScreen(
                    patientId = patientId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToNewCasePaper = { pId ->
                        navController.navigate(Screen.DigitalCasePaper.createRoute(pId))
                    },
                    onNavigateToBookAppointment = { pId ->
                        navController.navigate(Screen.BookAppointment.createRoute(pId))
                    }
                )
            }

            // Digital Case Paper Entry Screen
            composable(
                route = Screen.DigitalCasePaper.route,
                arguments = listOf(
                    navArgument("patientId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")
                DigitalCasePaperScreen(
                    patientId = patientId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onCasePaperSaved = { pId ->
                        navController.navigate(Screen.PatientProfile.createRoute(pId)) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    }
                )
            }

            // Book Appointment Screen
            composable(
                route = Screen.BookAppointment.route,
                arguments = listOf(
                    navArgument("patientId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")
                AppointmentBookingScreen(
                    preselectedPatientId = patientId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAppointmentConfirmed = {
                        navController.navigate(Screen.Appointments.route) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    }
                )
            }

            // Appointments Queue / Management Screen
            composable(Screen.Appointments.route) {
                AppointmentManagementScreen(
                    viewModel = viewModel,
                    onNavigateToBookAppointment = { navController.navigate(Screen.BookAppointment.createRoute()) },
                    onNavigateToPatientProfile = { patientId ->
                        viewModel.selectPatient(patientId)
                        navController.navigate(Screen.PatientProfile.createRoute(patientId))
                    }
                )
            }

            // Doctor Management Screen
            composable(Screen.Doctors.route) {
                DoctorManagementScreen(viewModel = viewModel)
            }

            // Reports Screen
            composable(Screen.Reports.route) {
                ReportsScreen(viewModel = viewModel)
            }

            // Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
