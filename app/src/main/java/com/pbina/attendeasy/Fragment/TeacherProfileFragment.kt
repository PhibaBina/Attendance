package com.pbina.attendeasy.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.ChangePassword
import com.pbina.attendeasy.EditProfile
import com.pbina.attendeasy.Login
import com.pbina.attendeasy.R
import com.pbina.attendeasy.model.Teacher

class TeacherProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvIDNumber: TextView
    private lateinit var tvDepartment: TextView
    private lateinit var tvSemester: TextView
    private lateinit var tvSubjectList: TextView
    private lateinit var tvGender: TextView
    private lateinit var ivProfilePic: ImageView
    private lateinit var btnEditProfile: LinearLayout
    private lateinit var btnChangePassword: LinearLayout

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
        val rootView =
            inflater.inflate(R.layout.activity_teacher_profile_fragment, container, false)

        // Set up toolbar
        val toolbar = rootView.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        // Initialize views
        tvName = rootView.findViewById(R.id.tvName)
        tvEmail = rootView.findViewById(R.id.tvEmail)
        tvPhone = rootView.findViewById(R.id.tvPhone)
        tvDepartment = rootView.findViewById(R.id.tvDepartment)
        tvSemester = rootView.findViewById(R.id.tvSemester)
        tvSubjectList = rootView.findViewById(R.id.tvSubjectList)
        tvGender = rootView.findViewById(R.id.tvGender)
        ivProfilePic = rootView.findViewById(R.id.ivProfilePic)
        tvIDNumber = rootView.findViewById(R.id.tvIDNumber)

        btnEditProfile = rootView.findViewById(R.id.btnEditProfile)
        btnChangePassword = rootView.findViewById(R.id.btnChangePassword)

        // Fetch data from Firestore
        currentUser?.let {
            val userRef = firestore.collection("users").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(Teacher::class.java)
                    user?.let {
                        tvName.text = "${it.firstName} ${it.lastName}"
                        tvEmail.text = it.email
                        tvPhone.text = "Phone: ${it.phone}"
                        tvIDNumber.text = "ID Number: ${it.idNumber}"
                        tvDepartment.text = "Department: ${it.department}"
                        tvSemester.text = "Semester: ${it.semesters}"
                        tvGender.text = "Gender: ${it.gender}"
                        tvSubjectList.text = "Subjects: ${it.subjects.joinToString(", ")}"
                    }
                }
            }
        }

        btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
        }

        btnChangePassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePassword::class.java)
            startActivity(intent)
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
