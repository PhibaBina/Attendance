package com.pbina.attendeasy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class AdminDepartmentScreen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val departmentList = mutableListOf<String>()
    private lateinit var adapter: DepartmentAdapter

    private lateinit var fabAdd: FloatingActionButton

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_department_screen)

        recyclerView = findViewById(R.id.recyclerViewDepartments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DepartmentAdapter(this, departmentList)
        recyclerView.adapter = adapter

        fabAdd = findViewById(R.id.fabAdd)

        fabAdd.setOnClickListener {
            val bottomSheet = AddBottomSheet { option ->
                when (option) {
                    "department" -> startActivity(Intent(this, AddDepartment::class.java))
                    "semester" -> startActivity(Intent(this, AddSemester::class.java))
                    "subject" -> startActivity(Intent(this, AddSubject::class.java))
                }
            }
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        Toast.makeText(this, "Tap a department to view its semesters and subjects.", Toast.LENGTH_LONG).show()
        fetchDepartments()


    }

    private fun fetchDepartments() {
        db.collection("Department")
            .get()
            .addOnSuccessListener { documents ->
                departmentList.clear()
                for (doc in documents) {
                    departmentList.add(doc.id)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching departments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    class DepartmentAdapter(
        private val context: AppCompatActivity,
        private val departments: List<String>
    ) : RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>() {

        private val db = FirebaseFirestore.getInstance()

        inner class DepartmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val departmentName: TextView = view.findViewById(R.id.departmentName)
            val semesterContainer: LinearLayout = view.findViewById(R.id.semesterContainer)
            val chevronIcon: TextView = view.findViewById(R.id.chevronIcon)
            val departmentHeader: LinearLayout = view.findViewById(R.id.departmentHeader)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_department, parent, false)
            return DepartmentViewHolder(view)
        }

        override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
            val department = departments[position]
            holder.departmentName.text = department
            holder.chevronIcon.text = "▶"

            holder.departmentHeader.setOnClickListener {
                if (holder.semesterContainer.visibility == View.VISIBLE) {
                    holder.semesterContainer.visibility = View.GONE
                    holder.chevronIcon.text = "▶"
                } else {
                    holder.semesterContainer.removeAllViews()
                    loadSemesters(department, holder.semesterContainer)
                    holder.semesterContainer.visibility = View.VISIBLE
                    holder.chevronIcon.text = "▼"
                }
            }
        }

        override fun getItemCount(): Int = departments.size

        private fun loadSemesters(department: String, container: LinearLayout) {
            db.collection("Department").document(department).collection("Semesters")
                .get()
                .addOnSuccessListener { semesters ->
                    for (semesterDoc in semesters) {
                        val semesterName = semesterDoc.id
                        val semesterView = LayoutInflater.from(context)
                            .inflate(R.layout.item_semesrer, container, false)

                        val semesterNameText = semesterView.findViewById<TextView>(R.id.semesterName)
                        val subjectContainer = semesterView.findViewById<LinearLayout>(R.id.subjectContainer)
                        val semesterChevron: TextView = semesterView.findViewById(R.id.semesterChevron)
                        val semesterHeader: LinearLayout = semesterView.findViewById(R.id.semesterHeader)

                        semesterNameText.text = semesterName
                        semesterChevron.text = "▶"

                        semesterHeader.setOnClickListener {
                            if (subjectContainer.visibility == View.VISIBLE) {
                                subjectContainer.visibility = View.GONE
                                semesterChevron.text = "▶"
                            } else {
                                subjectContainer.removeAllViews()
                                loadSubjects(department, semesterName, subjectContainer)
                                subjectContainer.visibility = View.VISIBLE
                                semesterChevron.text = "▼"
                            }
                        }


                        container.addView(semesterView)
                    }
                }
        }

        private fun loadSubjects(department: String, semester: String, container: LinearLayout) {
            db.collection("Department").document(department)
                .collection("Semesters").document(semester)
                .collection("Subjects")
                .get()
                .addOnSuccessListener { subjects ->
                    for (subjectDoc in subjects) {
                        val subjectName = subjectDoc.id
                        val subjectView = LayoutInflater.from(context)
                            .inflate(R.layout.item_subject, container, false)
                        subjectView.findViewById<TextView>(R.id.subjectName).text = subjectName
                        container.addView(subjectView)
                    }
                }
        }
    }
}
