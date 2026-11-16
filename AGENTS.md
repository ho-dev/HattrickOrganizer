# AGENTS.md

Guidance for AI coding agents (and humans) working on this repository.

## Project

HattrickOrganizer ("HO!") is a Java Swing desktop client for the online football manager
[Hattrick](https://www.hattrick.org). It downloads a team's data through the Hattrick CHPP API,
stores it in an embedded HSQLDB database, and builds analysis modules (training prediction,
lineup rating, transfer scouting, statistics, …) on top of that local history.

- Repository: <https://github.com/ho-dev/HattrickOrganizer>, default branch `master`, LGPL 3.0.
- User documentation: <https://ho.readthedocs.io/> (sources in `docs/*.rst`).
  Contribution workflow: `docs/contributing.md`. CHPP files HO uses: `docs/chpp_files.md`.
- Java 17 source/target, Kotlin JVM toolchain 17, Gradle 8.13 wrapper. CI builds on Temurin 17.
  Newer local JDKs work for running, but do not raise the language level.

## Workflow

- Contributions come as pull requests from a branch on your own fork, one feature or fix per
  branch, against `master` (or a `release/**` branch for fixes to a release).
- CI (`.github/workflows/ci.yml`) runs `./gradlew test` on every pull request. Run it locally
  before opening one.
- Fill in `.github/pull_request_template.md`: name the issue (`#1234`) and either add a line to
  `src/main/resources/release_notes.md` under the matching section, or tick that no update is
  needed. User-visible changes need a release-notes line.

## Build and run

```bash
./gradlew run                     # run HO from the classes dir - the normal dev loop
./gradlew test                    # full unit test suite (JUnit 5)
./gradlew test --tests "core.util.MathUtilsTest"          # single class
./gradlew test --tests "core.util.MathUtilsTest.someCase" # single method
./gradlew tasks                   # task list
./gradlew tiTree <task>           # task graph (taskinfo plugin)
```

A runnable jar for hand-out:

```bash
./gradlew createVersion createDistribution
cd build/install/XXX && java -jar HO.jar
```

- `createVersion` computes the four-part `major.minor.build.stage` version that
  `core.util.HOBuildLoader` reads from the jar manifest; it must run in the *same* Gradle
  invocation as `jar`. Without a manifest (IDE run) HO falls back to `core.BuildInfo`.
- `createDistribution` flattens the dependency jars next to `HO.jar` (the manifest `Class-Path`
  lists them by bare filename) and copies `prediction/` and `truststore.jks`, which `FileLoader`
  resolves from the working directory first. A bare `./gradlew jar` cannot start on its own.
- Run the jar from inside its distribution directory: in portable mode HO writes `db/`, `logs/`
  and `user.xml` relative to the working directory, and `./gradlew clean` deletes them with
  `build/`.

### Build pipeline quirks

- `compileJava.dependsOn(clean)`: **every build is a full clean rebuild.** There is no
  incremental compilation; expect a noticeable minimum turnaround.
- `processResources` → `poeditorPull` → `markdownToHtml` → `pushmd`, so every build calls the
  POEditor API and shells out to `git`. Without `POEDITOR_APIKEY` the pull prints
  `Invalid API Token` and continues. That is expected locally and not a failure.
- `gradle.properties` holds local-only knobs: `DEV_STAGE` (0 DEV / 1 BETA / 2 STABLE),
  `POEDITOR_APIKEY`, `install4jHomeDir`, and `resetDir`/`tempDir` for the `resetDB`/`pushDB`
  tasks that copy a database between a real installation and the dev tree. Do not commit
  secrets there.
- The marketing version lives in `build.gradle` (`version = '11.0'`); the build number comes from
  the `RUN_NUMBER` env var (GitHub Actions run number, `0` locally).
- `./gradlew release` builds every platform artefact through install4j
  (`utils/buildResources/HO.install4j`) and needs an install4j licence. It runs in
  `.github/workflows/release-dev.yml`; agents normally have no reason to run it.

## Architecture

### Startup

`core.HOLauncher.main` → `core.HO.main`. The launcher is a self-updater: if `update.piz` sits in
the install directory it backs the install up into `backup/`, unpacks the update, then hands off.
`HO.main` parses log-level args (`INFO`/`DEBUG`/…) and the `INSTALLED` flag, reads the version,
offers a user picker if several users exist, shows `SplashFrame`, backs up the DB, loads
`UserParameter`, applies the look and feel theme, runs DB migrations, initialises
`TrainingManager`, and finally shows `core.gui.HOMainFrame`.

### Singletons

The codebase is built on `X.instance()` singletons: `HOVerwaltung`, `DBManager`, `UserManager`,
`ModuleManager`, `UserParameter`, `TrainingManager`, `HOMainFrame`, `ThemeManager`,
`RefreshManager`, `HOLogger`. There is no dependency injection; follow the same pattern rather than
introducing a container.

`core.model.HOVerwaltung` ("administration") holds the current `HOModel`, one snapshot of the
team at one point in time; `loadLatestHoModel()` rebuilds it from the newest HRF in the database.
Most UI code reaches its data via `HOVerwaltung.instance().getModel()`.

### Data pipeline

Hattrick CHPP (OAuth 1.0a, scribejava) → `core.net.Connector` / `HattrickAPI` →
`core.net.OnlineWorker` (download orchestration: `getHrf`, `downloadMatchData`, `getMatches`,
`uploadMatchOrder`, …) → `core.file.xml.XML*Parser` (one parser per CHPP file) →
`ConvertXml2Hrf` / `core.file.hrf` → model objects → `core.db` tables.

HRF is the legacy Hattrick export format and remains the internal snapshot unit: one HRF per
download, and the training engine replays the HRF history to derive sub-skills.

### Persistence

`core.db.DBManager` is the facade over an embedded HSQLDB. Each table subclasses
`core.db.AbstractTable` and declares its columns as a `ColumnDescriptor[]` in `initColumns()`;
the base class generates select/insert/update/delete SQL from it, using `idColumns` for the key.

**Schema changes require a migration.** `DBManager.DB_VERSION` (currently `1100`, "HO 11.0") is
the target and `PREVIOUS_DB_VERSION` the one before. `core.db.DBUpdater.updateDB` is a
fall-through `switch` over the stored version: each `case` runs its `updateDBvNNN()` and falls
into the next, so an old database runs every later step. Adding a column means changing the
table's `initColumns()` and adding the change to the update method of the version under
development (for a new release: a new `case`, its `updateDBvNNNN()`, and a
`DB_VERSION`/`PREVIOUS_DB_VERSION` bump).
Migrations must be idempotent: `DBManager.getDBVersion()` returns `PREVIOUS_DB_VERSION` when
`HO.isDevelopment()`, so DEV builds re-run the newest migration on every start. Use the
`AbstractTable` helpers that check first (`tryAddColumn`, `columnExistsInTable`,
`tryChangeColumnDataType`, …) rather than raw `ALTER TABLE`.

HSQLDB is single-process: a dev run and an installed HO must not open the same DB at once.

### Where user data lives

`UserManager` picks the location. `HO.main` defaults to portable mode; the `INSTALLED` argument
flips it.

| Mode                                    | Location                                          |
|-----------------------------------------|---------------------------------------------------|
| portable (default, and any dev/jar run) | the working directory (`user.dir`)                |
| installed, macOS                        | `~/Library/Application Support/HO`                |
| installed, Linux                        | `~/.ho`                                           |
| installed, Windows                      | `%AppData%/HO`                                    |

Users are listed in `users.json`; each user gets its own DB subfolder.

### Modules

Feature areas are modules under `src/main/java/module/*`, each implementing `core.module.IModule`
(usually via `DefaultModule`): an id constant from `IModule`, a tab panel, an optional config
panel and menu, and an activation status persisted per user. `core.module.ModuleManager`
creates them all in `initMap()`: a new module needs an id constant in `IModule` and a
`map.put(...)` line there. `src/main/java/tool/*` holds smaller utilities (arena sizer, HRF
explorer, notepad, DB cleanup, updater) wired through `tool.ToolManager`.

### Ratings and prediction

Two distinct engines, easily confused:

- `core.rating` predicts *lineup ratings* from player skills. `RatingPredictionConfig` loads
  `prediction/<type>/*.conf` at runtime, so formula tweaks usually mean editing files under
  `prediction/`, not Java. `prediction/Akasolace/AkasolaceRatingPredictionModel.groovy` is an
  alternative model in Groovy.
- `core.prediction.engine` is a Monte-Carlo *match outcome* simulator built on those ratings
  (`MatchPredictionManager`, `ActionGenerator`, `CounterAttackGenerator`).

The `*RatingCache` classes exist because rating calculation is hot and re-runs on every lineup
edit. Keep it off paths that run per table cell or per repaint.

## Swing UI

HO is a long-lived desktop app with a large Swing UI. Most bugs users notice are in the UI, and
there is no automated UI test harness, so be careful here.

- **Threading.** Touch Swing components only on the Event Dispatch Thread. Long work (CHPP
  downloads, DB scans, prediction runs) must not block the EDT, or the whole window freezes; run it
  in a `SwingWorker` or a background thread and push results back with
  `SwingUtilities.invokeLater`. Do not call `invokeAndWait` from the EDT.
- **Refresh.** Panels that show model data implement `core.gui.IRefreshable` / `Refreshable` and
  register with `RefreshManager.instance()`. After a download or a data change, call
  `RefreshManager.instance().doRefresh()` (or `doReInit()` after a model reload) instead of
  refreshing individual panels by hand. Unregister panels you dispose of, or they leak and keep
  refreshing.
- **Main window.** `HOMainFrame.instance()` is the dialog parent and owns the tabs and the status
  line. Use it as the owner for new dialogs so they centre over HO and stay modal to it.
- **Themes.** HO ships several look and feels (darklaf-based light/dark, Nimbus, …) under
  `core.gui.theme`. Never hard-code colours, fonts or icon paths: use
  `ThemeManager.getColor(HOColorName.X)`, `HOIconName` with `ImageUtilities`/`ThemeManager`, and
  `FontUtil`. A new colour gets an `HOColorName` entry and a value in every theme. Check a UI
  change in at least one light and one dark theme.
- **Layouts.** Prefer layout managers (`GridBagLayout`, `BorderLayout`, …) over absolute
  positioning and fixed pixel sizes; users run HO with different fonts, font sizes and languages,
  and translated labels are often much longer than the English ones.
- **Tables.** Most tables extend `core.gui.comp.table.HOTableModel` with `UserColumn`
  definitions; column order, width and visibility are persisted per user in `UserColumnsTable`.
  Adding or reordering columns must keep existing user settings working: give new columns new ids,
  never reuse an old id.
- **Kotlin UI code.** Some newer components (`FixedColumnsTable`, `PlayersTable`,
  `HOTableModel`) are Kotlin. Match the language of the code you are changing.
- **Verify by running.** `./gradlew run` and click through the change. Mention in the PR what you
  checked by hand, since tests do not cover the UI.

## Localization

All user-visible strings go through `TranslationFacility.tr(key)`; never hard-code English
text in the UI. Translations live in `src/main/resources/language/*.properties` and are **owned by
POEditor**: `poeditorPull` overwrites them on every build with an API key. Add new keys to the
English file (`English.properties`) only; do not edit other languages by hand.
`createLanguageFileList` regenerates `language/ListLanguages.txt`, which HO reads to find the
available languages.

## Conventions

- Java, Kotlin and Groovy share `src/main/java`. `sourceSets` maps that directory as both a source
  and a resource root, so non-code files next to classes are packaged.
- Lombok comes from the `io.freefair.lombok` plugin; annotations work without a dependency line.
- Much of the domain vocabulary is legacy German: `HOVerwaltung` (administration), `LigaTable`
  (league), `FaktorenTable` (factors), `XMLSpielplanParser` (fixture list). Keep existing names
  when touching that code; renaming ripples into DB column names and persisted config keys.
- Logging goes through `HOLogger.instance()`, not `System.out`/`printStackTrace`. Uncaught
  exceptions land in `core.util.ExceptionHandler`.
- Tests are JUnit 5 with AssertJ and Mockito under `src/test/java`. They cover pure logic (parsers,
  ratings, training, utilities); there is no harness for the Swing UI or live CHPP calls. Put new
  logic where it can be tested without a window, and add a test for it. Tests run headless, so
  test code must not build `HOMainFrame` or show dialogs.
- Keep changes focused: no drive-by reformatting of untouched code, and no new compiler or
  inspection warnings. `.editorconfig` defines the formatting.
- Do not add dependencies lightly; every jar ships with the installers for all platforms.
