package core.training;

import core.constants.TrainingType;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import module.transfer.test.HTWeek;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Class that holds all information required to calculate training effect of a given week
 * (e.g. training intensity, stamina part, assistant level, played games ...)
 */
public class TrainingPerWeek  {

    private static DateTimeFormatter cl_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC));

    private int o_TrainingIntensity;
    private int o_StaminaShare;
    private int o_TrainingType;
    private int o_TrainingAssistantsLevel;
    private MatchKurzInfo[] o_Matches;
    private MatchKurzInfo[] o_NTmatches;
    private Instant o_TrainingDate;
    private String firstMatchDate, lastMatchDate;
    private final static int myClubID = HOVerwaltung.instance().getModel().getBasics().getTeamId();


    @Deprecated
    private int _HRFID;

    @Deprecated
    private int _Week;

    @Deprecated
    private int _Year;

    @Deprecated
    private int _PreviousHRFID;

    @Deprecated
    private Timestamp nextTrainingDate;

    @Deprecated
    private Timestamp trainingDate;

    @Deprecated
    private HattrickDate hattrickDate;


    public TrainingPerWeek(Instant trainingDate, int trainingType, int trainingIntensity, int staminaShare, int trainingAssistantsLevel) {
        o_TrainingDate = trainingDate;
        o_TrainingType = trainingType;
        o_TrainingIntensity = trainingIntensity;
        o_StaminaShare = staminaShare;
        o_TrainingAssistantsLevel = trainingAssistantsLevel;
        var startDate = o_TrainingDate.minus(7, ChronoUnit.DAYS);
        firstMatchDate = cl_Formatter.format(startDate);
        lastMatchDate = cl_Formatter.format(o_TrainingDate.plus(23, ChronoUnit.HOURS));
        o_Matches = fetchMatches();
        o_NTmatches = fetchNTMatches();
    }



    /**
     * function that fetch info of NT match played related to the TrainingPerWeek instance
     * @return MatchKurzInfo[] related to this TrainingPerWeek instance
     */
    private MatchKurzInfo[] fetchNTMatches() {

        var matchTypes= MatchType.getNTMatchType();

        String sOfficialMatchType = matchTypes.stream().map(m -> m.getId()+"").collect(Collectors.joining(","));

        final String where = String.format("WHERE MATCHDATE BETWEEN '%s' AND '%s' AND MATCHTYP in (%s) AND STATUS=%s ORDER BY MatchDate DESC",
                firstMatchDate, lastMatchDate, sOfficialMatchType, MatchKurzInfo.FINISHED);

        return DBManager.instance().getMatchesKurzInfo(where);
    }


    /**
     * function that fetch info of match played related to the TrainingPerWeek instance
     * @return MatchKurzInfo[] related to this TrainingPerWeek instance
     */
    private MatchKurzInfo[] fetchMatches() {


        var matchTypes= MatchType.getOfficialMatchType();

        String sOfficialMatchType = matchTypes.stream().map(m -> m.getId()+"").collect(Collectors.joining(","));

        final String where = String.format("WHERE (HEIMID = %s OR GASTID = %s) AND MATCHDATE BETWEEN '%s' AND '%s' AND MATCHTYP in (%s) AND STATUS=%s ORDER BY MatchDate DESC",
                myClubID, myClubID, firstMatchDate, lastMatchDate, sOfficialMatchType, MatchKurzInfo.FINISHED);

        return DBManager.instance().getMatchesKurzInfo(where);
    }

    public MatchKurzInfo[] getMatches() {
        return o_Matches;
    }

    public MatchKurzInfo[] getNTmatches() {
        return o_NTmatches;
    }



    @Deprecated
    public TrainingPerWeek() {}

    @Deprecated
    public TrainingPerWeek(int week, int year, int trType, int intensity, int stamina) {
        this._Week = week;
        this._Year = year;
        this.o_TrainingType = trType;
        this.o_TrainingIntensity = intensity;
        this.o_StaminaShare = stamina;
    }

    @Deprecated
    public final void setHrfId(int i) {
        _HRFID = i;
    }

    @Deprecated
    public final int getHrfId() {
        return _HRFID;
    }

    @Deprecated
    public final void setStaminaPart(int stamina) {
        this.o_StaminaShare = stamina;
    }

    @Deprecated
    public final int getStaminaPart() {
        return this.o_StaminaShare;
    }

    @Deprecated
    public final void setTrainingIntensity(int intensity) {
        this.o_TrainingIntensity = intensity;
    }

    @Deprecated
    public final int getTrainingIntensity() {
        return this.o_TrainingIntensity;
    }

    @Deprecated
    public final void setTrainingType(int trType) {
        this.o_TrainingType = trType;
    }

    @Deprecated
    public final int getTrainingType() {
        return this.o_TrainingType;
    }

    @Deprecated
    public final int getWeek() {
        return this._Week;
    }

    @Deprecated
    public final int getYear() {
        return this._Year;
    }

    @Deprecated
    public int getPreviousHrfId() {
		return _PreviousHRFID;
	}

    @Deprecated
	public void setPreviousHrfId(int i) {
		_PreviousHRFID = i;
	}

	/**
	 * Returns the timestamp with the training at the start of this trainingweek.
	 *
	 * @return	training date
	 */
    @Deprecated
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

    @Deprecated
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
    @Deprecated
	public void setTrainingDate(Timestamp date) {
		trainingDate = date;
	}
	
	/**
	 * Returns a timestamp with the  of the training at the end of this training week.
	 
	 * @return The timestamp with the next training date.
	 */
    @Deprecated
	public Timestamp getNextTrainingDate() {
		return nextTrainingDate;
	}
	
	/**
	 * Sets the time of the next training. 
	 * 
	 * @param t Timestamp containing the time
	 */
    @Deprecated
	public void setNextTrainingDate(Timestamp t) {
		nextTrainingDate = t;
	}
	
	/**
	 * Returns the number of assistants for the week.
	 * 
	 * @return an integer with the number of assistants
	 */
    @Deprecated
	public int getO_TrainingAssistantsLevel() {
		return o_TrainingAssistantsLevel;
	}
	
	/**
	 * Sets the number of assisstants
	 * 
	 * @param o_TrainingAssistantsLevel, an integer with the number of assistants
	 */
    @Deprecated
	public void setO_TrainingAssistantsLevel(int o_TrainingAssistantsLevel) {
		this.o_TrainingAssistantsLevel = o_TrainingAssistantsLevel;
	}



    @Deprecated
    public HattrickDate getHattrickDate() {
        return hattrickDate;
    }

    @Deprecated
    public void setHattrickDate(HattrickDate hattrickDate) {
        this.hattrickDate = hattrickDate;
    }

    @Override
    public final String toString() {
        return "TrainingPerWeek[" +
                "Training date: " + cl_Formatter.format(o_TrainingDate) +
                ", Training Type: " + TrainingType.toString(o_TrainingType)  +
                "%, Intensity: " + o_TrainingIntensity +
                "%, StaminaPart: " + o_StaminaShare +
                "]";
    }


}
