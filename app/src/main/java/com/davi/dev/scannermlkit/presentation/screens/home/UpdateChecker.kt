package com.davi.dev.scannermlkit.presentation.screens.home

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

@Composable
fun UpdateChecker(context: Context) {
    val appUpdateManager = remember { AppUpdateManagerFactory.create(context) }
    var updateInfo by remember { mutableStateOf<AppUpdateInfo?>(null) }

    val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            // Handle the result of the in-app update here if needed
            if (it.resultCode != android.app.Activity.RESULT_OK) {
                Log.d("UpdateChecker", "Update flow failed! Result code: ${it.resultCode}")
            }
        }

    // Checks for update availability on start
    LaunchedEffect(Unit) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                updateInfo = info
            }
        }
    }

    // If an update is available, show the Card
    updateInfo?.let { info ->
        UpdateCard(
            onUpdateClick = {
                // Starts the Google Play update flow
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                )
            }
        )
    }
}

@Composable
fun UpdateCard(onUpdateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("✨ New version available!", style = MaterialTheme.typography.titleMedium)
            Text("Update to get the latest improvements in Scanner MLKIT.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onUpdateClick) {
                Text("Update now")
            }
        }
    }
}

@Preview
@Composable
fun previewUpdateCard() {
    ScannermlkitTheme {
        UpdateCard { }
    }
}