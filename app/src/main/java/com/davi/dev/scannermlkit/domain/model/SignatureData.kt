package com.davi.dev.scannermlkit.domain.model

import androidx.compose.ui.graphics.Path

// Data class to hold signature information with transformations
data class SignatureData(
    val path: Path,
    val width: Float, // Original width of the path
    val height: Float, // Original height of the path
    val scale: Float = 1f, // User-applied scale
    val offsetX: Float = 0f, // User-applied offset X
    val offsetY: Float = 0f  // User-applied offset Y
)
