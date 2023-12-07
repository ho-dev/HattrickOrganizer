

## Highlights
* Refactoring rating prediction (implement Schum rating)
* Refactoring of the transfer module. Calculation of transfer fee income.
* Refactoring of the database prepared statement caching.
* Improve training planning 

## [Detailed Changelog](https://github.com/ho-dev/HattrickOrganizer/issues?q=milestone%3A8.0)

### Database
* Fix `unexpected token` issue with Transfer table query (#1965)

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

### Statistics

### Transfer
* Eefactoring of the transfer module. Calculation of transfer fee income. (#245)
* Fix transfer scout's copy and paste of own players (#1897)

### Training
* Fix scrolling of training table (#1936)
* Improve training planning. Each skill training can be prioritized (#1886)

### League

### Youth
* Increase effect of youth friendly match training (#1950)

### Misc
* Add a Linux-friendly default theme, called “Gnome.”
* Add Kotlin support to codebase
* Reset progress bar when action is finished (#1955)

## Translations

Reports by Contributors - September 24, 2023 - November 20, 2023

* Georgi 10
* Lidegand 3
* sich 3
* wsbrenk 3

Total 19