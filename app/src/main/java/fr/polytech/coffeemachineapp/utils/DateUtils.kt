package fr.polytech.coffeemachineapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtils {
    companion object {
        fun formatDate(timestamp: Long): String {
            val date = Date(timestamp*1000) // Convert seconds to milliseconds
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return format.format(date)
        }

        fun parseDate(dateString: String): Long {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = format.parse(dateString)
            return date?.time?.div(1000) ?: 0 // Convert milliseconds to seconds
        }

        fun isOver(timestamp: Long, offset: Long): Boolean {
            return timestamp + offset < Date().time/1000
        }
    }
}