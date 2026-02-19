package com.davi.dev.scannermlkit.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

enum class Destination(
    val route: NavKey,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    DOCUMENTS(ListDocument, "Documentos", Icons.AutoMirrored.Default.List, "Documentos"),
    SCANNER(ScanPdf, "Scanner", Icons.Default.Share, "Scanner"),
}