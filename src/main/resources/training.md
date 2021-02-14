# Senior training skill calculation

## Download updates TRAINING table
Each download checks whether a new TRAINING table entry has to be created. Automatic updates shouldn't be necessary.
They are only possible through interactive training editor.

## After each Download HoModel.CalcSubskills is called  

* determine the list of training weeks (training type, trainer skill, assistant level, stamina share) since previous download
* determine list of match lineups and match details of each training week (happens implicitly by lazy loading in training week objects)
* For each current player 
  * determine status of previous download
  * for all skills 
    * init skill value to value of previous download
    * For each training week
      * call player.calcSubskillIncrement(previous player status, training, match infos)
        * if skill is trained in training week  
          * aggregate players training minutes per week (bonus, full, partly, osmosis)
          * increment skill value training effect (training infos, training minutes)
* store status of current players 

## Interactiv Training editing, Recalc Subskills
User can edit training week items. Starting RecalcSubskills will calculate all stored Download since oldest changed
training item.
* for each Download call HoModel.CalcSubskills

Further options are available to recalculate 7 weeks or all downloads.


# Youth training skill calculation

Youthplayer's training development is realized as Hashmap in the class Youthplayer. It contains one
entry for each training match the player has participated mapping training match date to one development entry.

The method getTrainingDevelopment is used to initialize this:

* call getStartskills to determine the starting values. The starting values are part of the YouthSkillInfo class. So the 
initial values can easily be created by copying the start values of current skills to the current values of the starting instance.
  
* call HOModel.getYouthTrainingsAfter to get a list of youth' matches after the player's arrival date. Youth's matches are
cached in the HOModel instance.
  
* for each match/training get the matchlineup of youth team

  * if the player has played
    
    * create a new training development entry
    * set start skill to calculated values by entry.calcSkills
      * arguments are start skills, skill constraints, match lineup
      * for each skill type call training.calcSkill
        * arguments are skill, player, match lineup
        * create new SkillInfo instance, copying level, max, maxReached from input
        * if maximum is reached copy current value too
        * else calc current value the value plus training.calcSkillIncrement
          * arguments are value, player, match lineup
          * determine match type factor (friendly/league have different training effects)
          * for each training priority (primary, secondary)
            * determine training priority factor (1 for primary, 0.5 for secondary)
            * determine trained sectors calling YouthTrainingType.getTrainedSectors (Bonus, Full, Partly, Osmosis sectors)
            * for each sector type 
              * determine minutes in sector type calling MatchLineupTeam.getTrainingMinutesPlayedInSectors
                * arguments are player id, accepted sectors, is walkover matchwin
                * determine player match appearances calling initMinutesOfPlayersInSectors
                  * if not done already
                    * init a map of squad position to appearances of players (player, minute(=0) when player enters position). 
                    * for each substitution call examineSubstitution (not for man marking)
                      * NEW_BEHAVIOUR 
                      * SWAP_POSITION
                      * SUBSTITUTION terminate old position calling removeMatchAppearance, init new position in appearances
                      * removeMatchAppearance removes leaving players entry from appearance mapping and adds minutes in sector calling youthplayer.addMinutesInSector new player is entered in appearance mapping with minutes of substitution 
                    * at match end minute add minutes of remaining players
                * if no sectors are accepted return 0 minutes (training type has no partly or osmosis training sectors)
                * if player is not in lineup return 0 minutes
                * if walkovermatchWin 0 minutes experience (acceptedSectors=null), 90 minutes if player is in accepted sector
                * return player.getTrainingMinutesInAcceptedSectors
                    * arguments accepted sectors
              * limit overall minutes to 90 minutes
              * if remaining minutes in sector type (bonus, full,...) call YouthTrainingType.calcSkillIncrementPerMinute
                * arguments are skill id, current value, sector type, age of player
                * call WeeklyTrainingType.getBonusYouthTrainingPerMinute, Full, Partly, Osmosis
                * add match type factor * training factor * remaining minutes * training per minute to skill increment
              * next sector if more minutes are remaining
            * limit skill increment to 1.0
          * set new value to value plus skill increment
          * if new value needs adjustments (conflict with hattrick's value information), adjust start values and propagate changes through existing training development

        * set skill constraints (calculated values needs to be in the range of given hattrick skill levels)
      * add training development entry 
    * set the currents skills to the calculated skills and store player's new status in database
      

