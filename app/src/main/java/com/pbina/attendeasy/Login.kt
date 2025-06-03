package com.pbina.attendeasy

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        setupLoadingDialog()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.forgotPasswordText)

        // Register Click Listener
        tvRegister.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }

        // Login Button Click Listener
        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()

            if (!validateInputs(email, password)) return@setOnClickListener

            loginUser(email, password)
        }

        // Forgot Password Click Listener
        tvForgotPassword.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Send password reset email
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    private fun setupLoadingDialog() {
        loadingDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null)
        loadingDialog.setContentView(view)
        loadingDialog.setCancelable(true)
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

//    private fun loginUser(email: String, password: String) {
//        loadingDialog.show()
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
//                    FirebaseFirestore.getInstance().collection("users").document(userId)
//                        .get().addOnSuccessListener { document ->
//                            loadingDialog.dismiss()
//                            val role = document.getString("role") ?: ""
//                            navigateToHomeScreen(role)
//                        }
//                } else {
//                    // Check for specific error: incorrect password
//                    if (task.exception?.message?.contains("The password is invalid") == true) {
//                        loadingDialog.dismiss()
//                        Toast.makeText(this, "Wrong password. Please try again.", Toast.LENGTH_LONG).show()
//                    } else {
//                        loadingDialog.dismiss()
//                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//    }
private fun loginUser(email: String, password: String) {
    loadingDialog.show()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                FirebaseFirestore.getInstance().collection("users").document(userId)
                    .get().addOnSuccessListener { document ->
                        val role = document.getString("role") ?: ""
                        loadingDialog.dismiss()
                        navigateToHomeScreen(role)
                    }
            } else {
                loadingDialog.dismiss()
                val exception = task.exception
                when {
                    exception is FirebaseAuthInvalidUserException -> {
                        // User doesn't exist
                        showErrorDialog("No account found with this email.")
                    }
                    exception is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid password
                        showErrorDialog("Incorrect password. Please try again.")
                    }
                    else -> {
                        loadingDialog.dismiss()
                        // Generic error
                        showErrorDialog("Login failed: ${exception?.localizedMessage}")
                    }
                }
            }
        }
}
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Login Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }



    private fun navigateToHomeScreen(role: String) {
    val userId = auth.currentUser?.uid ?: return

    when (role) {
        "Admin" -> {
            startActivity(Intent(this, AdminHomescreen::class.java))
            finish()
        }
        "Student" -> {
            startActivity(Intent(this, StudentHomescreen::class.java))
            finish()
        }
        "Teacher" -> {
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userDocRef.get().addOnSuccessListener { document ->
                val isApproved = document.getBoolean("isApproved") ?: false

                val intent = if (isApproved) {
                    Intent(this, TeacherHomescreen::class.java)
                } else {
                    Intent(this, TeacherWaitingScreen::class.java)
                }

                startActivity(intent)
                finish()
            }
        }
    }
}

}
