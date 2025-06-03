package com.pbina.attendeasy

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.adapter.AttendanceSumaryAdapter
import com.pbina.attendeasy.model.AttendanceSummary

class StudentViewAttendance : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AttendanceSumaryAdapter
    private lateinit var tvOverallPercentage: TextView
    private val attendanceList = mutableListOf<AttendanceSummary>()  // 👈 Maintain mutable list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_view_attendance)

        recyclerView = findViewById(R.id.recyclerViewSubjectAttendance)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tvOverallPercentage = findViewById(R.id.tvOverallPercentage)

        // Initialize adapter once
        adapter = AttendanceSumaryAdapter(attendanceList)
        recyclerView.adapter = adapter

        loadAttendance()
        loadoverallAttendance()
    }

    private fun loadoverallAttendance() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val overallPercentage = document.getDouble("attendancePercentage") ?: 0.0
                    tvOverallPercentage.text = "🎯 Overall Attendance: %.2f%%".format(overallPercentage)
                } else {
                    tvOverallPercentage.text = "No overall attendance data found."
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch overall attendance.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadAttendance() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val subjectAttendanceMap = document.get("subjectAttendance") as? Map<String, Map<String, Long>>
                    if (subjectAttendanceMap != null) {
                        attendanceList.clear()

                        var totalAttended = 0
                        var totalPossible = 0

                        for ((subject, data) in subjectAttendanceMap) {
                            val attended = (data["attended"] ?: 0).toInt()
                            val total = (data["total"] ?: 0).toInt()
                            val percentage = if (total > 0) (attended.toDouble() / total) * 100 else 0.0

                            totalAttended += attended
                            totalPossible += total

                            attendanceList.add(
                                AttendanceSummary(
                                    subject = subject,
                                    attended = attended,
                                    total = total,
                                    percentage = percentage
                                )
                            )
                        }

                        adapter.notifyDataSetChanged()

                        // 🧮 Set overall attendance only if totalPossible > 0
                        if (totalPossible > 0) {
                            val overallPercentage = (totalAttended.toDouble() / totalPossible) * 100
                            tvOverallPercentage.text = "🎯 Overall Attendance: %.2f%%".format(overallPercentage)
                        } else {
                            tvOverallPercentage.text = "🎯 Overall Attendance: 0.00%"
                        }

                    } else {
                        tvOverallPercentage.text = "No subject-wise attendance data found."
                    }
                } else {
                    Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load attendance.", Toast.LENGTH_SHORT).show()
            }
    }


}




