package core.training;

import core.model.HOVerwaltung;
import core.model.player.Spieler;
import core.util.HOLogger;
import core.util.HelperWrapper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/**
 * Holds and calculates how much skill training a player received
 *
 * @author
 */
public class TrainingPerPlayer  {
    private Spieler _Player;
    private TrainingPoints _TrainingPair = null;
    private Date _TrainingDate = null;
    private TrainingPerWeek _TrainingWeek;
    
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TrainingPerPlayer object.
     */
    public TrainingPerPlayer() {
    }

    /**
     * Creates a new TrainingPerPlayer object initialized with a specific player
     */
    public TrainingPerPlayer(Spieler oPlayer) {
    	_Player = oPlayer; 
    }

    //~ Methods ------------------------------------------------------------------------------------

    
    /**
     * Setter for property spieler.
     *
     * @param spieler New value of property spieler.
     */
    public final void setPlayer(Spieler player) {
        this._Player = player;
    }

    /**
     * Getter for property spieler.
     *
     * @return Value of property spieler.
     */
    public final Spieler getPlayer() {
        return this._Player;
    }

    public void setTrainingPair(TrainingPoints trp) {
		_TrainingPair = trp;
	}

    public TrainingPoints getTrainingPair() {
    	return _TrainingPair;
    }
       /**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return this._TrainingDate;
	}

	/**
	 * Set the timestamp
	 * if not null, calculate sub increase for this training date only
	 * 
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this._TrainingDate = timestamp;
	}

    /**
     * add sub values of another ITrainingPerPlayer instance to this instance
     * @param values	the instance we take the values from
     */
    public void addValues(TrainingPerPlayer values) {
    	if (_TrainingPair == null) {
    		if (values != null && values.getTrainingPair() != null) {
    			_TrainingPair = values.getTrainingPair();
    		}
    		else
    		{
	    		HOLogger.instance().error(getClass(), "_TrainingPair is null. Aborting addValues.");
	    		return;
    		}
    	} else { 
	    	_TrainingPair.addPrimary(values.getTrainingPair().getPrimary());
	    	_TrainingPair.addSecondary(values.getTrainingPair().getSecondary());
    	}
    }
	/**
	 * Checks if trainingDate is after the last skill up in skillType
	 * 
	 * @param trainingDate
	 * @param skillType
	 * @return
	 */
	private boolean isAfterSkillup (Calendar trainingDate, int skillType) {
		if (getTimestamp() == null) {
			if (TrainingManager.TRAININGDEBUG) {
				HOLogger.instance().debug(getClass(), 
						"isAfterSkillup: traindate NULL (" + skillType + ") is always after skillup");
			}
			return true;			
		}
		Date skillupTime = getLastSkillupDate(skillType, getTimestamp());
		if (trainingDate.getTimeInMillis() > skillupTime.getTime()) {
			if (TrainingManager.TRAININGDEBUG) {
				HOLogger.instance().debug(getClass(), 
						"isAfterSkillup: traindate "+trainingDate.getTime().toString() 
						+ " (" + skillType + ") is after skillup");
			}
			return true;	
		} else {
			if (TrainingManager.TRAININGDEBUG) {
				HOLogger.instance().debug(getClass(), 
						"isAfterSkillup: traindate "+trainingDate.getTime().toString() 
						+ " (" + skillType + ") is NOT after skillup");
			}
			return false;
		}
	}
	
   /**
     * Calculates the last skillup for the player in the correct training
     *
     * @param trainskill Skill we are looking for a skillup
     * @param trainTime Trainingtime
     *
     * @return Last skillup Date, or Date(0) if no skillup was found
     */
    private Date getLastSkillupDate(int trainskill, Date trainTime) {
        //get relevant skillups for calculation period
        final Vector<Object[]> skillUps = getPlayer().getAllLevelUp(trainskill);
        Date skilluptime = new Date(0);
        for (Iterator<Object[]> it = skillUps.iterator(); it.hasNext();) {
            final Object[] aobj = it.next();
            final Boolean bLevel = (Boolean) aobj[1];

            if (bLevel.booleanValue() == true) {
                final Date tmpTime = new Date(((Timestamp) aobj[0]).getTime());
                if ((tmpTime.before(trainTime)) && (tmpTime.after(skilluptime))) {
                    skilluptime = HelperWrapper.instance().getLastTrainingDate(tmpTime, 
                    		HOVerwaltung.instance().getModel().getXtraDaten().getTrainingDate()).getTime();
                }
            }
        }
        return skilluptime;
    }

    /**
     * get the training point for this instance
     * @return	training point
     */
	public TrainingPerWeek getTrainingWeek() {
		return _TrainingWeek;
	}

	/**
	 * set the training point for this instance and
	 * calculate the sub skills for the player using 
	 * the training week from this training point
	 *  
	 * @param trainingPoint	training point
	 */
	public void setTrainingWeek(TrainingPerWeek trainingWeek) {
		this._TrainingWeek = trainingWeek;
	}
    
}
