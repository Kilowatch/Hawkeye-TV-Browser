# Changelog

All notable changes to Hawkeye TV Browser will be documented in this file.

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
