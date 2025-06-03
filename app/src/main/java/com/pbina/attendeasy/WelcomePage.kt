package com.pbina.attendeasy


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class WelcomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_page)

        // Initialize UI elements
        val logo: ImageView = findViewById(R.id.logoImage)
        val tvdescription: TextView = findViewById(R.id.tvDescription)
        val getStartedButton: Button = findViewById(R.id.btnGetStarted)

        // Set button click listener
        getStartedButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
