package module.youth;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.*;
import module.lineup.substitution.model.MatchOrderType;
import module.training.Skills;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class YouthTraining {

    public enum Priority {
        Primary,
        Secondary
    }

    private static double[] trainingPrioFactor = {
            UserParameter.instance().youthtrainingFactorPrimary,
            UserParameter.instance().youthtrainingFactorSecondary
    };

    private MatchLineup matchLineup;
    private Matchdetails matchdetails;
    private int youthMatchId;
    private YouthTrainingType[] training = new YouthTrainingType[2];
    private List<YouthTrainerComment> commentList=new ArrayList<>();

    public YouthTraining(int youthMatchId){
        this.youthMatchId=youthMatchId;
    }

    public YouthTraining(MatchLineup youthMatch, YouthTrainingType t1, YouthTrainingType t2) {
        setMatchLineup(youthMatch);
        this.matchLineup = youthMatch;
        this.training[Priority.Primary.ordinal()] = t1;
        this.training[Priority.Secondary.ordinal()] = t2;
    }

    private void setMatchLineup(MatchLineup youthMatch) {
        this.youthMatchId = youthMatch.getMatchID();
        this.matchLineup = youthMatch;
    }

    public YouthTraining(MatchLineup lineup) {
        setMatchLineup(lineup);
    }

    public YouthTrainingType getTraining(Priority p) {
        return training[p.ordinal()];
    }

    public void setTraining(Priority p, YouthTrainingType trainingType) {
        this.training[p.ordinal()] = trainingType;
    }

    public void recalcSkills() {
        var team = this.getMatchLineup().getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());

        for ( var player : team.getLineup()){
            recalcSkills(player.getPlayerId());
        }
        for ( var subs : team.getSubstitutions()){
            if ( subs.getOrderType().equals(MatchOrderType.SUBSTITUTION) ) {
                recalcSkills(subs.getObjectPlayerID());
            }
        }
    }

    private void recalcSkills(int playerId) {
        var since = this.matchLineup.getMatchDate();
        var p = HOVerwaltung.instance().getModel().getCurrentYouthPlayer(playerId);
        if ( p != null){
            p.recalcSkills(since);
        }
    }

    public void store() {
        DBManager.instance().storeYouthTraining(this);
    }

    public List<YouthTrainerComment> getCommentList() {
        return commentList;
    }

    public void addComment(YouthTrainerComment comment){
        commentList.add(comment);
        var youthplayerID = comment.getYouthPlayerId();
        var team = matchLineup.getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());

        var player = HOVerwaltung.instance().getModel().getCurrentYouthPlayer(youthplayerID);
        player.addComment(comment);
    }

    public int getMatchId() {
        return this.youthMatchId;
    }

    public MatchLineupTeam getTeam(Integer youthTeamId) {
        return this.getMatchLineup().getTeam(youthTeamId);
    }

    MatchLineup getMatchLineup() {
        if ( this.matchLineup == null){
            this.matchLineup = DBManager.instance().loadMatchLineup(SourceSystem.YOUTH.getValue(), this.youthMatchId);
        }
        return this.matchLineup;
    }

    private Matchdetails getMatchDetails() {
        if ( this.matchdetails == null){
            this.matchdetails = DBManager.instance().loadMatchDetails(SourceSystem.YOUTH.getValue(), this.youthMatchId);
        }
        return this.matchdetails;
    }

    public Timestamp getMatchDate() {
        return this.getMatchLineup().getMatchDate();
    }

    public String getHomeTeamName() {
        return this.getMatchLineup().getHomeTeamName();
    }

    public String getGuestTeamName() {
        return this.getMatchLineup().getGuestTeamName();
    }

    public SkillInfo calcSkill(SkillInfo value, YouthPlayer player, MatchLineupTeam team) {

        var ret = new SkillInfo(value.getSkillID());
        ret.setMax(value.getMax());
        ret.setCurrentLevel(value.getCurrentLevel());
        ret.setMaxReached(value.isMaxReached());
        if (value.isMaxReached()){
            ret.setCurrentValue(value.getCurrentValue());
        }
        else {
            var newVal = value.getCurrentValue() + calcSkillIncrement(value, player, team);
            ret.setCurrentValue(newVal);
            var adjustment = ret.getCurrentValue() - newVal;
            if (adjustment > 0) {
                value.setStartValue(player.adjustSkill(value.getSkillID(), adjustment));
            }
        }
        // Current value needs to be set before start value could be changed
        // (adjustment would reset start value otherwise)
        ret.setStartValue(value.getStartValue());
        return ret;
    }

    private double calcSkillIncrement(SkillInfo value, YouthPlayer player, MatchLineupTeam lineupTeam) {
        double ret = 0;
        var matchTypeFactor = getMatchTypeFactor();
        YouthTrainingType primaryTraining=null;
        // for each youth training
        for ( var priority : Priority.values()){
            var train = training[priority.ordinal()];
            if ( train != null) {
                // training type is specified
                var trainingFactor = UserParameter.instance().youthtrainingFactorPrimary;
                if (priority == Priority.Secondary) {
                    // specialty of second training type
                    trainingFactor = UserParameter.instance().youthtrainingFactorSecondary;
                    if (train == primaryTraining) {
                        // reduce efficiency if it is equal to primary training
                        trainingFactor /= 2;
                    }
                }
                if (train != YouthTrainingType.IndividualTraining) {
                    // NOT individual training
                    int minutes = 0;
                    int posPrio = 0;
                    // for Bonus-, Full-, Partly-, Osmosis sectors
                    for (var prioPositions : train.getTrainedSectors()) {
                        int minutesInPrioPositions = lineupTeam.getTrainingMinutesPlayedInSectors(player.getId(),
                                prioPositions,
                                this.getMatchDetails().isWalkoverMatchWin(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId()));
                        if (minutesInPrioPositions + minutes > 90) {
                            minutesInPrioPositions = 90 - minutes;
                        }
                        if (minutesInPrioPositions > 0) {
                            ret += matchTypeFactor *
                                    trainingFactor *
                                    minutesInPrioPositions *
                                    train.calcSkillIncrementPerMinute(value.getSkillID(), (int) value.getCurrentValue(), posPrio, player.getAgeYears());
                        }
                        minutes += minutesInPrioPositions;
                        if (minutes == 90) break;
                        posPrio++; // next position priority
                    }
                    if (primaryTraining == null) primaryTraining = train;
                } else {
                    // Calc Individual training
                    var matchLineupPlayer = lineupTeam.getPlayerByID(player.getId());
                    if ( matchLineupPlayer != null) {
                        List<MatchLineupPlayer.SectorAppearance> appearances = matchLineupPlayer.getMinutesInSectors();
                        for (var appearance : appearances) {
                            ret += trainingFactor * appearance.getMinutes() * train.calcSkillIncrementPerMinute(value.getSkillID(), (int) value.getCurrentValue(), appearance.getSector(), player.getAgeYears());
                        }
                    }
                }
            }
        }
        if ( ret > 1 ) ret = 1; // skill increment is limited
        return ret;
    }

    static final double TRAINING_FRIENDLYFACTOR=.5;
    public double getMatchTypeFactor() {
        if (this.getMatchLineup().getMatchType() == MatchType.YOUTHLEAGUE) {
            return 1.;
        }
        return TRAINING_FRIENDLYFACTOR;
    }

    public String getPlayerTrainedSectors(int playerId) {
        var ret = new StringBuilder();
        var hov = HOVerwaltung.instance();
        var lineupTeam = this.getTeam(hov.getModel().getBasics().getYouthTeamId());
        //var sectors = lineupTeam.getTrainMinutesPlayedInSectors(playerId);
        var player = lineupTeam.getPlayerByID(playerId);
        if ( player != null) {
            var sectors = player.getMinutesInSectors();
            for (var s : sectors) {
                ret.append(hov.getLanguageString("ls.youth.training.sector." + s.getSector()))
                        .append(":")
                        .append(s.getMinutes())
                        .append(" ");
            }
        }
        return ret.toString();
    }

    /**
     * Calc most effective training for given skill id
     *
     * training rate is 6 league matches/training in 44 days
     * plus             1 friendly match in 21 days
     * @param skillId
     * @param skillVal
     * @param age
     * @return
     */

    static final double fullTrainingsPerWeek = (6*21 + 44*TRAINING_FRIENDLYFACTOR)/(21*44)*7;
    static final double equalTrainings = 1.33;
    public static double getMaxTrainingPerWeek(Skills.HTSkillID skillId, int skillVal, int age) {

        switch (skillId){
            case Keeper:
                return YouthTrainingType.Goalkeeping.calcSkillIncrementPerMinute(skillId,skillVal,1,age)* equalTrainings*fullTrainingsPerWeek*90.;
            case Playmaker:
                return YouthTrainingType.Playmaking.calcSkillIncrementPerMinute(skillId,skillVal,1,age)* equalTrainings*fullTrainingsPerWeek*90.;
            case SetPieces:
                // TODO check if shooting as secondary training is more effective
                return YouthTrainingType.SetPieces.calcSkillIncrementPerMinute(skillId,skillVal,1,age)* equalTrainings*fullTrainingsPerWeek*90.;
            case Defender:
                // TODO check if defending position as secondary training is more effective
                return YouthTrainingType.Defending.calcSkillIncrementPerMinute(skillId,skillVal,1,age)* equalTrainings*fullTrainingsPerWeek*90.;
            case Winger:
                // TODO check if wing attack as secondary training is more effective
                return YouthTrainingType.Winger.calcSkillIncrementPerMinute(skillId,skillVal,1,age)* equalTrainings*fullTrainingsPerWeek*90.;
            case Passing:
                // TODO check if through passes as secondary training is more effective
                return YouthTrainingType.Passing.calcSkillIncrementPerMinute(skillId,skillVal,1,age)* equalTrainings*fullTrainingsPerWeek*90.;
            case Scorer:
                // TODO check if shooting as secondary training is more effective
                return YouthTrainingType.Scoring.calcSkillIncrementPerMinute(skillId,skillVal,1,age)* equalTrainings*fullTrainingsPerWeek*90.;
        }
        return 0;
    }


}
