package com.davi.dev.scannermlkit.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.graphics.shapes.RoundedPolygon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomContainedLoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        ContainedLoadingIndicator(
            polygons = listOf(
                RoundedPolygon(MaterialShapes.Cookie6Sided),
                RoundedPolygon(MaterialShapes.VerySunny),
                RoundedPolygon(MaterialShapes.Cookie4Sided),
                RoundedPolygon(MaterialShapes.Oval),
                RoundedPolygon(MaterialShapes.Pill)
            )
        )
    }
}