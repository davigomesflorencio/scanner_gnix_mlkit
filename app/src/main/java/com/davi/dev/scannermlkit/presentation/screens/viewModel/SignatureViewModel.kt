package com.davi.dev.scannermlkit.presentation.screens.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.davi.dev.scannermlkit.domain.model.SignatureData

class SignatureViewModel : ViewModel() {

    var currentPath by mutableStateOf(Path())
        private set

    var currentSignatureData by mutableStateOf<SignatureData?>(null)
        private set

    var selectedPathColor by mutableStateOf(Color.Black)
        private set

    val recentSignatures = mutableStateListOf<SignatureData>()

    var canvasWidth by mutableFloatStateOf(0f)
        private set
    var canvasHeight by mutableFloatStateOf(0f)
        private set

    fun setCanvasSize(width: Float, height: Float) {
        canvasWidth = width
        canvasHeight = height
    }

    fun startNewPath(point: Offset) {
        currentPath.moveTo(point.x, point.y)
        currentPath = Path().apply { addPath(currentPath) } // Trigger recomposition
        currentSignatureData = null // Reset current signature data when starting a new path
    }

    fun addPointToPath(point: Offset) {
        currentPath.lineTo(point.x, point.y)
        currentPath = Path().apply { addPath(currentPath) }
    }

    fun clearSignature() {
        currentPath = Path()
        currentSignatureData = null
    }

    fun saveSignature(scale: Float = 1f, offsetX: Float = 0f, offsetY: Float = 0f) {
        if (!currentPath.isEmpty) {
            // Create a copy of the path to store it, as currentPath might be modified later
            val savedPath = Path().apply { addPath(currentPath) }

            // Calculate bounds to get original width and height
            val bounds = savedPath.getBounds()
            val signature = SignatureData(
                path = savedPath,
                width = bounds.width,
                height = bounds.height,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                color = selectedPathColor
            )

            // Add to recent signatures, keeping only the last 3
            if (recentSignatures.size >= 3) {
                recentSignatures.removeAt(0)
            }
            recentSignatures.add(signature)

            clearSignature() // Clear the current drawing after saving
        }
    }

    fun loadSignature(signatureData: SignatureData) {
        currentPath = Path().apply { addPath(signatureData.path) }
        selectedPathColor = signatureData.color
        currentSignatureData = signatureData // Store the full data for transformations
    }

    fun setSelectedColor(color: Color) {
        selectedPathColor = color
    }
}
