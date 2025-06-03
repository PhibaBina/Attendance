package com.pbina.attendeasy.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.pbina.attendeasy.About
import com.pbina.attendeasy.FeedbackHelp
import com.pbina.attendeasy.R
import com.pbina.attendeasy.Setting

class StudentMoreFragment : Fragment() {

    private lateinit var btnNotification: LinearLayout
    private lateinit var btnDownload: LinearLayout
    private lateinit var btnFeedback: LinearLayout
    private lateinit var btnAbout: LinearLayout
    private lateinit var btnRate: LinearLayout
    private lateinit var btnSettings: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_student_more_fragment, container, false)

        // Initialize views
        btnNotification = view.findViewById(R.id.btn_notification)
        btnDownload = view.findViewById(R.id.btn_download)
        btnFeedback = view.findViewById(R.id.btn_feedback)
        btnAbout = view.findViewById(R.id.btn_about)

        btnSettings = view.findViewById(R.id.btn_settings)

        // Set Click Listeners
        btnNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Notifications Clicked", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }

        btnDownload.setOnClickListener {
            Toast.makeText(requireContext(), "Downloads Clicked", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(requireContext(), DownloadsActivity::class.java))
        }

        btnFeedback.setOnClickListener {
             startActivity(Intent(requireContext(), FeedbackHelp::class.java))
        }

        btnAbout.setOnClickListener {
             startActivity(Intent(requireContext(), About::class.java))
        }

        btnSettings.setOnClickListener {

             startActivity(Intent(requireContext(), Setting::class.java))
        }

        return view
    }
}
