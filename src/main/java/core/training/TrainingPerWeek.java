package core.training;

import core.constants.TrainingType;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.enums.DBDataSource;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.util.DateTimeUtils;
import core.util.Helper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * Class that holds all information required to calculate training effect of a given week
 * (e.g. training intensity, stamina part, assistant level, played games ...)
 */
public class TrainingPerWeek  {

    private final static int myClubID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
    private static DBManager cl_DBManager;

    private int o_TrainingIntensity;
    private int o_StaminaShare;
    private int o_TrainingType;
    private int o_CoachLevel;
    private int o_TrainingAssistantsLevel;
    private Instant o_TrainingDate;
    private Boolean o_IncludeMatches;
    private MatchKurzInfo[] o_Matches;
    private MatchKurzInfo[] o_NTmatches;
    private DBDataSource o_Source;


    /**
     *
     * Constructor, matches are not passsed as parameters but are loaded at object creation
     */
    public TrainingPerWeek(Instant trainingDate, int trainingType, int trainingIntensity, int staminaShare, int trainingAssistantsLevel, int coachLevel, boolean includeMatches, DBDataSource source, DBManager _DBManager) {
        if( cl_DBManager == null) {
            if (_DBManager == null) {
                cl_DBManager = DBManager.instance();
            }
            else {
                cl_DBManager = _DBManager;
            }
        }
        o_TrainingDate = trainingDate;
        o_TrainingType = trainingType;
        o_TrainingIntensity = trainingIntensity;
        o_StaminaShare = staminaShare;
        o_CoachLevel = coachLevel;
        o_TrainingAssistantsLevel = trainingAssistantsLevel;
        o_IncludeMatches = includeMatches;
        o_Source = source;

        // Loading matches played the week preceding the training date --------------------------
        var _startDate = o_TrainingDate.minus(7, ChronoUnit.DAYS);
        String _firstMatchDate = DateTimeUtils.InstantToSQLtimeStamp(_startDate);
        String _lastMatchDate = DateTimeUtils.InstantToSQLtimeStamp(o_TrainingDate.plus(23, ChronoUnit.HOURS));
        o_Matches = fetchMatches(_firstMatchDate, _lastMatchDate);
        o_NTmatches = fetchNTMatches(_firstMatchDate, _lastMatchDate);
    }

    /**
     *
     * Constructor, matches are not passsed as parameters but are loaded at object creation
     */
    public TrainingPerWeek(Instant trainingDate, int trainingType, int trainingIntensity, int staminaShare, int trainingAssistantsLevel, int coachLevel, boolean includeMatches, DBDataSource source) {
        this(trainingDate, trainingType, trainingIntensity, staminaShare, trainingAssistantsLevel, coachLevel, includeMatches, source, null);
    }



    public TrainingPerWeek(Instant trainingDate, int training_type, int training_intensity, int staminaShare, int trainingAssistantsLevel, int coachLevel) {
        this(trainingDate,training_type,training_intensity,staminaShare,trainingAssistantsLevel,coachLevel,false, DBDataSource.GUESS);
    }


    /**
     * function that fetch info of NT match played related to the TrainingPerWeek instance
     * @return MatchKurzInfo[] related to this TrainingPerWeek instance
     */
    private MatchKurzInfo[] fetchNTMatches(String firstMatchDate, String lastMatchDate) {

        var matchTypes= MatchType.getNTMatchType();

        String sOfficialMatchType = matchTypes.stream().map(m -> m.getId()+"").collect(Collectors.joining(","));

        final String where = String.format("WHERE MATCHDATE BETWEEN '%s' AND '%s' AND MATCHTYP in (%s) AND STATUS=%s ORDER BY MatchDate DESC",
                firstMatchDate, lastMatchDate, sOfficialMatchType, MatchKurzInfo.FINISHED);

        return cl_DBManager.getMatchesKurzInfo(where);
    }


    /**
     * function that fetch info of match played related to the TrainingPerWeek instance
     * @return MatchKurzInfo[] related to this TrainingPerWeek instance
     */
    private MatchKurzInfo[] fetchMatches(String firstMatchDate, String lastMatchDate) {


        var matchTypes= MatchType.getOfficialMatchType();
        String sOfficialMatchType = matchTypes.stream().map(m -> m.getId()+"").collect(Collectors.joining(","));

        final String where = String.format("WHERE (HEIMID = %s OR GASTID = %s) AND MATCHDATE BETWEEN '%s' AND '%s' AND MATCHTYP in (%s) AND STATUS in (%s, %s) ORDER BY MatchDate DESC",
                    myClubID, myClubID, firstMatchDate, lastMatchDate, sOfficialMatchType, MatchKurzInfo.FINISHED, MatchKurzInfo.UPCOMING);


        return cl_DBManager.getMatchesKurzInfo(where);
    }

    public MatchKurzInfo[] getMatches() {
        return o_Matches;
    }

    public MatchKurzInfo[] getNTmatches() {
        return o_NTmatches;
    }

    public Instant getTrainingDate() {
        return o_TrainingDate;
    }

    public void setTrainingDate(Instant trainingDate) {
        o_TrainingDate = trainingDate;
    }

    public final int getStaminaShare() {
        return o_StaminaShare;
    }

    public final void setStaminaPart(int staminaPart) {
        o_StaminaShare = staminaPart;
    }


    public final int getTrainingIntensity() {
        return o_TrainingIntensity;
    }

    public final void setTrainingIntensity(int trainingIntensity) {
        o_TrainingIntensity = trainingIntensity;
    }

    public final int getTrainingType() {
        return o_TrainingType;
    }

    public final void setTrainingType(int trainingType) {
        o_TrainingType = trainingType;
    }

	public int getTrainingAssistantsLevel() {
		return o_TrainingAssistantsLevel;
	}

	public int getCoachLevel(){return o_CoachLevel;}

    public DBDataSource getSource() {
        return o_Source;
    }

    public void setSource(DBDataSource source) {
        this.o_Source = source;
    }

    @Override
    public final String toString() {
        return "TrainingPerWeek[" +
                "Training date: " + DateTimeUtils.InstantToSQLtimeStamp(o_TrainingDate) +
                ", Training Type: " + TrainingType.toString(o_TrainingType)  +
                "%, Intensity: " + o_TrainingIntensity +
                "%, StaminaPart: " + o_StaminaShare +
                "]";
    }


    public void setStaminaShare(int staminaShare) {
        this.o_StaminaShare=staminaShare;
    }

    public void setCoachLevel(int coachLevel) {
        this.o_CoachLevel=coachLevel;
    }

    public void setTrainingAssistantLevel(int trainingAssistantsLevel) {
        this.o_TrainingAssistantsLevel=trainingAssistantsLevel;
    }
}
