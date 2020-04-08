package bogomolov.aa.wordstrainer.view.fragments

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
import bogomolov.aa.wordstrainer.android.*
import bogomolov.aa.wordstrainer.databinding.FragmentSettingsBinding
import bogomolov.aa.wordstrainer.view.MainActivity
import dagger.android.support.AndroidSupportInjection

class SettingsFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setSupportActionBar(binding.toolbar)

        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        val useGoogleSheet = getSetting<Boolean>(requireContext(), USE_GOOGLE_SHEET)!!
        binding.translationDirection.text =
            getSetting<String>(requireContext(), TRANSLATION_DIRECTION)
        binding.googleSheetLayout.visibility = if (useGoogleSheet) View.VISIBLE else View.GONE
        binding.googleSheetName.text = getSetting<String>(requireContext(), GOOGLE_SHEET_NAME)
            ?: requireContext().getString(R.string.select_sheet)
        binding.googleSheetSwitch.isChecked = useGoogleSheet

        val activity = requireActivity() as AppCompatActivity
        binding.translationDirection.setOnClickListener {
            SelectFirstLangDialogFragment(activity) {
                binding.translationDirection.text = it
                setSetting(requireContext(), TRANSLATION_DIRECTION, it)
            }.show(
                activity.supportFragmentManager,
                "SelectFirstLangDialogFragment"
            )
        }

        binding.googleSheetSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.googleSheetLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) mainActivity.requestSignIn(requireContext())
        }

        binding.googleSheetName.setOnClickListener {
            navController.navigate(R.id.googleSheetsFragment)
        }

        return binding.root
    }


}
