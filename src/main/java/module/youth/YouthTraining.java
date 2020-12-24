package module.youth;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupTeam;
import core.model.match.SourceSystem;
import core.training.YouthTrainerComment;
import module.lineup.substitution.model.MatchOrderType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class YouthTraining {

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
        for ( var priority : Priority.values()){
            var train = training[priority.ordinal()];
            int minutes=0;
            int posPrio = 0;
            for ( var prioPositions : train.getTrainedPositions()){
                int minutesInPrioPositions = lineupTeam.getMinutesInPositions(player.getId(), prioPositions);
                if ( minutesInPrioPositions + minutes > 90){
                    minutesInPrioPositions = 90 - minutes;
                }
                if ( minutesInPrioPositions > 0){
                    ret += minutesInPrioPositions * train.calcSkillIncrementPerMinute(value.getSkillID(), (int)value.getCurrentValue(), posPrio, player.getAgeYears());
                }
                minutes += minutesInPrioPositions;
                if ( minutes == 90) break;
                posPrio++; // next position priority
            }
            // Calc Bonus
        }
        return 0;
    }

    public enum Priority {
        Primary,
        Secondary
    }

    private double[] trainingPrioFactor = {
            UserParameter.instance().youthtrainingFactorPrimary,
            UserParameter.instance().youthtrainingFactorSecondary
    };

    private MatchLineup matchLineup;
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
        if ( this.training[p.ordinal()] != trainingType){
            this.training[p.ordinal()] = trainingType;
            recalcSkills();
            store();
        }
    }

    private void recalcSkills() {
        var team = this.getMatchLineup().getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());

        for ( var player : team.getStartingLineup()){
            recalcSkills(player.getSpielerId());
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

    void store() {
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
            this.matchLineup = DBManager.instance().getMatchLineup(SourceSystem.YOUTH.getValue(), this.youthMatchId);
        }
        return this.matchLineup;
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
}
