package core.training;

import core.constants.TrainingType;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.enums.DBDataSource;
import core.model.match.MatchKurzInfo;
import core.util.HODateTime;
import java.time.temporal.ChronoUnit;

/**
 * Class that holds all information required to calculate training effect of a given week
 * (e.g. training intensity, stamina part, assistant level, played games ...)
 */
public class TrainingPerWeek  {

    private final static int myClubID = HOVerwaltung.instance().getModel().getBasics().getTeamId();

    private int o_TrainingIntensity;
    private int o_StaminaShare;
    private int o_TrainingType;
    private int o_CoachLevel;
    private int o_TrainingAssistantsLevel;
    private HODateTime o_TrainingDate;
    private MatchKurzInfo[] o_Matches;
    private MatchKurzInfo[] o_NTmatches;
    private DBDataSource o_Source;


    /**
     *
     * Constructor, matches are not passsed as parameters but are loaded at object creation
     */
    public TrainingPerWeek(HODateTime trainingDate, int trainingType, int trainingIntensity, int staminaShare, int trainingAssistantsLevel, int coachLevel, DBDataSource source, boolean o_includeMatches) {
        o_TrainingDate = trainingDate;
        o_TrainingType = trainingType;
        o_TrainingIntensity = trainingIntensity;
        o_StaminaShare = staminaShare;
        o_CoachLevel = coachLevel;
        o_TrainingAssistantsLevel = trainingAssistantsLevel;
        o_Source = source;

        if (o_includeMatches) {
            loadMatches();
        }
    }

    public TrainingPerWeek(HODateTime trainingDate, int trainingType, int trainingIntensity, int staminaShare, int trainingAssistantsLevel, int coachLevel, DBDataSource source) {
        this(trainingDate, trainingType, trainingIntensity, staminaShare, trainingAssistantsLevel, coachLevel, source, false);
    }


    public TrainingPerWeek(HODateTime trainingDate, int training_type, int training_intensity, int staminaShare, int trainingAssistantsLevel, int coachLevel) {
        this(trainingDate,training_type,training_intensity,staminaShare,trainingAssistantsLevel,coachLevel, DBDataSource.GUESS);
    }

    public void loadMatches(){
        var _firstMatchDate = o_TrainingDate.minus(7, ChronoUnit.DAYS);
        var _lastMatchDate = o_TrainingDate.plus(23, ChronoUnit.HOURS);
        var teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
        o_Matches = DBManager.instance().loadOfficialMatchesBetween(teamId, _firstMatchDate, _lastMatchDate);
        o_NTmatches = DBManager.instance().loadNTMatchesBetween(teamId,_firstMatchDate, _lastMatchDate);
    }

    public MatchKurzInfo[] getMatches() {
        if ( o_Matches == null){
            var _firstMatchDate = o_TrainingDate.minus(7, ChronoUnit.DAYS);
            var _lastMatchDate = o_TrainingDate.plus(23, ChronoUnit.HOURS);
            var teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
            o_Matches = DBManager.instance().loadOfficialMatchesBetween(teamId, _firstMatchDate, _lastMatchDate);
        }
        return o_Matches;
    }

    public MatchKurzInfo[] getNTmatches() {
        if ( o_NTmatches==null){
            var _firstMatchDate = o_TrainingDate.minus(7, ChronoUnit.DAYS);
            var _lastMatchDate = o_TrainingDate.plus(23, ChronoUnit.HOURS);
            var teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
            o_NTmatches = DBManager.instance().loadNTMatchesBetween(teamId,_firstMatchDate, _lastMatchDate);
        }
        return o_NTmatches;
    }

    public HODateTime getTrainingDate() {
        return o_TrainingDate;
    }

    public void setTrainingDate(HODateTime trainingDate) {
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

    public Integer getSourceAsInt() {
        return o_Source.getValue();
    }

    public void setSource(DBDataSource source) {
        this.o_Source = source;
    }

    @Override
    public final String toString() {
        return "TrainingPerWeek[" +
                "Training date: " + o_TrainingDate.toHT() +
                ", Training Type: " + TrainingType.toString(o_TrainingType)  +
                ", Intensity: " + o_TrainingIntensity +
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

    public boolean skillDropDayIsBetween(HODateTime from, HODateTime to) {
        var skillDropDay = o_TrainingDate.minus(7*12, ChronoUnit.HOURS); // half week. TODO: check exact time difference
        return from.isBefore(skillDropDay) && !to.isBefore(skillDropDay);
    }
}
