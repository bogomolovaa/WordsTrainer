package bogomolov.aa.wordstrainer.android

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

inline fun <reified T> getSetting(context: Context, name: String): T? {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    if (T::class == String::class) return sharedPreferences.getString(name, null) as T?
    if (T::class == Boolean::class) return sharedPreferences.getBoolean(name, false) as T
    return null
}

inline fun <reified T> setSetting(context: Context, name: String, value: T?) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    if (T::class == String::class) sharedPreferences.edit(true) { putString(name, value as String?) }
    if (T::class == Boolean::class) sharedPreferences.edit(true) {
        putBoolean(name, value as Boolean)
    }
}