# Project Rules

## Development Pipeline

This project follows a **Spec-Driven Development (SDD)** pipeline. Every feature or change must go through these phases in order:

1. **Specify** (`/specify`) — Define WHAT to build. Produces `spec.md`.
2. **Plan** (`/plan`) — Design HOW to build it. Produces `plan.md`.
3. **Clarify** (`/clarify`) — Review the spec (and plan if it exists) and ask targeted questions to fill gaps before implementation. Produces `clarifications.md`.
4. **Tasks** (`/tasks`) — Break into ordered steps. Produces `tasks.md`.
5. **Implement** (`/implement`) — Execute tasks one by one. Produces code changes.
6. **Review** (`/review`) — Audit code quality, Kotlin idioms, and Android-specific issues. Produces `review.md`.
7. **Security Check** (`/securitycheck`) — Full security audit of the implementation. Checks for hardcoded credentials, insecure storage, network security, data exposure, and more. Produces `security-audit.md`. Also usable on-demand against any named feature at any time.
8. **Verify** (`/verify`) — Confirm the implementation satisfies the spec. Produces `verification.md`.
9. **Test** (`/test`) — Generate and execute a step-by-step test plan on device. Produces `test-plan.md`.
10. **Release** (`/release`) — Prepare for production: version bump, build, store copy. Produces `release.md`. *(Run only when shipping to Google Play or Amazon.)*
11. **Reddit** (`/reddit`) — Write a community announcement post for the release. Produces `reddit-post.md`. *(Optional — run after /release when you want to post to the community.)*

> **Rule:** Do not skip phases. `/clarify` must be completed (all questions answered) before `/tasks`. It requires only `spec.md` — `plan.md` is used as optional context if available. `/review` must pass (no Critical/Major findings unresolved) before `/securitycheck`. `/securitycheck` must pass (no Critical/High findings unresolved) before `/verify`. `/verify` must pass before `/test`. `/test` must be fully executed on device before `/release`. `/reddit` is optional and runs after `/release` when you want to announce to the community. `/securitycheck` may also be run on-demand at any time against any named feature outside the pipeline.

## Project Memory

All persistent project memory files live in `.claude/memory/` (project-relative), indexed by `.claude/memory/MEMORY.md`. When writing new memories:

- Write the `.md` file to `.claude/memory/<name>.md` inside the project repository.
- Add a one-line pointer to `.claude/memory/MEMORY.md`.
- Do NOT write to `C:\Users\Nibiru\.claude\projects\F--Share-UFM\memory\` — that is the user-level mirror. Keep memories in the repo so they travel with the codebase.

## Rules

- **Do NOT write any code** until a plan and task list have been approved.
- Always present the plan for approval before implementing.
- Store all planning artifacts in `.plans/` — they are part of the project's documentation.
- Each task in `tasks.md` must be checked off as complete during implementation.
- If new requirements emerge during implementation, stop and re-specify.
- Always commit planning artifacts to version control alongside code changes.
- `/clarify` must be run before `/tasks` — all questions must be answered before task breakdown begins. It can run after `/specify` or after `/plan`; it only requires `spec.md`. If there are no gaps, it will confirm this and allow proceeding directly.
- `/review` must be run after `/implement` and before `/securitycheck` — do not skip it even for small changes.
- `/securitycheck` must be run after `/review` and before `/verify` — Critical and High findings block the pipeline. It can also be run at any time on-demand against any named feature.
- `/test` must be run after `/verify` — generate the plan, execute it on a device, then proceed to `/release`.
- `/release` is only run when the user explicitly asks to ship a build to Google Play or Amazon.
- `/reddit` is optional and can be run after `/release` to draft a community post. It is never run automatically.

## Repository Documentation (CHANGELOG & SECURITY)

### Ask Before Writing
**Never write to `CHANGELOG.md` or `SECURITY.md` without first asking the user for a description of what to add.** Present a proposed changelog entry and get explicit approval before saving changes to these files. The user knows their release notes best — do not guess or auto-generate changelog descriptions.

### CHANGELOG.md Updates
Every significant change **must** update `CHANGELOG.md`. After implementing a change, present the user with a proposed entry and ask them to confirm or edit it. Categorize under:
- `### Added` — New features
- `### Changed` — Behavior modifications
- `### Fixed` — Bug fixes
- `### Deprecated` — Deprecations
- `### Removed` — Removals
- `### Security` — Security patches

Group entries under the next version header (`## [x.y.z] — YYYY-MM-DD`). Before bumping the version number in `app/build.gradle.kts` (`appVersionName`, `appVersionCode`), **ask the user** with a prompt like: *"Do you want to bump to version x.y.z?"* giving them three options: **Yes** (use the proposed version), **No** (skip the bump), or **Other** (they type a custom version number). Only write the new version after receiving their explicit confirmation.

