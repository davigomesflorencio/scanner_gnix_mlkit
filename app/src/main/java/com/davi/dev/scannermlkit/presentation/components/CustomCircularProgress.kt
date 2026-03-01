package com.davi.dev.scannermlkit.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme

@Composable
fun CustomCircularProgress() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("loading.json")
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress }
    )
}

@Preview
@Composable
fun previewCustomCircularProgress() {
    ScannermlkitTheme {
        CustomCircularProgress()
    }
}