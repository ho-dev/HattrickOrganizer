package core.training;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.match.MatchType;
import core.model.player.Player;
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
    private Player _Player;
    private TrainingPoints _TrainingPair = null;
    private Date _TrainingDate = null;
    private TrainingPerWeek _TrainingWeek;
	private double experienceSub=0;

	//~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TrainingPerPlayer object.
     */
    public TrainingPerPlayer() {
    }

    /**
     * Creates a new TrainingPerPlayer object initialized with a specific player
     */
    public TrainingPerPlayer(Player oPlayer) {
    	_Player = oPlayer; 
    }

    //~ Methods ------------------------------------------------------------------------------------

    
    /**
     * Setter for property spieler.
     *
     * @param spieler New value of property spieler.
     */
    public final void setPlayer(Player player) {
        this._Player = player;
    }

    /**
     * Getter for property spieler.
     *
     * @return Value of property spieler.
     */
    public final Player getPlayer() {
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
                    		HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate()).getTime();
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

    public void addExperienceIncrease(int minutes, MatchType matchType) {
		double p = 0;
		switch (matchType){
			case CUP: p = 2; break;
			case EMERALDCUP:
			case RUBYCUP:
			case SAPPHIRECUP:
			case CONSOLANTECUP: p = .5; break;
			case FRIENDLYNORMAL:
			case FRIENDLYCUPRULES: p = .1; break;
			case INTFRIENDLYCUPRULES:
			case INTFRIENDLYNORMAL: p = .2; break;
			case LEAGUE: p = 1; break;
			case MASTERS: p = 5; break;
			case NATIONALCOMPCUPRULES:
			case NATIONALCOMPNORMAL: p = 10.; break;
			case NATIONALFRIENDLY: p = 2. ; break;
			case QUALIFICATION: p = 2.; break;
			case INTSPIEL:
		}
		this.experienceSub += minutes * p / 90. / 28.571;
    }

	public double getExperienceSub() {
		return experienceSub;
	}

    public float calcSubskillIncrement(int skill, int skillValue) {
		float ret = 0;

		/* Time to perform skill drop */
		if (SkillDrops.instance().isActive()) {
			ret -= SkillDrops.instance().getSkillDrop(skillValue, this._Player.getAlter(), skill) / 100;
		}

		//var trainingLength = wt.getTrainingLength(this, trainerlevel, trForPlayer.getTrainingWeek().getTrainingIntensity(), trForPlayer.getTrainingWeek().getStaminaPart(), trForPlayer.getTrainingWeek().getTrainingAssistantsLevel());

		var wt = WeeklyTrainingType.instance( this._TrainingWeek.getTrainingType());
		if ( skill == wt.getPrimaryTrainingSkill()) {
			ret += wt.getTrainingAlternativeFormula(_Player, skillValue, this, true);

			HOLogger.instance().info(this.getClass(),
					_Player.getLastName()+ "; " + PlayerSkill.toString(skill) +
							"; Age=" + _Player.getAlter() +
							"; Skill=" + skillValue +
							"; Minutes=" + this.getTrainingPair().getTrainingDuration().getFullTrainingMinutes() +
							";" + this.getTrainingPair().getTrainingDuration().getPartlyTrainingMinutes() +
							";" + this.getTrainingPair().getTrainingDuration().getOsmosisTrainingMinutes() +
							"; training=" + ret);
		}
		else if ( skill == wt.getSecondaryTrainingSkill()){
			ret += wt.getTrainingAlternativeFormula(_Player, skillValue, this, false);
			HOLogger.instance().info(this.getClass(),
					_Player.getLastName()+ "; " + PlayerSkill.toString(skill) +
							"; Age=" + _Player.getAlter() +
							"; Skill=" + skillValue +
							"; Minutes=" + this.getTrainingPair().getTrainingDuration().getFullTrainingMinutes() +
							";" + this.getTrainingPair().getTrainingDuration().getPartlyTrainingMinutes() +
							";" + this.getTrainingPair().getTrainingDuration().getOsmosisTrainingMinutes() +
							"; training=" + ret);
		}

		if (ret > 1) ret = 1; // limit 1

		return ret;
	}
}
