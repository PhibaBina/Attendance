package com.pbina.attendeasy.model

data class AttendanceReport(
    val studentId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    var attended: Int = 0,// The number of periods the student was present
    var total: Int = 0,  // The total number of periods for this subject
    val rollNumber: Int = 0,  // Change to Int if roll number is small enough
    val department: String = "",
    val attendancePercentage: Double = 0.0,
    val attendanceRecords: List<AttendanceRecord> = listOf()
)
