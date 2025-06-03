package com.pbina.attendeasy

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.R
import com.pbina.attendeasy.TeacherWaitingScreen
import java.util.*

class RegisterTeacher : AppCompatActivity() {

    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var idNumber: TextInputEditText
    private lateinit var mobileNo: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var genderSpinner: Spinner
    private lateinit var departmentSpinner: Spinner
    private lateinit var semesterTextView: TextView
    private lateinit var subjectTextView: TextView
    private lateinit var registerBtn: Button

    private lateinit var loadingDialog: Dialog

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var selectedGender = ""
    private var selectedDepartment = ""
    private var selectedSemesters = mutableListOf<String>()
    private var selectedSubjects = mutableListOf<String>()
    private var allSemesters = listOf<String>()
    private var allSubjects = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_teacher)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupLoadingDialog()
        setupGenderSpinner()
        loadDepartments()

        departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Handle selection
                selectedDepartment = parent?.getItemAtPosition(position).toString()

                // Avoid triggering loadSemesters when 'Select Department' is selected
                if (selectedDepartment != "Select Department") {
                    loadSemesters(selectedDepartment)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when no item is selected (if needed)
            }
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

        registerBtn.setOnClickListener {
            registerTeacher()
        }
    }

    private fun initViews() {
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        idNumber = findViewById(R.id.idnumber)
        mobileNo = findViewById(R.id.mobileno)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        genderSpinner = findViewById(R.id.spinner_gender)
        departmentSpinner = findViewById(R.id.spinner_department)
        semesterTextView = findViewById(R.id.semesterMultiSelect)
        subjectTextView = findViewById(R.id.subjectMultiSelect)
        registerBtn = findViewById(R.id.btnRegister)
    }

    private fun setupLoadingDialog() {
        loadingDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null)
        loadingDialog.setContentView(view)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupGenderSpinner() {
        val genderList = listOf("Select Gender", "Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderList)
        genderSpinner.adapter = adapter
        genderSpinner.setSelection(0)
    }

    private fun loadDepartments() {
        db.collection("Department").get().addOnSuccessListener { result ->
            val departmentList = mutableListOf("Select Department")
            for (doc in result) {
                departmentList.add(doc.id)
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, departmentList)
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

    private fun showMultiSelectDialog(title: String, items: List<String>, selectedItems: MutableList<String>, onConfirm: () -> Unit) {
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

    private fun registerTeacher() {
        val fName = firstName.text.toString().trim()
        val lName = lastName.text.toString().trim()
        val emailStr = email.text.toString().trim()
        val idStr = idNumber.text.toString().trim()
        val phone = mobileNo.text.toString().trim()
        val pass = password.text.toString().trim()
        val cPass = confirmPassword.text.toString().trim()
        val gender = genderSpinner.selectedItem.toString()
        val department = departmentSpinner.selectedItem.toString()

        if (fName.isEmpty() || lName.isEmpty() || emailStr.isEmpty() || idStr.isEmpty()
            || phone.isEmpty() || pass.isEmpty() || cPass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (!phone.matches(Regex("^\\d{10}$"))) {
            Toast.makeText(this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show()
            return
        }


        if (gender == "Select Gender" || department == "Select Department") {
            Toast.makeText(this, "Please select valid gender and department", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSemesters.isEmpty()) {
            Toast.makeText(this, "Please select at least one semester", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "Please select at least one subject", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != cPass) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog.show()

        auth.createUserWithEmailAndPassword(emailStr, pass)
            .addOnSuccessListener {
                val uid = it.user?.uid ?: return@addOnSuccessListener
                val data = hashMapOf(
                    "firstName" to fName,
                    "lastName" to lName,
                    "email" to emailStr,
                    "idNumber" to idStr,
                    "phone" to phone,
                    "gender" to gender,
                    "department" to department,
                    "semesters" to selectedSemesters,
                    "subjects" to selectedSubjects,
                    "role" to "Teacher",
                    "isApproved" to false,
                )
                db.collection("users").document(uid).set(data)
                    .addOnSuccessListener {
                        loadingDialog.dismiss()

                        startActivity(Intent(this, TeacherWaitingScreen::class.java))
                        finish()
                    }
            }
            .addOnFailureListener {
                loadingDialog.dismiss()
                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
