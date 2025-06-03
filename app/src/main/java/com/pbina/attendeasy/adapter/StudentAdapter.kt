package com.pbina.attendeasy.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.R
import com.pbina.attendeasy.model.Student

class StudentAdapter(
    private val attendanceMap: MutableMap<String, Boolean>,
    private val selectedPeriod: String // Periods selected by the teacher (e.g., "Period 1, Period 2")
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var students = listOf<Student>()

    fun getStudentById(uid: String): Student? {
        return students.find { it.uid == uid }
    }

    fun updateList(newStudents: List<Student>) {
        students = newStudents.sortedBy { it.rollNumber }
        notifyDataSetChanged()
    }

    fun updateData(newMap: Map<String, Boolean>) {
        attendanceMap.clear()
        attendanceMap.putAll(newMap)
        notifyDataSetChanged()
    }

    fun getAttendanceMap(): Map<String, Boolean> = attendanceMap

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int = students.size

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roll: TextView = itemView.findViewById(R.id.textRollNo)
        private val name: TextView = itemView.findViewById(R.id.textName)
        private val status: TextView = itemView.findViewById(R.id.textAttendancePercentage)
        private val presentBtn: TextView = itemView.findViewById(R.id.tvPresent)
        private val absentBtn: TextView = itemView.findViewById(R.id.tvAbsent)

        fun bind(student: Student) {
            roll.text = student.rollNumber.toString()
            name.text = "${student.firstName} ${student.lastName}"
            status.text = "%.2f%%".format(student.attendancePercentage)

            // Reset button colors
            presentBtn.setTextColor(Color.BLACK)
            absentBtn.setTextColor(Color.BLACK)
            presentBtn.setBackgroundColor(Color.WHITE)
            absentBtn.setBackgroundColor(Color.WHITE)

            // Set button states based on attendance map
            if (attendanceMap[student.uid] == true) {
                presentBtn.setTextColor(Color.WHITE)
                presentBtn.setBackgroundColor(Color.GREEN)
            } else if (attendanceMap[student.uid] == false) {
                absentBtn.setTextColor(Color.WHITE)
                absentBtn.setBackgroundColor(Color.RED)
            }

            // Count the number of periods selected
            val periodsCount = selectedPeriod.split(",").size

            // Handle present button click
            presentBtn.setOnClickListener {
                attendanceMap[student.uid] = true

                // Set button states
                presentBtn.setTextColor(Color.WHITE)
                presentBtn.setBackgroundColor(Color.GREEN)
                absentBtn.setTextColor(Color.BLACK)
                absentBtn.setBackgroundColor(Color.WHITE)

                // Update attended and total counts based on selected periods
                val newTotal = student.total + periodsCount
                val newAttended = student.attended + periodsCount
                val newPercentage = (newAttended.toFloat() * 100f) / newTotal
                status.text = "%.2f%%".format(newPercentage)

                // Update the Firebase database with the new attendance data
                updateAttendanceInFirebase(student, newAttended, newTotal, newPercentage)
            }

            // Handle absent button click
            absentBtn.setOnClickListener {
                attendanceMap[student.uid] = false

                // Set button states
                absentBtn.setTextColor(Color.WHITE)
                absentBtn.setBackgroundColor(Color.RED)
                presentBtn.setTextColor(Color.BLACK)
                presentBtn.setBackgroundColor(Color.WHITE)

                // Update attended and total counts based on selected periods
                val newTotal = student.total + periodsCount
                val newAttended = student.attended
                val newPercentage = (newAttended.toFloat() * 100f) / newTotal
                status.text = "%.2f%%".format(newPercentage)

                // Update the Firebase database with the new attendance data
                updateAttendanceInFirebase(student, newAttended, newTotal, newPercentage)
            }
        }

        // Function to update attendance in Firebase
        private fun updateAttendanceInFirebase(
            student: Student,
            newAttended: Int,
            newTotal: Int,
            newPercentage: Float
        ) {
            val studentId = student.uid
            if (!studentId.isNullOrEmpty()) {
                val studentRef = FirebaseFirestore.getInstance().collection("students").document(studentId)
                studentRef.update(
                    "attended", newAttended,
                    "total", newTotal,
                    "attendancePercentage", newPercentage
                ).addOnSuccessListener {
                    Log.d("Attendance", "Attendance updated for $studentId")
                }.addOnFailureListener { e ->
                    Log.e("Attendance", "Error updating attendance: $e")
                }
            } else {
                Log.e("Attendance", "Invalid student ID")
            }
        }
    }
}
