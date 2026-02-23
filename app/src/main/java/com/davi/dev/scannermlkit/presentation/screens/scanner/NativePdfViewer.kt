package com.davi.dev.scannermlkit.presentation.screens.scanner

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.davi.dev.scannermlkit.presentation.components.SignaturePad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    var signaturePath by remember { mutableStateOf<Path?>(null) }
    var signatureOffset by remember { mutableStateOf(Offset.Zero) }
    var signatureScale by remember { mutableStateOf(1f) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Default.Draw, contentDescription = "Sign PDF")
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (rendererResource != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(rendererResource.pageCount) { pageIndex ->
                        PdfPageItem(rendererResource, pageIndex)
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
                    sheetState = sheetState
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SignaturePad(
                            onDone = { path ->
                                signaturePath = path
                                signatureScale = 1f
                                val pathBounds = path.getBounds()
                                with(density) {
                                    signatureOffset = Offset(
                                        (this@BoxWithConstraints.maxWidth.toPx() / 2) - pathBounds.center.x,
                                        (this@BoxWithConstraints.maxHeight.toPx() / 2) - pathBounds.center.y
                                    )
                                }
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                            },
                            onCancel = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                            }
                        )
                    }
                }
            }

            signaturePath?.let { path ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(signatureOffset.x.roundToInt(), signatureOffset.y.roundToInt()) }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                signatureScale *= zoom
                                signatureOffset += pan
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = 4.dp.toPx() * signatureScale)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PdfPageItem(renderer: PdfRenderer, pageIndex: Int) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pageIndex) {
        withContext(Dispatchers.IO) {
            val page = renderer.openPage(pageIndex)
            val destinationBitmap = createBitmap(page.width, page.height)
            page.render(destinationBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmap = destinationBitmap
            page.close()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let {
                androidx.compose.foundation.Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Página ${pageIndex + 1}",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            } ?: CircularProgressIndicator() // Placeholder enquanto carrega
        }
    }
}
