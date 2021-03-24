package bogomolov.aa.wordstrainer.view.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import bogomolov.aa.wordstrainer.R

class SelectFirstLangDialogFragment(
    private val activity: AppCompatActivity,
    private val onSelect: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val locales = activity.resources.getStringArray(R.array.LangLocale)
        builder.setTitle(R.string.select_lang_source).setItems(R.array.Lang) { dialog, which ->
            val locale = locales[which]
            dismiss()
            SelectSecondLangDialogFragment(locale, activity, onSelect).show(
                activity.supportFragmentManager,
                "SelectSecondLangDialogFragment"
            )
        }
        return builder.create()
    }
}

class SelectSecondLangDialogFragment(
    private val sourceLocale: String,
    private val activity: AppCompatActivity,
    private val onSelect: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val langs = activity.resources.getStringArray(R.array.Lang).toMutableList()
        val locales = activity.resources.getStringArray(R.array.LangLocale).toMutableList()
        val sourceId = locales.indexOf(sourceLocale)
        langs.removeAt(sourceId)
        locales.removeAt(sourceId)
        builder.setTitle(R.string.select_lang_translation)
            .setItems(langs.toTypedArray()) { dialog, which ->
                val translationLocale = locales[which]
                val direction = "$sourceLocale-$translationLocale"
                onSelect(direction)
            }
        return builder.create()
    }
}