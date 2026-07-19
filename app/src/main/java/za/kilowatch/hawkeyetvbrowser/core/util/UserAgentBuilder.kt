package za.kilowatch.hawkeyetvbrowser.core.util

object UserAgentBuilder {
    enum class Mode { DESKTOP, MOBILE, CUSTOM }

    private const val DESKTOP_UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 HawkeyeTVBrowser/1.0"
    private const val MOBILE_UA = "Mozilla/5.0 (Linux; Android 13; TV) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 HawkeyeTVBrowser/1.0"

    fun build(mode: Mode, customUa: String? = null): String = when (mode) {
        Mode.DESKTOP -> DESKTOP_UA
        Mode.MOBILE -> MOBILE_UA
        Mode.CUSTOM -> customUa ?: DESKTOP_UA
    }
}
