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
import bogomolov.aa.wordstrainer.dagger.ViewModelFactory
import bogomolov.aa.wordstrainer.databinding.FragmentGoogleSheetsBinding
import bogomolov.aa.wordstrainer.databinding.FragmentTranslationBinding
import bogomolov.aa.wordstrainer.viewmodel.GoogleSheetsViewModel
import bogomolov.aa.wordstrainer.viewmodel.TranslationViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class GoogleSheetsFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: GoogleSheetsViewModel by activityViewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentGoogleSheetsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_google_sheets,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)


        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        return binding.root
    }

}
