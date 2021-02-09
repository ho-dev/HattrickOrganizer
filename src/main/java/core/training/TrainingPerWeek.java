package core.training;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.util.HOLogger;
import module.transfer.test.HTWeek;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * New Training Class
 */
public class TrainingPerWeek  {
    //~ Instance fields ----------------------------------------------------------------------------
    private MatchKurzInfo[] matches;

    private int _HRFID;
    private int _Intensity = -1;
    private int _Stamina = -1;
    private int _TrainingType = -1;
    private int _Week = -1;
    private int _Year = -1;
    private int _PreviousHRFID;
    private Timestamp nextTrainingDate = null;
    private Timestamp trainingDate = null;
    private int assistants = -1;
    private HattrickDate hattrickDate;

    //~ Constructors -------------------------------------------------------------------------------
    public TrainingPerWeek() {
    	
    }
    /**
     * Creates a new Training object.
     */
    public TrainingPerWeek(int week, int year, int trType, int intensity, int stamina) {
        this._Week = week;
        this._Year = year;
        this._TrainingType = trType;
        this._Intensity = intensity;
        this._Stamina = stamina;
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final void setHrfId(int i) {
        _HRFID = i;
    }

    public final int getHrfId() {
        return _HRFID;
    }

    public final void setStaminaPart(int stamina) {
        this._Stamina = stamina;
    }

    public final int getStaminaPart() {
        return this._Stamina;
    }

    public final void setTrainingIntensity(int intensity) {
        this._Intensity = intensity;
    }

    public final int getTrainingIntensity() {
        return this._Intensity;
    }

    public final void setTrainingType(int trType) {
        this._TrainingType = trType;
    }

    public final int getTrainingType() {
        return this._TrainingType;
    }

    public final int getWeek() {
        return this._Week;
    }

    public final int getYear() {
        return this._Year;
    }

    /**
     * toString method: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public final String toString() {
        return "TrainingPerWeek[" +
                "intensity = " + _Intensity +
                ", staminaTrainingPart = " + _Stamina +
                ", typ = " + _TrainingType +
                ", week = " + _Week +
                ", year = " + _Year +
                ", hattrickWeek = " + this.hattrickDate.getWeek() +
                ", hattrickSeason = " + this.hattrickDate.getSeason() +
                ", trainDate = " + trainingDate +
                ", hrfId = " + _HRFID +
                "]";
    }
	public int getPreviousHrfId() {
		return _PreviousHRFID;
	}

	public void setPreviousHrfId(int i) {
		_PreviousHRFID = i;
	}

	/**
	 * Returns the timestamp with the training at the start of this trainingweek.
	 *
	 * @return	training date
	 */
	public Timestamp getTrainingDate() {
	    if ( trainingDate == null){
            HTWeek week = new HTWeek();
            week.setSeason(this.hattrickDate.getSeason());
            week.setWeek(this.hattrickDate.getWeek());
            Date d = week.toDate();
            long trainingOffset = getTrainingOffset();
            trainingDate = new Timestamp(new Date(d.getTime()+trainingOffset).getTime());
        }
        return trainingDate;
	}

	private static long trainingOffset=-1;
    private static  long getTrainingOffset() {
        if ( trainingOffset == -1) {
            Date trainingDate = HOVerwaltung.instance().getModel().getXtraDaten().getTrainingDate();
            HTWeek thisWeek = new HTWeek();
            thisWeek.setSeason(HOVerwaltung.instance().getModel().getBasics().getSeason());
            thisWeek.setWeek(HOVerwaltung.instance().getModel().getBasics().getSpieltag());
            trainingOffset = trainingDate.getTime() - thisWeek.toDate().getTime();
        }
        return trainingOffset;
    }


    /**
	 *  Sets the date of the training at the start of this training week.
	 *	
	 * @param date with the training date.
	 */
	public void setTrainingDate(Timestamp date) {
		trainingDate = date;
	}
	
	/**
	 * Returns a timestamp with the  of the training at the end of this training week.
	 
	 * @return The timestamp with the next training date.
	 */
	public Timestamp getNextTrainingDate() {
		return nextTrainingDate;
	}
	
	/**
	 * Sets the time of the next training. 
	 * 
	 * @param t Timestamp containing the time
	 */
	public void setNextTrainingDate(Timestamp t) {
		nextTrainingDate = t;
	}
	
	/**
	 * Returns the number of assistants for the week.
	 * 
	 * @return an integer with the number of assistants
	 */
	public int getAssistants() {
		return assistants;
	}
	
	/**
	 * Sets the number of assisstants
	 * 
	 * @param assistants, an integer with the number of assistants
	 */
	public void setAssistants(int assistants) {
		this.assistants = assistants;
	}

    public MatchKurzInfo[] getMatches() {
        if (matches == null) {
            matches = getMatches(MatchKurzInfo.user_team_id);
        }
        return matches;
    }

    public MatchKurzInfo[] getMatches(int teamId) {

        final Calendar old = Calendar.getInstance();
        old.setTimeInMillis(this.trainingDate.getTime());
        // set time one week back
        old.add(Calendar.WEEK_OF_YEAR, -1);

        final Timestamp ots = new Timestamp(old.getTimeInMillis());
        final String where = "WHERE ( HEIMID=" + teamId
                + " OR GASTID=" + teamId + " )"
                + " AND MatchDate BETWEEN '" + ots.toString() + "' AND '" + this.trainingDate.toString() + "' "
                + " AND (MatchTyp>" + MatchType.NONE.getId()
                + " AND MatchTyp<" + MatchType.TOURNAMENTGROUP.getId()
                + " OR MatchTyp>=" + MatchType.EMERALDCUP.getId()
                + " AND MatchTyp<=" + MatchType.CONSOLANTECUP.getId()
                + ") AND STATUS=" + MatchKurzInfo.FINISHED
                + " ORDER BY MatchDate DESC";

        HOLogger.instance().info(this.getClass(), where);
        return DBManager.instance().getMatchesKurzInfo(where);
    }

    public HattrickDate getHattrickDate() {
        return hattrickDate;
    }

    public void setHattrickDate(HattrickDate hattrickDate) {
        this.hattrickDate = hattrickDate;
    }
}
