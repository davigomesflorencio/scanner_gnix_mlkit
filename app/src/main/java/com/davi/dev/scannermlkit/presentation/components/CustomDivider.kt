package com.davi.dev.scannermlkit.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomDivider() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 10.dp)
            .background(Color.Gray.copy(alpha = 0.7f))
    )
}