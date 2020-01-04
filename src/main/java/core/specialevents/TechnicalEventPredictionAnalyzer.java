package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Vector;

// Technical Goes around a Head Player
// This is known as a tech vs head special event. Any technical Winger, Inner Midfielder and Forward can take
// advantage of it against any Defender, Wing Back and Inner Midfielder. It doesn’t have to be in the exact opposite
// slot. This is a great improvement for the technical players, especially if your opponent is a headers oriented team.
//
// Example: Your technical guy has 12 scoring and 5 experience. Your opponent defender has 14 defending and 6 experience,
// while the keeper has 15 goalkeeping. Then the chance to score is 38% for FW/IM and 48% for Wingers.
//
// Create a Non-Tactical Counter Attack: Every technical defender and wing back will give you a small chance to create a
// non-tactical counter attack from a missed normal chance of your opponent. Your chances to trigger an event vary from
// 1.7 to 3% based on how many technical back players you have.

public class TechnicalEventPredictionAnalyzer implements ISpecialEventPredictionAnalyzer  {
    private SpecialEventsPredictionManager.Analyse analyse;

    @Override
    public List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        this.analyse = analyse;
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        double scoreBoost=1;
        if (p.hasSpeciality(Speciality.TECHNICAL)) {
            switch (position.getId()) {
                case IMatchRoleID.leftWinger:
                case IMatchRoleID.rightWinger:
                    scoreBoost *= 48./38.;
                case IMatchRoleID.leftForward:
                case IMatchRoleID.rightForward:
                case IMatchRoleID.centralForward:
                case IMatchRoleID.centralInnerMidfield:
                case IMatchRoleID.leftInnerMidfield:
                case IMatchRoleID.rightInnerMidfield:
                    // Look for Heads in opponent team
                    Vector<SpecialEventsPrediction> ret = new Vector<>();
                    getTechHeadEvent(ret, position, IMatchRoleID.rightInnerMidfield, scoreBoost);
                    getTechHeadEvent(ret, position, IMatchRoleID.centralInnerMidfield, scoreBoost);
                    getTechHeadEvent(ret, position, IMatchRoleID.leftInnerMidfield, scoreBoost);
                    getTechHeadEvent(ret, position, IMatchRoleID.rightBack, scoreBoost);
                    getTechHeadEvent(ret, position, IMatchRoleID.leftBack, scoreBoost);
                    getTechHeadEvent(ret, position, IMatchRoleID.rightCentralDefender, scoreBoost);
                    getTechHeadEvent(ret, position, IMatchRoleID.middleCentralDefender, scoreBoost);
                    getTechHeadEvent(ret, position, IMatchRoleID.leftCentralDefender, scoreBoost);
                    return ret;
            }
        }
        return new Vector<SpecialEventsPrediction>();
    }

    private void getTechHeadEvent(Vector<SpecialEventsPrediction> ret, MatchRoleID position, int opponentPosition, double scoreBoost) {
        Player opp = analyse.getOpponentPlayerByPosition(opponentPosition);
        if ( opp.hasSpeciality(Speciality.HEAD)){
            Player p = analyse.getPlayerByPosition(position.getSpielerId());
            SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position,
                    SpecialEventType.TECHNICAL_HEAD,
                    1., 20, -20,
                    p.getSCskill()+p.getErfahrung() - opp.getDEFskill() - opp.getErfahrung()
                    );
            if  ( se != null ) {
                se.setGoalProbability(scoreBoost * se.getChanceCreationProbability() * analyse.getGoalProbability(p));
                ret.add(se);
            }
        }
    }
}
