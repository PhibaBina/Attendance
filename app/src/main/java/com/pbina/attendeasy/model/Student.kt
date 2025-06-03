package com.pbina.attendeasy.model

data class Student(
    var uid: String = "",
    var email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val rollNumber: Int = 0,
    val department: String = "",
    val semester: String = "",
    val subject: List<String> = listOf(),
    var attended: Int = 0,// The number of periods the student was present
    var total: Int = 0,  // The total number of periods for this subject
   // var batchYear: String = "",  // Changed from Year to String
    var dob: String = "",
    var gender: String = "",
    var phone: String = "",      // Changed from Phone to String
    val attendancePercentage: Float = 0f,  // Changed from Int to Float
    val periods: List<String> = listOf() // The list of periods for which attendance is tracked
)
// {
//    val attendancePercentage: Int
//        get() = if (total > 0) (attended * 100) / total else 0
//}


