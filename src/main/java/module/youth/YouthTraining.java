package module.youth;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupTeam;
import core.model.match.SourceSystem;
import core.training.YouthTrainerComment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class YouthTraining {

    public enum Priority {
        Primary,
        Secondary
    }

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
            store();
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
