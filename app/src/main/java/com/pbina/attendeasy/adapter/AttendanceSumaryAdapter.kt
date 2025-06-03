package com.pbina.attendeasy.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbina.attendeasy.R
import com.pbina.attendeasy.AttendanceDetail
import com.pbina.attendeasy.model.AttendanceSummary

class AttendanceSumaryAdapter(
    private val attendanceList: List<AttendanceSummary>
) : RecyclerView.Adapter<AttendanceSumaryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        val tvAttendance: TextView = itemView.findViewById(R.id.tvAttendance)
        val tvPercentage: TextView = itemView.findViewById(R.id.tvPercentage)
        val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_attendance_sumary_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = attendanceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = attendanceList[position]

        // Set subject name
        holder.tvSubject.text = "📘 ${item.subject}"

        // Set attended and total
        holder.tvAttendance.text = "✅ Attended: ${item.attended} / ${item.total}"

        // Set percentage
        holder.tvPercentage.text = "📊 Percentage: %.2f%%".format(item.percentage)

        // Button to view detailed attendance
        holder.btnViewDetails.setOnClickListener {
            val context: Context = holder.itemView.context
            val intent = Intent(context, AttendanceDetail::class.java)
            intent.putExtra("subject", item.subject)
            context.startActivity(intent)
        }
    }
}
