package com.pbina.attendeasy

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.R

class EditProfile : AppCompatActivity() {

    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var mobileNo: TextInputEditText
    private lateinit var departmentSpinner: Spinner
    private lateinit var semesterTextView: TextView
    private lateinit var subjectTextView: TextView
    private lateinit var btnSave: Button
    private lateinit var toolbar: Toolbar
    private lateinit var loadingDialog: Dialog

    private lateinit var db: FirebaseFirestore

    private var selectedDepartment = ""
    private var selectedSemesters = mutableListOf<String>()
    private var selectedSubjects = mutableListOf<String>()
    private var allSemesters = listOf<String>()
    private var allSubjects = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        toolbar = findViewById(R.id.toolbar)

        db = FirebaseFirestore.getInstance()

        initViews()
        setupLoadingDialog()
        loadDepartments()


        departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedDepartment = parent?.getItemAtPosition(position).toString()
                loadSemesters(selectedDepartment)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        semesterTextView.setOnClickListener {
            showMultiSelectDialog("Select Semesters", allSemesters, selectedSemesters) {
                semesterTextView.text = selectedSemesters.joinToString(", ")
                loadSubjects(selectedDepartment, selectedSemesters)
            }
        }

        subjectTextView.setOnClickListener {
            showMultiSelectDialog("Select Subjects", allSubjects, selectedSubjects) {
                subjectTextView.text = selectedSubjects.joinToString(", ")
            }
        }

        btnSave.setOnClickListener {
            updateTeacher()
        }
    }
    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow) // Set back arrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initViews() {
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        mobileNo = findViewById(R.id.mobileno)

        departmentSpinner = findViewById(R.id.spinner_department)
        semesterTextView = findViewById(R.id.semesterMultiSelect)
        subjectTextView = findViewById(R.id.subjectMultiSelect)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun setupLoadingDialog() {
        loadingDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null)
        loadingDialog.setContentView(view)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun loadDepartments() {
        db.collection("Department").get().addOnSuccessListener { result ->
            val departmentList = mutableListOf("Select Department")
            for (doc in result) {
                departmentList.add(doc.id)
            }
            val adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, departmentList)
            departmentSpinner.adapter = adapter
        }
    }

    private fun loadSemesters(department: String) {
        db.collection("Department").document(department)
            .collection("Semesters").get()
            .addOnSuccessListener { result ->
                allSemesters = result.map { it.id }
                selectedSemesters.clear()
                semesterTextView.text = "Tap to select"
            }
    }

    private fun loadSubjects(department: String, semesters: List<String>) {
        selectedSubjects.clear()
        allSubjects = listOf()
        val tempSubjects = mutableSetOf<String>()

        val tasks = semesters.map { semester ->
            db.collection("Department").document(department)
                .collection("Semesters").document(semester)
                .collection("Subjects").get()
        }

        // Collect all subjects from the selected semesters
        com.google.android.gms.tasks.Tasks.whenAllSuccess<Any>(tasks)
            .addOnSuccessListener { results ->
                for (result in results) {
                    if (result is com.google.firebase.firestore.QuerySnapshot) {
                        for (doc in result) {
                            tempSubjects.add(doc.getString("name") ?: doc.id)
                        }
                    }
                }
                allSubjects = tempSubjects.toList()
                subjectTextView.text = "Tap to select"
            }
    }

    private fun showMultiSelectDialog(
        title: String,
        items: List<String>,
        selectedItems: MutableList<String>,
        onConfirm: () -> Unit
    ) {
        val selectedBooleans = BooleanArray(items.size) { selectedItems.contains(items[it]) }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(items.toTypedArray(), selectedBooleans) { _, which, isChecked ->
                if (isChecked) selectedItems.add(items[which])
                else selectedItems.remove(items[which])
            }
            .setPositiveButton("OK") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateTeacher() {
        val fName = firstName.text.toString().trim()
        val lName = lastName.text.toString().trim()
        val phone = mobileNo.text.toString().trim()
        val department = departmentSpinner.selectedItem.toString()

        // If any field that is required is empty, show an error.
        if (fName.isEmpty() && lName.isEmpty() && phone.isEmpty() && department.isEmpty() && selectedSubjects.isEmpty() && selectedSemesters.isEmpty()) {
            Toast.makeText(this, "Please fill at least one field", Toast.LENGTH_SHORT).show()
            return
        }

        // Only allow updating selected fields and ignore unchanged ones
        val updatedUser = mutableMapOf<String, Any>()

        // Add fields to update only if they are changed (not empty)
        if (fName.isNotEmpty()) updatedUser["firstName"] = fName
        if (lName.isNotEmpty()) updatedUser["lastName"] = lName
        if (phone.isNotEmpty()) updatedUser["phone"] = phone
        if (department != "Select Department") updatedUser["department"] = department // Check if department is changed

        if (selectedSemesters.isNotEmpty()) updatedUser["semesters"] = selectedSemesters
        if (selectedSubjects.isNotEmpty()) updatedUser["subjects"] = selectedSubjects

        if (updatedUser.isEmpty()) {
            Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog.show()

        // Get the current user ID from Firebase Auth
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Update the Firestore document with only the modified fields
            db.collection("users")
                .document(userId)
                .update(updatedUser)
                .addOnSuccessListener {
                    loadingDialog.dismiss()
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()  // Go back to the previous screen
                }
                .addOnFailureListener {
                    loadingDialog.dismiss()
                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
