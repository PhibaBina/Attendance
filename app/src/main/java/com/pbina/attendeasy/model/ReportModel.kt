package com.pbina.attendeasy.model

//data class ReportModel(
//    val firstName: String = "",
//    val lastName: String = "",
//    val rollNumber: Int = 0,
//    val department: String = "",
//    val semester: String = "",
//    val subject: List<String> = emptyList(), // For subject-wise reporting
//    val subjectName: String? = null,         // Optional: Single subject for report title
//    val total: Int = 0,                      // Total classes for subject
//    val attended: Int = 0,                   // Attended classes for subject
//    val overallTotal: Int = 0,               // Total classes overall
//    val overallAttended: Int = 0,            // Attended classes overall
//    val attendancePercentage: Double = 0.0,  // Subject-wise or overall %
//    val date: String? = null                 // Optional: Date if needed
//)

import com.google.firebase.Timestamp

data class ReportModel(
    val studentId: String = "",
    val subject: String = "",
    val date: Timestamp = Timestamp.now(), // Use Timestamp for Firestore date
    val time: String = "",
    val status: String = "" ,
    val attendancePercentage: Float = 0f,  // Changed from Int to Float
) {
    // Convert Timestamp to a human-readable date format
    fun getFormattedDate(): String {
        val date = date.toDate() // Convert Timestamp to Date object
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return formatter.format(date) // Return the formatted date string
    }
}

