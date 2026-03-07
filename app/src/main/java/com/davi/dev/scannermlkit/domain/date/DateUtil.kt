package com.davi.dev.scannermlkit.domain.date

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd 'de' MMMM HH:mm", Locale.getDefault())
        return format.format(date)
    }

}