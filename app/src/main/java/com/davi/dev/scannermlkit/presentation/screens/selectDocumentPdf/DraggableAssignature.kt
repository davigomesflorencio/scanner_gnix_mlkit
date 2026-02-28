package com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf

import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.domain.extensions.normalizePath
import com.davi.dev.scannermlkit.domain.model.SignatureData
import kotlin.math.roundToInt


@Composable
fun DraggableAssignature(
    signatureData: SignatureData,
    parentWidth: Dp,
    parentHeight: Dp,
    onDelete: () -> Unit,
    onUpdate: (SignatureData) -> Unit
) {
    val density = LocalDensity.current

    val normalized = remember(signatureData.path) {
        normalizePath(signatureData.path.asAndroidPath())
    }
    val bounds = remember(normalized) {
        RectF().apply { normalized.computeBounds(this, true) }
    }

    val signatureWidthPx = bounds.width()
    val signatureHeightPx = bounds.height()

    // Estados locais para fluidez, sincronizados com o model
    var offsetX by remember { mutableFloatStateOf(signatureData.offsetX) }
    var offsetY by remember { mutableFloatStateOf(signatureData.offsetY) }
    var scaleFactor by remember { mutableFloatStateOf(signatureData.scale) }

    // Centraliza apenas se for uma assinatura nova (offset zerado)
    LaunchedEffect(key1 = signatureWidthPx) {
        if (offsetX == 0f && offsetY == 0f) {
            offsetX = (with(density) { parentWidth.toPx() } - signatureWidthPx * scaleFactor) / 2f
            offsetY = (with(density) { parentHeight.toPx() } - signatureHeightPx * scaleFactor) / 2f
            onUpdate(signatureData.copy(offsetX = offsetX, offsetY = offsetY))
        }
    }

    // Dimensões visuais calculadas dinamicamente
    val currentWidthPx = signatureWidthPx * scaleFactor
    val currentHeightPx = signatureHeightPx * scaleFactor

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(
                width = with(density) { currentWidthPx.toDp().coerceAtLeast(1.dp) },
                height = with(density) { currentHeightPx.toDp().coerceAtLeast(1.dp) }
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Usamos as dimensões atuais para garantir que o arraste respeite os limites
                        val currentW = signatureWidthPx * scaleFactor
                        val currentH = signatureHeightPx * scaleFactor
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, parentWidth.toPx() - currentW)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, parentHeight.toPx() - currentH)
                    },
                    onDragEnd = {
                        onUpdate(signatureData.copy(offsetX = offsetX, offsetY = offsetY, scale = scaleFactor))
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Aplicamos o scale apenas no desenho do path para manter os botões com tamanho fixo
            scale(scaleFactor, pivot = Offset.Zero) {
                drawPath(
                    path = normalized.asComposePath(),
                    color = Color.Black,
                    style = Stroke(width = 5f, cap = StrokeCap.Round)
                )
            }

            // Linha tracejada interna opcional para feedback
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawRect(
                color = Color.Blue.copy(alpha = 0.1f),
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = 1.dp.toPx(), pathEffect = pathEffect)
            )
        }

        // Botão Deletar (Topo-Esquerda)
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset((-12).dp, (-12).dp)
                .background(Color.Red, CircleShape)
                .size(24.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        // Botão Expandir (Base-Direita)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(12.dp, 12.dp)
                .background(Color.Blue, CircleShape)
                .size(24.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()

                            // Cálculo da nova escala baseado na posição do arraste em relação ao tamanho original
                            val currentW = signatureWidthPx * scaleFactor
                            val currentH = signatureHeightPx * scaleFactor
                            
                            val newScaleX = (currentW + dragAmount.x) / signatureWidthPx
                            val newScaleY = (currentH + dragAmount.y) / signatureHeightPx
                            
                            // Média dos eixos para um ajuste diagonal que segue o dedo
                            val newScale = ((newScaleX + newScaleY) / 2f).coerceIn(0.2f, 5f)

                            // Verifica se a nova escala cabe na página
                            val maxScaleX = (parentWidth.toPx() - offsetX) / signatureWidthPx
                            val maxScaleY = (parentHeight.toPx() - offsetY) / signatureHeightPx
                            val maxPossibleScale = minOf(maxScaleX, maxScaleY)

                            scaleFactor = newScale.coerceAtMost(maxPossibleScale)
                        },
                        onDragEnd = {
                            onUpdate(signatureData.copy(offsetX = offsetX, offsetY = offsetY, scale = scaleFactor))
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Expand,
                contentDescription = "Expand",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
