# Changelog

All notable changes to Hawkeye TV Browser will be documented in this file.

## [1.1.0] — 2026-07-20

### Added
- **Voice Search Integration**: Built-in Android `SpeechRecognizer` service with voice command parsing ("go back", "open bookmarks", search queries) and Compose TV animated pulse visualizer dialog (`VoiceSearchDialog`).
- **Secure DNS (DNS-over-HTTPS)**: Integrated `com.squareup.okhttp3:okhttp-dnsoverhttps:5.4.0` supporting Cloudflare (`1.1.1.1`), Google (`8.8.8.8`), AdGuard DNS, and custom endpoints in `NetworkModule`.
- **Ad & Content Blocker Engine**: EasyList-compatible domain and pattern matching engine (`AdBlockEngine`) intercepting ad network requests in `HawkeyeWebViewClient`.
- **Reading Mode**: Distraction-free article reader (`ReaderModeScreen`) evaluating Mozilla Readability (`readability.js`) with Dark, Sepia, and Light themes plus typography controls.
- **Bookmark Folders**: Multi-level folder creation, folder navigation, and breadcrumb path tracking in `BookmarkDao`, `BookmarkUseCase`, and `BookmarkScreen`.
- **Google Safe Browsing Interstitial Warning**: Red TV security warning screen (`SafeBrowsingDialog`) triggered via `onSafeBrowsingHit` callback.
- **Accessibility Suite**: High-contrast CSS injection script and force-zoom viewport override in `AccessibilityHelper`.
- **Desktop Mode Default**: Set Desktop User-Agent as the default experience out of the box for Android TV screens.
- **Smart D-Pad Back Navigation**: Pressing D-pad Back on a root page with no web history resets to the Start Page state with cleared URL bar input instead of exiting. Pressing Back again from the Start Page exits the app.

## [1.0.1] — 2026-07-20

### Added
- **FileDownloader Engine**: Integrated OkHttp streaming download engine supporting chunked reads, live byte tracking, and dynamic filename guessing via `URLUtil` for all file types and websites.
- **Download Progress UI**: Added TV-optimized Compose `DownloadProgressDialog` showing progress bar, percentage counter, downloaded size vs. total file length, and cancellation controls.
- **APK Package Installation**: Automatically triggers Android `FileProvider` package installer upon download completion for `.apk` files with `REQUEST_INSTALL_PACKAGES` permission.
- **Universal WebView Download Listener**: Wired WebView `DownloadListener` and Downloader code redirect handling to automatically capture file downloads across all websites.
- **Full Localization**: Added XML string resources for all download dialog labels and user-facing text in `strings.xml`.

### Fixed
- Fixed an issue where Downloader codes and direct APK links failed to download or show download progress in WebView.

### Changed
- Updated `APKScripts` build and deployment tools (`build_tool.py`, `build_tool_builder.py`) for package `za.kilowatch.hawkeyetvbrowser` and activity `.MainActivity`.

## [1.0.0] — 2026-07-19

### Added
- Initial release of Hawkeye TV Browser for Android TV
- Chromium-based WebView engine with JavaScript, DOM storage, and hardware acceleration
- D-pad navigation with virtual cursor mode (configurable color and speed)
- URL bar with search engine support (Google, Bing, Yahoo, DuckDuckGo)
- Tab management with visual grid switcher
- Bookmarks with Room database persistence
- Browsing history grouped by date with search and clear
- Incognito/private browsing mode (no cookies, no history, ephemeral cache)
- Downloader code integration (aftv.news shortcodes) with safety confirmation dialog
- Desktop/Mobile site User-Agent toggle
- Text zoom control (50–200%)
- Start page with search bar and quick links
- Pop-up blocking and Do Not Track settings
- Full-screen video playback support
- Bluetooth keyboard shortcuts (Ctrl+T/W/L/D, Alt+Left/Right)
- Settings screen with search engine, display, privacy, and cursor configuration
