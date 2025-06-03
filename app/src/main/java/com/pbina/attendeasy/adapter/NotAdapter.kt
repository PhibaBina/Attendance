package com.pbina.attendeasy.adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.pbina.attendeasy.R
import com.pbina.attendeasy.model.Note

class NotAdapter(private val notes: List<Note>) :
    RecyclerView.Adapter<NotAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvNoteTitle)
        val description: TextView = itemView.findViewById(R.id.tvNoteDescription)
        val link: TextView = itemView.findViewById(R.id.tvNoteLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title
        holder.description.text = note.description
        holder.link.text = "View Note"
        holder.link.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(note.noteLink))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = notes.size
}
