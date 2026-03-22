package com.davi.dev.scannermlkit.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.screens.viewModel.HomeViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home(
    backStack: NavBackStack<NavKey>,
    homeViewModel: HomeViewModel = viewModel(),
    scannerDocumentViewModel: ScannerDocumentViewModel = viewModel()
) {
    val context = LocalContext.current
    val pdfFiles by homeViewModel.filteredFiles.collectAsState()
    val isSearchActive by homeViewModel.isSearchActive.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val hasShownTooltip by homeViewModel.hasShownTooltip.collectAsState()

    val scope = rememberCoroutineScope()
    val functionsTooltipState = rememberTooltipState(isPersistent = true)
    val recentsTooltipState = rememberTooltipState(isPersistent = true)

    LaunchedEffect(Unit) {
        homeViewModel.loadFiles()
    }

    LaunchedEffect(hasShownTooltip) {
        if (!hasShownTooltip) {
            delay(1000)
            functionsTooltipState.show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        UpdateChecker(context = context)

        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = {
                RichTooltip(
                    title = { Text("🚀 App Functions") },
                    caretShape = TooltipDefaults.caretShape(),
                    action = {
                        TextButton(
                            onClick = {
                                functionsTooltipState.dismiss()
                                scope.launch {
                                    recentsTooltipState.show()
                                }
                            }
                        ) {
                            Text("Next")
                        }
                    }
                ) {
                    Text("Scan documents, read QR Codes and handle your PDFs using these tools.")
                }
            },
            onDismissRequest = {
                functionsTooltipState.dismiss()
            },
            state = functionsTooltipState,
        ) {
            FunctionsHomeApp(backStack, scannerDocumentViewModel)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = {
                        RichTooltip(
                            title = { Text("📄 Recent Documents") },
                            caretShape = TooltipDefaults.caretShape(),
                            action = {
                                TextButton(
                                    onClick = {
                                        recentsTooltipState.dismiss()
                                        homeViewModel.markTooltipAsShown()
                                    }
                                ) {
                                    Text("Got it")
                                }
                            }
                        ) {
                            Text("Here you can find the PDFs you've scanned or modified recently.")
                        }
                    },
                    onDismissRequest = {
                        recentsTooltipState.dismiss()
                    },
                    state = recentsTooltipState,
                ) {
                    Text(
                        if (isSearchActive || searchQuery.isNotEmpty()) "Search results" else "Recents",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            }
            item {
                CustomDivider()
            }
            itemsIndexed(pdfFiles.take(10)) { index, file ->
                PDFItem(
                    homeViewModel = homeViewModel,
                    navBackStack = backStack,
                    file = file,
                )
            }
        }
    }
}