package com.davi.dev.scannermlkit.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.screens.viewModel.HomeViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel
import kotlinx.coroutines.delay

@Composable
fun Home(
    backStack: NavBackStack<NavKey>,
    homeViewModel: HomeViewModel = viewModel(),
    scannerDocumentViewModel: ScannerDocumentViewModel = viewModel()
) {
    val pdfFiles by homeViewModel.filteredFiles.collectAsState()
    val isSearchActive by homeViewModel.isSearchActive.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadFiles()
    }

    val visibleItems = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(10) // Small delay to start the animation
        visibleItems.value = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FunctionsHomeApp(backStack, scannerDocumentViewModel)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Text(
                    if (isSearchActive || searchQuery.isNotEmpty()) "Search results" else "Recents",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
            item {
                CustomDivider()
            }
            itemsIndexed(pdfFiles.take(10)) { index, file ->
                AnimatedVisibility(
                    visible = visibleItems.value,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = index * 100)) +
                            scaleIn(animationSpec = tween(durationMillis = 500, delayMillis = index * 100))
                ) {
                    PDFItem(
                        navBackStack = backStack,
                        file = file
                    )
                }
            }
        }
    }
}
