package com.pbina.attendeasy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class FeedbackHelp : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_help)

        toolbar = findViewById(R.id.toolbar)
        setupToolbar()
        supportActionBar?.title = "Feedback & Help"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val btnFeedback = findViewById<Button>(R.id.btnSendFeedback)
//        btnFeedback.setOnClickListener {
//            sendFeedbackEmail()
//        }
    }
    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow) // Set back arrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

//    private fun sendFeedbackEmail() {
//        val intent = Intent(Intent.ACTION_SENDTO).apply {
//            data = Uri.parse("mailto:")
//            putExtra(Intent.EXTRA_EMAIL, arrayOf("phiba@gmail.com"))
//            putExtra(Intent.EXTRA_SUBJECT, "App Feedback - Attend Easy")
//        }
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivity(intent)
//        } else {
//            Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
