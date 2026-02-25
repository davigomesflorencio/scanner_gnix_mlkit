package com.davi.dev.scannermlkit.presentation.screens.scanner

import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.davi.dev.scannermlkit.domain.extensions.normalizePath
import com.davi.dev.scannermlkit.presentation.components.SignatureData
import com.davi.dev.scannermlkit.presentation.components.SignaturePad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativePdfViewer(uri: Uri) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val rendererResource = remember(uri) {
        val pfd = context.contentResolver.openFileDescriptor(uri, "r")
        pfd?.let { PdfRenderer(it) }
    }

    DisposableEffect(rendererResource) {
        onDispose {
            rendererResource?.close()
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentPageIndex by remember { mutableStateOf(0) } // Track the currently viewed page

    // Store signatures per page
    val signaturesOnPage = remember { mutableStateMapOf<Int, MutableList<SignatureData>>() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showBottomSheet = true
            }) {
                Icon(imageVector = Icons.Default.Draw, contentDescription = "Sign PDF")
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val maxWidthPx = with(density) { maxWidth }
            val maxHeightPx = with(density) { maxHeight }

            if (rendererResource != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(rendererResource.pageCount) { pageIdx ->
                        val pageSignatures = signaturesOnPage.getOrDefault(pageIdx, emptyList())
                        PdfPageItem(
                            renderer = rendererResource,
                            pageIndex = pageIdx,
                            signatures = pageSignatures,
                            onPageScrolled = { currentPageIndex = it }, // Update current page when scrolled
                            parentMaxWidthPx = maxWidthPx, // Pass max width constraint
                            parentMaxHeightPx = maxHeightPx // Pass max height constraint
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                ) {
                    Box(modifier = Modifier.height(300.dp)) {
                        SignaturePad(
                            currentPageIndex = currentPageIndex, // Pass the current page index
                            onDone = { signatureData ->
                                val pageIndex = currentPageIndex
                                val pageSignatures = signaturesOnPage.getOrPut(pageIndex) { mutableListOf() }
                                pageSignatures.add(signatureData)
                                signaturesOnPage[pageIndex] = pageSignatures

                                scope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                            },
                            onCancel = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PdfPageItem(
    renderer: PdfRenderer,
    pageIndex: Int,
    signatures: List<SignatureData>,
    onPageScrolled: (Int) -> Unit, // Callback to notify when page changes due to scroll
    parentMaxWidthPx: Dp, // Max width constraint from parent
    parentMaxHeightPx: Dp // Max height constraint from parent
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val pageBitmapWidthPx = remember { mutableStateOf(0f) } // Store actual width in pixels
    val pageBitmapHeightPx = remember { mutableStateOf(0f) } // Store actual height in pixels
    val density = LocalDensity.current

    LaunchedEffect(pageIndex) {
        withContext(Dispatchers.IO) {
            val page = renderer.openPage(pageIndex)
            // Renderização em alta resolução (3x)
            val scale = 3f
            val bitmapT = createBitmap((page.width * scale).toInt(), (page.height * scale).toInt())
            val matrix = android.graphics.Matrix().apply { postScale(scale, scale) }

            page.render(bitmapT, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            bitmap = bitmapT
            pageBitmapWidthPx.value = page.width.toFloat()
            pageBitmapHeightPx.value = page.height.toFloat()
            page.close()
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .heightIn(min = 300.dp)
            .onGloballyPositioned { coordinates ->
                // Simple check: if the top of the page is near the top of the screen, consider it the current page.
                // This might need refinement for better accuracy with scroll detection.
                val positionY = coordinates.positionInRoot().y
                val screenHeight = coordinates.size.height
                if (positionY < screenHeight * 0.5f && positionY > -coordinates.size.height * 0.5f) {
                    onPageScrolled(pageIndex)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let {
            val pageW = pageBitmapWidthPx.value // Original PDF page width in pixels
            val pageH = pageBitmapHeightPx.value // Original PDF page height in pixels

            // Calculate the displayed width and height of the PDF image in Dp
            val displayedImageWidthDp = parentMaxWidthPx
            val displayedImageHeightDp = with(density) {
                // Calculate displayed height based on aspect ratio and displayedImageWidthDp
                (parentMaxWidthPx.toPx() * (pageH / pageW)).toDp()
            }

            Box(modifier = Modifier.fillMaxWidth()) { // Use fillMaxWidth to respect page aspect ratio
                androidx.compose.foundation.Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Página ${pageIndex + 1}",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                // Draw signatures on this page
                signatures.forEach { sd ->
                    DraggableAssignature(
                        signatureData = sd,
                        parentWidth = displayedImageWidthDp,
                        parentHeight = displayedImageHeightDp
                    )
                }
            }
        }
    } ?: CircularProgressIndicator() // Placeholder while loading
}

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
