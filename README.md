<div align="center">
  <img src="hawkeye_tv_logo_amber.png" alt="Hawkeye TV Browser" width="128" height="128">
  <h1 align="center">Hawkeye TV Browser</h1>
  <p align="center">
    <strong>A premium web browser for Android TV & Google TV</strong>
  </p>
  <p align="center">
    <a href="https://github.com/Kilowatch/Hawkeye-TV-Browser/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-GPLv3-blue.svg" alt="License: GPL v3"></a>
    <img src="https://badgen.net/badge/Platform/Android%20TV/3DDC84?icon=android" alt="Platform: Android TV">
    <img src="https://badgen.net/badge/Language/Kotlin/7F52FF?icon=kotlin" alt="Language: Kotlin">
    <img src="https://badgen.net/badge/Min%20SDK/24/FF6F00?icon=android" alt="Min SDK: 24">
    <img src="https://badgen.net/badge/UI/Compose%20TV/4285F4?icon=jetpackcompose" alt="UI: Compose for TV">
  </p>
</div>

---

**Hawkeye TV Browser** is a native Android TV web browser purpose-built for the big screen. It is designed from the ground up for D-pad and remote-control navigation, bringing a desktop-class browsing experience to Android TV and Google TV devices. Built with Kotlin and Jetpack Compose for TV, Hawkeye delivers modern rendering via the Android system WebView (Chromium) without needing a custom fork.

## ✨ Features

### 🔧 Core Browsing
- **Chromium-based rendering** — Powered by the Android System WebView with JavaScript, DOM storage, and hardware acceleration
- **Dual navigation modes** — Switch between spatial (focus-based) navigation and a virtual cursor mode, both optimised for D-pad remotes
- **Tab management** — Visual grid-based tab switcher with live thumbnails; open, close, and switch between tabs effortlessly
- **Incognito / Private browsing** — Ephemeral mode with no history, no cookies, and automatic session cleanup
- **Full-screen video** — Seamless HTML5 video playback with dedicated full-screen controls
- **Desktop / Mobile site toggle** — User-Agent switching with automatic page reload

### 🎯 TV-Optimised UX
- **D-pad / Remote navigation** — Full spatial focus system plus a configurable virtual cursor with adjustable colour and speed
- **Bluetooth keyboard shortcuts** — `Ctrl+T/W/L/D`, `Alt+Left/Right`, and more for keyboard users
- **Start page** — Customisable homepage with search bar, quick links grid, and recently visited sites
- **Configurable text zoom** — 50% to 200% text scaling for comfortable reading on any screen size

### 🔍 Search & Navigation
- **Multi-engine search** — Google, Bing, Yahoo, DuckDuckGo, or a custom engine of your choice
- **Downloader code integration** — Type a numeric AFTVnews shortcode (e.g. `68546`) directly in the URL bar. Hawkeye resolves it via `aftv.news` and shows a safety confirmation dialog before navigating.
- **Smart URL resolution** — Distinguishes URLs, search queries, and downloader codes automatically

### 🔒 Privacy & Security
- **Do Not Track** — Sends DNT:1 headers when enabled
- **Pop-up blocking** — Built-in pop-up blocker with per-session control
- **Clear browsing data** — Granular clearing of history, cache, cookies, and session data
- **Incognito mode** — Isolated WebView with no persistent state

### 📚 Data Management
- **Bookmarks** — Room database-backed bookmarking with add/remove persistence
- **Browsing history** — Searchable history grouped by date with clear controls
- **Settings backup** — Configurable defaults for search engine, display, privacy, and cursor preferences

## 📸 Screenshots

*(Coming soon)*

## 📋 Requirements

- **Android TV** or **Google TV** device (API 24+)
- Android System WebView (updated via Play Store)
- A D-pad remote control (included with all Android TV devices)

## 🛠️ Tech Stack

