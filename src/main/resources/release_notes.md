---
title: HO release notes
layout: page
---

Changelist HO! 4.0

## Some numbers:
  - 254 commits
  - 765 files changed (20'477 additions and 21'045 deletions)
  - 111 issues closed
  - 9 contributors


## Highlights

  - New build process:
    - **HO! now requires Java 14 but artefacts including JRE are made available**
    - **in-app upgrade is now supported**
    - directory structure has changed (db location, log files, ...)
   - [FEAT] Improved HO themes, and implemented dark mode. #85
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

### Training

  - [FIX] Fix bug of season correction calculation. Training effect table shows wrong seasons in week 16 [#539]
  - [FIX] Subskill recalc takes into account training that took place before the first hrf download [#512]
  - [FIX] Fix bug of training effect of Walkover matches [#623]
  - [FEAT] Individual training plans for each player in training preview [#587]

### IFA

  - [FIX] add supports for 9 new leagues: Belize, Madagascar, Botswana, Saint Vincent and the Grenadines, Myanmar, Zambia, San Marino, Puerto Rico and Haiti [#539]
  
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


| username       | nb translated terms |
|----------------|:-------------------:|
