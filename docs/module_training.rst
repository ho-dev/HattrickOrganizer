.. _training_calculation:


Training Calculation
===============================

* **Subskills**

  * Download (F11)
    current HOModel is updated

    * HOModel.calcSubskills is called
    * Find the training weeks since previously stored hrf (homodel) (<==== check if correct weeks are examined)
    * call TrainingManager.instance().calculateTraining
        - training week list
        - current status of players
        - status of players in previous hrf
      * for each player
      * find combi old/current player status (special handling if player is a new one)
      * handle/check skill drops
      * for each training week call player.calcIncrementalSubskills
      * calls (back) TrainingManager.calculateWeeklyTrainingForPlayer   (should be moved to PLayer!?)
        * get the training type class (PM, ...)
        * get the list of matches used for training (<=== look here if correct matches are loaded with correct lineups)
        * for each match increment minutes played in bonus, full, partly, osmosis training sectors only if match is not a MASTERS match
        * add total minutes played, even on Masters match to calculate experience progress
        * this is stored in TrainingPerPlayer/TrainingPoints object which is returned

      * incrementSubskills primary training
      * incrementSubskills secondary training
      * addExperienceSub

  * Recalc Subskills

----

* **Trained this week**

  blabla
