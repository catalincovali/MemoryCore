# Learning Plan — MemoryCore (Simon variant)

This document lists the concepts you need to study to complete the project described in
`Project ESP2526 1 (intermediate).pdf`. Topics are grouped roughly in the order in which
they will become useful.

---

## 1. Kotlin foundations (prerequisite)

You are using Kotlin + Jetpack Compose, so make sure you are comfortable with:

- **Lambdas and higher-order functions** — Compose APIs accept lambdas everywhere
  (`onClick = { ... }`, trailing-lambda syntax).
- **Data classes** — perfect for modelling a "completed game" (sequence + length).
- **Immutable collections** (`List`, `listOf`, `+ operator`) vs. mutable ones. In Compose
  you almost always replace a list, you don't mutate it.
- **Pair / destructuring** — already used in `GameScreen.kt`.
- **Sealed classes / enums** — useful to model the 6 colors as a domain type instead of
  raw `String`s.
- **Scoping functions** (`let`, `also`, `apply`, `run`) — handy but not mandatory.

---

## 2. Android app anatomy

- **Activity lifecycle** — what `onCreate` is, why it runs again on rotation, what a
  *configuration change* is. The spec explicitly requires you to **survive
  portrait↔landscape rotation without losing the sequence**.
- **`AndroidManifest.xml`** — declares the activity, app theme, supported orientations.
- **Resource system** (`res/`) — `values/`, `values-it/`, `drawable/`, `mipmap/`,
  qualifier folders (`-land`, `-night`, `-w600dp`...).
- **Build system (Gradle / `build.gradle.kts`)** — where dependencies and SDK versions
  live.

---

## 3. Jetpack Compose — core

This is the biggest chunk. Without it, nothing in the project works.

- **What a `@Composable` function is** and why it can only be called from another
  `@Composable`.
- **Recomposition** — the mental model: when state changes, Compose re-runs the
  affected functions. Side-effects must be inside `LaunchedEffect`/`SideEffect`, not
  in the function body.
- **Layout primitives**: `Box`, `Row`, `Column`, `Spacer`, `Arrangement`, `Alignment`.
- **`Modifier`** chain — order matters. Study `padding`, `fillMaxSize/Width/Height`,
  `weight`, `clip`, `background`, `border`, `clickable`.
- **Material 3 components**: `Scaffold`, `Button`, `Text`, `Surface`,
  `MaterialTheme.colorScheme` / `.typography`.
- **Previews** (`@Preview`) — for fast iteration, including `widthDp`/`heightDp` and
  `uiMode = Configuration.UI_MODE_NIGHT_YES`.

---

## 4. State in Compose (THE critical topic)

The current code has zero state — that is the gap to fill.

- **`remember { ... }`** — keeps a value across recompositions but **not** across
  rotation.
- **`mutableStateOf(...)`** — creates an observable state holder. `var x by
  remember { mutableStateOf(...) }` is the idiomatic form.
- **`rememberSaveable { ... }`** — survives configuration changes (rotation) and
  process death. Required by the spec for the in-progress sequence and the games
  list.
- **Custom `Saver`** — needed when storing a `List<MyEnum>` or a complex data class
  with `rememberSaveable`. Alternatives: store the sequence as a `String` of letters
  and parse it back.
- **State hoisting** — child composables receive `state` + `onEvent` from a parent
  instead of owning state. This is what your `ColorGrid`, `SequenceText`, and
  `ActionButtons` should do.
- **Unidirectional Data Flow (UDF)** — the Compose mantra: state flows down, events
  flow up.
- **`derivedStateOf`** — for values computed from other state (e.g. the comma-joined
  string shown in `SequenceText`).

---

## 5. ViewModel (recommended over rememberSaveable for the games list)

- **`androidx.lifecycle.ViewModel`** and the `viewModel()` Compose helper.
- **Why a ViewModel survives rotation** automatically.
- **`StateFlow` / `MutableStateFlow`** vs. `mutableStateOf` inside a ViewModel.
- **`collectAsStateWithLifecycle()`** — to observe a `StateFlow` from a Composable.
- For this project you can use either approach, but using a single ViewModel shared
  by both screens is the cleanest way to make the games list survive navigation and
  rotation.

  Dependency to add (Jetpack, allowed by the spec):
  `androidx.lifecycle:lifecycle-viewmodel-compose`.

---

## 6. Navigation Compose

The skeleton is already in `MainActivity.kt`. Concepts to learn:

- **`NavHost`, `NavController`, `composable("route")`** — already there.
- **`navController.navigate("route")`** to switch screens (you need this on
  "Fine partita" / End game).
- **Back stack & system back button** — Compose Navigation handles `onBackPressed`
  automatically; this is what the spec means by "tasto back di sistema".
