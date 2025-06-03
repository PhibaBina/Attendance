package com.pbina.attendeasy.model

import com.google.firebase.Timestamp

data class AttendanceRecord(
    val studentId: String = "",
    val subject: String = "",
    val date: Timestamp = Timestamp.now(),
    val time: String = "",
    val status: String = "" // "Present" or "Absent"
) {
    fun getFormattedDate(): String {
        val date = date.toDate()
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }
}