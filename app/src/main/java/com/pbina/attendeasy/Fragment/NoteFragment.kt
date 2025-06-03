package com.pbina.attendeasy.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.pbina.attendeasy.R
import com.pbina.attendeasy.adapter.NotAdapter
import com.pbina.attendeasy.model.Note

class NoteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noteList: ArrayList<Note>
    private lateinit var adapter: NotAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_note_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewNotes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        noteList = ArrayList()
        adapter = NotAdapter(noteList)
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        // Load notes
        db.collection("Notes")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val note = doc.toObject(Note::class.java)
                    noteList.add(note)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load notes", Toast.LENGTH_SHORT).show()
            }

        return view
    }
}
