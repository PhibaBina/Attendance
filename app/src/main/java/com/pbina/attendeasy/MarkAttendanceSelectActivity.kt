package com.pbina.attendeasy

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MarkAttendanceSelectActivity : AppCompatActivity() {

    private lateinit var spinnerSemester: Spinner
    private lateinit var spinnerSubject: Spinner
    private lateinit var editTextPeriods: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var endTextTime: EditText
    private lateinit var btnNext: AppCompatButton

    private var semesterList: List<String> = listOf()
    private var subjectList: List<String> = listOf()
    private var periodsList: MutableList<String> = mutableListOf()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var selectedSemester: String = ""
    private var selectedSubject: String = ""
    private var selectedPeriod: String = ""
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedEndTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_attendance_select)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        spinnerSemester = findViewById(R.id.spinnerSemester)
        spinnerSubject = findViewById(R.id.spinnerSubject)
        editTextPeriods = findViewById(R.id.editTextPeriods)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        endTextTime = findViewById(R.id.EndtextTime)
        btnNext = findViewById(R.id.btnNext)

        loadUserDepartment(user.uid)
        setListeners()
    }

    private fun loadUserDepartment(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val department = document.getString("department")
                if (!department.isNullOrEmpty()) {
                    loadSemesters(department)
                } else {
                    Toast.makeText(this, "Department not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load department", Toast.LENGTH_SHORT).show()
            }
    }

private fun loadSemesters(department: String) {
    firestore.collection("Department")
        .document(department)
        .collection("Semesters")
        .get()
        .addOnSuccessListener { result ->
            // Adding a hint at the top of the list
            semesterList = mutableListOf("Select Semester").apply {
                addAll(result.documents.map { it.getString("name") ?: "" })
            }

            val semesterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, semesterList)
            semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSemester.adapter = semesterAdapter

            spinnerSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    // Ensure the hint is not selected
                    if (position != 0) {
                        selectedSemester = semesterList[position]
                        loadSubjects(department, selectedSemester)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(this, "Failed to load semesters", Toast.LENGTH_SHORT).show()
        }
}


private fun loadSubjects(department: String, semester: String) {
    val subjectRef = firestore.collection("Department")
        .document(department)
        .collection("Semesters")
        .document(semester)
        .collection("Subjects")

    subjectRef.get().addOnSuccessListener { querySnapshot ->
        // Adding a hint at the top of the subject list
        val subjectList = mutableListOf("Select Subject").apply {
            for (document in querySnapshot.documents) {
                add(document.id)
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjectList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = adapter

        spinnerSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Ensure the hint is not selected
                if (position != 0) {
                    selectedSubject = spinnerSubject.selectedItem.toString()
                } else {
                    selectedSubject = ""
                }
                Log.d("MarkAttendanceSelectActivity", "Selected Subject: $selectedSubject")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedSubject = ""
            }
        }
    }.addOnFailureListener {
        Toast.makeText(this, "Failed to load subjects: ${it.message}", Toast.LENGTH_SHORT).show()
    }
}


    private fun setListeners() {
        editTextDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                selectedDate = "$day/${month + 1}/$year"
                editTextDate.setText(selectedDate)
            }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        editTextTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, { _, hour, minute ->
                selectedTime = "%02d:%02d".format(hour, minute)
                editTextTime.setText(selectedTime)
            }, 9, 0, true)
            timePicker.show()
        }

        endTextTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, { _, hour, minute ->
                selectedEndTime = "%02d:%02d".format(hour, minute)
                endTextTime.setText(selectedEndTime)
            }, 10, 0, true)
            timePicker.show()
        }

        editTextPeriods.setOnClickListener {
            val periods = arrayOf("1", "2", "3", "4", "5", "6")
            val selectedItems = BooleanArray(periods.size)
            periodsList.clear()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Period(s)")
            builder.setMultiChoiceItems(periods, selectedItems) { _, which, isChecked ->
                if (isChecked) {
                    periodsList.add(periods[which])
                } else {
                    periodsList.remove(periods[which])
                }
            }
            builder.setPositiveButton("OK") { _, _ ->
                selectedPeriod = periodsList.joinToString(", ")
                editTextPeriods.setText(selectedPeriod)
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        btnNext.setOnClickListener {
            // Check if all fields are properly selected/filled
            if (selectedSemester != "Select Semester" &&
                selectedSubject != "Select Subject" &&
                selectedDate.isNotEmpty() &&
                selectedTime.isNotEmpty() &&
                selectedEndTime.isNotEmpty()) {

                // Proceed to the next activity
                val intent = Intent(this, MarkAttendanceActivity::class.java)
                intent.putExtra("selectedSemester", selectedSemester)
                intent.putExtra("selectedSubject", selectedSubject)
                intent.putExtra("selectedPeriod", selectedPeriod)
                intent.putExtra("selectedDate", selectedDate)
                intent.putExtra("selectedTime", selectedTime)
                intent.putExtra("selectedEndTime", selectedEndTime)
                startActivity(intent)

            } else {
                // Show error message if any field is missing or invalid
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
