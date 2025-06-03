package com.pbina.attendeasy

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.core.content.ContextCompat

class Setting : AppCompatActivity() {

    private lateinit var switchNotifications: Switch
    private lateinit var btnDeleteAccount: LinearLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Initialize views
        switchNotifications = findViewById(R.id.switch_notifications)
        btnDeleteAccount = findViewById(R.id.btn_delete_account)
        toolbar = findViewById(R.id.toolbar)

        // Setup Toolbar
        setupToolbar()

        // Switch for notifications
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Handle enabling notifications logic
                switchNotifications.thumbTintList = ContextCompat.getColorStateList(this, R.color.primaryColor)
                switchNotifications.trackTintList = ContextCompat.getColorStateList(this, R.color.primaryColor)
                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
            } else {
                // Handle disabling notifications logic
                switchNotifications.thumbTintList = ContextCompat.getColorStateList(this, R.color.darker_gray)
                switchNotifications.trackTintList = ContextCompat.getColorStateList(this, R.color.darker_gray)
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Delete Account click
        btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow) // Set back arrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun showDeleteAccountDialog() {
        // Confirmation dialog before account deletion
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure you want to delete your account?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                // Handle account deletion logic here (e.g., Firebase Auth deletion)
                Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()
                // You can log out or navigate to the login screen here
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        // Show the dialog
        val alert = dialogBuilder.create()
        alert.show()
    }
}
