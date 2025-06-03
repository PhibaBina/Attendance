package com.pbina.attendeasy



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddBottomSheet(val onOptionSelected: (String) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the bottom sheet
        val view = inflater.inflate(R.layout.activity_add_bottom_sheet, container, false)

        // Set click listeners for each option
        val addDepartment = view.findViewById<TextView>(R.id.addDepartment)
        val addSemester = view.findViewById<TextView>(R.id.addSemester)
        val addSubject = view.findViewById<TextView>(R.id.addSubject)

        addDepartment.setOnClickListener {
            onOptionSelected("department")
            dismiss() // Close the bottom sheet
        }
        addSemester.setOnClickListener {
            onOptionSelected("semester")
            dismiss() // Close the bottom sheet
        }
        addSubject.setOnClickListener {
            onOptionSelected("subject")
            dismiss() // Close the bottom sheet
        }

        return view
    }
}
