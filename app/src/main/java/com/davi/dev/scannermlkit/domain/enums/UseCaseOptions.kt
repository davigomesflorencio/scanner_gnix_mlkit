package com.davi.dev.scannermlkit.domain.enums

import androidx.compose.ui.graphics.Color
import com.davi.dev.scannermlkit.R

enum class UseCaseOptions(
    var title: String,
    var icon: Int,
    var color: Color
) {
    SCANCODE("Scan Code", R.drawable.ic_scan_qr_code, color = Color.Blue.copy(alpha = 0.74f)),
    WATERMARK("Watermark", R.drawable.ic_watermark, color = Color.Green.copy(alpha = 0.24f)),
    SPLITPDF("Split PDF", R.drawable.ic_split, color = Color.DarkGray),
    MERGEPDF("Merge PDF", R.drawable.ic_combine, color = Color.Red.copy(alpha = 0.94f)),
    PROTECTPDF("ProtectPDF", R.drawable.ic_protected, color = Color.Gray.copy(alpha = 0.74f)),
    COMPRESSPDF("Compress PDF", R.drawable.ic_wand, color = Color.Yellow.copy(alpha = 0.84f))

}