package za.kilowatch.hawkeyetvbrowser.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
private val NormalColorScheme = darkColorScheme(
    primary = TvPrimary,
    onPrimary = TvOnPrimary,
    primaryContainer = TvPrimaryContainer,
    onPrimaryContainer = TvOnPrimaryContainer,
    secondary = TvSecondary,
    onSecondary = TvOnSecondary,
    background = TvBackground,
    surface = TvSurface,
    surfaceVariant = TvSurfaceVariant,
    onBackground = TvOnBackground,
    onSurface = TvOnSurface,
    onSurfaceVariant = TvOnSurfaceVariant,
    error = TvError,
    onError = TvOnError,
    errorContainer = TvErrorContainer,
    onErrorContainer = TvOnErrorContainer
)

@OptIn(ExperimentalTvMaterial3Api::class)
private val IncognitoColorScheme = darkColorScheme(
    primary = IncognitoPrimary,
    onPrimary = IncognitoOnPrimary,
    primaryContainer = IncognitoPrimaryContainer,
    onPrimaryContainer = IncognitoOnPrimaryContainer,
    background = IncognitoBackground,
    surface = IncognitoSurface,
    surfaceVariant = IncognitoSurfaceVariant,
    onBackground = IncognitoOnBackground,
    onSurface = IncognitoOnSurface,
    error = TvError,
    onError = TvOnError,
    errorContainer = TvErrorContainer,
    onErrorContainer = TvOnErrorContainer
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HawkeyeTVBrowserTheme(
    isIncognito: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isIncognito) IncognitoColorScheme else NormalColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
