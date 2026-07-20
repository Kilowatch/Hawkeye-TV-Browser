package za.kilowatch.hawkeyetvbrowser.core.voice

import javax.inject.Inject
import javax.inject.Singleton

sealed class VoiceCommandResult {
    data class Action(val actionType: ActionType) : VoiceCommandResult()
    data class Navigate(val url: String) : VoiceCommandResult()
    data class Search(val query: String) : VoiceCommandResult()

    enum class ActionType {
        GO_BACK,
        GO_FORWARD,
        GO_HOME,
        OPEN_BOOKMARKS,
        OPEN_HISTORY,
        OPEN_DOWNLOADS,
        NEW_TAB,
        RELOAD
    }
}

@Singleton
class VoiceCommandParser @Inject constructor() {

    fun parse(input: String): VoiceCommandResult {
        val trimmed = input.trim().lowercase()

        // 1. Browser Commands
        when {
            trimmed in listOf("go back", "back", "previous page") ->
                return VoiceCommandResult.Action(VoiceCommandResult.ActionType.GO_BACK)

            trimmed in listOf("go forward", "forward", "next page") ->
                return VoiceCommandResult.Action(VoiceCommandResult.ActionType.GO_FORWARD)

            trimmed in listOf("go home", "home", "open home", "start page") ->
                return VoiceCommandResult.Action(VoiceCommandResult.ActionType.GO_HOME)

            trimmed in listOf("open bookmarks", "bookmarks", "show bookmarks") ->
                return VoiceCommandResult.Action(VoiceCommandResult.ActionType.OPEN_BOOKMARKS)

            trimmed in listOf("open history", "history", "show history") ->
                return VoiceCommandResult.Action(VoiceCommandResult.ActionType.OPEN_HISTORY)

            trimmed in listOf("open downloads", "downloads", "show downloads") ->
                return VoiceCommandResult.Action(VoiceCommandResult.ActionType.OPEN_DOWNLOADS)

            trimmed in listOf("new tab", "open new tab", "create tab") ->
                return VoiceCommandResult.Action(VoiceCommandResult.ActionType.NEW_TAB)

            trimmed in listOf("reload", "refresh", "reload page") ->
                return VoiceCommandResult.Action(VoiceCommandResult.ActionType.RELOAD)
        }

        // 2. Explicit speech URL formatting (e.g. "open wikipedia dot org", "google.com")
        var normalizedUrl = trimmed
            .replace(" dot ", ".")
            .replace(" slash ", "/")
            .replace("colon ", ":")

        if (normalizedUrl.startsWith("open ")) {
            normalizedUrl = normalizedUrl.removePrefix("open ").trim()
        }

        if (normalizedUrl.startsWith("http://") || normalizedUrl.startsWith("https://")) {
            return VoiceCommandResult.Navigate(normalizedUrl)
        }

        if (normalizedUrl.contains(".") && !normalizedUrl.contains(" ")) {
            return VoiceCommandResult.Navigate("https://$normalizedUrl")
        }

        // 3. Fallback: Search Query
        return VoiceCommandResult.Search(input.trim())
    }
}
