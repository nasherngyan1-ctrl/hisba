package com.example.ui.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object NumberUtils {
    private val decimalFormat = DecimalFormat("#,###")

    fun formatIqd(amount: Double): String {
        return "${decimalFormat.format(amount.toLong())} د.ع"
    }

    fun formatNumber(number: Int): String {
        return decimalFormat.format(number)
    }

    fun getCurrentMonthKey(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        return String.format(Locale.US, "%04d-%02d", year, month)
    }

    fun getPreviousMonthKey(monthKey: String): String {
        val parts = monthKey.split("-")
        if (parts.size != 2) return monthKey
        var year = parts[0].toIntOrNull() ?: 2026
        var month = parts[1].toIntOrNull() ?: 11
        month -= 1
        if (month < 1) {
            month = 12
            year -= 1
        }
        return String.format(Locale.US, "%04d-%02d", year, month)
    }

    fun getNextMonthKey(monthKey: String): String {
        val parts = monthKey.split("-")
        if (parts.size != 2) return monthKey
        var year = parts[0].toIntOrNull() ?: 2026
        var month = parts[1].toIntOrNull() ?: 11
        month += 1
        if (month > 12) {
            month = 1
            year += 1
        }
        return String.format(Locale.US, "%04d-%02d", year, month)
    }

    fun getKurdishMonthName(monthKey: String): String {
        val parts = monthKey.split("-")
        if (parts.size != 2) return monthKey
        val year = parts[0]
        val monthNum = parts[1].toIntOrNull() ?: 1
        val kurdishMonths = arrayOf(
            "کانوونی دووەم (١)",
            "شوبات (٢)",
            "ئازار (٣)",
            "نیسان (٤)",
            "ئایار (٥)",
            "حوزەیران (٦)",
            "تەممووز (٧)",
            "ئاب (٨)",
            "ئەیلوول (٩)",
            "تشرینی یەکەم (١٠)",
            "تشرینی دووەم (١١)",
            "کانوونی یەکەم (١٢)"
        )
        val monthName = kurdishMonths.getOrElse(monthNum - 1) { "مانگی $monthNum" }
        return "$monthName - $year"
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatRelativeTime(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / (1000 * 60)
        val hours = diff / (1000 * 60 * 60)
        val days = diff / (1000 * 60 * 60 * 24)

        return when {
            minutes < 2 -> "کەمێک پێش ئێستا"
            minutes < 60 -> "پێش $minutes خولەک"
            hours < 24 -> "پێش $hours کاتژمێر"
            days < 30 -> "پێش $days ڕۆژ"
            else -> SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
