package module.youth;

import core.constants.TrainingType;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupTeam;
import core.model.match.SourceSystem;
import core.training.YouthTrainerComment;

import java.util.ArrayList;
import java.util.List;

public class YouthTraining {
    private MatchLineup matchLineup;
    private int youthMatchId;
    private YouthTrainingType training1;
    private YouthTrainingType training2;
    private List<YouthTrainerComment> commentList=new ArrayList<>();

    public YouthTraining(int youthMatchId){
        this.youthMatchId=youthMatchId;
    }

    public YouthTraining(MatchLineup youthMatch, YouthTrainingType t1, YouthTrainingType t2){
        this.matchLineup = youthMatch;
        this.training1=t1;
        this.training2=t2;
    }

    public YouthTraining(MatchLineup lineup){
        this.matchLineup = lineup;
    }

    public YouthTrainingType getTraining1() {
        return training1;
    }

    public void setTraining1(YouthTrainingType training1) {
        this.training1 = training1;
    }

    public YouthTrainingType getTraining2() {
        return training2;
    }

    public void setTraining2(YouthTrainingType training2) {
        this.training2 = training2;
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
}
