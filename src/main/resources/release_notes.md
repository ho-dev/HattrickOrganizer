

## Highlights
* Refactoring rating prediction (implement Schum rating)
* Refactoring of the transfer module. Calculation of transfer fee income.
* Refactoring of the database prepared statement caching.
* Improve training planning
* New color editor in options dialog

## [Detailed Changelog](https://github.com/ho-dev/HattrickOrganizer/issues?q=milestone%3A8.0)

### Database
* Fix `unexpected token` issue with Transfer table query (#1965)
* Fix potential NPE when using a prepared statement (#1941)
* Improve Database Cleanup tool by adding additional match types (#1587)

### Squad
* Fix length of owner notes in players' database table (#1816)
* player avatar image can be reloaded (#1815)
* Fix error player download nickname null pointer exception (#1938)
* Fix initial sorting by player group (#1909)

### Team Analyzer
* Restore size of match prediction dialog box (#1898)

### Rating
* Implement schum rating prediction (#1782)

### Matches
* Fix bug loading matches with no region id (#1975)

### Lineup
* Fix missing player id column in lineup assistant's player table (#1930)

### Special events
* Refactoring special events table layout (#816)

### Transfer
* Refactoring of the transfer module. Calculation of transfer fee income. (#245)
* Fix transfer scout's copy and paste of own players (#1897)
* Calculation of player's total cost of ownership (#741)

### Training
* Fix scrolling of training table (#1936)
* Improve training planning. Each skill training can be prioritized (#1886)
* Change subskill recalculation dialog display (#1556)

### League

### Youth
* Increase effect of youth friendly match training (#1950, #1994)
* Modify youth module layout (#1449)
* Set initial youth module view layout (#1558)
* Display specialty icons in youth player tables (#1999)

### Option setting
* new color editor in option settings (#1242)

### Misc
* Add a Linux-friendly default theme, called “Gnome.”
* Add Kotlin support to codebase
* Reset progress bar when action is finished (#1955)
* Upgrade Gradle to version 8.5
* Fix issue with startup post-installation (#2002)
* Fix NPE when saving preferences on un-managed HO install (#1992)
  HO now downloads the update in the browser when HO install is un-managed.

## Translations

Reports by Contributors - September 24, 2023 - December 07, 2023

* Sebastien 157
* \_KOHb\_ 22
* Georgi 10
* Lidegand 3
* sich 3
* wsbrenk 3

Total 198
