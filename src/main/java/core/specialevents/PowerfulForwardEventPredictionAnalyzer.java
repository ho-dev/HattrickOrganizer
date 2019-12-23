package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.substitution.model.MatchOrderType;

import java.util.List;
import java.util.Vector;

public class PowerfulForwardEventPredictionAnalyzer implements  ISpecialEventPredictionAnalyzer {
    public static final String eventName = "Powerful Forward";
    private SpecialEventsPredictionManager theManager = null;

    public PowerfulForwardEventPredictionAnalyzer(SpecialEventsPredictionManager specialEventsPredictionManager) {
        theManager = specialEventsPredictionManager;
    }
    @Override
    public List<SpecialEventsPrediction> analyzePosition(MatchRoleID position) {
        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p = theManager.getPlayer(id);
        Speciality speciality = Speciality.values()[p.getPlayerSpecialty()];
        if (speciality.equals(Speciality.POWERFUL)) {

            switch (position.getId()) {
                case IMatchRoleID.leftForward:
                case IMatchRoleID.centralForward:
                case IMatchRoleID.rightForward:

                    if( position.getTaktik() == IMatchRoleID.NORMAL){

                        // TODO: Probability depends on defence skills of opponent defenders
                        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position, eventName,
                                .5, 0, 20*20, 8*8,
                                p.getSCskill() * p.getPMskill());
                        if (se != null) {
                            ret.add(se);
                        }
                    }

                    break;
            }
        }
        return ret;
    }
}
