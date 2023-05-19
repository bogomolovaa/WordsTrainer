package bogomolov.aa.wordstrainer.features.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.WordsTrainerApplication
import bogomolov.aa.wordstrainer.android.*
import bogomolov.aa.wordstrainer.dagger.ViewModelFactory
import bogomolov.aa.wordstrainer.databinding.FragmentSettingsBinding
import bogomolov.aa.wordstrainer.features.main.MainActivity
import bogomolov.aa.wordstrainer.view.fragments.SelectFirstLangDialogFragment
import javax.inject.Inject

class SettingsFragment() : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }
    private lateinit var binding: FragmentSettingsBinding

    override fun onAttach(context: Context) {
        (requireActivity().application as WordsTrainerApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            SelectFirstLangDialogFragment(activity) { direction ->
                binding.translationDirection.text = direction
                setSetting(requireContext(), TRANSLATION_DIRECTION, direction)
                viewModel.initWords()
            }.show(activity.supportFragmentManager, "")
        }

        binding.googleSheetSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.googleSheetLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            setSetting(requireContext(), USE_GOOGLE_SHEET, isChecked)
            requireActivity().overridePendingTransition(0, 0)
            NavDeepLinkBuilder(requireContext())
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.settingsFragment)
                .createPendingIntent().send()
            requireActivity().overridePendingTransition(0, 0)
        }
        binding.googleSheetName.setOnClickListener {
            navController.navigate(R.id.googleSheetsFragment)
        }
        binding.importButton.setOnClickListener {
            binding.importButton.visibility = View.GONE
            binding.importedIcon.visibility = View.VISIBLE
            viewModel.importWords()
        }
        binding.exportButton.setOnClickListener {
            binding.exportButton.visibility = View.GONE
            binding.exportedIcon.visibility = View.VISIBLE
            viewModel.exportWords()
        }
        binding.privacyPolicy.setOnClickListener { openPrivacyPolicy() }
    }

    private fun openPrivacyPolicy() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(resources.getString(R.string.privacy_link))
        startActivity(i)
    }
}