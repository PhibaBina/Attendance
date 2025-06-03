package com.pbina.attendeasy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.adapter.StudentAdapter
import com.pbina.attendeasy.model.Student
import java.text.SimpleDateFormat
import java.util.Locale

class MarkAttendanceActivity : AppCompatActivity() {

    private lateinit var selectedSemester: String
    private lateinit var selectedSubject: String
    private lateinit var selectedDate: String
    private lateinit var selectedTime: String
    private lateinit var selectedEndTime: String
    private lateinit var selectedPeriod: String
    private lateinit var studentAdapter: StudentAdapter

    private var teacherDepartment: String? = null
    private val attendanceMap = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_attendance2)

        // Get data passed from the previous activity
        selectedSemester = intent.getStringExtra("selectedSemester") ?: ""
        selectedSubject = intent.getStringExtra("selectedSubject") ?: ""
        selectedDate = intent.getStringExtra("selectedDate") ?: ""
        selectedTime = intent.getStringExtra("selectedTime") ?: ""
        selectedEndTime = intent.getStringExtra("selectedEndTime") ?: ""
        selectedPeriod = intent.getStringExtra("selectedPeriod") ?: ""

        // Initialize RecyclerView and adapter
        studentAdapter = StudentAdapter(attendanceMap, selectedPeriod = selectedSubject)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = studentAdapter

        // Fetch teacher department and then students
        fetchTeacherDepartment()

        // Handle Submit button
        val btnSubmitAttendance: AppCompatButton = findViewById(R.id.btnSubmitAttendance)
        btnSubmitAttendance.setOnClickListener {
            saveAttendance()
        }


    }

    private fun fetchTeacherDepartment() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseFirestore.getInstance().collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                teacherDepartment = document.getString("department")
                if (!teacherDepartment.isNullOrEmpty()) {
                    fetchStudents()
                } else {
                    Toast.makeText(this, "Department not found for teacher", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("DepartmentFetch", "Error fetching teacher department", e)
                Toast.makeText(this, "Failed to fetch teacher department", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchStudents() {
        val subject = selectedSubject
        val semester = selectedSemester
        val department = teacherDepartment ?: return

        Log.d("FetchStudents", "Subject: $subject, Department: $department, Semester: $semester")

        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("role", "Student")
            .whereEqualTo("department", department)
            .whereEqualTo("semester", semester)
            .whereArrayContains("subject", subject)
            .get()
            .addOnSuccessListener { querySnapshot ->

                Log.d("FetchStudents", "Fetched ${querySnapshot.size()} students")
                val students = querySnapshot.documents.mapNotNull { doc ->
                    val student = doc.toObject(Student::class.java)
                    student?.uid = doc.id
                    student
                }

                attendanceMap.clear()
                students.forEach { student ->
                    attendanceMap[student.uid ?: ""] = false
                }

                studentAdapter.updateList(students)
                studentAdapter.updateData(attendanceMap)
            }
            .addOnFailureListener { e ->
                Log.e("FetchStudents", "Error fetching students", e)
                Toast.makeText(this, "Error fetching students", Toast.LENGTH_SHORT).show()
            }
    }

    //    private fun saveAttendance {
//        Toast.makeText(this, "Attendance submitted successfully", Toast.LENGTH_SHORT).show()
//        // You can finish the activity if needed
//        // finish()
//    }
    private fun saveAttendance() {
        val db = FirebaseFirestore.getInstance()
        val attendanceCollection = db.collection("Attendance")

        // Calculate the total number of periods selected
        val periodsCount = selectedPeriod.split(",").size

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val parsedDate = dateFormat.parse(selectedDate)  // Parse the selected date
        val dateTimestamp = Timestamp(parsedDate)  // Convert to Timestamp

        for ((studentId, isPresent) in attendanceMap) {
            val student = studentAdapter.getStudentById(studentId) ?: continue

            val studentDocRef = db.collection("users").document(studentId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(studentDocRef)

                // Overall attendance

                val currentAttended = (snapshot.getLong("attended") ?: 0).toInt()
                val currentTotal = (snapshot.getLong("total") ?: 0).toInt()

                // Update attended based on periods (add periodsCount if present)
                val newAttended = if (isPresent) currentAttended + periodsCount else currentAttended
                val newTotal = currentTotal + periodsCount
                val newPercentage = if (newTotal > 0) (newAttended.toDouble() / newTotal) * 100 else 0.0

                // Subject-wise attendance
                val subjectMap = snapshot.get("subjectAttendance") as? Map<*, *> ?: emptyMap<String, Any>()
                val subjectData = subjectMap[selectedSubject] as? Map<*, *> ?: mapOf("attended" to 0L, "total" to 0L)

                val subjectAttended = (subjectData["attended"] as? Long ?: 0L).toInt()
                val subjectTotal = (subjectData["total"] as? Long ?: 0L).toInt()

                // Update subject-wise attended based on periods (add periodsCount if present)
                val newSubjectAttended = if (isPresent) subjectAttended + periodsCount else subjectAttended
                val newSubjectTotal = subjectTotal + periodsCount

                val updatedSubjectMap = mapOf(
                    "attended" to newSubjectAttended,
                    "total" to newSubjectTotal
                )

                val subjectField = "subjectAttendance.$selectedSubject"

                // Apply updates to user document
                transaction.update(studentDocRef, mapOf(
                    "attended" to newAttended,
                    "total" to newTotal,
                    "attendancePercentage" to newPercentage,
                    subjectField to updatedSubjectMap
                ))

                // Save individual attendance record to Attendance collection
                val attendanceRecord = hashMapOf(
                    "studentId" to studentId,
                    "subject" to selectedSubject,
                    "semester" to selectedSemester,
                    "date" to dateTimestamp,
                    "time" to selectedTime,
                    "endTime" to selectedEndTime,
                    "period" to selectedPeriod,
                    "status" to if (isPresent) "Present" else "Absent"
                )

                val newDocRef = attendanceCollection.document()
                transaction.set(newDocRef, attendanceRecord)
            }.addOnSuccessListener {
                Log.d("Attendance", "Saved for $studentId")
            }.addOnFailureListener { e ->
                Log.e("Attendance", "Error saving for $studentId", e)
            }
        }

        Toast.makeText(this, "Attendance submitted and saved.", Toast.LENGTH_SHORT).show()
        finish()
    }

}
