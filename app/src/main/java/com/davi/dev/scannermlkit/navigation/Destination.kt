package com.davi.dev.scannermlkit.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

enum class Destination(
    val route: NavKey,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    SONGS( ListDocument, "Documentos", Icons.Default.List, "Documentos"),
    ALBUM(ScanPdf, "Scanner", Icons.Default.Share, "Scanner"),
}