package fr.polytech.coffeemachineapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtils {
    companion object {
        fun formatDate(timestamp: Long): String {
            val date = Date(timestamp) // Convert seconds to milliseconds
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            return format.format(date)
        }

        fun parseDate(dateString: String): Long {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val date = format.parse(dateString)
            return date?.time ?: 0 // Convert milliseconds to seconds
        }
    }
}