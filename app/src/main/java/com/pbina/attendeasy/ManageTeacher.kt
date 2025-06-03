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

class ManageTeacher : AppCompatActivity() {

    private lateinit var teacherRecyclerView: RecyclerView
    //private lateinit var fabAddStudent: FloatingActionButton
    private lateinit var searchView: SearchView
    private val teacherList = mutableListOf<Teacher>()
    private lateinit var manageTeacherAdapter: ManageTeacherAdapter
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_teacher)

        teacherRecyclerView = findViewById(R.id.teacher_recycler_view)
        //fabAddStudent = findViewById(R.id.fab_add_student)
        searchView = findViewById(R.id.search_bar)

        toolbar = findViewById(R.id.toolbar)

        // Setup Toolbar
        setupToolbar()

        manageTeacherAdapter = ManageTeacherAdapter(
            context = this, // or `requireContext()` if inside a Fragment
            teacherList = teacherList,
            onDeleteClick = { teacher ->
                // Handle delete action here
            }
        )

        teacherRecyclerView.layoutManager = LinearLayoutManager(this)
        teacherRecyclerView.adapter = manageTeacherAdapter

        // Set up Floating Action Button click listener
//        fabAddStudent.setOnClickListener {
//            // Open Add Student activity
//            startActivity(Intent(this, AddStudent::class.java))
//        }

        // Dummy data for students
        loadTeacher()

        // Set up Search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("Search", "onQueryTextSubmit: $query")
                // Filter students based on search query
                manageTeacherAdapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("Search", "onQueryTextChange: $newText")
                // Filter students based on search query
                manageTeacherAdapter.filter.filter(newText)
                return true
            }
        })
    }
    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow) // Set back arrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun loadTeacher() {
        val db = FirebaseFirestore.getInstance()
        val teachers = mutableListOf<Teacher>()

        db.collection("users")
            .whereEqualTo("role", "Teacher")
            .whereEqualTo("isApproved", true)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val teacher = document.toObject(Teacher::class.java).copy(id = document.id)
                    teachers.add(teacher)
                }

                // Log the size of the student list to ensure it is populated
                Log.d("TeacherList", "Size of student list: ${teachers.size}")

                // Now update RecyclerView or UI here
                updateRecyclerView(teachers)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateRecyclerView(teacherList: MutableList<Teacher>)
    {
        val adapter = ManageTeacherAdapter(
            context = this,
            teacherList = teacherList.toMutableList(),
            onDeleteClick = { teacher ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Confirmation")
                    .setMessage("Are you sure you want to delete ${teacher.firstName}?")
                    .setPositiveButton("Delete") { _, _ ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users")
                            .document(teacher.id) // assuming UID is stored in teacher.uid
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "${teacher.firstName} deleted", Toast.LENGTH_SHORT).show()

                                // Remove from list and refresh adapter
                                teacherList.remove(teacher)
                                updateRecyclerView(teacherList)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to delete ${teacher.firstName}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

        )
        teacherRecyclerView.adapter = adapter
    }

}

