package com.davi.dev.scannermlkit.presentation.screens.signaturepad

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davi.dev.scannermlkit.domain.model.SignatureData
import com.davi.dev.scannermlkit.presentation.screens.viewModel.SignatureViewModel
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme
import kotlin.math.min

@Composable
fun SignaturePadScreen(
    onSignatureConfirmed: (SignatureData) -> Unit,
    viewModel: SignatureViewModel = viewModel()
) {
    val currentPath = viewModel.currentPath
    val selectedColor = viewModel.selectedPathColor
    val recentSignatures = viewModel.recentSignatures

    Column(modifier = Modifier.fillMaxSize()) {
        // Color selection
        ColorSelectionRow(selectedColor = selectedColor) { color ->
            viewModel.setSelectedColor(color)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(Color.White, RoundedCornerShape(30.dp))
                    .pointerInput(Unit) { // KEY CHANGED TO Unit
                        detectDragGestures(
                            onDragStart = { offset ->
                                viewModel.startNewPath(offset)
                            },
                            onDragEnd = { /* Optional: finalize path */ },
                            onDragCancel = { /* Optional: handle cancel */ },
                            onDrag = { change, _ ->
                                change.consume()
                                viewModel.addPointToPath(change.position)
                            }
                        )
                    }
            ) {
                // Set canvas size for ViewModel
                viewModel.setCanvasSize(size.width, size.height)

                val pathForDrawing = viewModel.currentSignatureData?.path ?: currentPath
                val colorForDrawing = viewModel.currentSignatureData?.color ?: selectedColor

                if (!pathForDrawing.isEmpty) {
                    withTransform(transformBlock = {
                        // Apply transformations if a saved signature is loaded
                        viewModel.currentSignatureData?.let { data ->
                            // Calculate scale to fit the original signature within the current canvas aspect ratio
                            val originalWidth = data.width
                            val originalHeight = data.height

                            if (originalWidth > 0 && originalHeight > 0) {
                                val scaleFactorX = size.width / originalWidth
                                val scaleFactorY = size.height / originalHeight
                                val overallScale = min(scaleFactorX, scaleFactorY) * data.scale

                                scale(overallScale, overallScale)

                                // Calculate translation to center the scaled path, considering original offset
                                val scaledOriginalWidth = originalWidth * overallScale
                                val scaledOriginalHeight = originalHeight * overallScale

                                val translateX = (size.width - scaledOriginalWidth) / 2 + data.offsetX * overallScale
                                val translateY = (size.height - scaledOriginalHeight) / 2 + data.offsetY * overallScale

                                translate(translateX, translateY)
                            }
                        }
                    }) {
                        drawPath(
                            path = pathForDrawing,
                            color = colorForDrawing,
                            style = Stroke(
                                width = 8.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { viewModel.clearSignature() }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear")
            }
            Button(onClick = {
                // Pass current scale/offset from ViewModel if available, otherwise defaults
                val currentData = viewModel.currentSignatureData
                viewModel.saveSignature(
                    scale = currentData?.scale ?: 1f,
                    offsetX = currentData?.offsetX ?: 0f,
                    offsetY = currentData?.offsetY ?: 0f
                )
            }) {
                Icon(Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save")
            }
            Button(
                onClick = {
                    val finalSignature = viewModel.currentSignatureData ?: SignatureData(
                        path = currentPath,
                        width = viewModel.canvasWidth,
                        height = viewModel.canvasHeight,
                        color = selectedColor
                    )
                    onSignatureConfirmed(finalSignature)
                },
                enabled = !currentPath.isEmpty || viewModel.currentSignatureData != null
            ) {
//                Icon(Icons.Default.Check, contentDescription = "Confirm")
//                Spacer(modifier = Modifier.width(4.dp))
                Text("Confirm")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Recent signatures
        if (recentSignatures.isNotEmpty()) {
            Text(
                text = "Recent Signatures",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentSignatures) { signature ->
                    SignaturePreview(signature = signature) { loadedSignature ->
                        viewModel.loadSignature(loadedSignature)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColorSelectionRow(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    val colors = listOf(Color.Black, Color.Blue, Color.Red, Color.Green, Color.Magenta, Color.Yellow)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedPolygon(MaterialShapes.Arch).toShape(0))
                    .background(color)
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Composable
fun SignaturePreview(signature: SignatureData, onClick: (SignatureData) -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp, 60.dp) // Fixed size for preview
            .border(1.dp, Color.Gray)
            .background(Color.White)
            .clickable { onClick(signature) },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!signature.path.isEmpty) {
                withTransform(transformBlock = {
                    // Scale the signature path to fit within the preview box
                    val pathBounds = signature.path.getBounds()
                    if (pathBounds.width > 0 && pathBounds.height > 0) {
                        val scaleFactor = min(size.width / pathBounds.width, size.height / pathBounds.height)
                        val finalScale = scaleFactor * 0.1f // Fit with some padding

                        scale(finalScale, finalScale)

                        // Translate to center the path
                        translate(
                            (size.width - pathBounds.width * finalScale) / 2 - pathBounds.left * finalScale,
                            (size.height - pathBounds.height * finalScale) / 2 - pathBounds.top * finalScale
                        )
                    }
                }) {
                    drawPath(
                        path = signature.path,
                        color = signature.color,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignaturePadScreen() {
    ScannermlkitTheme {
        SignaturePadScreen(onSignatureConfirmed = {})
    }
}