> **Note:** Version bumping during `/implement` is for development increments only. The definitive production version bump happens in `/release`. Do not bump to a release version number during `/implement`.

### SECURITY.md Updates
- Only update with user approval and a clear description of what changed.
- Update the **Supported Versions** table when a new major/minor version is released.
- If the disclosure process changes (new email, new response SLA), update the relevant sections.
- Do NOT include vulnerability details in SECURITY.md itself — that's what security advisories are for.

### Version References
When updating the app version in `app/build.gradle.kts`, verify these files are also up to date (with user approval):
1. `CHANGELOG.md` — new version header and entries
2. `SECURITY.md` — supported versions table (if applicable)

## Code Conventions

### Activity Pattern
Every new Activity must follow these three conventions:
1. **attachBaseContext** — Override to call `LocaleManagerWrapper.setLocale(this, super.attachBaseContext(newBase))`.
2. **Edge-to-edge** — Call `EdgeToEdge.enable(this)` in `onCreate()` before `setContentView` and handle system bar insets.
3. **One activity, two layouts** — Single Activity class switching between `activity_<name>.xml` (mobile) and `activity_<name>_tv.xml` (TV) using an `isTv` check.

### Icon Registration Checklist
Every new `ic_*.xml` drawable must be registered in ALL of these locations:

1. **`IconCustomizationManager.kt`** — Add `R.drawable.ic_*` to the `ALL_BUILTIN_ICONS` int array (master palette for the icon picker grid).
2. **`IconCustomizationActivity.kt`** — Add `IconItemData(id, label, defaultRes, builtinAlts)` in the appropriate category inside `loadIconCategories()` or `setupViews()`.
3. **`SettingsActivity.kt`** — Add a `CardIcon(cardId, iconId, defaultRes)` entry in `applySettingsCardIcons()` if the icon represents a settings card row.
4. **`ToolbarIconsActivity.kt`** — Add an `IconItem(iconResId, nameResId, prefKey)` in `populateIconsList()` if it's a toolbar action.
5. **`IconPackExportActivity.kt`** — Add the icon's ID to the appropriate category in `loadCategories()`, or add a new `CategorySelection` if the category doesn't exist yet.
6. **`strings.xml`** — Add a translatable string resource following the naming convention `icon_{category}_{name}` (e.g. `icon_nav_back`, `icon_settings_language`). No hardcoded English strings in Kotlin.
7. **`ToolbarIconsPreferenceManager.kt`** — Add a `KEY_*` constant if it's a toolbar action toggle.

For non-toolbar icons skip #4 and #7. For non-settings-card icons skip #3. For utility/palette-only icons (not settings card, not toolbar), skip #3, #4, #7.

### Main Menu Tile Registration Checklist
Every new tile added to the Main Menu (`StorageBrowserActivity.loadStorageVolumes()`) MUST also be registered in ALL of these locations:

1. **`StorageBrowserActivity.buildAllKnownTiles()`** — Add the tile to this static method inside the `companion object`. This is the canonical tile registry used by `CustomTileActivity` to resolve tile IDs to full `StorageItem` objects. Without this entry, a tile moved into a custom tile will NOT render and will appear as missing. For dynamically-generated tiles (favorites, network shares, etc.), ensure the generation logic in `buildAllKnownTiles()` matches `loadStorageVolumes()`.

2. **`TileOrderManager.isFeatureTile()`** — Add the tile's boolean flag (e.g. `isNewFeatureTile`) to the `isFeatureTile()` check so it gets categorized correctly for order merging.

3. **`StorageItem.kt`** — If the tile has a fixed ID and needs hideability, add its boolean flag to the `isHideable` computed property.

4. **`strings.xml`** — Add a translatable string resource for the tile's label and subtitle. No hardcoded English strings in Kotlin.

For tiles created at runtime (dynamic IDs), skip #3 unless the tile type itself needs hideability configuration.

### Dialog Pattern
Every new dialog must follow this pattern (reference: `IndexingUiHelper.kt`, `PinDialogHelper.kt`):

