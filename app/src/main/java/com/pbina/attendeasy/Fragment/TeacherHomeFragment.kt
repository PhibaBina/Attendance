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
import com.pbina.attendeasy.Contents
import com.pbina.attendeasy.GenerateReport
import com.pbina.attendeasy.MarkAttendanceSelectActivity
import com.pbina.attendeasy.R
import com.pbina.attendeasy.ViewNotes

class TeacherHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.activity_teacher_home_fragment, container, false)

        view.findViewById<LinearLayout>(R.id.btnAttendance).setOnClickListener {
             startActivity(Intent(requireContext(), MarkAttendanceSelectActivity ::class.java))
        }

        view.findViewById<LinearLayout>(R.id.btnContents).setOnClickListener {
            startActivity(Intent(requireContext(), Contents::class.java))
        }

        view.findViewById<LinearLayout>(R.id.btnRoutine).setOnClickListener {
           // startActivity(Intent(requireContext(), ViewNotes::class.java))
        }
        view.findViewById<LinearLayout>(R.id.btnReport).setOnClickListener {
             startActivity(Intent(requireContext(), GenerateReport::class.java))
        }
        return view
    }
}
