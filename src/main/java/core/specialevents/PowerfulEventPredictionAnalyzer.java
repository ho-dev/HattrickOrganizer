package core.specialevents;

import core.constants.player.Specialty;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

public class PowerfulEventPredictionAnalyzer implements  ISpecialEventPredictionAnalyzer {
    private SpecialEventsPredictionManager.Analyse analyse;


    // Power Forward (“Powerful Normal Forward”)
    // Size matters. If you have a big forward playing Normal, they can put their bodies in the right place, forcing a
    // second chance to shoot and score after a normal chance for your team has been missed. Keep in mind that this
    // physical play could result in yellow cards! For that reason, if your player has already been carded, he will
    // tread more carefully in these situations. One PNF has 10% probs to create a second chance. Two of them have 16%
    // and three 20%.
    //
    // Playmaking of your player VS Defending of central defenders and Scoring of your player VS Goalkeeping of the
    // keeper
    //
    // Sitting Midfielder (“Powerful Defensive Inner Midfielder”)
    // Lets your powerful midfielder play defensively so that he can concentrate on breaking up attacks and winning the
    // ball. If he succeeds in pressing, the opponent’s attack breaks down. Same as before, the percentages to trigger
    // this event is 10%, 16% and 20%.
    // Example: Your stamina needs to be close or higher than your opponent. If you have 13 defending and your opponent
    // 17 scoring, you have 41% chance to press it.

    @Override
    public void analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        this.analyse = analyse;

        int id = position.getPlayerId();
        if (id == 0) return;
        Player p = analyse.getPlayer(id);
        if (p.hasSpecialty(Specialty.POWERFUL)) {

            switch (position.getId()) {
                case IMatchRoleID.leftForward:
                case IMatchRoleID.centralForward:
                case IMatchRoleID.rightForward:

                    if (position.getTactic() == IMatchRoleID.NORMAL) {
                        getPowerfulNormalForward(position);
                    }
                    break;

                case IMatchRoleID.rightInnerMidfield:
                case IMatchRoleID.centralInnerMidfield:
                case IMatchRoleID.leftInnerMidfield:
                    if (position.getTactic() == IMatchRoleID.DEFENSIVE) {
                        getSittingMidfielder(position);
                    }
                    break;
            }
        }
    }

    // PDIMs reduces goal chances of opponent teams
    // => chance probability < 0 to display it in opponents column
    // => goal probability > 0  to reduce opponents goals
    private void getSittingMidfielder(MatchRoleID position, MatchRoleID opponentScorer) {
        if (position.getPlayerId() == 0 || opponentScorer.getPlayerId() == 0) return;
        Player p = analyse.getPlayer(position.getPlayerId());
        Player op = analyse.getOpponentPlayer(opponentScorer.getPlayerId());
        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position, SpecialEventType.PDIM,
                -.1, 10, -10,
                p.getDEFskill() + p.getStamina() - op.getSCskill() - op.getStamina()
        );
        if (se != null) {
            double goalP = analyse.getOpponentGoalProbability(opponentScorer);
            if (goalP > 0) {
                se.setInvolvedOpponentPosition(opponentScorer);
                se.setGoalProbability(-se.getChanceCreationProbability() * goalP);
                analyse.addSpecialEventPrediction(se);
            }
        }
    }

    private void getSittingMidfielder(MatchRoleID position) {
        double overcrowdingFactor = getOvercrowding(position, IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftInnerMidfield, IMatchRoleID.DEFENSIVE);

        // Any opponent player, except keeper, could be involved
        for (int i = IMatchRoleID.rightBack; i <= IMatchRoleID.leftForward; i++) {
            MatchRoleID opponentScorer = analyse.getOpponentPosition(i);
            if (opponentScorer.getPlayerId() != 0) {
                getSittingMidfielder(position, opponentScorer);
            }
        }
    }

    private double getOvercrowding(MatchRoleID position, int right, int left, byte taktik) {
        int overcrowding = 0;
        for (int i = right; i <= left; i++) {
            if (i != position.getId()) {
                MatchRoleID mid = this.analyse.getPosition(i);
                if (mid.getPlayerId() != 0 && mid.getTactic() == taktik) {
                    Player p = analyse.getPlayer(mid.getPlayerId());
                    if (p.hasSpecialty(Specialty.POWERFUL)) {
                        overcrowding++;
                    }
                }
            }
        }

        double overcrowdingFactor = 1;
        if (overcrowding == 1) {
            overcrowdingFactor = 1.6 / 2;
        } else if (overcrowding == 2) {
            overcrowdingFactor = 2. / 3.;
        }

        return overcrowdingFactor;
    }

    private void getPowerfulNormalForward(MatchRoleID position) {
        if (position.getPlayerId() == 0) return;
        double overcrowdingFactor = getOvercrowding(position, IMatchRoleID.rightForward, IMatchRoleID.leftForward, IMatchRoleID.NORMAL);

        double defence = 0;
        for (int i = IMatchRoleID.rightCentralDefender; i <= IMatchRoleID.leftCentralDefender; i++) {
            Player opponentDefender = analyse.getOpponentPlayerByPosition(i);
            if (opponentDefender != null) {
                defence += opponentDefender.getDEFskill();
            }
        }

        Player p = analyse.getPlayer(position.getPlayerId());
        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position, SpecialEventType.PNF,
                0.5, 10, -20,
                p.getPMskill() - defence);

        if (se != null) {
            se.setChanceCreationProbability(se.getChanceCreationProbability() * overcrowdingFactor);
            se.setGoalProbability(analyse.getGoalProbability(position) * se.getChanceCreationProbability());

            analyse.addSpecialEventPrediction(se);
        }
    }
}