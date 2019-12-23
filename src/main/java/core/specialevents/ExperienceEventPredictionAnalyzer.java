package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.Lineup;

import java.util.List;
import java.util.Vector;

public class ExperienceEventPredictionAnalyzer implements ISpecialEventPredictionAnalyzer {

    private SpecialEventsPredictionManager theManageer = null;

    public ExperienceEventPredictionAnalyzer(SpecialEventsPredictionManager specialEventsPredictionManager) {
        theManageer=specialEventsPredictionManager;
    }

    @Override
    public List<SpecialEventsPrediction> analyzePosition(MatchRoleID position) {
        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p = theManageer.getPlayer(id);
        switch (position.getId()){
            case IMatchRoleID.rightBack:
            case IMatchRoleID.leftCentralDefender:
            case IMatchRoleID.middleCentralDefender:
            case IMatchRoleID.rightCentralDefender:
            case IMatchRoleID.leftBack:
            case IMatchRoleID.centralInnerMidfield:
            case IMatchRoleID.leftInnerMidfield:
            case IMatchRoleID.rightInnerMidfield:
                if ( p.getErfahrung() < 2 ){
                    ret.add(new SpecialEventsPrediction(position, "Experience", -1));
                }
                break;

            case IMatchRoleID.leftWinger:
            case IMatchRoleID.rightWinger:
            case IMatchRoleID.leftForward:
            case IMatchRoleID.centralForward:
            case IMatchRoleID.rightForward:
                if (p.getErfahrung() > 7){
                    ret.add(new SpecialEventsPrediction(position, "Experience", 1));
                }
                break;
        }
        return ret;
    }
}
