//package com.pbina.attendeasy
//
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.FirebaseFirestore
//import com.pbina.attendeasy.adapter.ReportAdapter
//import com.pbina.attendeasy.model.AttendanceRecord
//import com.pbina.attendeasy.model.AttendanceReport
//import com.pbina.attendeasy.model.Student
//import com.pbina.attendeasy.model.Teacher
//
//class GenerateReport: AppCompatActivity() {
//
//    private lateinit var reportAdapter: ReportAdapter

//
//    private val studentAttendanceList = mutableListOf<AttendanceReport>()
//
//    private var totalStudents = 0
//    private var processedStudents = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_generate_report)
//
//        reportAdapter = ReportAdapter(studentAttendanceList)
//        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewReport)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = reportAdapter
//
//
//        fetchAttendanceReport()
//    }
//
//
//
//
//    private fun fetchAttendanceReport() {
//        val db = FirebaseFirestore.getInstance()
//
//        db.collection("users")
//            .whereEqualTo("role", "Student")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val firstName = document.getString("firstName") ?: ""
//                    val lastName = document.getString("lastName") ?: ""
//                    val rollNumber = document.getLong("rollNumber")?.toInt() ?: 0
//                    val attendancePercentage = document.getDouble("attendancePercentage") ?: 0.0
//
//                    val fullName = "$firstName $lastName"
//
//                    Log.d(
//                        "StudentInfo",
//                        "Name: $fullName, Roll: $rollNumber, Attendance: $attendancePercentage%"
//                    )
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirestoreError", "Error fetching students", e)
//            }
//    }
//
////    private fun fetchAttendanceReport() {
////        val db = FirebaseFirestore.getInstance()
////        db.collection("users")
////            .whereEqualTo("role", "Student")
////            .get()
////            .addOnSuccessListener { querySnapshot ->
////                val studentIds = querySnapshot.documents.mapNotNull { it.id }
////                totalStudents = studentIds.size
////                if (totalStudents == 0) return@addOnSuccessListener  // No students
////                studentIds.forEach { studentId ->
////                    fetchStudentAttendance(studentId)
////                }
////            }
////            .addOnFailureListener { e ->
////                Log.e("GenerateReport", "Error fetching students", e)
////                Toast.makeText(this, "Error fetching students", Toast.LENGTH_SHORT).show()
////            }
////    }
////
////
////    private fun fetchStudentAttendance(studentId: String) {
////        val db = FirebaseFirestore.getInstance()
////        db.collection("Attendance")
////            .whereEqualTo("studentId", studentId)
////            .get()
////            .addOnSuccessListener { querySnapshot ->
////                val attendanceRecords = querySnapshot.documents.mapNotNull { it.toObject(AttendanceRecord::class.java) }
////                val total = attendanceRecords.size
////                val present = attendanceRecords.count { it.status == "Present" }
////                val percentage = if (total > 0) (present.toDouble() / total) * 100 else 0.0
////                fetchStudentData(studentId, percentage, attendanceRecords)
////            }
////            .addOnFailureListener { e ->
////                Log.e("GenerateReport", "Error fetching attendance", e)
////                Toast.makeText(this, "Error fetching attendance", Toast.LENGTH_SHORT).show()
////            }
////    }
////
////    private fun fetchStudentData(studentId: String, percentage: Double, records: List<AttendanceRecord>) {
////        val db = FirebaseFirestore.getInstance()
////        db.collection("users").document(studentId)
////            .get()
////            .addOnSuccessListener { document ->
////                val student = document.toObject(Student::class.java)
////                student?.let {
////                    val rollNumber = it.rollNumber.toInt()
////
////                    val report = AttendanceReport(
////                        studentId = studentId,
////                        firstName = it.firstName,
////                        lastName = it.lastName,
////                        rollNumber = rollNumber,
////                        department = it.department,
////                        attendancePercentage = percentage,
////                        attendanceRecords = records
////                    )
////                    studentAttendanceList.add(report)
////                }
//
////                processedStudents++
////                if (processedStudents == totalStudents) {
////                    // All students loaded, now sort and refresh
////                    studentAttendanceList.sortBy { it.rollNumber }
////                    reportAdapter.notifyDataSetChanged()
////                }
////            }
////            .addOnFailureListener { e ->
////                Log.e("GenerateReport", "Error fetching student data", e)
////                Toast.makeText(this, "Error fetching student data", Toast.LENGTH_SHORT).show()
////
////                processedStudents++
////                if (processedStudents == totalStudents) {
////                    studentAttendanceList.sortBy { it.rollNumber }
////                    reportAdapter.notifyDataSetChanged()
////                }
////            }
////    }
////
//
//}
package com.pbina.attendeasy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.adapter.ReportAdapter
import com.pbina.attendeasy.model.AttendanceReport

class GenerateReport : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private val reportList = mutableListOf<AttendanceReport>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_report)

        recyclerView = findViewById(R.id.recyclerViewReport)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReportAdapter(reportList)
        recyclerView.adapter = adapter
        toolbar = findViewById(R.id.toolbar)

//        // Setup Toolbar
        setupToolbar()
        fetchReportData()
    }
    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow) // Set back arrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun fetchReportData() {
        db.collection("users")
            .whereEqualTo("role", "Student")
            .get()
            .addOnSuccessListener { result ->
                val reportList = mutableListOf<AttendanceReport>()

                for (doc in result) {
                    val firstName = doc.getString("firstName") ?: ""
                    val lastName = doc.getString("lastName") ?: ""
                    val rollNumber = doc.getLong("rollNumber")?.toInt() ?: 0
                    val percentage = doc.getDouble("attendancePercentage") ?: 0.0

                    val report = AttendanceReport(
                        firstName = firstName,
                        lastName = lastName,
                        rollNumber = rollNumber,
                        attendancePercentage = percentage
                    )
                    reportList.add(report)
                }

                // ✅ Sort by rollNumber
                val sortedList = reportList.sortedBy { it.rollNumber }

                // Set adapter with sorted list
                recyclerView.adapter = ReportAdapter(sortedList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
    }

}

