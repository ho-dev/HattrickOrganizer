---
title: HO release notes
layout: page
---

Changelist HO! 4.0

## Some numbers:
  - 277 commits
  - 794 files changed (20'997 additions and 21'491 deletions)
  - 108 issues closed
  - 9 contributors


## Highlights

  - New build process:
    - **HO! now requires Java 14 but artefacts including JRE are made available**
    - **in-app upgrade is now supported**
    - directory structure has changed (db location, log files, ...)
   - [FEAT] Improved GUI, new themes, and implemented dark mode. #85
   - [FEAT] Complete rewrite of Special Events module


## Detailed Changelog


### Squad

  - [FIX] Remove lag in sync between tables in Squad #465
  - [FIX] Provide better default for initial position of player comparison divider.

### Team Analyser

 - [FEAT] Save adjusted lineup ratings of MatchPredictionPanel in an extra row #66

### Rating

  - [FIX] Fixes Long Shots tactic level calculation

### Matches

  - [FIX] Some icons fixes in match report
  - [FEAT] Information about extra time or penalty shoot out in match result #561

### Lineup

  - [FEAT] Provide better defaults for initial position of dividers.
  - [FEAT] Edit man marking match order [#660]
  - [FIX] Fix bug in download of man marking match orders [#632]
  - [FIX] Fix bug in upload of position change match orders [#633]

### IFA

  - [FIX] add supports for 9 new leagues: Belize, Madagascar, Botswana, Saint Vincent and the Grenadines, Myanmar, Zambia, San Marino, Puerto Rico and Haiti [#539]
  
  
### Training

  - [FIX] Fix bug of season correction calculation. Training effect table shows wrong seasons in week 16 [#539]
  - [FIX] Subskill recalc takes into account training that took place before the first hrf download [#512]
  - [FIX] Fix bug of training effect of Walkover matches [#623]
  - [FEAT] Individual training plans for each player in training preview [#587]

### Misc

  - [FEAT] Remove jcalendar dependency.
  - [FIX] ExperienceViewer removed [#503]
  - [FIX] Avoid potential infinite loop at startup. [#584]
  - [FIX] Layout issue in multiple screen setup. [#618]
  - [FEAT] Remove all printing functionality.
  - [FEAT] Remove player state colour, and display icons instead in Lineup.
  - [FEAT] Refactor progress bar display [#722]

### League

  - [FEAT] Make Promotion status more visible. [#521]

## Translations
  - HO! is currently available in 35 languages thanks to the work of 78 translators. The translation status varies a lot from one language to another. If you can help in a language requiring attention please join in the effort and register on [POeditor](https://poeditor.com/join/project/jCaWGL1JCl):
  - Translators contribution since the previous release (number of translated terms):


| Contributor       | Translations | Percentage |
|-------------------|--------------|------------|
| Kimmo             | 843          | 48.03      |
| Andreas           | 745          | 42.45      |
| \_KOHb\_            | 436          | 24.84      |
| Moorhuhninho      | 409          | 23.3       |
| TeamBMW           | 349          | 19.89      |
| Csaba             | 230          | 13.11      |
| André             | 207          | 11.79      |
| h3t3r0            | 139          | 7.92       |
| asteins           | 79           | 4.5        |
| Hakkarainen       | 62           | 3.53       |
| DavidatorusF      | 61           | 3.48       |
| Manny             | 60           | 3.42       |
| Baler0            | 54           | 3.08       |
| Stef              | 46           | 2.62       |
| dzsoo             | 44           | 2.51       |
| Bartosz           | 41           | 2.34       |
| murko             | 39           | 2.22       |
| Volker            | 38           | 2.17       |
| sich              | 38           | 2.17       |
| Sebas90           | 32           | 1.82       |
| akasolace         | 31           | 1.77       |
| Lidegand          | 29           | 1.65       |
| Saleh             | 14           | 0.8        |
| LeSchmuh          | 11           | 0.63       |
| Ricardo           | 6            | 0.34       |
| Motavali          | 5            | 0.28       |
| karelant.         | 4            | 0.23       |
| Philipp           | 2            | 0.11       |
| Sumame.           | 2            | 0.11       |
| RaV               | 1            | 0.06       |
| Total translated: | 4057         | 6.42       |