- **Sharing state across destinations** — three options: shared ViewModel scoped
  to the NavGraph, navigation arguments, or a top-level state holder. Pick **one**
  and understand the trade-offs.
- **Type-safe routes** (Kotlin Serialization-based) — newer API, optional but nice.

---

## 7. Lazy lists (Screen 2)

- **`LazyColumn`** + **`items(list) { ... }`** — efficient scrolling list.
- **`ListItem`** (Material 3) — an out-of-the-box row layout with leading/trailing
  slots that fits the spec perfectly.
- **Text overflow**: `maxLines = 1`, `overflow = TextOverflow.Ellipsis` — this is the
  "indicatore grafico del troncamento" the spec asks for.
- **`Modifier.weight`** in a `Row` — to push the count and the sequence to opposite
  sides.

---

## 8. Configuration handling (portrait ↔ landscape)

- **`LocalConfiguration.current.orientation`** — already used in your code; works,
  but couples the composable to the device orientation.
- **Preferred alternative: `WindowSizeClass`** (`androidx.compose.material3
  .windowsizeclass`) — Google's recommended way: pick layouts based on width/height
  size class instead of raw orientation. Worth learning even if you stick with
  orientation for this assignment.
- **Resource qualifier `-land`** — for XML resources only; not used in Compose-only
  code, but useful to know.

---

## 9. Localization (Italian + English)

- **`res/values/strings.xml`** (default — English) and **`res/values-it/strings.xml`**
  (Italian).
- **`stringResource(R.string.xxx)`** in Compose to load strings.
- **Per-app language preferences** (`AppCompatDelegate.setApplicationLocales` /
  `LocaleListCompat`) — optional, but Android 13+ users expect it.
- **Important spec detail**: the color letters in the sequence text (`R, G, B, ...`)
  must always be the **English** initials regardless of UI language. Keep them as
  hard-coded constants tied to the color enum, not as translatable strings.

---

## 10. Theming & Material 3

- **Color scheme**, `MaterialTheme.colorScheme` — the 6 game colors should be
  defined in your theme (already done in `Color.kt`) and accessed via the theme,
  not hard-coded in the screen.
- **Dynamic color** on Android 12+ — currently enabled in `Theme.kt`. Be aware it
  *overrides* your custom palette on supported devices; for a game where colors
  must be R/G/B/M/Y/C it is usually safer to **disable** dynamic color.
- **Dark / light theme** — verify your six game colors stay distinguishable in both.

---

## 11. Testing & previews

- **`@Preview`** variants (orientation, locale, dark mode) for fast manual checks.
- **Unit tests** with JUnit — for any pure-Kotlin logic (e.g. building the
  comma-separated string).
- **Compose UI tests** with `createComposeRule()` — optional, but good practice.

---

## 12. Project hygiene (required by the spec)

- **`README.md`** in repo root listing the dev device + Android version.
- **`.gitignore`** that excludes `build/`, `app/build/`, `.idea/`, `local.properties`,
  `.gradle/`. The spec explicitly forbids committing build output.
- **No 3rd-party libraries** outside Android framework + Jetpack without written
  permission.
- **AI usage rules**: AI may give you feedback on existing code, but may not write
  more than 10 lines of code or fix issues for you. This `learning.md` and the
  feedback below comply with that rule.

---

## Suggested study order

1. Kotlin refresher (sec. 1) — 1 day max.
2. Compose layout + state (sec. 3 + 4) — biggest investment.
3. Navigation Compose (sec. 6) — small, but unblocks Screen 2.
4. ViewModel + StateFlow (sec. 5) — pick this OR `rememberSaveable` for the games
   list.
5. LazyColumn + text overflow (sec. 7).
6. Localization + theming polish (sec. 9 + 10).
7. Configuration changes & rotation tests (sec. 8 + 11).

Good free resources:
- *Jetpack Compose Pathway* on developer.android.com (official, hands-on).
- *Now in Android* sample repo on GitHub — reference for ViewModel + Navigation
  patterns.
- *Compose samples* repo (`android/compose-samples`).

---

# Feedback on the existing code

You asked me **not** to change the code — I will only point out what to look at when
you come back to it after studying. File:line references included.

## Will not compile / functional bugs

- **`GameScreen.kt:63` and `GameScreen.kt:89`** — `selectedSequence = emptyList()`
  references a symbol that is never declared anywhere in the file. The project will
  fail to build. Once you learn state hoisting (sec. 4), this becomes a `var`
  backed by `rememberSaveable { mutableStateOf(emptyList<...>()) }` owned by the
  parent and passed down via `onClear`.
- **`GameScreen.kt:152-153`** — inside `ActionButtons`, the `Row` uses
  `Modifier.fillMaxWidth()` instead of the `modifier` parameter passed in by the
  caller. The weight you set on `:60` and `:87` is therefore ignored. Standard
  pattern: `Row(modifier = modifier.fillMaxWidth(), ...)`.
