package bogomolov.aa.wordstrainer.repository

import android.content.Context
import android.util.Log
import bogomolov.aa.wordstrainer.domain.Word
import bogomolov.aa.wordstrainer.features.shared.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.features.shared.getSetting
import bogomolov.aa.wordstrainer.repository.json.fromJson
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexTranslateProvider @Inject constructor(val context: Context) {
    private val client = OkHttpClient()

    fun translate(text: String): Single<Word> = Single.create<Word> {
        val request = Request.Builder().url(getUrl(text)).build()
        try {
            client.newCall(request).execute().use { response ->
                val json = response.body!!.string()
                it.onSuccess(
                    Word(
                        word = text,
                        translation = fromJson(json)?.def?.get(0)?.tr?.get(0)?.text ?: "",
                        json = json
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("YandexTranslateProvider", "translate", e)
            it.onError(e)
        }
    }.subscribeOn(Schedulers.io())

    private fun getUrl(text: String): String {
        val direction = getSetting<String>(
            context,
            TRANSLATION_DIRECTION
        ) ?: "ru-en"
        val lang = direction.split("-")[1]
        return "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20190813T074253Z.2531cef640eec2d6.66f19e3b927bd253c4e6c310f66b91bd14ebadc7&lang=$direction&ui=$lang&text=$text"
    }
}