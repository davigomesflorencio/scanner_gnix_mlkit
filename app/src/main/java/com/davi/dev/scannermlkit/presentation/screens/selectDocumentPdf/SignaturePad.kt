package com.davi.dev.scannermlkit.presentation.screens.documentPdf

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.domain.model.SignatureData


@Composable
fun SignaturePad(
    currentPageIndex: Int, // Added to pass the current page index
    onDone: (SignatureData) -> Unit,
    onCancel: () -> Unit
) {
    var path by remember { mutableStateOf(Path()) }
    var currentScale by remember { mutableStateOf(1f) }
    var currentOffset by remember { mutableStateOf(Offset.Zero) }

    // Keep track of the original path bounds to use as a reference
    var originalPathBounds by remember { mutableStateOf(Rect.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .graphicsLayer(
                    scaleX = currentScale,
                    scaleY = currentScale,
                    translationX = currentOffset.x,
                    translationY = currentOffset.y
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            path.moveTo(offset.x, offset.y)
                            path = Path().apply { addPath(path) } // Create a copy to trigger recomposition
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            path.lineTo(change.position.x, change.position.y)
                            originalPathBounds = path.getBounds()
                            path = Path().apply { addPath(path) } // Create a copy to trigger recomposition
                        }
                    )
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        currentScale *= zoom
                        currentOffset += pan
                    }
                }
        ) {
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = onCancel,
                colors = IconButtonColors(
                    containerColor = Color(0XFFb6407f),
                    contentColor = Color.White,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancel Assignature")
            }
            IconButton(
                onClick = {
                    path = Path()
                    currentScale = 1f
                    currentOffset = Offset.Zero
                    originalPathBounds = Rect.Zero
                },
                colors = IconButtonColors(
                    containerColor = Color(0XFFb6407f),
                    contentColor = Color.White,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Assignature")
            }
            IconButton(
                onClick = {
                    val signatureData = SignatureData(
                        path = path,
                        width = originalPathBounds.width,
                        height = originalPathBounds.height,
                        scale = currentScale,
                        offsetX = currentOffset.x,
                        offsetY = currentOffset.y
                    )
                    onDone(signatureData)
                },
                colors = IconButtonColors(
                    containerColor = Color(0XFFb6407f),
                    contentColor = Color.White,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = "Done Assignature")
            }
        }
    }
}