- **`GameScreen.kt:139`** — `SequenceText` displays the hard-coded string
  `"R, G, B, Y"`. After sec. 4 you will pass in the live sequence and join it.
- **`GameList.kt:3-4`** — `GameList` is an empty top-level function and is **not**
  `@Composable`. Calling it from `NavHost` (`MainActivity.kt:35`) compiles only
  because the trailing-lambda block accepts `Unit`, but it renders nothing. Needs
  the `@Composable` annotation and an actual `LazyColumn`.
- **`MainActivity.kt:34`** — the second route is named `"message"`, but it points
  to a games list. Rename to `"history"` or `"games"` for clarity.
- The color rectangles in `ColorGrid` (`GameScreen.kt:114-128`) are **not
  clickable**. The whole gameplay loop (tap a color → append to sequence) is
  missing. After sec. 3, add `Modifier.clickable { onColorClick(letter) }`.
- The "End game" button (`GameScreen.kt:64`, `:90`) has an empty lambda. It must:
  (1) snapshot the current sequence into the games list, (2) clear the current
  sequence, (3) navigate to Screen 2. Even an **empty** sequence must be saved —
  re-read the PDF, this is explicitly required.

## Architecture / state

- **No state at all yet.** This is by far the biggest gap. Decide between:
  - `rememberSaveable` + state hoisting (simpler, fits the spec since persistence is
    not required), or
  - a single `GameViewModel` exposing `currentSequence: StateFlow<List<Color>>` and
    `finishedGames: StateFlow<List<Game>>` (cleaner once Screen 2 exists).
- **No domain model.** Right now a color is a `Pair<String, Color>`. A small
  `enum class GameColor(val letter: Char, val hue: Color)` would let `SequenceText`
  do `sequence.joinToString(", ") { it.letter.toString() }` and would make the
  saver trivial.
- **Colors live in the screen, not in the theme.** `ColorRed`, `ColorGreen`, etc.
  are defined in `ui/theme/Color.kt` (good) but consumed directly. Consider adding
  them to a custom `LocalGameColors` `CompositionLocal` or as extension properties
  on `MaterialTheme` so dark/light variants stay together.
- **Dynamic color enabled** (`Theme.kt:40`). On Android 12+ this **overrides** your
  carefully picked R/G/B/M/Y/C palette. For a Simon-style game, set
  `dynamicColor = false` when calling `MemoryCoreTheme` (or remove the parameter).

## Spec compliance gaps to address later

- **No localization.** `strings.xml` contains only `app_name`. You need translations
  for "Cancella / Clear", "Fine partita / End game", screen titles, the empty-list
  message, etc., plus a `values-it/strings.xml` file.
- **Screen 2 is missing entirely** — `GameList.kt` is a stub.
- **No back-navigation handling shown.** `NavHost` gives you back-button support
  for free, but make sure you do not call `navigate("game")` again on End-game,
  which would push a new copy. Use `popBackStack()` or `navigate("game") { popUpTo
  ... }`.
- **No `README.md`** in the repository root, which the spec requires.
- **`.idea/` files are tracked** (visible in `git status`). The spec forbids
  committing local Android Studio configuration. Add an appropriate `.gitignore`
  (the standard Android Studio template is fine) and untrack the offenders.

## Small style nitpicks (optional)

- `GameScreen.kt:108-112` — the inner `Row` uses `Modifier.fillMaxSize().weight(1f)`.
  `fillMaxSize()` is redundant inside a vertically-weighted column row;
  `fillMaxWidth().weight(1f)` is enough and more readable.
- `GameScreen.kt:21` — wildcard import `com.catalincovali.memorycore.ui.theme.*`.
  Many style guides prefer explicit imports; not a bug, just convention.
- The two commented-out `.weight(5f)` lines (`:56`, `:61`) hint at unfinished
  layout work — keep or delete intentionally.

## What is good already

- Project is set up with **Compose BOM**, **Material 3**, **Navigation Compose**,
  and **runtime-saveable**. All the dependencies you need are present.
- The portrait/landscape branching in `GameScreen` is the correct *shape* for the
  spec (3×2 grid in both orientations, b)+c) stacked beside the grid in landscape).
- `ColorGrid` already uses the `chunked(2)` trick to lay out 3 rows of 2 — clean.
- Custom palette in `Color.kt` uses pleasant Material 200-level shades that work
  well on a light background.
- The composables already accept a `modifier: Modifier = Modifier` parameter — that
  is the recommended Compose API convention. You just need to actually *use* it
  inside `ActionButtons`.

Focus on **state and state hoisting first** (sec. 4); most of the bugs above
disappear once that is in place.
