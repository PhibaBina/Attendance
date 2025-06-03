package com.pbina.attendeasy

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.pbina.attendeasy.R

class LoadingDialog(context: Context) {
    private val dialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.activity_loading_dialog, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
