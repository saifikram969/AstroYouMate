package com.example.astroyoumat.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.astroyoumat.presentation.viewModel.CounselorViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppointmentsListScreen(
    viewModel: CounselorViewModel,
    counselorId: String
) {
    val appointments by viewModel.appointments.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var appointmentIdToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.startListening(counselorId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // ✅ Bottom title
        Text(
            text = "Appointments List",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 19.dp)
        )

        LazyColumn {
            items(appointments) { appointment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .combinedClickable(
                            onClick = { },
                            onLongClick = {
                                appointmentIdToDelete = appointment.appointmentId
                                showDialog = true
                            }
                        ),
                    elevation = CardDefaults.cardElevation()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("User: ${appointment.userEmail}")
                        Text("Issue: ${appointment.issue}")
                        Text("Date/Time: ${appointment.dateTime}")
                        Text("Status: ${appointment.status}")

                        if (appointment.status == "pending") {
                            Row {
                                Button(onClick = {
                                    viewModel.updateStatus(
                                        appointment.appointmentId,
                                        "accepted",
                                        appointment.userId
                                    )
                                }) {
                                    Text("Accept")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    viewModel.updateStatus(
                                        appointment.appointmentId,
                                        "rejected",
                                        appointment.userId
                                    )
                                }) {
                                    Text("Reject")
                                }
                            }
                        }
                    }
                }
            }
        }

        // 🔔 Confirmation Dialog
        if (showDialog && appointmentIdToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Delete Appointment") },
                text = { Text("Are you sure you want to delete this appointment?") },
                confirmButton = {
                    TextButton(onClick = {
                        appointmentIdToDelete?.let {
                            viewModel.deleteAppointment(it)
                        }
                        showDialog = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}
