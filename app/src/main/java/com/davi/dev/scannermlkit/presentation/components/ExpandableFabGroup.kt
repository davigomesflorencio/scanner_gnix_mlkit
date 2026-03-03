package com.davi.dev.scannermlkit.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandableFabGroup(
    expand: Boolean,
    action1: () -> Unit = {},
    action2: () -> Unit = {},
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedVisibility(
                visible = expand,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkVertically()
            ) {
                MediumFloatingActionButton(
                    onClick = action1,
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Check")
                }
            }
            MediumFloatingActionButton(
                onClick = action2,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(imageVector = Icons.Default.Draw, contentDescription = "Sign PDF")
            }
        }
    }
}

@Preview
@Composable
fun previewGroupButton() {
    ScannermlkitTheme {
//        GroupButton()
        ExpandableFabGroup(
            expand = true
        )
    }
}