package com.davi.dev.scannermlkit.domain.date

import java.text.SimpleDateFormat
import java.util.Date

object DateUtil {
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd 'de' MMMM HH:mm")
        return format.format(date)
    }

}