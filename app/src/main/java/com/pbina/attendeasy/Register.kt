package com.pbina.attendeasy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity


class Register : AppCompatActivity() {

    private lateinit var roleSpinner: Spinner
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        roleSpinner = findViewById(R.id.roleSpinner)
        continueButton = findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            val selectedRole = roleSpinner.selectedItem.toString()

            if (selectedRole == "Student") {
                startActivity(Intent(this, RegisterStudent::class.java))
            } else if (selectedRole == "Teacher") {
                startActivity(Intent(this, RegisterTeacher::class.java))
            }
        }
    }
}
