package bogomolov.aa.wordstrainer.repository

import android.util.Log
import bogomolov.aa.wordstrainer.repository.entity.Word
import bogomolov.aa.wordstrainer.repository.json.fromJson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

class YandexTranslateProvider @Inject constructor() {
    private val client = OkHttpClient()

    fun translate(text: String): Word? {
        val request = Request.Builder().url(URL + text).build()
        try {
            client.newCall(request).execute().use { response ->
                val json = response.body!!.string()
                return Word(
                    word = text,
                    translation = fromJson(json)?.def?.get(0)?.tr?.get(0)?.text ?: "",
                    json = json
                )
            }
        } catch (e: IOException) {
            Log.e("test", "get error", e)
        }
        return null
    }

    companion object {
        private const val URL =
            "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20190813T074253Z.2531cef640eec2d6.66f19e3b927bd253c4e6c310f66b91bd14ebadc7&lang=en-ru&ui=ru&text="
    }
}