package com.davi.dev.scannermlkit.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.presentation.navigation.Routes
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    backStack: NavBackStack<NavKey>
) {
    LaunchedEffect(Unit) {
        delay(4000)
        backStack.add(Routes.Home)
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("sandy_loading.json") )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.scanner),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Text(
            "Scanner MLKIT",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 40.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 30.dp)

        )
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )

    }
}

@Preview
@Composable
fun previewSplashScreen() {
    ScannermlkitTheme {
        SplashScreen(rememberNavBackStack())
    }
}