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
import com.pbina.attendeasy.Fragment.TeacherHomeFragment
import com.pbina.attendeasy.Fragment.TeacherMoreFragment
import com.pbina.attendeasy.Fragment.TeacherProfileFragment

class TeacherHomescreen : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_homescreen)

        bottomNav = findViewById(R.id.teacher_bottom_nav)

        // Load default home fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.teacher_fragment_container, TeacherHomeFragment())
                .commit()
        }

        // Bottom nav click handling
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> TeacherHomeFragment()
                R.id.nav_profile -> TeacherProfileFragment()
                R.id.nav_more -> TeacherMoreFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.teacher_fragment_container, it as Fragment)
                    .commit()
                true
            } ?: false
        }
    }
}
