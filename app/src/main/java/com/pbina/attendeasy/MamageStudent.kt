package com.pbina.attendeasy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.adapters.ManageStudentAdapter
import com.pbina.attendeasy.adapters.ManageTeacherAdapter
import com.pbina.attendeasy.model.Student
import com.pbina.attendeasy.model.Teacher

class MamageStudent : AppCompatActivity() {

    private lateinit var studentRecyclerView: RecyclerView
    //private lateinit var fabAddStudent: FloatingActionButton
    private lateinit var searchView: SearchView
    private val studentList = mutableListOf<Student>()
    private lateinit var manageStudentAdapter: ManageStudentAdapter
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mamage_student)

        studentRecyclerView = findViewById(R.id.student_recycler_view)
        //fabAddStudent = findViewById(R.id.fab_add_student)
        searchView = findViewById(R.id.search_bar)
        toolbar = findViewById(R.id.toolbar)

        // Setup Toolbar
        setupToolbar()
        manageStudentAdapter = ManageStudentAdapter(
            context = this, // or `requireContext()` if inside a Fragment
            studentList = studentList,
            onDeleteClick = { student ->
                // Handle delete action here
            }
        )


        studentRecyclerView.layoutManager = LinearLayoutManager(this)
        studentRecyclerView.adapter = manageStudentAdapter

        loadStudents()

        // Set up Search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("Search", "onQueryTextSubmit: $query")
                // Filter students based on search query
                manageStudentAdapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("Search", "onQueryTextChange: $newText")
                // Filter students based on search query
                manageStudentAdapter.filter.filter(newText)
                return true
            }
        })
    }
    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow) // Set back arrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun loadStudents() {
        val db = FirebaseFirestore.getInstance()
        val students = mutableListOf<Student>()

        db.collection("users")
            .whereEqualTo("role", "Student")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val student = document.toObject(Student::class.java).copy(uid = document.id)
                    students.add(student)
                }

                // Log the size of the student list to ensure it is populated
                Log.d("StudentList", "Size of student list: ${students.size}")

                // Now update RecyclerView or UI here
                updateRecyclerView(students)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateRecyclerView(studentList: MutableList<Student>)
    {
        val adapter = ManageStudentAdapter(
            context = this,
            studentList = studentList.toMutableList(),
            onDeleteClick = { student ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Confirmation")
                    .setMessage("Are you sure you want to delete ${student.firstName}?")
                    .setPositiveButton("Delete") { _, _ ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users")
                            .document(student.uid) // assuming UID is stored in teacher.uid
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "${student.firstName} deleted", Toast.LENGTH_SHORT).show()

                                // Remove from list and refresh adapter
                                studentList.remove(student)
                                updateRecyclerView(studentList)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to delete ${student.firstName}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

        )
        studentRecyclerView.adapter = adapter
    }
}

