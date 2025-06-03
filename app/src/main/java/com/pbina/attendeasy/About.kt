package com.pbina.attendeasy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class About : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        toolbar = findViewById(R.id.toolbar)
        setupToolbar()

        supportActionBar?.title = "About"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow) // Set back arrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
