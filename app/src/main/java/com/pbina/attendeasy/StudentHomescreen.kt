package com.pbina.attendeasy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pbina.attendeasy.Fragment.StudentHomeFragment
import com.pbina.attendeasy.Fragment.StudentMoreFragment
import com.pbina.attendeasy.Fragment.StudentProfileFragment

class StudentHomescreen : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_homescreen)

        bottomNav = findViewById(R.id.student_bottom_nav)

        // Load default home fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.student_fragment_container, StudentHomeFragment())
                .commit()
        }

        // Bottom nav click handling
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> StudentHomeFragment()
                R.id.nav_profile -> StudentProfileFragment()
                R.id.nav_more -> StudentMoreFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.student_fragment_container, it as Fragment)
                    .commit()
                true
            } ?: false
        }
    }
}
