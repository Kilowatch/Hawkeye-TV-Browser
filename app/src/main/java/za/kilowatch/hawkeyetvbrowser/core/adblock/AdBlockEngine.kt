package za.kilowatch.hawkeyetvbrowser.core.adblock

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.kilowatch.hawkeyetvbrowser.data.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdBlockEngine @Inject constructor(
    private val filterListManager: FilterListManager,
    private val settingsRepository: SettingsRepository
) {
    private val blockedDomains = mutableSetOf<String>()
    private val blockedPatterns = mutableListOf<Regex>()
    private var isInitialized = false

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initialize()
        }
    }

    suspend fun initialize() {
        if (isInitialized) return
        val rules = filterListManager.loadBundledRules()
        rules.forEach { rule ->
            when {
                rule.startsWith("||") -> {
                    val domain = rule.removePrefix("||").removeSuffix("^").lowercase()
                    blockedDomains.add(domain)
                }
                rule.contains("*") -> {
                    try {
                        val pattern = rule.replace("*", ".*").toRegex(RegexOption.IGNORE_CASE)
                        blockedPatterns.add(pattern)
                    } catch (e: Exception) {
                        // Ignore invalid regex
                    }
                }
            }
        }
        isInitialized = true
    }

    fun shouldBlock(url: String, headers: Map<String, String>? = null): Boolean {
        if (!settingsRepository.isAdBlockEnabled()) return false
        val host = try {
            Uri.parse(url).host?.lowercase() ?: return false
        } catch (e: Exception) {
            return false
        }

        // Fast domain check
        if (blockedDomains.any { host == it || host.endsWith(".$it") }) {
            return true
        }

        // Pattern matching check
        if (blockedPatterns.any { it.containsMatchIn(url) }) {
            return true
        }

        return false
    }
}
