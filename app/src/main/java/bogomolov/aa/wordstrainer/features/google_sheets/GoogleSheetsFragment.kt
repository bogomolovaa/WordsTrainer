package bogomolov.aa.wordstrainer.features.google_sheets

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.android.GOOGLE_SHEET_ID
import bogomolov.aa.wordstrainer.android.GOOGLE_SHEET_NAME
import bogomolov.aa.wordstrainer.android.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.android.setSetting
import bogomolov.aa.wordstrainer.dagger.ViewModelFactory
import bogomolov.aa.wordstrainer.databinding.FragmentGoogleSheetsBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class GoogleSheetsFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: GoogleSheetsViewModel by viewModels { viewModelFactory }
    private lateinit var navController: NavController

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGoogleSheetsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.toolbar, navController)
        viewModel.loadSheets()

        val adapter = GoogleSheetsAdapter {
            setSetting(requireContext(), GOOGLE_SHEET_NAME, it.name)
            setSetting(requireContext(), GOOGLE_SHEET_ID, it.id)
            setSetting(requireContext(), USE_GOOGLE_SHEET, true)
            viewModel.onSheetSelected()
            navController.navigate(R.id.settingsFragment)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        viewModel.sheetsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.google_sheets_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.create_sheet_action) {
            SheetNameBottomDialogFragment {
                viewModel.createGoogleSheet(it)
                navController.navigate(R.id.settingsFragment)
            }.show(parentFragmentManager, "GoogleSheetsFragment")
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}