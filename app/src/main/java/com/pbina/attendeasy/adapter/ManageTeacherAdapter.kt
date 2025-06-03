package com.pbina.attendeasy.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.pbina.attendeasy.R
import com.pbina.attendeasy.model.Teacher

class ManageTeacherAdapter(
    private val context: Context,
    private val teacherList: MutableList<Teacher>,
    private val onDeleteClick: (Teacher) -> Unit
) : RecyclerView.Adapter<ManageTeacherAdapter.TeacherViewHolder>(), Filterable {

    private val teacherListFull = ArrayList(teacherList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_teacher, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teacherList[position]
        holder.name.text = "${teacher.firstName} ${teacher.lastName}"
        holder.rollNumber.text = "ID Number: ${teacher.idNumber}"
        holder.department.text = "Department: ${teacher.department}"
        holder.semester.text = "Semester: ${teacher.semesters}"
        holder.subject.text = "Subject: ${teacher.subjects}"

        holder.threeDotMenu.setOnClickListener {
            showPopupMenu(it, teacher)
        }
    }

    private fun showPopupMenu(view: View, teacher: Teacher) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.delete_edid_teacher) // Make sure you create this XML
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_teacher -> {
                    onDeleteClick(teacher) // let activity handle confirmation
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


    override fun getItemCount(): Int = teacherList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Teacher>()
                val topMatches = ArrayList<Teacher>()

                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(teacherListFull)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    for (teacher in teacherListFull) {
                        if (teacher.firstName.lowercase().contains(filterPattern)) {
                            topMatches.add(teacher)
                        } else if (teacher.lastName.lowercase().contains(filterPattern) || teacher.idNumber.toString().contains(filterPattern)) {
                            filteredList.add(teacher)
                        }
                    }
                }

                filteredList.addAll(0, topMatches)

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                teacherList.clear()
                if (results?.values != null) {
                    teacherList.addAll(results.values as List<Teacher>)
                }
                notifyDataSetChanged()
            }
        }
    }

    inner class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.teacher_name)
        val rollNumber: TextView = itemView.findViewById(R.id.teacherID)
        val department: TextView = itemView.findViewById(R.id.teacher_department)
        val semester: TextView = itemView.findViewById(R.id.teacher_semester)
        val subject: TextView = itemView.findViewById(R.id.subject)
        val threeDotMenu: ImageView = itemView.findViewById(R.id.three_dot_menu)
    }
}
