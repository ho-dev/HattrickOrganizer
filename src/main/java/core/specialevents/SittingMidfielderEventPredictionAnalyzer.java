package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Vector;

public class SittingMidfielderEventPredictionAnalyzer implements  ISpecialEventPredictionAnalyzer {

    @Override
    public List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        if (p.hasSpeciality(Speciality.POWERFUL)) {

            switch (position.getId()) {
                case IMatchRoleID.leftInnerMidfield:
                case IMatchRoleID.centralInnerMidfield:
                case IMatchRoleID.rightInnerMidfield:

                    if( position.getTaktik() == IMatchRoleID.DEFENSIVE){

                        // TODO: Probability depends on skills of opponent forwards
                        // TODO: Overcrowding, if more than one PDIM is in  lineup
                        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(
                                position,
                                SpecialEventType.PDIM,
                                .5, 20*20, 8*8,
                                p.getDEFskill() * p.getPMskill());
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
