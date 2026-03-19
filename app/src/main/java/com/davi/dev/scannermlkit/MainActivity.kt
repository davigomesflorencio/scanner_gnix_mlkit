package com.davi.dev.scannermlkit

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davi.dev.scannermlkit.data.repository.UserPreferencesRepositoryImpl
import com.davi.dev.scannermlkit.presentation.navigation.AppNavHost
import com.davi.dev.scannermlkit.presentation.screens.viewModel.AccountViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.AuthViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.CompressPdfViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.HomeViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.MergeDocumentViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ProtectPdfViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerQrCodeViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.SplitPdfViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.WatermarkPdfViewModel
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val supabase = run {
    val url = BuildConfig.supabaseUrl
    val key = BuildConfig.supabaseKey
    require(url.isNotEmpty()) { "SUPABASE_URL is missing from local.properties" }
    require(key.isNotEmpty()) { "SUPABASE_KEY is missing from local.properties" }
    createSupabaseClient(supabaseUrl = url, supabaseKey = key) {
        install(Auth)
    }
}

class MainActivity : ComponentActivity() {

    private val userPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(dataStore)
    }

    val scannerQrCodeViewModel: ScannerQrCodeViewModel by viewModels()
    val scannerDocumentViewModel: ScannerDocumentViewModel by viewModels()
    val mergeDocumentViewModel: MergeDocumentViewModel by viewModels()
    val splitPdfViewModel: SplitPdfViewModel by viewModels()
    val protectPdfViewModel: ProtectPdfViewModel by viewModels()
    val compressPdfViewModel: CompressPdfViewModel by viewModels()
    val watermarkPdfViewModel: WatermarkPdfViewModel by viewModels()
    val homeViewModel: HomeViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()

    val accountViewModel: AccountViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AccountViewModel(userPreferencesRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScannermlkitTheme(
                themeMode = accountViewModel.themeMode,
                selectedColor = accountViewModel.selectedColor.color
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AppNavHost(
                        scannerQrCodeViewModel = scannerQrCodeViewModel,
                        scannerDocumentViewModel = scannerDocumentViewModel,
                        mergeDocumentViewModel = mergeDocumentViewModel,
                        splitPdfViewModel = splitPdfViewModel,
                        protectPdfViewModel = protectPdfViewModel,
                        compressPdfViewModel = compressPdfViewModel,
                        watermarkPdfViewModel = watermarkPdfViewModel,
                        homeViewModel = homeViewModel,
                        authViewModel = authViewModel,
                        accountViewModel = accountViewModel
                    )
                }
            }
        }
    }
}
