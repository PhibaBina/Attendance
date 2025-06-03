package com.pbina.attendeasy.Fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pbina.attendeasy.Login
import com.pbina.attendeasy.R
import com.pbina.attendeasy.model.Student

class StudentProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvRollNumber: TextView
    private lateinit var tvDepartment: TextView
    private lateinit var tvSemester: TextView
    private lateinit var tvSubjectList: TextView
    //private lateinit var tvBatch: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvDob: TextView
    private lateinit var tvAttendancePercentage: TextView
    private lateinit var ivProfilePic: ImageView

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable options menu in fragment
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_student_profile_fragment, container, false)

        val toolbar = rootView.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        // Initialize views
        tvName = rootView.findViewById(R.id.tvName)
        tvEmail = rootView.findViewById(R.id.tvEmail)
        tvPhone = rootView.findViewById(R.id.tvPhone)
        tvRollNumber = rootView.findViewById(R.id.tvRollNumber)
        tvDepartment = rootView.findViewById(R.id.tvDepartment)
        tvSemester = rootView.findViewById(R.id.tvSemester)
        tvSubjectList = rootView.findViewById(R.id.tvSubjectList)
        //tvBatch = rootView.findViewById(R.id.tvBatch)
        tvGender = rootView.findViewById(R.id.tvGender)
        tvDob = rootView.findViewById(R.id.tvDob)
        tvAttendancePercentage = rootView.findViewById(R.id.tvattendandePercentage)
        ivProfilePic = rootView.findViewById(R.id.ivProfilePic)


        // Fetch data from Firestore when the fragment is created
        currentUser?.let {
            val userRef = firestore.collection("users").document(it.uid)
            val attendanceRef = firestore.collection("Attendance").document(it.uid)

            // Fetch user details
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(Student::class.java)
                    user?.let {
                        // Populate fields
                        tvName.text = it.firstName + "" + it.lastName
                        tvEmail.text = it.email
                        tvPhone.text = "Phone: ${it.phone}"
                        tvRollNumber.text = "Roll Number: ${it.rollNumber}"
                        tvDepartment.text = "Department: ${it.department}"
                        tvSemester.text = "Semester: ${it.semester}"
                        //tvBatch.text = "Batch: ${it.batchYear}"
                        tvGender.text = "Gender: ${it.gender}"
                        tvDob.text = "DOB: ${it.dob}"
                        val formattedPercentage = String.format("%.2f", it.attendancePercentage)
                        tvAttendancePercentage.text = "Attendance Percentage: $formattedPercentage%"
                        // Display subject list (assuming it's a list of subjects stored in Firestore)
                        tvSubjectList.text = "Subjects: ${it.subject.joinToString(", ")}"


                    }
                }
            }

        }


        return rootView
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(requireContext(), Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
