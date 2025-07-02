package com.example.astroyoumat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astroyoumat.presentation.screens.AppointmentsListScreen
import com.example.astroyoumat.presentation.viewModel.CounselorViewModel
import com.example.astroyoumat.ui.theme.AstroYouMatTheme
import com.example.astroyoumat.data.remote.sendNotification //  Import notification sender
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AstroYouMatTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val counselorViewModel: CounselorViewModel = viewModel()

                    AppointmentsListScreen(
                        viewModel = counselorViewModel,
                        counselorId = "counselor1"
                    )
                }
            }
        }

        // Test FCM Push (replace with real user UID)
        val testUserId = "QdcyJTz2dPMKcEdE8FPcngK5sMv1"//  Replace with real UID from Firestore "users" collection

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(testUserId)
            .get()
            .addOnSuccessListener { doc ->
                val token = doc.getString("fcmToken")
                if (!token.isNullOrEmpty()) {
                    sendNotification.sendPushNotification(
                        context = this,
                        token = token,
                        title = "Test FCM",
                        message = "This is a test push from counselor!"
                    )
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}
