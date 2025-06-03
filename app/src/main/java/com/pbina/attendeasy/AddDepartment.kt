package com.pbina.attendeasy

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore

class AddDepartment : AppCompatActivity() {

    private lateinit var etDepartmentName: EditText
    private lateinit var semesterContainer: LinearLayout
    private lateinit var btnAddSemester: AppCompatButton
    private lateinit var btnSaveDepartment: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_department)

        etDepartmentName = findViewById(R.id.etDepartmentName)
        semesterContainer = findViewById(R.id.semesterContainer)
        btnAddSemester = findViewById(R.id.btnAddSemester)
        btnSaveDepartment = findViewById(R.id.btnSaveDepartment)

        btnAddSemester.setOnClickListener {
            addSemesterView()
        }

        btnSaveDepartment.setOnClickListener {
            saveToFirestore()
        }
    }

    private fun addSemesterView() {
        val semesterLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 16, 0, 16)
        }

        val etSemester = EditText(this).apply {
            hint = "Semester Name (e.g., 1)"
            setPadding(12, 12, 12, 12)
            background = ContextCompat.getDrawable(context, R.drawable.bg_input)
        }

        val subjectContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val btnAddSubject = Button(this).apply {
            text = "Add Subject"
            setBackgroundColor(Color.parseColor("#A5D6A7"))
            setOnClickListener {
                val etSubject = EditText(this@AddDepartment).apply {
                    hint = "Subject Name"
                    setPadding(12, 12, 12, 12)
                    background = ContextCompat.getDrawable(context, R.drawable.bg_input)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = 8 }
                }
                subjectContainer.addView(etSubject)
            }
        }

        semesterLayout.addView(etSemester)
        semesterLayout.addView(btnAddSubject)
        semesterLayout.addView(subjectContainer)

        semesterContainer.addView(semesterLayout)
    }

    private fun saveToFirestore() {
        val departmentName = etDepartmentName.text.toString().trim()
        if (departmentName.isEmpty()) {
            etDepartmentName.error = "Required"
            return
        }

        val db = FirebaseFirestore.getInstance()
        val departmentRef = db.collection("Department").document(departmentName)

        // Save department
        departmentRef.set(mapOf("name" to departmentName)).addOnSuccessListener {
            for (i in 0 until semesterContainer.childCount) {
                val semesterLayout = semesterContainer.getChildAt(i) as LinearLayout
                val etSemester = semesterLayout.getChildAt(0) as EditText
                val semesterName = etSemester.text.toString().trim()

                if (semesterName.isEmpty()) continue

                val semesterRef = departmentRef.collection("Semesters").document(semesterName)
                semesterRef.set(mapOf("name" to semesterName))

                val subjectContainer = semesterLayout.getChildAt(2) as LinearLayout
                for (j in 0 until subjectContainer.childCount) {
                    val etSubject = subjectContainer.getChildAt(j) as EditText
                    val subjectName = etSubject.text.toString().trim()
                    if (subjectName.isNotEmpty()) {
                        val subjectRef = semesterRef.collection("Subjects").document(subjectName)
                        subjectRef.set(mapOf("name" to subjectName))
                    }
                }
            }
            Toast.makeText(this, "Department Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
