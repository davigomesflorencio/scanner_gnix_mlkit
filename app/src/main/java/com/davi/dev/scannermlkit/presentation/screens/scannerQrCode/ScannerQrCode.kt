package com.davi.dev.scannermlkit.presentation.screens.scannerQrCode

import android.Manifest
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davi.dev.scannermlkit.presentation.components.camera.CameraPreview
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerQrCodeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerQrCode(viewModel: ScannerQrCodeViewModel) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val qrCodeData by viewModel.qrCodeData.collectAsState()
    val boundingBox by viewModel.barcodeBoundingBox.collectAsState()
    val imageDimensions by viewModel.imageDimensions.collectAsState()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        CameraPreview(viewModel)

        // Overlay para desenhar as bordas e posicionar o Card
        boundingBox?.let { rect ->
            imageDimensions?.let { dimensions ->
                // Lógica de escala FILL_CENTER
                val scale = maxOf(
                    screenWidthPx / dimensions.first.toFloat(),
                    screenHeightPx / dimensions.second.toFloat()
                )

                val scaledWidth = dimensions.first * scale
                val scaledHeight = dimensions.second * scale

                val offsetX = (screenWidthPx - scaledWidth) / 2
                val offsetY = (screenHeightPx - scaledHeight) / 2

                val left = rect.left * scale + offsetX
                val top = rect.top * scale + offsetY
                val right = rect.right * scale + offsetX
                val bottom = rect.bottom * scale + offsetY

                // Desenha o retângulo no Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = Color.Green,
                        topLeft = Offset(left, top),
                        size = Size(right - left, bottom - top),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                // Card centralizado exatamente dentro da área da bounding box
                qrCodeData?.let { data ->
                    if (data.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(left.roundToInt(), top.roundToInt())
                                }
                                .size(
                                    width = with(density) { (right - left).toInt().toDp() },
                                    height = with(density) { (bottom - top).toInt().toDp() }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .sizeIn(minWidth = 60.dp, maxWidth = 200.dp)
                                    .clickable {
                                        try {
                                            uriHandler.openUri(data)
                                        } catch (e: Exception) {
                                            // Ignora erro de URI
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Black.copy(alpha = 0.7f)
                                ),
                                shape = MaterialTheme.shapes.small,
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Text(
                                    text = if (data.length < 10) data.padEnd(10) else data,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(6.dp),
                                    textAlign = TextAlign.Center,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Botão para copiar (Bottom End)
        qrCodeData?.let { data ->
            if (data.isNotBlank()) {
                FloatingActionButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(data))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy to clipboard")
                }
            }
        }

        // Mensagem de instrução
        if (qrCodeData.isNullOrBlank()) {
            Text(
                text = "Point to a QR Code",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp),
                fontSize = 16.sp
            )
        }
    }
}