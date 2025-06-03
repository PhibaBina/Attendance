package com.pbina.attendeasy

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pbina.attendeasy.adapter.ContentPageAdapter

class Contents : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contents  )

        // Bind the TabLayout and ViewPager2
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val fabAddContent: FloatingActionButton = findViewById(R.id.fab_add_content)
        // Make sure the ID matches in XML

        fabAddContent.setOnClickListener {
            val options = arrayOf("Add Note", "Add Assignment")
            AlertDialog.Builder(this)
                .setTitle("Choose Content Type")
                .setItems(options) { dialogInterface: DialogInterface, which: Int ->
                    when (which) {
                        0 -> startActivity(Intent(this, AddNoteActivity::class.java))
                        1 -> startActivity(Intent(this, AddAssignment::class.java))
                    }
                }
                .show()
        }

        // Set up the ViewPager with the ContentPagerAdapter
        val adapter = ContentPageAdapter(this)
        viewPager.adapter = adapter

        // Link the TabLayout and ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Notes"
                else -> "Assignments"
            }
        }.attach()
    }
}
