package com.davi.dev.scannermlkit.presentation.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.davi.dev.scannermlkit.presentation.components.GoogleSignInButton
import com.davi.dev.scannermlkit.presentation.screens.viewModel.AuthState
import com.davi.dev.scannermlkit.presentation.screens.viewModel.AuthViewModel

@Composable
fun WelcomeScreen(
    onNavigateToHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("welcome.json")
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateToHome()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
        Spacer(Modifier.height(26.dp))
        Text(
            "Let's you in",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 52.sp
        )

        Spacer(Modifier.height(50.dp))

        GoogleSignInButton(
            isLoading = authState is AuthState.Loading,
            onClick = {
                authViewModel.signInWithGoogle(context)
            }
        )

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(26.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(.8f)
        ) {
            Spacer(
                Modifier
                    .height(1.dp)
                    .weight(.45f)
                    .background(Color.Gray)
            )
            Text(
                "or",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(.1f)
            )
            Spacer(
                Modifier
                    .height(1.dp)
                    .weight(.45f)
                    .background(Color.Gray)
            )
        }

        Spacer(Modifier.height(26.dp))
        Button(
            onClick = {

            },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier.fillMaxWidth(.8f)
        ) {
            Text(
                "Sign with password",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(Modifier.height(26.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                "Don't have an account? ",
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

            Text(
                "Sign Up",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {

                    }
            )

        }
    }
}
