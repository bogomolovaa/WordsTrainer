package bogomolov.aa.wordstrainer.features.google_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.databinding.FragmentSheetNameBottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SheetNameBottomDialogFragment(private val onSave: (String) -> Unit) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
