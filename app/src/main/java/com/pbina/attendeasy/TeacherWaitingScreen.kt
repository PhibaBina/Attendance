package com.pbina.attendeasy


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class TeacherWaitingScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvWaitingMessage: TextView
    private lateinit var btnLogout: Button
    private var statusListener: ListenerRegistration? = null
    private var hasNavigated = false  // ✅ Prevent multiple redirects

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_waiting_screen)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvWaitingMessage = findViewById(R.id.tvWaitingMessage)
        btnLogout = findViewById(R.id.btnLogout)

        checkApprovalStatus()

        btnLogout.setOnClickListener {
            statusListener?.remove() // Remove listener on logout
            auth.signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun checkApprovalStatus() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val docRef = db.collection("users").document(userId)
            statusListener = docRef.addSnapshotListener { document, error ->
                if (error != null) {
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val isApproved = document.getBoolean("isApproved")
                    if (isApproved == true && !hasNavigated)
                    {
                        hasNavigated = true  // ✅ Block further triggers
                        Toast.makeText(this, "Approved! Redirecting...", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, TeacherHomescreen::class.java))
                        finish()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        statusListener?.remove()
    }
}
