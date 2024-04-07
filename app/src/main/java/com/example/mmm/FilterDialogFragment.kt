package com.example.mmm

import android.app.Dialog
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class FilterDialogFragment : DialogFragment() {

    private var selectedGenres: MutableList<String> = mutableListOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.filter_dialog, null)

            // Get references to the checkboxes in the dialog layout
            val checkboxAdventure = view.findViewById<CheckBox>(R.id.checkboxAdventure)
            val checkboxAction = view.findViewById<CheckBox>(R.id.checkboxAction)
            val checkboxComedy = view.findViewById<CheckBox>(R.id.checkboxComedy)
            val checkboxDrama = view.findViewById<CheckBox>(R.id.checkboxDrama)
            val checkboxThriller = view.findViewById<CheckBox>(R.id.checkboxThriller)
            val checkboxHorror = view.findViewById<CheckBox>(R.id.checkboxHorror)
            val checkboxRomanticComedy = view.findViewById<CheckBox>(R.id.checkboxRomanticComedy)
            val checkboxMusical = view.findViewById<CheckBox>(R.id.checkboxMusical)
            val checkboxDocumentary = view.findViewById<CheckBox>(R.id.checkboxDocumentary)

            builder.setView(view)
                .setTitle("Filter Options")
                .setPositiveButton("Apply") { dialog, _ ->
                    // Handle applying filters
                    applyFilters(checkboxHorror.isChecked, checkboxAction.isChecked)                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Handle canceling filter selection
                    dialog.dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun applyFilters(isHorrorSelected: Boolean, isActionSelected: Boolean) {
        // Clear the list of selected genres
        selectedGenres.clear()

        // Find checkboxes and add selected genres to the list
        if (isHorrorSelected) selectedGenres.add("Horror")
        if (isActionSelected) selectedGenres.add("Action")
        // Add other genres similarly

        // Get a reference to MainActivity and call onFiltersApplied method
        val mainActivity = activity as? MainActivity
        mainActivity?.onFiltersApplied(selectedGenres.joinToString(", "))
    }
}