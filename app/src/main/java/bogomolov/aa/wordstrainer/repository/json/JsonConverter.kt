package bogomolov.aa.wordstrainer.repository.json

import android.util.Log
import com.squareup.moshi.Moshi
import java.io.IOException

fun fromJson(json: String?) =
    try {
        Moshi.Builder().build().adapter(Translation::class.java).fromJson(json ?: "")
    } catch (e: IOException) {
        Log.e("test", "json convert error", e)
        null
    }