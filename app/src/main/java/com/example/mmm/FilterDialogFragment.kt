package com.example.mmm

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class FilterDialogFragment : DialogFragment() {
    interface FilterListener {
        fun onFiltersApplied(selectedFilters: List<String>)
    }

    private var filterListener: FilterListener? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedGenres: MutableList<String> = mutableListOf()

    fun setFilterListener(listener: FilterListener) {
        filterListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("FilterPreferences", Context.MODE_PRIVATE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.filter_dialog, null)

            val checkboxAdventure = view.findViewById<CheckBox>(R.id.checkboxAdventure)
            val checkboxAction = view.findViewById<CheckBox>(R.id.checkboxAction)
            val checkboxComedy = view.findViewById<CheckBox>(R.id.checkboxComedy)
            val checkboxDrama = view.findViewById<CheckBox>(R.id.checkboxDrama)
            val checkboxThriller = view.findViewById<CheckBox>(R.id.checkboxThriller)
            val checkboxHorror = view.findViewById<CheckBox>(R.id.checkboxHorror)
            val checkboxRomance = view.findViewById<CheckBox>(R.id.checkboxRomance)
            val checkboxDocumentary = view.findViewById<CheckBox>(R.id.checkboxDocumentary)

            // Load checkbox states from SharedPreferences
            checkboxAdventure.isChecked = sharedPreferences.getBoolean("Adventure", false)
            checkboxAction.isChecked = sharedPreferences.getBoolean("Action", false)
            checkboxComedy.isChecked = sharedPreferences.getBoolean("Comedy", false)
            checkboxDrama.isChecked = sharedPreferences.getBoolean("Drama", false)
            checkboxThriller.isChecked = sharedPreferences.getBoolean("Thriller", false)
            checkboxHorror.isChecked = sharedPreferences.getBoolean("Horror", false)
            checkboxRomance.isChecked = sharedPreferences.getBoolean("Romance", false)
            checkboxDocumentary.isChecked = sharedPreferences.getBoolean("Documentary", false)

            builder.setView(view)
                .setTitle("Filter Options")
                .setPositiveButton("Apply") { dialog, _ ->
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
                        sharedPreferences.edit().putBoolean(checkbox.text.toString(), checkbox.isChecked).apply()
                    }

                    println("Selected genres: ${selectedGenres.joinToString(", ")}")

                    filterListener?.onFiltersApplied(selectedGenres)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Handle canceling filter selection
                    dialog.dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}