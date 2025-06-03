package com.pbina.attendeasy.adapter

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pbina.attendeasy.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbina.attendeasy.model.AdminMenuItem

class AdminGridAdapter(
    private val menuItems: List<AdminMenuItem>,
    private val onItemClick: (AdminMenuItem) -> Unit
) : RecyclerView.Adapter<AdminGridAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.menuIcon)
        val title: TextView = view.findViewById(R.id.menuTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = menuItems[position]
        holder.icon.setImageResource(item.iconRes)
        holder.title.text = item.title
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = menuItems.size
}
