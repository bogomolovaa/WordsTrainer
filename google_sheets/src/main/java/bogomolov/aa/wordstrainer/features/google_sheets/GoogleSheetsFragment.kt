package bogomolov.aa.wordstrainer.features.google_sheets

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import bogomolov.aa.wordstrainer.features.google_sheets.databinding.FragmentGoogleSheetsBinding
import bogomolov.aa.wordstrainer.features.google_sheets.di.GoogleSheetsComponentProvider
import bogomolov.aa.wordstrainer.features.shared.GOOGLE_SHEET_NAME
import bogomolov.aa.wordstrainer.features.shared.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.features.shared.setSetting
import javax.inject.Inject

class GoogleSheetsFragment : Fragment(R.layout.fragment_google_sheets) {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: GoogleSheetsViewModel by viewModels { viewModelFactory }
    private lateinit var navController: NavController

    override fun onAttach(context: Context) {
        (requireActivity().application as GoogleSheetsComponentProvider).get().inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentGoogleSheetsBinding.bind(requireView())
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        navController = findNavController()
        NavigationUI.setupWithNavController(binding.toolbar, navController)
        viewModel.loadSheets()

        val adapter = GoogleSheetsAdapter {
            setSetting(requireContext(), GOOGLE_SHEET_NAME, it.name)
            setSetting(requireContext(), GOOGLE_SHEET_ID, it.id)
            setSetting(requireContext(), USE_GOOGLE_SHEET, true)
            viewModel.onSheetSelected()
            navController.popBackStack()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        viewModel.sheetsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
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
                navController.popBackStack()
            }.show(parentFragmentManager, "GoogleSheetsFragment")
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}