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


    fun splitValueUnit(input: String, defaultUnit: String): Pair<String, String> {
        val parts = input.trim().split(" ")

        if (parts.size >= 2) {
            return Pair(parts[0], parts[1])
        }

        // If no space, separate numeric and alphabetic
        val match = Regex("(\\d+(?:\\.\\d+)?)([a-zA-Z]+)").find(input)

        return if (match != null) {
            val (value, unit) = match.destructured
            Pair(value, unit)
        } else {
            Pair(input, defaultUnit)
        }
    }





}