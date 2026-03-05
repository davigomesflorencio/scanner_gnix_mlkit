package com.davi.dev.scannermlkit.presentation.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.domain.enums.UseCaseOptions
import com.davi.dev.scannermlkit.presentation.navigation.Routes
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FunctionsHomeApp(
    backStack: NavBackStack<NavKey>,
    scannerDocumentViewModel: ScannerDocumentViewModel
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            scannerDocumentViewModel.setUri(uri)
            backStack.add(Routes.SelectViewDocument)
        }
    }

    val visibleItems = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(10) // Small delay to start the animation
        visibleItems.value = true
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 90.dp),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        itemsIndexed(UseCaseOptions.entries.toTypedArray()) { index, it ->
            AnimatedVisibility(
                visible = visibleItems.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = index * 50)) +
                        scaleIn(animationSpec = tween(durationMillis = 500, delayMillis = index * 50))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 5.dp)
                ) {
                    Button(
                        onClick = {
                            if (it.route == Routes.SignPDF) {
                                launcher.launch(arrayOf("application/pdf"))
                            } else {
                                backStack.add(it.route)
                            }
                        },
                        elevation = ButtonDefaults.buttonElevation(2.dp),
                        shape = RoundedPolygon(MaterialShapes.Cookie6Sided).toShape(0),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = it.backgroundColor
                        ),
                        modifier = Modifier.size(74.dp)
                    ) {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.title,
                            tint = it.color,
                            modifier = Modifier.size(58.dp)
                        )
                    }

                    Text(
                        it.title, style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                }
            }
        }
    }
}
