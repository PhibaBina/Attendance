package com.pbina.attendeasy

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.adapter.TeacherAdapter
import com.pbina.attendeasy.model.Teacher

class ApproveTeacher : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var teacherAdapter: TeacherAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val teacherList = mutableListOf<Teacher>()
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approve_teacher)



        recyclerView = findViewById(R.id.teacherRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        toolbar = findViewById(R.id.toolbar)
        setupToolbar()

        teacherAdapter = TeacherAdapter(teacherList,
            onApproveClick = { teacher ->
                showConfirmationDialog("Approve", teacher) {
                    updateTeacherStatus(teacher, true) // Approving = true
                }
            },
            onRejectClick = { teacher ->
                showConfirmationDialog("Reject", teacher) {
                    updateTeacherStatus(teacher, false) // Rejecting = false
                }
            }
        )

        recyclerView.adapter = teacherAdapter

        loadPendingTeachers()
    }
    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow) // Set back arrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun loadPendingTeachers() {
        firestore.collection("users")
            .whereEqualTo("isApproved", false)

            .get()
            .addOnSuccessListener { querySnapshot ->
                teacherList.clear()

                // Check if there are any pending teacher requests
                if (querySnapshot.isEmpty) {
                    // No pending teachers, show the "No Pending Teachers" message
                    findViewById<LinearLayout>(R.id.noPendingTeachersLayout).visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    // There are pending teachers, show the RecyclerView
                    findViewById<LinearLayout>(R.id.noPendingTeachersLayout).visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                    for (doc in querySnapshot) {
                        val teacher = doc.toObject(Teacher::class.java).apply {
                            id = doc.id
                        }
                        teacherList.add(teacher)
                    }
                    teacherAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load teachers", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onBackPressed() {
        setResult(RESULT_OK) // This triggers refresh in AdminHomescreen when you return
        super.onBackPressed()
    }


//    private fun updateTeacherStatus(teacher: Teacher, newStatus: Boolean) {
//        firestore.collection("users").document(teacher.id)
//            .update("isApproved", newStatus) // Using Boolean true/false
//            .addOnSuccessListener {
//                Toast.makeText(this, "${teacher.name} has been updated", Toast.LENGTH_SHORT).show()
//                loadPendingTeachers() // Reload the list after update
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
//            }
//    }
private fun updateTeacherStatus(teacher: Teacher, newStatus: Boolean) {
    firestore.collection("users").document(teacher.id)
        .update("isApproved", newStatus) // Using Boolean true/false
        .addOnSuccessListener {
            Toast.makeText(this, "${teacher.firstName} has been updated", Toast.LENGTH_SHORT).show()

            setResult(RESULT_OK) //  This line is needed for AdminHomescreen to refresh red badge

            loadPendingTeachers() // Reload the list after update
        }
        .addOnFailureListener {
            Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
        }
}


    private fun showConfirmationDialog(action: String, teacher: Teacher, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("$action Teacher")
            .setMessage("Are you sure you want to $action ${teacher.firstName}?")
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