1. **Mobile + TV layout pair** — `dialog_<name>.xml` and `dialog_<name>_tv.xml`. Mobile: `bg_bottom_sheet` background. TV: `bg_dialog_glass` background, fixed width (700-900dp).
2. **Builder** — `MaterialAlertDialogBuilder(context, R.style.UFM_Dialog)`. Set `dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))` after creation.
3. **Input fields** — Always `TextInputLayout` + `TextInputEditText` (NEVER plain `EditText`). OutlinedBox style. Password fields use `app:endIconMode="password_toggle"`. TV adds `boxBackgroundColor="@android:color/transparent"` and `hintTextColor="@color/tv_text_hint"`.
4. **Mobile buttons** — `MaterialButton` with `app:cornerRadius="12dp"`. Primary: `backgroundTint="@color/ufm_primary"`, `textColor="@android:color/white"`. Secondary: `style="@style/Widget.MaterialComponents.Button.TextButton"`.
5. **TV buttons** — `Button` (not MaterialButton) with `selector_tv_button_yellow` / `selector_tv_button` background drawables. Focus styling applied programmatically after `dialog.show()`: yellow `ColorStateList` + `setOnFocusChangeListener` + `requestFocus()`.
6. **TV post-show focus** — Apply `ColorStateList` to primary button (yellow, `requestFocus()`), secondary button (glass→yellow on focus), following the exact pattern in `IndexingUiHelper.showIndexingOfferDialog()`.

### Settings Backup Registration Checklist
Every new SharedPreferences file or user-facing setting MUST be registered in the backup/restore system so it is included when the user exports/imports their configuration. Without this, settings silently disappear when transferring between devices.

1. **`SettingsBackupManager.kt` — `getAvailableBackupItems()`** — Add a new `add("prefs_name", context.getString(R.string.backup_pref_*))` call to register the prefs file in the export selection list.
2. **`SettingsBackupManager.kt` — `parseBackupContent()`** — Add the same prefs key to the `prefsMapping` map so the import UI shows a human-readable label for this setting.
3. **`strings.xml`** — Add a new `backup_pref_*` string resource with a user-friendly display name describing what the setting controls.

**If the setting stores file paths** (e.g. custom icon images), you MUST also:
4. **Export** — Embed the referenced files as base64 in the JSON (see `icon_files` pattern in `performExport()`).
5. **Import** — Decode the base64 data, write files to the target device's `filesDir`, and rewrite any absolute paths stored in SharedPreferences to use the new device's `filesDir` (see `fixIconPathsInPrefs()` pattern in `performRestore()`).

**Simple boolean/string/int prefs** only require steps 1–3. The generic `shared_preferences` export/import loop handles the actual data automatically.

**Backward compatibility rule**: Never bump the backup version for additive changes. Old backups that lack the new keys import cleanly because `optJSONObject`/`optString` return null for missing keys. Old app versions ignore unknown keys in the JSON.

### Translation Verification After New Strings
Every time new English `string` resources are added to `strings.xml`, the **final task in the implementation phase** must run the translation verification script:

```bash
python -u translate_strings.py verify
```

This ensures all locale-specific `strings.xml` files have corresponding entries. The task list in `tasks.md` should include this as the last step:
```
- [ ] [T-NNN] Run `python -u translate_strings.py verify` to sync translations
```

See `.claude/memory/translation_verify_after_strings.md` for details.

### Folder Picker Standard
Every new folder/directory picker must follow this pattern (reference: Document Scanner "Select Folder", Auto Backup custom location):

1. **Define `EXTRA_*`** — Add a unique `const val EXTRA_MY_PICKER = "extra_my_picker"` in `FileBrowserActivity` companion object.
2. **Add boolean flag** — `private var isMyPicker = false` read from intent in `FileBrowserActivity.onCreate()`.
3. **Configure FAB** — Add a `when` branch in `FileBrowserActivity.onResume()` setting FAB text, icon, and click listener.
4. **Confirmation dialog** — `MaterialAlertDialogBuilder(this, R.style.UFM_Dialog)` confirming selection before returning the path.
5. **Return values** — Local: `RESULT_SELECTED_LOCAL_PATH`. Network: `RESULT_SELECTED_SHARE_ID` + `RESULT_SELECTED_NET_PATH`.
6. **Launch** — From the calling Activity via `registerForActivityResult` → `Intent(StorageBrowserActivity)` with the EXTRA.

See `.claude/memory/folder_picker_standard.md` for the full reference with code examples.

### SMB Server Mode Path Stripping (isServerMode)
When an SMB profile is configured in "Server mode" (`share.isServerMode` is true), the profile represents the entire server rather than a specific share. 

1. **Path Structure**: In server-mode, UI paths start with the share name (e.g. `docker/_projects/file.txt`), whereas the database profile has an empty `remotePath`.
2. **Duplication Guard**: To prevent the SMB client from connecting to `\\server\docker\docker\_projects` (duplicating the share name segment), always resolve the dynamic share name and strip the prefix before client calls.
3. **Pattern**: Use a resolver helper to get the effective `NetworkShare` (with `remotePath` set to `"/$shareName"`) and the cleaned relative path (e.g. `_projects/file.txt`).

See `.claude/memory/smb_server_mode_stripping.md` for the full reference and code examples.

