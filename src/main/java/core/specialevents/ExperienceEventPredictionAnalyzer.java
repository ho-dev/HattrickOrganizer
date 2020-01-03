package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Vector;

import static java.lang.Math.abs;

public class ExperienceEventPredictionAnalyzer implements ISpecialEventPredictionAnalyzer {

    @Override
    public List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        SpecialEventsPrediction se=null;
        switch (position.getId()){
            case IMatchRoleID.rightBack:
            case IMatchRoleID.leftCentralDefender:
            case IMatchRoleID.middleCentralDefender:
            case IMatchRoleID.rightCentralDefender:
            case IMatchRoleID.leftBack:
            case IMatchRoleID.centralInnerMidfield:
            case IMatchRoleID.leftInnerMidfield:
            case IMatchRoleID.rightInnerMidfield:
                 se = SpecialEventsPrediction.createIfInRange(
                        position,
                        SpecialEventType.EXPERIENCE,
                        -.5,0,4,
                        p.getErfahrung());
                break;

            case IMatchRoleID.leftWinger:
            case IMatchRoleID.rightWinger:
            case IMatchRoleID.leftForward:
            case IMatchRoleID.centralForward:
            case IMatchRoleID.rightForward:
                se = SpecialEventsPrediction.createIfInRange(
                        position,
                        SpecialEventType.EXPERIENCE,
                        .5,20,10,
                        p.getErfahrung());
                break;
        }
        if ( se != null){
            se.setGoalProbability(se.getChanceCreationProbability()/4);
            ret.add(se);
        }
        return ret;
    }
}
