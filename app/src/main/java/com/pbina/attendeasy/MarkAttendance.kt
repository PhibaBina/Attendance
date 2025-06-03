//package com.pbina.attendeasy
//
//
//import android.app.DatePickerDialog
//import android.app.TimePickerDialog
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.pbina.attendeasy.adapter.StudentAdapter
//import com.pbina.attendeasy.model.Student
//import java.text.SimpleDateFormat
//import java.util.*
//
//class MarkAttendance : AppCompatActivity() {
//
//    private val db = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: StudentAdapter
//    private val attendanceMap = mutableMapOf<String, Boolean>()
//
//    private lateinit var spinnerSemester: Spinner
//    private lateinit var spinnerSubject: Spinner
//    private lateinit var editTextPeriods: EditText
//    private lateinit var editTextDate: EditText
//    private lateinit var editTextTime: EditText
//    private lateinit var EndTextTime: EditText
//    private lateinit var btnSubmit: Button
//
//    private var teacherDepartment: String? = null
//    private var registeredSemesters = listOf<String>()
//    private var subjectMap = mutableMapOf<String, List<String>>() // semester -> subjects
//
//    private val studentList = mutableListOf<Student>()
//
//    //Declare
//    private var selectedSemester: String? = null
//    private var selectedSubject: String? = null
//    private var selectedPeriods = listOf<String>()
//    private var selectedDate = ""
//    private var selectedTime = ""
//    private var selectedEndTime = ""
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_mark_attendance)
//
//        recyclerView = findViewById(R.id.recyclerViewStudents)
//        adapter = StudentDisplay(attendanceMap)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
//
//
//        spinnerSemester = findViewById(R.id.spinnerSemester)
//        spinnerSubject = findViewById(R.id.spinnerSubject)
//        editTextPeriods = findViewById(R.id.editTextPeriods)
//        editTextDate = findViewById(R.id.editTextDate)
//        editTextTime = findViewById(R.id.editTextTime)
//        EndTextTime = findViewById(R.id.EndtextTime)
//
//        btnSubmit = findViewById(R.id.btnSubmitAttendance)
//
//
//
//        loadTeacherData()
//
//        editTextDate.setOnClickListener {
//            val c = Calendar.getInstance()
//            DatePickerDialog(this, { _, y, m, d ->
//                val day = if (d < 10) "0$d" else "$d"
//                val month = if ((m + 1) < 10) "0${m + 1}" else "${m + 1}"
//                selectedDate = "$day-$month-$y" // eg: "22-04-2025"
//                editTextDate.setText(selectedDate)
//            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
//        }
//
//
//        editTextTime.setOnClickListener {
//            val c = Calendar.getInstance()
//            TimePickerDialog(this, { _, h, m ->
//                selectedTime = String.format("%02d:%02d", h, m)
//                editTextTime.setText(selectedTime)
//            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
//        }
//        EndTextTime.setOnClickListener {
//            val c = Calendar.getInstance()
//            TimePickerDialog(this, { _, h, m ->
//                selectedEndTime = String.format("%02d:%02d", h, m)
//                EndTextTime.setText(selectedEndTime)
//            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
//        }
//
//
//        editTextPeriods.setOnClickListener {
//            val periods = arrayOf("1", "2", "3", "4", "5", "6")
//            val selectedItems = BooleanArray(periods.size)
//            val selectedList = mutableListOf<String>()
//
//            val builder = android.app.AlertDialog.Builder(this)
//            builder.setTitle("Select Periods")
//            builder.setMultiChoiceItems(periods, selectedItems) { _, i, isChecked ->
//                if (isChecked) selectedList.add(periods[i]) else selectedList.remove(periods[i])
//            }
//            builder.setPositiveButton("OK") { _, _ ->
//                selectedPeriods = selectedList
//                editTextPeriods.setText(selectedList.joinToString(", "))
//            }
//            builder.setNegativeButton("Cancel", null)
//            builder.show()
//        }
//
//            spinnerSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    p: AdapterView<*>?,
//                   v: android.view.View?,
//                   pos: Int,
//                    id: Long
//                ) {
//                   selectedSemester = registeredSemesters[pos]
//                   loadSubjectsForSemester(selectedSemester!!)
//                }
//
//                override fun onNothingSelected(p: AdapterView<*>?) {}
//            }
//
//
//
//
////        spinnerSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////            override fun onItemSelected(
////                p: AdapterView<*>?,
////                v: android.view.View?,
////                pos: Int,
////                id: Long
////            ) {
////                selectedSubject = spinnerSubject.selectedItem.toString()
////                fetchStudents(selectedSemester!!)
////            }
////
////            override fun onNothingSelected(p: AdapterView<*>?) {}
////        }
//
//        spinnerSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                pos: Int,
//                id: Long
//            ) {
//                // Skip if "Select Subject" is selected
//                if (pos != 0) {
//                    selectedSubject = spinnerSubject.selectedItem.toString()
//                    fetchStudents(selectedSemester!!)
//                } else {
//                    selectedSubject = null
//                    attendanceMap.clear()
//                    adapter.notifyDataSetChanged()
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//
//
//
//        btnSubmit.setOnClickListener {
//            if (selectedDate.isBlank() || selectedTime.isBlank() || selectedPeriods.isEmpty()) {
//                Toast.makeText(this, "Please select date, time, and periods", Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }
//
//            if (selectedSemester.isNullOrBlank() || selectedSubject.isNullOrBlank()) {
//                Toast.makeText(this, "Semester or Subject not selected", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val attendanceMap = adapter.getAttendanceMap()
//
//
//
//            if (attendanceMap.isEmpty()) {
//                Toast.makeText(
//                    this,
//                    "No student data available to mark attendance",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return@setOnClickListener
//            }
//
//
//            for ((studentId, isPresent) in attendanceMap) {
//                val attendanceDocRef = db.collection("Attendance")
//                    .document(selectedDate)
//                    .collection(selectedSemester!!)
//                    .document(selectedSubject!!)
//                    .collection("Students")
//                    .document(studentId)
//
//                val studentRef = db.collection("students").document(studentId)
//
//                // Fetch current student record first
//                studentRef.get().addOnSuccessListener { snapshot ->
//                    val currentAttended = snapshot.getLong("attended")?.toInt() ?: 0
//                    val currentTotal = snapshot.getLong("total")?.toInt() ?: 0
//
//                    val newAttended = if (isPresent) currentAttended + 1 else currentAttended
//                    val newTotal = currentTotal + 1
//                    val percentage = if (newTotal > 0) (newAttended * 100) / newTotal else 0
//
//                    // Save attendance for the date
//                    val attendanceData = mapOf(
//                        "studentId" to studentId,
//                        "subject" to selectedSubject,
//                        "date" to selectedDate,
//                        "StartTime" to selectedTime,
//                        "EndTime" to selectedEndTime,
//                        "periods" to selectedPeriods,
//                        "isPresent" to isPresent,
//                        "percentage" to percentage
//                    )
//
//                    attendanceDocRef.set(attendanceData)
//                        .addOnSuccessListener {
//                            Log.d("Attendance", "Marked for $studentId")
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("Attendance", "Failed for $studentId: ${e.message}", e)
//                            Toast.makeText(this, "Failed for $studentId", Toast.LENGTH_SHORT).show()
//                        }
//
//                    // Update student overall stats
//                    studentRef.update(
//                        "attended", newAttended,
//                        "total", newTotal
//                    )
//
//                }.addOnFailureListener { e ->
//                    Log.e("Attendance", "Failed to fetch student data: ${e.message}")
//                }
//            }
//
//            Toast.makeText(this, "Attendance submitted!", Toast.LENGTH_SHORT).show()
//
//        }
//    }
//
////    private fun loadTeacherData() {
////        val uid = auth.currentUser?.uid ?: return
////        db.collection("users").document(uid).get()
////            .addOnSuccessListener { doc ->
////                teacherDepartment = doc.getString("department")
////                registeredSemesters = doc.get("semesters") as? List<String> ?: listOf()
////                val subjects = doc.get("subjects") as? List<String> ?: listOf()
////
////                // Build a simple map where all semesters share the same subjects
////                subjectMap = mutableMapOf()
////                registeredSemesters.forEach { sem ->
////                    subjectMap[sem] = subjects
////                }
////
////                val semAdapter = ArrayAdapter(
////                    this,
////                    android.R.layout.simple_spinner_dropdown_item,
////                    registeredSemesters
////                )
////                spinnerSemester.adapter = semAdapter
////            }
////    }
////
////    private fun loadSubjectsForSemester(semester: String) {
////        val subjects = subjectMap[semester] ?: listOf()
////        val subAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjects)
////        spinnerSubject.adapter = subAdapter
////        Log.d("Subjects", "Loaded subjects: $subjects")
////    }
//private fun loadTeacherData() {
//    val uid = auth.currentUser?.uid ?: return
//    db.collection("users").document(uid).get()
//        .addOnSuccessListener { doc ->
//            teacherDepartment = doc.getString("department")
//            registeredSemesters = doc.get("semesters") as? List<String> ?: listOf()
//            val subjects = doc.get("subjects") as? List<String> ?: listOf()
//
//            // Build a simple map where all semesters share the same subjects
//            subjectMap = mutableMapOf()
//            registeredSemesters.forEach { sem ->
//                subjectMap[sem] = subjects
//            }
//
//            // Add hint to the spinner
//            val semAdapter = ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_dropdown_item,
//                listOf("Select Semester") + registeredSemesters  // Adding hint as the first item
//            )
//            spinnerSemester.adapter = semAdapter
//        }
//}
//
////    private fun loadSubjectsForSemester(semester: String) {
////        val subjects = subjectMap[semester] ?: listOf()
////
////        // Add hint to the spinner
////        val subAdapter = ArrayAdapter(
////            this,
////            android.R.layout.simple_spinner_dropdown_item,
////            listOf("Select Subject") + subjects // Adding hint as the first item
////        )
////        spinnerSubject.adapter = subAdapter
////    }
//private fun loadSubjectsForSemester(semester: String) {
//    val subjectsList = mutableListOf("Select Subject") // Add hint at the start
//    val subjects = subjectMap[semester] ?: listOf()
//
//    subjectsList.addAll(subjects) // Add all actual subjects after the hint
//
//    val subAdapter = ArrayAdapter(
//        this,
//        android.R.layout.simple_spinner_dropdown_item,
//        subjectsList
//    )
//    spinnerSubject.adapter = subAdapter
//}
//
//
//    private fun fetchStudents(ssemester: String) {
//        val subject = selectedSubject ?: return
//        // val semester = selectedSemester ?: return
//        val semester = ssemester
//        val department = teacherDepartment ?: return
//
//        db.collection("users")
//            .whereEqualTo("role", "Student")
//            .whereEqualTo("department", department)
//            .whereEqualTo("semester", semester)
//            .whereArrayContains("subject", subject)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                val students = querySnapshot.documents.mapNotNull { doc ->
//                    val student = doc.toObject(Student::class.java)
//                    student?.uid = doc.id
//                    student
//                }
//                adapter.updateList(students)
//                attendanceMap.clear()
//
//                for (document in querySnapshot.documents) {
//                    val id = document.id
//                    attendanceMap[id] = false
//                }
//
//                adapter.updateData(attendanceMap)
//                Log.d("Students", "Loaded : $students")
//            }
//
//
//
//    }
//}