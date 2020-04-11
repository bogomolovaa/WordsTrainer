package bogomolov.aa.wordstrainer.view.fragments

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.android.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.dagger.ViewModelFactory
import bogomolov.aa.wordstrainer.databinding.FragmentTranslationBinding
import bogomolov.aa.wordstrainer.repository.entity.Word
import bogomolov.aa.wordstrainer.repository.json.fromJson
import bogomolov.aa.wordstrainer.viewmodel.TranslationViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class TranslationFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: TranslationViewModel by viewModels { viewModelFactory }
    private lateinit var navController: NavController
    private lateinit var binding: FragmentTranslationBinding

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_translation,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).title = resources.getString(R.string.translation)
        setHasOptionsMenu(true)

        Log.i("test", "TranslationFragment onCreateView")



        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        binding.textInputLayout.setEndIconOnClickListener { translate() }
        viewModel.translationLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.translationText.text = getTranslation(it)
            } else {
                binding.translationText.text = resources.getString(R.string.not_translated)
            }

        }

        binding.wordInput.setOnEditorActionListener { v, actionId, event ->
            Log.i("test","actionId $actionId")
            if (actionId == EditorInfo.IME_ACTION_GO) {
                translate()
                true
            } else false

        }

        return binding.root
    }

    private fun translate() {
        val text = binding.wordInput.text
        Log.i("test", "translate word $text")
        if (text != null) viewModel.translate(text.toString())
        val imm =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.translation_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.repetitionFragmentAction) navController.navigate(R.id.repetitionFragment)
        if (item.itemId == R.id.settingsFragmentAction) navController.navigate(R.id.settingsFragment)
        return true
    }


}

fun getTranslation(word: Word): Spanned =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            translationToHtml(word),
            Html.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(translationToHtml(word))
    }

private fun translationToHtml(word: Word): String? {
    val sb = StringBuilder()
    val translation = fromJson(word.json)
    if (translation?.def != null)
        for (def in translation.def!!.iterator()) {
            sb.append("<p>\t")
            sb.append("<strong>" + def.text.toString() + "</strong> <font color='#070'><i>" + def.pos.toString() + "</i></font>")
            var counter = 1
            for (tr in def.tr!!) {
                val syns = if (tr.syn != null) tr.syn.toString() else ""
                sb.append(
                    "<p>\t\t\t" + counter++ + " " + tr.text + (if (syns.length > 0) ", " + syns.substring(
                        1,
                        syns.length - 1
                    ) else "") + "</p>"
                )
            }
            sb.append("</p>")
        }
    Log.i("test", sb.toString())
    return sb.toString()
}
