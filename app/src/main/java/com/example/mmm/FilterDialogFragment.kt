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
            val checkboxRomance = view.findViewById<CheckBox>(R.id.checkboxRomance)
            val checkboxDocumentary = view.findViewById<CheckBox>(R.id.checkboxDocumentary)

            builder.setView(view)
                .setTitle("Filter Options")
                .setPositiveButton("Apply") { dialog, _ ->
                    // Clear the list of selected genres
                    selectedGenres.clear()

                    // Map each checkbox to its TMDB genre ID
                    val checkboxIds = mapOf(
                        checkboxAdventure to "12",
                        checkboxAction to "28",
                        checkboxComedy to "35",
                        checkboxDrama to "18",
                        checkboxThriller to "53",
                        checkboxHorror to "27",
                        checkboxRomance to "10749",
                        checkboxDocumentary to "99"
                    )

                    // Iterate over each checkbox and check if it's checked
                    checkboxIds.forEach { (checkbox, genreId) ->
                        if (checkbox.isChecked) {
                            selectedGenres.add(genreId)
                        }
                    }

                    // Print out the selected genres
                    println("Selected genres: ${selectedGenres.joinToString(", ")}")

//                    // Find checkboxes and add selected genres to the list
//                    if(checkboxAdventure.isChecked) selectedGenres.add("12")
//                    if(checkboxAction.isChecked) selectedGenres.add("28")
//                    if(checkboxComedy.isChecked) selectedGenres.add("35")
//                    if(checkboxDrama.isChecked) selectedGenres.add("18")
//                    if(checkboxThriller.isChecked) selectedGenres.add("53")
//                    if(checkboxHorror.isChecked) selectedGenres.add("27")
//                    if(checkboxRomance.isChecked) selectedGenres.add("10749")
//                    if(checkboxDocumentary.isChecked) selectedGenres.add("99")

//                    applyFilters()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Handle canceling filter selection
                    dialog.dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun applyFilters() {

    }
}