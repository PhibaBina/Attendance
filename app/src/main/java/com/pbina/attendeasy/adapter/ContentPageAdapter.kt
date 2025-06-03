package com.pbina.attendeasy.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pbina.attendeasy.Fragment.AssignmentFragment
import com.pbina.attendeasy.Fragment.NoteFragment

class ContentPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Two tabs: Notes and Assignments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NoteFragment()        // First tab: Notes
            else -> AssignmentFragment() // Second tab: Assignments
        }
    }
}
