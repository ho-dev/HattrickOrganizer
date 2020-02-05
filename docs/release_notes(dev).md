---
title: HO release notes
layout: page
---

Changelist HO! 2.1
====================

## Some numbers: 
  - 350 commits
  - 903 files changed (51,839 additions and 34,085 deletions)
  - 8 contributors


## Documentation
 - Documentation moved to [Read the docs](https://ho.readthedocs.io/)


## GUI
 - New icons across the app: specialty, match type, match events, ...

## Download
- Warning when "not possible to fetch data at the moment because this team is currently playing a game" #291


## Player details
  - Best positions are highlighted #213
    - In the preference (bottom of tab “Misc”) you can set the tolerance level to highlight the player's alternative positions
  - Harmonization of absolute vs relative player contribution/performance #282
      - In squad and Lineup, are calculated both the absolute and the relative contribution of a player to the ratings
        - The absolute is more or less what would be the sum of his contribution to the 7 sectors
        - The relative contribution helps to determine the best position for a given player. It is the absolute contribution corrected by a factor. This allows accounting for that some positions contributes more to the total ratings than others 
  - In various tab, allow sorting by position and ratings #293
  - Fix decimal negative difference #295


## Lineup
  - Fix set-pieces taker slot when reloading saved match lineup  #316
  - Fix “exclude last Lineup” when doing manual lineup #328


## Rating
  - Fix a bug that was causing faulty ratings  #292
  - Fix Hatstats and Loddarstats in Teamanalyser #321


## Matches
  - Division battle match icon + game specifics #284 #310 #313
  - Fix "show my cup games" #320 #323 #326
  - Match Highlights: complete rewrite - full support of all match events  #344
  - Match modules, cleaning of statistics tabs #355


## NT
  - Extended style of play options #191


## Training
  - Training week preview in Training Tabs #234
  - Stamina training preview  #125 #311
  - Fix training for secondary cups #306
  - Track experience acquisition #199
  - Fix training forecast displaying wrong skill up #368


## Misc
  - Out of bond exception for Hall of Fame players #350
  - Summary line in player overview presenting tram value (avg TSI, total TSI, avg form, ….) #373
  - Team analyzer: Fix HatStats and LoddarStats always displaying zero #324
  - Fix MatchDayPanel download button not available while the match is over #330 
  - Fix MatchDayPanel null pointer exception #337
  - Fix a bug when downloading data while having a generation match planned #365


## Translations
  - HO! is currently available in 36 languages thanks to the work of 45 translators. The translation status varies a lot from one language to another. If you can help in a language requiring attention please join in the effort and register on [POeditor](https://poeditor.com/join/project/jCaWGL1JCl):
  - Translators contribution since the previous release (number of translated terms):
| username       | nb translated terms |
|----------------|:-------------------:|
| sich           |         524         |
| Moorhuhninho   |         332         |
| Boy            |         193         |
| Saleh          |         185         |
| Motavali       |         181         |
| Sergejs        |         114         |
| TeamBMW        |          81         |
| Juan           |          67         |
| Lidegand       |          56         |
| Globe96        |          46         |
| Zigmas         |          45         |
| dzsoo          |          44         |
| murko          |          38         |
| Baler0         |          28         |
| taimikko       |          21         |
| Ricardo        |          18         |
| DavidatorusF   |          17         |
| brokenelevator |          3          |
| Massimo        |          3          |