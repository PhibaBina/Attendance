package com.pbina.attendeasy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbina.attendeasy.R
import com.pbina.attendeasy.model.Teacher

class TeacherAdapter(
    private var teachers: List<Teacher>,
    private val onApproveClick: (Teacher) -> Unit,
    private val onRejectClick: (Teacher) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    inner class TeacherViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.TeacherName)
        val tvEmail: TextView = view.findViewById(R.id.TeacherEmail)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teacher, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teachers[position]
        holder.tvName.text = teacher.firstName
        holder.tvEmail.text = teacher.email

        holder.btnApprove.setOnClickListener {
            onApproveClick(teacher)
        }

        holder.btnReject.setOnClickListener {
            onRejectClick(teacher)
        }
    }

    override fun getItemCount(): Int = teachers.size
}
