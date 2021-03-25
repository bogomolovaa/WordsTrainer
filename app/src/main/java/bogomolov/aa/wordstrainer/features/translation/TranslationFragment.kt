package bogomolov.aa.wordstrainer.features.translation

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.dagger.ViewModelFactory
import bogomolov.aa.wordstrainer.databinding.FragmentTranslationBinding
import bogomolov.aa.wordstrainer.repository.entity.Word
import bogomolov.aa.wordstrainer.repository.json.fromJson
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
    ) = FragmentTranslationBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).title = resources.getString(R.string.translation)
        setHasOptionsMenu(true)
        navController = findNavController()
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        binding.textInputLayout.setEndIconOnClickListener { translate() }
        viewModel.translationLiveData.observe(viewLifecycleOwner) { word ->
            binding.translationText.text =
                getTranslationText(word) ?: resources.getString(R.string.not_translated)
        }
        binding.wordInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                translate()
                true
            } else false
        }
    }

    private fun translate() {
        val text = binding.wordInput.text
        if (text != null) viewModel.translate(text.toString())
        hideKeyboard()
    }

    private fun hideKeyboard() {
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

fun getTranslationText(word: Word?): Spanned? {
    if (word?.json == null) return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            translationToHtml(word.json),
            Html.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(translationToHtml(word.json))
    }
}


private fun translationToHtml(json: String): String {
    val sb = StringBuilder()
    if (json.startsWith("{\"head\"")) {
        val translation = fromJson(json)
        if (translation?.def != null)
            for (def in translation.def!!.iterator()) {
                sb.append("<p>\t")
                sb.append("<font color='#00A'><i>${def.ts ?: ""}</i></font>\t")
                sb.append("<p>\t")
                sb.append("<strong>${def.text}</strong> <font color='#070'><i>${def.pos}</i></font>")
                var counter = 1
                for (tr in def.tr!!) {
                    sb.append("<p>\t\t\t${counter++} ${tr.text}")
                    val synonyms = tr.syn?.toString() ?: ""
                    if (synonyms.isNotEmpty())
                        sb.append(", " + synonyms.substring(1, synonyms.length - 1))
                    sb.append("</p>")
                }
                sb.append("</p>")
            }
    } else {
        sb.append(
            json.replace("{\n", "{<br>")
                .replace("\n}", "<br>}")
                .replace("\n ", "<br>&nbsp;")
                .replace("\n", "<p>")
                .replace(" ", "&nbsp;")
        )
    }
    return sb.toString()
}