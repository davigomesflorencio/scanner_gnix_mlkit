package com.davi.dev.scannermlkit.domain.enums

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.presentation.navigation.Routes

enum class UseCaseOptions(
    var title: String,
    var icon: Int,
    var color: Color,
    var backgroundColor: Color = Color.White,
    var route: NavKey
) {
    SCANCODE(
        "Scan Code", R.drawable.ic_scan_qr_code, color = Color(0xFFfda82f),
        backgroundColor = Color(0XFFfff7eb), route = Routes.ScanQrCode
    ),
    WATERMARK(
        "Watermark", R.drawable.ic_watermark, color = Color(0xFFa9715e),
        backgroundColor = Color(0XFFfbf2f1), route = Routes.ScanQrCode
    ),
    SPLITPDF(
        "Split PDF", R.drawable.ic_split, color = Color(0xFF7b5eff),
        backgroundColor = Color(0XFFf0f1fe), route = Routes.ScanQrCode
    ),
    MERGEPDF(
        "Merge PDF", R.drawable.ic_combine, color = Color(0xFFfb6062),
        backgroundColor = Color(0XFFfff1f1), route = Routes.MergePdf
    ),
    PROTECTPDF(
        "ProtectPDF", R.drawable.ic_protected, color = Color(0xFF30d79c),
        backgroundColor = Color(0XFFebf7f6), route = Routes.ScanQrCode
    ),
    COMPRESSPDF(
        "Compress PDF", R.drawable.ic_wand, color = Color(0xFFfba322),
        backgroundColor = Color(0XFFfff7eb), route = Routes.ScanQrCode
    )

}