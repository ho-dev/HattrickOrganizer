package core.training;

import core.constants.TrainingType;
import core.model.HOVerwaltung;
import core.model.match.MatchLineup;

import java.util.ArrayList;
import java.util.List;

public class YouthTraining {
    private MatchLineup matchLineup;
    private int youthMatchId;
    private TrainingType training1;
    private TrainingType training2;
    private List<YouthTrainerComment> commentList=new ArrayList<>();

    public YouthTraining(MatchLineup youthMatch, TrainingType t1, TrainingType t2){
        this.matchLineup = youthMatch;
        this.training1=t1;
        this.training2=t2;
    }

    public TrainingType getTraining1() {
        return training1;
    }

    public void setTraining1(TrainingType training1) {
        this.training1 = training1;
    }

    public TrainingType getTraining2() {
        return training2;
    }

    public void setTraining2(TrainingType training2) {
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

}
