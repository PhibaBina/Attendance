package com.pbina.attendeasy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbina.attendeasy.R
import com.pbina.attendeasy.model.AttendanceReport

class ReportAdapter(private val reportList: List<AttendanceReport>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]

        // Set the student name, department, and attendance percentage
        holder.tvStudentName.text = report.firstName + " " +report.lastName
       // holder.tvAttended.text = report.attended.toString()
        //holder.tvTotal.text = report.total.toString()
        //holder.tvAttendancePercentage.text = "${report.attendancePercentage}%"  // Display attendance percentage
        // Format the attendance percentage to two decimal places
        holder.tvAttendancePercentage.text = String.format("%.2f%%", report.attendancePercentage)

        // Set the roll number as a string
        holder.tvRollNumber.text = report.rollNumber.toString()  // Convert Int to String


    }

    override fun getItemCount(): Int = reportList.size

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvRollNumber: TextView = view.findViewById(R.id.tvRollNumber)
        //val tvAttended: TextView = view.findViewById(R.id.tvAttended)
        //val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val tvAttendancePercentage: TextView = view.findViewById(R.id.tvAttendancePercentage)
    }
}
