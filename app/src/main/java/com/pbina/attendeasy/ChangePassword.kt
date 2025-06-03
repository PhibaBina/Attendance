package com.pbina.attendeasy

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class ChangePassword : AppCompatActivity() {

    private lateinit var currentPassword: TextInputEditText
    private lateinit var newPassword: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var btnSave: AppCompatButton
    private lateinit var toolbar: Toolbar

    private lateinit var loadingDialog: Dialog

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Initialize views
        currentPassword = findViewById(R.id.password)
        newPassword = findViewById(R.id.newPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        btnSave = findViewById(R.id.btnSave)
        toolbar = findViewById(R.id.toolbar)

        setupLoadingDialog()

        // Set up toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()  // Handle the back button
        }

        // Handle save button click
        btnSave.setOnClickListener {
            val currentPwd = currentPassword.text.toString().trim()
            val newPwd = newPassword.text.toString().trim()
            val confirmPwd = confirmPassword.text.toString().trim()


            if (TextUtils.isEmpty(currentPwd)) {
                Toast.makeText(this, "Current password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidPassword(newPwd)) {
                Toast.makeText(this, "Password must be atleast 8 characters, one letter, one number, and one special character", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(newPwd)) {
                Toast.makeText(this, "New password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPwd != confirmPwd) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadingDialog.show()
            changePassword(currentPwd, newPwd)
        }
    }
    private fun setupLoadingDialog() {
        loadingDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null)
        loadingDialog.setContentView(view)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    private fun isValidPassword(password: String): Boolean {
        // Minimum 8 characters, at least one letter, one number, and one special character
        val regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
        return password.matches(regex.toRegex())
    }
    private fun changePassword(currentPwd: String, newPwd: String) {
        val user = firebaseAuth.currentUser

        user?.let {
            val email = user.email

            // Re-authenticate the user with the current password
            val credential = EmailAuthProvider.getCredential(email!!, currentPwd)

            user.reauthenticate(credential)
                .addOnSuccessListener {
                    // After successful re-authentication, update password
                    user.updatePassword(newPwd)
                        .addOnSuccessListener {
                            loadingDialog.dismiss()
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            finish()  // Close activity after success
                        }
                        .addOnFailureListener { exception ->
                            loadingDialog.dismiss()
                            Toast.makeText(this, "Failed to update password: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    loadingDialog.dismiss()
                    Toast.makeText(this, "Wrong current password: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    // Adding the "Forgot Password" functionality
    private fun onForgotPasswordClicked(view: View) {
        val email = firebaseAuth.currentUser?.email

        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "Unable to find email address for password reset", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Password reset sent to your email", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to send password reset email: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
