package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Vector;

public class QuickEventPredictionAnalyzer  implements ISpecialEventPredictionAnalyzer {

    // From devblog: https://devblog.hattrick.org/2017/12/a-new-match-engine-odyssey/
    //Quick Scores
    //Any Winger, Inner Midfielder or Forward can start a quick rush to score. If the exact opposite player is also
    // quick, then he has 100% chance to stop the attack. Otherwise, nearby quick players might stop it instead, but
    // they have a much smaller chance of doing so.
    //
    //Example: Your FW has 9 scoring. The opposite defender has 14 defending and the opponent keeper has 15 goalkeeping.
    // Then the success rate to score is ~39% and if the same player was a winger, it would be 58% since it needs less
    // scoring compared to IM and FW players.
    //
    //Quick Passes
    //Again, Wingers, Inner Midfielders, and Forwards can start a quick rush with potential for a subsequent pass.
    // Any Winger and Forward can be the ball receiver.
    //
    //Example: You have a Winger with passing 10 and the average of the Defenders’ defending skill of the same side
    // has 14 defending. The ball receiver has 12 Scoring and the Keeper has 15 goalkeeping. Then the success ratio is
    // 54% for a FW and 71% for a Winger.

    private SpecialEventsPredictionManager.Analyse analyse;
    @Override
    public List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        this.analyse = analyse;
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        if (p.hasSpeciality(Speciality.QUICK)) {

            switch (position.getId()) {
                case IMatchRoleID.leftWinger:
                case IMatchRoleID.leftForward:
                    return getQuickEvents( position, IMatchRoleID.rightBack, IMatchRoleID.rightCentralDefender,0);
                case IMatchRoleID.rightWinger:
                case IMatchRoleID.rightForward:
                    return getQuickEvents( position, IMatchRoleID.leftBack, IMatchRoleID.leftCentralDefender, 0);
                case IMatchRoleID.centralForward:
                case IMatchRoleID.centralInnerMidfield:
                    return getQuickEvents( position, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender, IMatchRoleID.leftCentralDefender);
                case IMatchRoleID.leftInnerMidfield:
                    return getQuickEvents( position, IMatchRoleID.rightCentralDefender, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightBack);
                case IMatchRoleID.rightInnerMidfield:
                    return getQuickEvents( position, IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender, IMatchRoleID.leftBack);
            }
        }

        return new Vector<SpecialEventsPrediction>();
    }

    private Vector<SpecialEventsPrediction> getQuickEvents(
            MatchRoleID position,
            int block100PercentIfQuick,
            int block25PercentIfQuick,
            int block25PercentIfQuick2) {
        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();

        SpecialEventsPrediction se = new SpecialEventsPrediction(position, SpecialEventType.QUICK_SCORES, .08);

        double goalProbabilityFactor = 0;
        Player p = analyse.getOpponentPlayerByPosition(block100PercentIfQuick);
        if ( p == null || !p.hasSpeciality(Speciality.QUICK)){
            goalProbabilityFactor = 1;
            p = analyse.getOpponentPlayerByPosition(block25PercentIfQuick);
            if (  p != null && p.hasSpeciality(Speciality.QUICK)){
                goalProbabilityFactor *= .75;
                se.addInvolvedOpponentPosition(analyse.getOpponentPosition(block25PercentIfQuick));
            }

            if (  block25PercentIfQuick2 != 0 ) {
                p = analyse.getOpponentPlayerByPosition(block25PercentIfQuick2);
                if ( p != null && p.hasSpeciality(Speciality.QUICK)){
                    goalProbabilityFactor *= .75;
                    se.addInvolvedOpponentPosition(analyse.getOpponentPosition(block25PercentIfQuick2));
                }
            }
        }
        else{
            se.addInvolvedOpponentPosition(analyse.getOpponentPosition(block100PercentIfQuick));
        }
        se.setGoalProbability(goalProbabilityFactor);

        ret.add(se);
        return ret;
    }
}