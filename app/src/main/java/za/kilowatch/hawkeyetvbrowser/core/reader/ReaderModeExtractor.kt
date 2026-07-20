package za.kilowatch.hawkeyetvbrowser.core.reader

import android.content.Context
import android.webkit.WebView
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import za.kilowatch.hawkeyetvbrowser.R
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReaderModeExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var scriptContent: String? = null

    private fun getScript(): String {
        if (scriptContent == null) {
            try {
                val inputStream = context.resources.openRawResource(R.raw.readability)
                val reader = BufferedReader(InputStreamReader(inputStream))
                scriptContent = reader.readText()
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return scriptContent ?: ""
    }

    fun extractArticle(webView: WebView, callback: (ArticleData?) -> Unit) {
        val script = getScript()
        if (script.isBlank()) {
            callback(null)
            return
        }

        webView.evaluateJavascript(script) { resultJson ->
            if (resultJson.isNullOrBlank() || resultJson == "null") {
                callback(null)
                return@evaluateJavascript
            }

            try {
                // evaluateJavascript returns JSON string enclosed in quotes
                var jsonStr = resultJson
                if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                    jsonStr = JSONObject.quote(jsonStr) // sanitize if needed
                    jsonStr = resultJson.substring(1, resultJson.length - 1)
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\")
                }

                val json = JSONObject(jsonStr)
                if (json.has("error")) {
                    callback(null)
                    return@evaluateJavascript
                }

                val title = json.optString("title", "Article")
                val byline = json.optString("byline", "")
                val content = json.optString("content", "")
                val length = json.optInt("length", content.length)

                if (content.isBlank()) {
                    callback(null)
                } else {
                    callback(ArticleData(title, byline, content, length))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }
}
