package com.bussiness.curemegptapp.util

import java.text.SimpleDateFormat
import java.util.Locale

object CommonUtils {

    fun convertTo24HourFormat(time12: String): String {
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.US)
        val date = inputFormat.parse(time12)
        return outputFormat.format(date)
    }

}