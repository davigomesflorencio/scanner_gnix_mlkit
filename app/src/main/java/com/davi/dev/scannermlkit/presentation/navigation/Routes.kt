package com.davi.dev.scannermlkit.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data object ListDocument : NavKey

@Serializable
data object ScanPdf : NavKey

@Serializable
data object ScanQrCode : NavKey

@Serializable
data object ViewPDF : NavKey