| Component | Technology |
|---|---|
| **Language** | Kotlin 2.2+ |
| **UI Framework** | Jetpack Compose for TV (`androidx.tv.material3`) |
| **Rendering** | Android System WebView (Chromium) |
| **Architecture** | MVVM with Repository pattern |
| **DI** | Hilt (Dagger) |
| **Database** | Room (SQLite) |
| **Networking** | OkHttp |
| **Image Loading** | Coil |
| **Min SDK / Target** | API 24 / API 36 |
| **Build System** | Gradle with Kotlin DSL |

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) Hedgehog (2023.1.1) or later
- An Android TV emulator or physical device running API 24+
- JDK 17+

### Build & Install

```bash
# Clone the repository
git clone https://github.com/Kilowatch/Hawkeye-TV-Browser.git
cd Hawkeye-TV-Browser

# Build a debug APK
./gradlew assembleDebug

# Install on a connected device or emulator
./gradlew installDebug
```

### Running Tests

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumentation tests (requires emulator or device)
./gradlew connectedDebugAndroidTest

# Lint checks
./gradlew lintDebug
```

## 🎮 Usage

### Navigation

| Action | Remote / D-Pad | Keyboard |
|---|---|---|
| Move cursor / Focus | D-pad arrows | Arrow keys |
| Select / Click | Center / Enter | Enter |
| Go back | Back button | `Alt + Left` |
| Go forward | — | `Alt + Right` |
| New tab | Toolbar menu → New Tab | `Ctrl + T` |
| Close tab | Toolbar menu → Close Tab | `Ctrl + W` |
| Focus URL bar | Toolbar menu → URL | `Ctrl + L` |
| Add bookmark | Toolbar menu → Bookmark | `Ctrl + D` |
| Open bookmarks | Toolbar menu → Bookmarks | `Ctrl + B` |
| Open history | Toolbar menu → History | `Ctrl + H` |
| Reload | Toolbar menu → Reload | `Ctrl + R` / `F5` |

### Downloader Codes

Type a **numeric-only** code (1–8 digits) in the URL bar and press Go. Hawkeye resolves it against `aftv.news` and presents a confirmation dialog showing the destination URL, file type, and trust level. You choose whether to proceed.

## 🗺️ Roadmap

### Phase 1 — Core Browser ✅ *(Current)*
- [x] WebView integration with hardware acceleration
- [x] D-pad navigation with spatial + cursor mode
- [x] Tab management with visual tab switcher
- [x] Bookmarks & browsing history (Room database)
- [x] Incognito mode
- [x] Start page with quick links
- [x] Full-screen video playback
- [x] Downloader code integration
- [x] Settings: search engine, display, privacy, cursor configuration
- [x] Bluetooth keyboard shortcuts

### Phase 2 — Enhanced Features *(Planned)*
- [ ] Voice search with Google Assistant integration
- [ ] Download manager with progress UI
- [ ] Bookmark folders
- [ ] Ad blocking engine (EasyList)
- [ ] Reading mode (Readability.js)
- [ ] Google Safe Browsing
- [ ] DNS-over-HTTPS
- [ ] Cookie controls
- [ ] Enhanced accessibility (TalkBack, magnifier, high contrast)

### Phase 3 — Advanced *(Planned)*
- [ ] Form autofill with encrypted storage
- [ ] Password manager
- [ ] Predictive page preloading
- [ ] Picture-in-Picture video playback
- [ ] HTTP/2 connection optimisation
- [ ] Cross-device sync (bookmarks & history)

## 📄 License

Hawkeye TV Browser is free software: you can redistribute it and/or modify it under the terms of the **GNU General Public License v3.0**. See [LICENSE](LICENSE) for the full text.

Copyright © 2026 [Kilowatch](https://github.com/Kilowatch)

## 🙏 Acknowledgements

- [AFTVnews](https://www.aftvnews.com/) — for the downloader shortcode system that inspired the Downloader Code feature
- [Jetpack Compose for TV](https://developer.android.com/training/tv/start/libraries) — Google's modern TV UI toolkit
- All open-source libraries that make this project possible (Room, Hilt, OkHttp, Coil, and others)

---

<div align="center">
  <sub>Built with ❤️ for the Android TV community</sub>
</div>
