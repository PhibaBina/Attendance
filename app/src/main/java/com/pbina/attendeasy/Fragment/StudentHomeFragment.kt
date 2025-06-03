package com.pbina.attendeasy.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.pbina.attendeasy.R
import com.pbina.attendeasy.StudentViewAttendance
import com.pbina.attendeasy.ViewNotes

class StudentHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.activity_student_home_fragment, container, false)

        view.findViewById<LinearLayout>(R.id.btn_view_attendance).setOnClickListener {
            startActivity(Intent(requireContext(), StudentViewAttendance::class.java))
        }

        view.findViewById<LinearLayout>(R.id.btn_view_assignments).setOnClickListener {
           // startActivity(Intent(requireContext(), ViewAssignmentsActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.btn_view_notes).setOnClickListener {
            startActivity(Intent(requireContext(), ViewNotes::class.java))
        }



        return view
    }
}
