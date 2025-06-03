package com.pbina.attendeasy.model

data class Teacher(
    var id: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    val idNumber: String = "",
    val department: String = "",
    val semesters: List<String> = listOf(),
    val subjects: List<String> = listOf(),
    var gender: String = "",
    var phone: String = "",
    var isApproved: Boolean = false
)


