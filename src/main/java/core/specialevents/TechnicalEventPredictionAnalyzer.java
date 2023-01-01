package core.specialevents;

import core.constants.player.Specialty;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

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
    public void analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        this.analyse = analyse;
        int id = position.getPlayerId();
        if (id == 0) return;
        Player p = analyse.getPlayer(id);
        double scoreBoost = 1;
        if (p.hasSpecialty(Specialty.TECHNICAL)) {
            switch (position.getId()) {
                case IMatchRoleID.leftWinger:
                case IMatchRoleID.rightWinger:
                    scoreBoost *= 48. / 38.;
                case IMatchRoleID.leftForward:
                case IMatchRoleID.rightForward:
                case IMatchRoleID.centralForward:
                case IMatchRoleID.centralInnerMidfield:
                case IMatchRoleID.leftInnerMidfield:
                case IMatchRoleID.rightInnerMidfield:
                    // Look for Heads in opponent team
                    getTechHeadEvent(position, IMatchRoleID.rightInnerMidfield, scoreBoost);
                    getTechHeadEvent(position, IMatchRoleID.centralInnerMidfield, scoreBoost);
                    getTechHeadEvent(position, IMatchRoleID.leftInnerMidfield, scoreBoost);
                    getTechHeadEvent(position, IMatchRoleID.rightBack, scoreBoost);
                    getTechHeadEvent(position, IMatchRoleID.leftBack, scoreBoost);
                    getTechHeadEvent(position, IMatchRoleID.rightCentralDefender, scoreBoost);
                    getTechHeadEvent(position, IMatchRoleID.middleCentralDefender, scoreBoost);
                    getTechHeadEvent(position, IMatchRoleID.leftCentralDefender, scoreBoost);
            }
        }
    }

    private void getTechHeadEvent( MatchRoleID position, int opponentPosition, double scoreBoost) {
        Player opp = analyse.getOpponentPlayerByPosition(opponentPosition);
        if (opp == null || position.getPlayerId() == 0) return;
        if (opp.hasSpecialty(Specialty.HEAD)) {
            Player p = analyse.getPlayer(position.getPlayerId());
            SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position,
                    SpecialEventType.TECHNICAL_HEAD,
                    1., 20, -20,
                    p.getSCskill() + p.getExperience() - opp.getDEFskill() - opp.getExperience()
            );
            if (se != null) {
                se.addInvolvedOpponentPosition(analyse.getOpponentPosition(opponentPosition));
                se.setGoalProbability(scoreBoost * se.getChanceCreationProbability() * analyse.getGoalProbability(position));
                analyse.addSpecialEventPrediction(se);
            }
        }
    }
}