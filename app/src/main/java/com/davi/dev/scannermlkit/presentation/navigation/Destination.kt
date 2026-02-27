package com.davi.dev.scannermlkit.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.R

enum class Destination(
    val route: NavKey,
    val label: String,
    val icon: Int,
    val contentDescription: String
) {
    DOCUMENTS(Routes.Home, "Home", R.drawable.ic_house, "Home"),
    SCANNER(Routes.ScanDocument, "Scanner", R.drawable.ic_scan_pdf, "Scanner"),
    ACCOUNT(Routes.Account, "Account", R.drawable.ic_user_round_cog, "Account"),
}