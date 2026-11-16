# AGENTS.md

HattrickOrganizer (HO) is a Java Swing desktop app for the online football manager Hattrick.
It downloads team data via the Hattrick CHPP API and stores it in an embedded HSQLDB database.

For the project overview, contribution workflow and developer tips, read
[`docs/README.md`](docs/README.md), [`docs/contributing.md`](docs/contributing.md) and
[`docs/FOR_DEVELOPERS.md`](docs/FOR_DEVELOPERS.md). This file only adds what coding agents need on top.

## Commands

```bash
./gradlew run                                   # start HO
./gradlew test                                  # all tests (what CI runs on pull requests)
./gradlew test --tests "core.util.MathUtilsTest" # one test class
./gradlew createVersion createDistribution      # runnable jar in build/install/
```

- Java 17 (source and target). Do not raise the language level.
- Every compile does a clean build and pulls translations from POEditor. Locally this prints
  `Invalid API Token`; that is expected, not an error.
- A plain `./gradlew jar` can't start on its own. Use `createDistribution`.

## Rules

- **Database:** a schema change needs a migration in `core.db.DBUpdater`. Migrations must be
  safe to run twice, because DEV builds re-run the newest one on every start. Use the
  `AbstractTable` helpers such as `tryAddColumn`.
- **Translations:** all UI text goes through `TranslationFacility.tr(key)`. Add new keys to
  `src/main/resources/language/English.properties` only; POEditor owns the other languages and
  overwrites them.
- **Swing:** touch components only on the Event Dispatch Thread, and keep long work (downloads,
  DB scans) off it. Update panels through `RefreshManager`.
- **Themes:** HO has light and dark themes. Never hard-code colours, fonts or icons; use
  `ThemeManager` with `HOColorName` / `HOIconName`.
- **Table columns** are saved per user. Give new columns new ids; never reuse an old one.
- **Legacy German names** (`HOVerwaltung`, `Spieler…`, `Liga…`) stay as they are. Renaming
  them breaks DB columns and saved settings.
- Log with `HOLogger.instance()`, not `System.out`.
- Follow the existing style: singletons via `X.instance()`, no dependency injection. Java and
  Kotlin share `src/main/java`; write in the language of the file you change.

## Java style

- The limit is Java 17. Java 21 features don't compile here: no pattern matching in `switch`, no
  record patterns, no `getFirst()`/`getLast()` on lists.
- In new code, use what Java 17 offers: `var` when the type is obvious from the right-hand side,
  `switch` expressions with `->`, pattern matching `instanceof`, records for simple data classes,
  and text blocks for multi-line strings.
- Use `ArrayList`/`HashMap` rather than `Vector`/`Hashtable`, and `HODateTime` rather than
  `java.util.Date`.
- Only modernise the code you are changing anyway. Don't rewrite unrelated code in the same pull
  request.

## Testing

Tests (JUnit 5, AssertJ, Mockito) cover logic only. They run headless, so they must not open
windows. There are no UI tests: check UI changes by hand with `./gradlew run`, in a light and a
dark theme, and say what you checked in the pull request.

## Pull requests

Use `.github/pull_request_template.md`. User-visible changes need a line in
`src/main/resources/release_notes.md`.
