package com.davi.dev.scannermlkit.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


object Routes {
    @Serializable
    data object Home : NavKey

    @Serializable
    data object ScanDocument : NavKey

    @Serializable
    data object ScanQrCode : NavKey

    @Serializable
    data object SelectViewDocument : NavKey

    @Serializable
    data class ViewDocument(val filePath: String) : NavKey

    @Serializable
    data object MergePdf : NavKey

    @Serializable
    data object SplitPdf : NavKey

    @Serializable
    data object ProtectPdf : NavKey

    @Serializable
    data object CompressPdf : NavKey

    @Serializable
    data object WatermarkPdf : NavKey

    @Serializable
    data object Account : NavKey
}