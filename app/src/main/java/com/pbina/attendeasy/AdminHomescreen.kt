package com.pbina.attendeasy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminHomescreen : AppCompatActivity() {

    private lateinit var tvPendingCount: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Launcher to handle result from ApproveTeacher activity
    private lateinit var approveTeacherLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_admin_homescreen)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        tvPendingCount = findViewById(R.id.tvPendingCount)
        val btnManageStudent = findViewById<LinearLayout>(R.id.btnManageStudent)
        val btnApproveTeachers = findViewById<RelativeLayout>(R.id.btnApproveTeachers)
        val btnManageTeacher = findViewById<LinearLayout>(R.id.btnManageTeacher)
        val btnReport = findViewById<LinearLayout>(R.id.btnReports)
        val btnDepartmentOverview = findViewById<LinearLayout>(R.id.btnDepartmentOverview)

        // Initialize launcher
        approveTeacherLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                checkPendingApprovals() // Refresh red badge count when returning
            }
        }
        btnDepartmentOverview.setOnClickListener {
            val intent = Intent(this, AdminDepartmentScreen::class.java)
            approveTeacherLauncher.launch(intent)
        }
        btnApproveTeachers.setOnClickListener {
            val intent = Intent(this, ApproveTeacher::class.java)
            approveTeacherLauncher.launch(intent)
        }
        btnManageStudent.setOnClickListener {
            val intent = Intent(this, MamageStudent::class.java)
            approveTeacherLauncher.launch(intent)
        }
        btnManageTeacher.setOnClickListener {
            val intent = Intent(this, ManageTeacher::class.java)
            approveTeacherLauncher.launch(intent)
        }
        btnReport.setOnClickListener {
            val intent = Intent(this, GenerateReport::class.java)
            approveTeacherLauncher.launch(intent)
        }


        checkPendingApprovals()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Perform logout functionality
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun logout() {
        // Create an AlertDialog for confirmation
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")

        // If user clicks "Yes", log them out
        builder.setPositiveButton("Yes") { _, _ ->
            // Sign out the user from Firebase Authentication
            FirebaseAuth.getInstance().signOut()

            // Redirect to the login screen
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

            // Finish the current activity so the user cannot go back to it
            finish()
        }

        // If user clicks "No", dismiss the dialog
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the confirmation dialog
        builder.create().show()
    }



    private fun checkPendingApprovals() {
        db.collection("users")
            .whereEqualTo("role", "Teacher")
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    tvPendingCount.visibility = View.GONE
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val count = snapshots.size()
                    if (count > 0) {
                        tvPendingCount.text = count.toString()
                        tvPendingCount.visibility = View.VISIBLE
                    } else {
                        tvPendingCount.visibility = View.GONE
                    }
                }
            }
    }

}
