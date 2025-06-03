package com.pbina.attendeasy


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterStudent : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var dob: EditText
    private lateinit var rollNumber: EditText
    private lateinit var batchYear: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var departmentSpinner: Spinner
    private lateinit var semesterSpinner: Spinner
    private lateinit var subjectTextView: TextView
    private val selectedSubjects = mutableListOf<String>()

    private lateinit var registerButton: AppCompatButton
    private lateinit var loginText: TextView
    private lateinit var loadingDialog: AlertDialog

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var departmentList: MutableList<String>
    private lateinit var semesterList: MutableList<String>
    private lateinit var subjectList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_student)



        initViews()
        setupLoadingDialog()
        setupListeners()
        loadDepartments()
    }

    private fun initViews() {
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.mobileno)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        dob = findViewById(R.id.dob)
        rollNumber = findViewById(R.id.rollNumber)
        batchYear = findViewById(R.id.batchYear)
        genderSpinner = findViewById(R.id.spinner_gender)
        departmentSpinner = findViewById(R.id.spinner_department)
        semesterSpinner = findViewById(R.id.spinner_semester)
        subjectTextView = findViewById(R.id.subjectMultiSelect)

        registerButton = findViewById(R.id.btnRegister)
        loginText = findViewById(R.id.goToLogin)

        departmentList = mutableListOf("Select Department")
        semesterList = mutableListOf("Select Semester")
        subjectList = mutableListOf("Select Subject")

        genderSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Select Gender", "Male", "Female", "Other"))
    }

    private fun setupListeners() {
        dob.setOnClickListener {
            showDatePicker()
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        registerButton.setOnClickListener {
            registerStudent()
        }

        departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) loadSemesters(departmentList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        semesterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val dept = departmentSpinner.selectedItem.toString()
                    val sem = semesterList[position]
                    loadSubjects(dept, sem)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupLoadingDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null)
        loadingDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    private fun loadDepartments() {
        db.collection("Department").get().addOnSuccessListener { result ->
            departmentList.clear()
            departmentList.add("Select Department")
            for (doc in result) {
                departmentList.add(doc.id)
            }
            departmentSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, departmentList)
        }
    }

    private fun loadSemesters(department: String) {
        db.collection("Department").document(department).collection("Semesters")
            .get().addOnSuccessListener { result ->
                semesterList.clear()
                semesterList.add("Select Semester")
                for (doc in result) {
                    semesterList.add(doc.id)
                }
                semesterSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, semesterList)
            }
    }

    private fun loadSubjects(department: String, semester: String) {
        db.collection("Department").document(department)
            .collection("Semesters").document(semester)
            .collection("Subjects")
            .get()
            .addOnSuccessListener { result ->
                subjectList.clear()
                for (doc in result) {
                    val subjectName = doc.getString("name") ?: doc.id
                    subjectList.add(subjectName)
                }

                subjectTextView.setOnClickListener {
                    showSubjectMultiSelectDialog()
                }
            }
    }
    private fun showSubjectMultiSelectDialog() {
        val selectedItems = BooleanArray(subjectList.size) { index ->
            selectedSubjects.contains(subjectList[index])
        }

        AlertDialog.Builder(this)
            .setTitle("Select Subjects")
            .setMultiChoiceItems(subjectList.toTypedArray(), selectedItems) { _, which, isChecked ->
                val subject = subjectList[which]
                if (isChecked) {
                    if (!selectedSubjects.contains(subject)) selectedSubjects.add(subject)
                } else {
                    selectedSubjects.remove(subject)
                }
            }
            .setPositiveButton("OK") { _, _ ->
                subjectTextView.text = if (selectedSubjects.isEmpty()) "Select Subjects"
                else selectedSubjects.joinToString(", ")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            dob.setText("$dayOfMonth/${month + 1}/$year")
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    private fun registerStudent() {


        val fName = firstName.text.toString().trim()
        val lName = lastName.text.toString().trim()
        val mail = email.text.toString().trim()
        val phoneNo = phone.text.toString().trim()
        val pass = password.text.toString()
        val confirmPass = confirmPassword.text.toString()
        val dateOfBirth = dob.text.toString().trim()
        val roll = rollNumber.text.toString().trim().toIntOrNull()
        val batch = batchYear.text.toString().trim().toIntOrNull()
        val gender = genderSpinner.selectedItem.toString()
        val dept = departmentSpinner.selectedItem?.toString() ?: ""
        val sem = semesterSpinner.selectedItem?.toString() ?: ""
        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "Please select at least one subject", Toast.LENGTH_SHORT).show()
            return
        }


        if (fName.isEmpty() || lName.isEmpty() || mail.isEmpty()  || pass.isEmpty()
            || confirmPass.isEmpty() || dateOfBirth.isEmpty() || roll == null
            || gender == "Select Gender" || dept.isEmpty() || dept == "Select Department"
            || sem.isEmpty() || sem == "Select Semester"
        )
        {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "Please select at least one subject", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.error = "Invalid Email"
            return
        }

        if (pass != confirmPass) {
            confirmPassword.error = "Passwords do not match"
            return
        }
        // Remove non-numeric characters from phone number
        val cleanPhoneNo = phoneNo.replace(Regex("[^0-9]"), "")

        // Check if the phone number is valid
        if (phoneNo.isEmpty()) {
            phone.error = "Phone number is required"
            phone.requestFocus()
            return
        }

        if (cleanPhoneNo.length != 10 || !cleanPhoneNo.matches(Regex("^[6-9]\\d{9}$"))) {
            phone.error = "Enter a valid 10-digit phone number"
            phone.requestFocus()
            return
        }


        loadingDialog.show()

        auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser!!.uid
                val userMap = hashMapOf(
                    "firstName" to fName,
                    "lastName" to lName,
                    "email" to mail,
                    "phone" to cleanPhoneNo,
                    "dob" to dateOfBirth,
                    "rollNumber" to roll,
                    "batchYear" to batch,
                    "gender" to gender,
                    "department" to dept,
                    "semester" to sem,
                    "subjects" to selectedSubjects,
                    "role" to "Student"
                )

                db.collection("users").document(uid).set(userMap).addOnSuccessListener {
                    loadingDialog.dismiss()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }.addOnFailureListener {
                    loadingDialog.dismiss()
                    Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show()
                }
            } else {
                loadingDialog.dismiss()
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
