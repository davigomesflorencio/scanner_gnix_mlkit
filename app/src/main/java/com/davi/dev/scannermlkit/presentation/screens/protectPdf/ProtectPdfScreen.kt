package com.davi.dev.scannermlkit.presentation.screens.protectPdf

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.components.PDFSimpleItem
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ProtectPdfViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectPdfScreen(
    protectPdfViewModel: ProtectPdfViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedPdfUri by protectPdfViewModel.selectedPdfUri.collectAsState()

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMatchError by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            protectPdfViewModel.selectPdf(it)
        }
    }

    LaunchedEffect(Unit) {
        protectPdfViewModel.splitStatus.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Protect PDF",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CustomDivider()

        Button(
            onClick = { launcher.launch("application/pdf") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FileUpload, contentDescription = null)
                Text(text = "Selecionar Arquivo PDF", modifier = Modifier.padding(start = 8.dp))
            }
        }

        if (selectedPdfUri != null) {

            Spacer(Modifier.height(8.dp))

            PDFSimpleItem(selectedPdfUri!!)

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordMatchError = false // Reseta o erro quando o usuário digita
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Ícone de Cadeado") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordMatchError = false // Reseta o erro quando o usuário digita
                },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Ícone de Cadeado") },
                modifier = Modifier.fillMaxWidth(),
                isError = passwordMatchError
            )

            if (passwordMatchError) {
                Text(
                    text = "As senhas não coincidem",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                enabled = password == confirmPassword && password.isNotBlank(),
                onClick = {
                    if (password == confirmPassword && password.isNotBlank()) {
                        // Aqui você chamaria a lógica para proteger o PDF
                        // Por exemplo: onProtectClick(password)
                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

                        protectPdfViewModel.protectPdf("protect_${timestamp}.pdf", password, confirmPassword)
                        passwordMatchError = false
                        // Limpar os campos após o sucesso, se desejar
                        // password = ""
                        // confirmPassword = ""
                    } else {
                        passwordMatchError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Protect PDF")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProtectPdfScreenPreview() {
    ProtectPdfScreen()
}