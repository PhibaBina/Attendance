package com.pbina.attendeasy.model

data class AttendanceSummary(
    val subject: String = "",
    val attended: Int = 0,
    val total: Int = 0,
    val percentage: Double = 0.0
)
