package com.pbina.attendeasy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbina.attendeasy.R
import com.pbina.attendeasy.model.Student
import com.pbina.attendeasy.model.Teacher


class ManageStudentAdapter(
    private val context: Context,
    private val studentList: MutableList<Student>,
    private val onDeleteClick: (Student) -> Unit
) : RecyclerView.Adapter<ManageStudentAdapter.StudentViewHolder>(), Filterable {

    private val studentListFull = ArrayList(studentList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.name.text = student.firstName + " " + student.lastName
        holder.rollNumber.text = "Roll No: ${student.rollNumber}"
        holder.department.text = "Department: ${student.department}"
        holder.semester.text = "Semester: ${student.semester}"
        holder.subject.text = "Subject: ${student.subject}"

        holder.threeDotMenu.setOnClickListener {
            showPopupMenu(it, student)
        }
    }
    private fun showPopupMenu(view: View, student: Student) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.delete_edid_teacher) // from menu
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_teacher -> {
                    onDeleteClick(student) // let activity handle confirmation
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun getItemCount(): Int = studentList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Student>()
                val topMatches = ArrayList<Student>()

                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(studentListFull)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    for (student in studentListFull) {
                        // If first name matches, add to topMatches list
                        if (student.firstName.lowercase().contains(filterPattern)) {
                            topMatches.add(student)
                        } else if (student.lastName.lowercase().contains(filterPattern) || student.rollNumber.toString().contains(filterPattern)) {
                            // If last name or roll number matches, add to filteredList
                            filteredList.add(student)
                        }
                    }
                }

                // Combine top matches and other matches to prioritize first name matches at the top
                filteredList.addAll(0, topMatches)

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                studentList.clear()
                if (results?.values != null) {
                    studentList.addAll(results.values as List<Student>)
                }
                notifyDataSetChanged() // This will update the RecyclerView
            }
        }
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.student_name)
        val rollNumber: TextView = itemView.findViewById(R.id.student_roll_number)
        val department: TextView = itemView.findViewById(R.id.student_department)
        val semester: TextView = itemView.findViewById(R.id.student_semester)
        val subject: TextView = itemView.findViewById(R.id.subject)
        val threeDotMenu: ImageView = itemView.findViewById(R.id.three_dot_menu)
    }
}
