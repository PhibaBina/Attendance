package com.pbina.attendeasy

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Logout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show the logout confirmation dialog when the activity starts
        showLogoutDialog()
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        // Positive button for logout
        builder.setPositiveButton("Yes") { dialog, _ ->
            logoutUser()
            dialog.dismiss()
        }

        // Negative button to cancel logout
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Just dismiss the dialog
        }

        // Create and show the dialog
        builder.create().show()
    }

    private fun logoutUser() {
        // Sign out the user from Firebase
        FirebaseAuth.getInstance().signOut()

        // Show a toast message indicating successful logout
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Redirect user to the Welcome or Login screen
        val intent = Intent(this, WelcomePage::class.java)
        startActivity(intent)
        finish() // Finish the current activity (LogoutActivity)
    }
}
