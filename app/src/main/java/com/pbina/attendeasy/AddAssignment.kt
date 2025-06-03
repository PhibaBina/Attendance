package com.pbina.attendeasy

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddAssignment : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var spinnerSemester: Spinner
    private lateinit var spinnerSubject: Spinner
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var btnSubmit: AppCompatButton

    private var selectedDepartmentId: String? = null
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()

        // Init Views
        spinnerSemester = findViewById(R.id.spinnerSemester)
        spinnerSubject = findViewById(R.id.spinnerSubject)
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        btnSubmit = findViewById(R.id.btnSubmitNote)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userId = currentUser.uid
            firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        selectedDepartmentId = document.getString("department")
                        loadSemesters()
                    } else {
                        Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
        }

        // Submit
        btnSubmit.setOnClickListener {
            if (validateForm()) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Submitting Note...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val semester = spinnerSemester.selectedItem.toString()
                val subject = spinnerSubject.selectedItem.toString()

                val noteData = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "semester" to semester,
                    "subject" to subject,
                    "department" to selectedDepartmentId,
                    "uploadedBy" to userId,
                    "timestamp" to FieldValue.serverTimestamp()
                )

                firestore.collection("Notes")
                    .add(noteData)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Note submitted!", Toast.LENGTH_SHORT).show()
                        clearForm()
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun loadSemesters() {
        selectedDepartmentId?.let { deptId ->
            firestore.collection("Department")
                .document(deptId)
                .collection("Semesters")
                .get()
                .addOnSuccessListener { result ->
                    val semesters = result.map { it.id }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, semesters)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerSemester.adapter = adapter
                    setupSubjectLoader()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load semesters", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupSubjectLoader() {
        spinnerSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selectedSemester = spinnerSemester.selectedItem.toString()
                selectedDepartmentId?.let { deptId ->
                    firestore.collection("Department")
                        .document(deptId)
                        .collection("Semesters")
                        .document(selectedSemester)
                        .collection("Subjects")
                        .get()
                        .addOnSuccessListener { result ->
                            val subjects = result.mapNotNull { it.getString("name") }
                            val adapter = ArrayAdapter(this@AddAssignment, android.R.layout.simple_spinner_item, subjects)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerSubject.adapter = adapter
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@AddAssignment, "Failed to load subjects", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun validateForm(): Boolean {
        if (etTitle.text.isNullOrEmpty()) {
            etTitle.error = "Enter title"
            return false
        }
        if (etDescription.text.isNullOrEmpty()) {
            etDescription.error = "Enter description"
            return false
        }

        if (spinnerSemester.selectedItem == null || spinnerSubject.selectedItem == null) {
            Toast.makeText(this, "Please select semester and subject", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun clearForm() {
        etTitle.text?.clear()
        etDescription.text?.clear()
        spinnerSemester.setSelection(0)
        spinnerSubject.setSelection(0)
    }
}
