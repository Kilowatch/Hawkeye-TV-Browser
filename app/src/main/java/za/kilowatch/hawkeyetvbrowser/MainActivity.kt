package za.kilowatch.hawkeyetvbrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import za.kilowatch.hawkeyetvbrowser.ui.main.MainViewModel
import za.kilowatch.hawkeyetvbrowser.ui.theme.HawkeyeTVBrowserTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HawkeyeTVBrowserTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val isIncognito = false // Will be updated from BrowserViewModel state

    NavHost(
        navController = navController,
        startDestination = "browser"
    ) {
        composable("browser") {
            HawkeyeTVBrowserTheme(isIncognito = isIncognito) {
                za.kilowatch.hawkeyetvbrowser.ui.browser.BrowserScreen(
                    mainViewModel = mainViewModel,
                    onNavigateToTabs = { navController.navigate("tabs") },
                    onNavigateToBookmarks = { navController.navigate("bookmarks") },
                    onNavigateToHistory = { navController.navigate("history") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
        }

        composable("tabs") {
            za.kilowatch.hawkeyetvbrowser.ui.tabs.TabManagerScreen(
                onTabSelected = {
                    navController.popBackStack()
                },
                onNavigateToBrowser = {
                    navController.popBackStack()
                }
            )
        }

        composable("bookmarks") {
            za.kilowatch.hawkeyetvbrowser.ui.bookmarks.BookmarkScreen(
                onBookmarkOpened = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("history") {
            za.kilowatch.hawkeyetvbrowser.ui.history.HistoryScreen(
                onEntryOpened = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("settings") {
            za.kilowatch.hawkeyetvbrowser.ui.settings.SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
