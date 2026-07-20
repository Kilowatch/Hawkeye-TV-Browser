package za.kilowatch.hawkeyetvbrowser.core.adblock

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import za.kilowatch.hawkeyetvbrowser.R
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterListManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun loadBundledRules(): List<String> = withContext(Dispatchers.IO) {
        val rules = mutableListOf<String>()
        try {
            val inputStream = context.resources.openRawResource(R.raw.easylist_bundled)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("!")) {
                    rules.add(trimmed)
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        rules
    }
}
