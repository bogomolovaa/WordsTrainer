package bogomolov.aa.wordstrainer.features.google_sheets

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI

import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.databinding.FragmentSheetNameBottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection

class SheetNameBottomDialogFragment(
    val onSave: (String) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSheetNameBottomDialogBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sheet_name_bottom_dialog,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner

        binding.saveButton.setOnClickListener {
            onSave(binding.enterText.text.toString())
            dismiss()
        }
        binding.cancelButton.setOnClickListener { dismiss() }

        return binding.root
    }

}
