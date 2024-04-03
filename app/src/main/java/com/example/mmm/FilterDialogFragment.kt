package com.example.mmm

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class FilterDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Inflate the layout for the dialog
            builder.setView(R.layout.filter_dialog)
                .setTitle("Filter Options")
                .setPositiveButton("Apply") { dialog, _ ->
                    // Handle applying filters
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Handle canceling filter selection
                    dialog.dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
