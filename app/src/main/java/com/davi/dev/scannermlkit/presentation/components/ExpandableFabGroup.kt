package com.davi.dev.scannermlkit.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    val size = SplitButtonDefaults.MediumContainerHeight
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    enabled = expand,
                    onClick = action1,
                    modifier = Modifier.heightIn(size),
                    shapes = SplitButtonDefaults.leadingButtonShapesFor(size),
                    contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(size),

                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    Icon(
                        Icons.Default.Check, contentDescription = "Check",
                        modifier = Modifier.size(SplitButtonDefaults.leadingButtonIconSizeFor(size))
                    )
                }
            },
            trailingButton = {
                val description = "Toggle Button"
                // Icon-only trailing button should have a tooltip for a11y.
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { PlainTooltip { Text(description) } },
                    state = rememberTooltipState(),
                ) {
                    SplitButtonDefaults.TrailingButton(
                        onClick = action2,
                        modifier = Modifier.heightIn(size),
                        shapes = SplitButtonDefaults.trailingButtonShapesFor(size),
                        contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(size),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)

                    ) {
                        Icon(
                            imageVector = Icons.Default.Draw, contentDescription = "Sign PDF",
                            modifier = Modifier.size(SplitButtonDefaults.trailingButtonIconSizeFor(size))
                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewGroupButton() {
    ScannermlkitTheme {
        ExpandableFabGroup(
            expand = false
        )
    }
}