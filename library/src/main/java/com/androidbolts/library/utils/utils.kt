package com.androidbolts.library.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.androidbolts.library.R

internal inline fun <R> R?.orElse(block: () -> R): R {
    return this ?: block()
}

internal fun showLoadingDialog(
    context:Context,
    message: String?,
    title: String? = "",
    cancelable: Boolean = false,
    onPositiveButtonClicked:() -> Unit, onNegativeButtonClicked:()->Unit): AlertDialog? {
    val builder = AlertDialog.Builder(context)
    val view = LayoutInflater.from(context).inflate(R.layout.progress_layout, null)
    val tvTitle = view.findViewById<TextView>(R.id.title)
    tvTitle.visibility = View.GONE
    if (!title.isNullOrEmpty()) {
        tvTitle.text = title
        tvTitle.visibility = View.VISIBLE
    }
    view.findViewById<TextView>(R.id.message).text = message
    builder.setPositiveButton("Retry") { _, _->
        onPositiveButtonClicked()
    }
    builder.setNegativeButton("Cancel"){_,_ ->
        onNegativeButtonClicked()
    }
    builder.setView(view)
    val dialog = builder.create()
    dialog.setCancelable(cancelable)
    return dialog
}