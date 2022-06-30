

## Highlights
* refactoring player details panel showing players' avatars now (#1349)
* refactoring of lineup panel (#1267)
* enable more than three teams (#1415)

## [Detailed Changelog](https://github.com/akasolace/HO/issues?q=milestone%3A6.0)

### Database
* reducing data base file's disk space accelerates HO startup duration (#958)
* fix bug in hrf file import (#1445)

### Squad
* refactoring player details panel showing players' avatars now (#1349)
* display jersey numbers greater than 49 (#1410)
* download and display players' category, statement and owner notes (#1441)
* shirt number column settings reset to editable (#1454)
* hrf file list shows training week instead of match day (#1452)
* skill recalculation no longer ignores very first downloaded hrf file (#1489)
* fix error of skill recalculation of replaced players (#1489)
* fix another recalculation error concerning wrong skill up handling (#1519)

### Team Analyser
* display confidence and team spirit of nt team opponents (#1305)

### Rating
* Initial calculation of the ratings takes into account the trainer type (#1281)
* calculate effect of man marking on ratings (#682)

### Matches

### Lineup
* refactoring, including loading and storing of lineup templates (#1267)
* fixed a bug when downloading team logos (#1300)
* substitutions of the same minute can be ordered (#1039)
* fixed goalkeeper could not be selected as team captain (#1340)
* fix null pointer exception in lineup assistant if no current lineup exists (#1496)

### NT
* fix download xml parse errors (#1305)
* tracking confidence and team spirit of nt teams (#1305)

### Training
* fix NPE in training tab in America-based timezones (#1296)
* enable cell editing in future training table (#1396)
* fix concurrent modification exception on training refresh (#1430)
* reimport of hrf files sets training information (#1469)

### League
* fix download of power rating (not only one team per match) (#1293)
* download all matches of latest league match day (#1373)
* fix download of old league data (#1413)
* initial width of league table pane adjusted (#1451)
  
### Youth
* fix bug in potential calculation due to wrong isTop3 skill tagging (#1278)
* fix escaping of youth team names (#1295) 
* fix set pieces is not limited by top3 skill rules (#1309)
* fix settings loss of the first table columns (#1402)
* show youth player's training development as line charts (#1418)

### Misc
* fix finance bug concerning missing spectators' income in misc module (#1282)
* fix last weeks profit/loss, temporary income sum and missing sponsors bonus (#1301)
* fix CHPP token not being saved when exiting HO on macos (#1291)
* fix portable HO update error when java version is changed (#1545)

## QA Team
* masterpatje does a great job of testing the development versions of the HO to get rid of even the last bugs in it.

## Translations

Reports by Contributors - Dec 20, 2021 - June 29, 2022

* Foppe	690
* brokenelevator 510
* Pablo 313
* Ante 218
* Adrian 107
* Fresty di Lot 101
* sich	96
* akasolace	96
* Frank 94
* Lidegand 	93
* \_KOHb\_	76
* TeamBMW 75
* anti_anti 69
* Moorhuhninho	67
* wsbrenk 30
* dzsoo 18
* Sophia 17
* Csaba 14
* Michał 6
* Motavali 2
* Philipp 1
* Total translated:	2693
