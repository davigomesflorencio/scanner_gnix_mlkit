package com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf

import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
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
    parentHeight: Dp
) {
    val density = LocalDensity.current

    val normalized = normalizePath(signatureData.path.asAndroidPath())
    val bounds = RectF()
    normalized.computeBounds(bounds, true)

    val signatureWidthPx = bounds.width()
    val signatureHeightPx = bounds.height()

    // Ensure dimensions are not zero or negative for modifiers
    val signatureWidthDp = with(density) { signatureWidthPx.toDp().coerceAtLeast(1.dp) }
    val signatureHeightDp = with(density) { signatureHeightPx.toDp().coerceAtLeast(1.dp) }

    // Initial centering of the signature box within the parent page
    val initialOffsetX = with(density) { (parentWidth.toPx() - signatureWidthPx) / 2f }
    val initialOffsetY = with(density) { (parentHeight.toPx() - signatureHeightPx) / 2f }

    var offsetX by remember { mutableFloatStateOf(initialOffsetX) }
    var offsetY by remember { mutableFloatStateOf(initialOffsetY) }

    LaunchedEffect(signatureWidthPx, signatureHeightPx, parentWidth, parentHeight) {
        Log.d("DraggableAssignature", "bounds: $bounds")
        Log.d("DraggableAssignature", "signatureWidthPx: $signatureWidthPx, signatureHeightPx: $signatureHeightPx")
        Log.d("DraggableAssignature", "signatureWidthDp: $signatureWidthDp, signatureHeightDp: $signatureHeightDp")
        Log.d("DraggableAssignature", "parentWidth (Dp): $parentWidth, parentHeight (Dp): $parentHeight")
        Log.d("DraggableAssignature", "initialOffsetX: $initialOffsetX, initialOffsetY: $initialOffsetY")
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .width(signatureWidthDp)
            .height(signatureHeightDp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newOffsetX = with(density) { (offsetX + dragAmount.x).coerceIn(0f, parentWidth.toPx() - signatureWidthPx) }
                    val newOffsetY = with(density) { (offsetY + dragAmount.y).coerceIn(0f, parentHeight.toPx() - signatureHeightPx) }
                    if (newOffsetX != offsetX || newOffsetY != offsetY) {
                        offsetX = newOffsetX
                        offsetY = newOffsetY
                    }
                }
            }
            .background(Color.Red.copy(alpha = 0.2f)) // Semi-transparent red background for debugging boundaries
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Assuming normalizePath translates the path so its top-left is at (0,0)
            // relative to its bounds, we can draw it directly.
            drawPath(
                path = normalized.asComposePath(),
                color = Color.Black,
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            // Draw bounding box for visual indication
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawRect(
                color = Color.Blue.copy(alpha = 0.4f),
                // Draw from (0,0) of this Canvas, as the Box is sized to the bounds.
                topLeft = Offset(0f, 0f),
                size = Size(signatureWidthPx, signatureHeightPx),
                style = Stroke(width = 2f, pathEffect = pathEffect)
            )
        }
    }
}