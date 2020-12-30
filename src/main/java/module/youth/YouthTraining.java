package module.youth;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;
import core.model.match.SourceSystem;
import core.model.player.MatchRoleID;
import core.training.YouthTrainerComment;
import core.training.type.IndividualWeeklyTraining;
import module.lineup.substitution.model.MatchOrderType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class YouthTraining {

    public enum Priority {
        Primary,
        Secondary
    }

    private double[] trainingPrioFactor = {
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

        for ( var player : team.getStartingLineup()){
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

    private MatchLineup getMatchLineup() {
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

    public YouthPlayer.SkillInfo calcSkill(YouthPlayer.SkillInfo value, YouthPlayer player, MatchLineupTeam team) {

        var ret = new YouthPlayer.SkillInfo(value.getSkillID());
        ret.setMax(value.getMax());
        ret.setCurrentLevel(value.getCurrentLevel());
        ret.setMaxReached(value.isMaxReached());
        ret.setStartValue(value.getStartValue());
        if (value.isMaxReached()){
            ret.setCurrentValue(value.getCurrentValue());
        }
        else {
            ret.setCurrentValue(value.getCurrentValue()+calcSkillIncrement(value, player, team));
        }
        return ret;
    }

    private double calcSkillIncrement(YouthPlayer.SkillInfo value, YouthPlayer player, MatchLineupTeam lineupTeam) {
        double ret = 0;
        YouthTrainingType primaryTraining=null;
        for ( var priority : Priority.values()){
            var train = training[priority.ordinal()];
            if ( train != null) {
                var trainingFactor = UserParameter.instance().youthtrainingFactorPrimary;
                if (priority == Priority.Secondary) {
                    trainingFactor = UserParameter.instance().youthtrainingFactorSecondary;
                    if (train == primaryTraining) {
                        trainingFactor /= 2;
                    }
                }
                if (train != YouthTrainingType.IndividualTraining) {
                    int minutes = 0;
                    int posPrio = 0;    // Bonus, Full, Partly, Osmosis
                    for (var prioPositions : train.getTrainedPositions()) {
                        int minutesInPrioPositions = lineupTeam.getTrainMinutesPlayedInPositions(player.getId(),
                                prioPositions,
                                this.getMatchDetails().isWalkoverMatchWin(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId()));
                        if (minutesInPrioPositions + minutes > 90) {
                            minutesInPrioPositions = 90 - minutes;
                        }
                        if (minutesInPrioPositions > 0) {
                            ret += trainingFactor * minutesInPrioPositions * train.calcSkillIncrementPerMinute(value.getSkillID().getValue(), (int) value.getCurrentValue(), posPrio, player.getAgeYears());
                        }
                        minutes += minutesInPrioPositions;
                        if (minutes == 90) break;
                        posPrio++; // next position priority
                    }

                    if (primaryTraining == null) primaryTraining = train;
                } else {
                    // Calc Individual training
                    var sectors = lineupTeam.getTrainMinutesPlayedInSectors(player.getId());
                    for (var sector : sectors.entrySet()) {
                        ret += trainingFactor * sector.getValue() * train.calcSkillIncrementPerMinute(value.getSkillID().getValue(), (int) value.getCurrentValue(), sector.getKey(), player.getAgeYears());
                    }
                }
            }
        }
        return ret;
    }
}
