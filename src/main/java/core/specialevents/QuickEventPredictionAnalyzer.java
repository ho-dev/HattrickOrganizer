package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import static java.lang.Math.min;

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

    // https://www.hattrick.org/Forum/Read.aspx?t=17161178&a=1&n=1&v=4
    // About Quick scores, about the check Scoring of attacking player VS Defending of defending
    // player: If you don't have any player to stop him, then it is a random player from the defender line (wing back
    // or central defender). If you don't have any there, then next on the queue are IM and Wingers.
    //
    // About Quick pass: the defense rating of the same side means the average of the defending skill from the same
    // side. What the same side means here:
    //        -- FWR, WR, IMR vs WBL, CDL (corrected here: (17161178.954))
    //        -- FWL, WL, IML vs WBR, CDR (corrected here: (17161178.954))
    //        -- all the rest vs CDL, CD, CDR

    private SpecialEventsPredictionManager.Analyse analyse;
    @Override
    public void analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        this.analyse = analyse;
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        if (p.hasSpeciality(Speciality.QUICK)) {

            switch (position.getId()) {
                case IMatchRoleID.leftWinger:
                case IMatchRoleID.leftForward:
                     getQuickEvents( position, IMatchRoleID.rightBack, IMatchRoleID.rightCentralDefender,0, false);
                     break;
                case IMatchRoleID.rightWinger:
                case IMatchRoleID.rightForward:
                    getQuickEvents( position, IMatchRoleID.leftBack, IMatchRoleID.leftCentralDefender, 0, false);
                    break;
                case IMatchRoleID.centralForward:
                case IMatchRoleID.centralInnerMidfield:
                    getQuickEvents( position, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender, IMatchRoleID.leftCentralDefender, true);
                    break;
                case IMatchRoleID.leftInnerMidfield:
                     getQuickEvents( position, IMatchRoleID.rightCentralDefender, IMatchRoleID.rightBack, IMatchRoleID.middleCentralDefender, false);
                     break;
                case IMatchRoleID.rightInnerMidfield:
                     getQuickEvents( position, IMatchRoleID.leftCentralDefender, IMatchRoleID.leftBack, IMatchRoleID.middleCentralDefender,false);
                     break;
            }
        }
     }

    private void  getQuickEvents(
            MatchRoleID position,
            int block100PercentIfQuick,
            int block25PercentIfQuick,
            int block25PercentIfQuick2,
            boolean useQuick2ForPassCalculation
    ) {
        double goalProbabilityFactor = 0;   // used if block100Percent player is a quick one
        HashSet<IMatchRoleID> involvedOpponents = new HashSet<>();
        Player p = analyse.getOpponentPlayerByPosition(block100PercentIfQuick);
        if (p == null || !p.hasSpeciality(Speciality.QUICK)) {
            goalProbabilityFactor = 1;
            p = analyse.getOpponentPlayerByPosition(block25PercentIfQuick);
            if (p != null && p.hasSpeciality(Speciality.QUICK)) {
                goalProbabilityFactor *= .75;
                involvedOpponents.add(analyse.getOpponentPosition(block25PercentIfQuick));
            }

            if (block25PercentIfQuick2 != 0) {
                p = analyse.getOpponentPlayerByPosition(block25PercentIfQuick2);
                if (p != null && p.hasSpeciality(Speciality.QUICK)) {
                    goalProbabilityFactor *= .75;
                    involvedOpponents.add(analyse.getOpponentPosition(block25PercentIfQuick2));
                }
            }
        } else {
            involvedOpponents.add(analyse.getOpponentPosition(block100PercentIfQuick));
        }

        // Quick - scores
        for (int pos = IMatchRoleID.rightBack; pos <= IMatchRoleID.leftBack; pos++) {
            getQuickScoresEvent( position, pos, goalProbabilityFactor, involvedOpponents);
        }

        // Quick - pass
        // Calculate opponent Defence skill
        double opponentDefenceSkill = 0;
        int n = 0;
        Player opp = analyse.getOpponentPlayerByPosition(block100PercentIfQuick);
        if (opp != null) {
            n++;
            opponentDefenceSkill += opp.getDEFskill();
        }
        opp = analyse.getOpponentPlayerByPosition(block25PercentIfQuick);
        if (opp != null) {
            n++;
            opponentDefenceSkill += opp.getDEFskill();
        }
        if (useQuick2ForPassCalculation == true && block25PercentIfQuick2 != 0) {
            opp = analyse.getOpponentPlayerByPosition(block25PercentIfQuick2);
            if (opp != null) {
                n++;
                opponentDefenceSkill += opp.getDEFskill();
            }
        }
        if (n > 1) {
            opponentDefenceSkill /= n;
        }

        getQuickPassEvent( position, IMatchRoleID.rightWinger, opponentDefenceSkill, goalProbabilityFactor, involvedOpponents);
        getQuickPassEvent( position, IMatchRoleID.leftWinger, opponentDefenceSkill, goalProbabilityFactor, involvedOpponents);
        getQuickPassEvent( position, IMatchRoleID.rightForward, opponentDefenceSkill, goalProbabilityFactor, involvedOpponents);
        getQuickPassEvent( position, IMatchRoleID.centralForward, opponentDefenceSkill, goalProbabilityFactor, involvedOpponents);
        getQuickPassEvent( position, IMatchRoleID.leftForward, opponentDefenceSkill, goalProbabilityFactor, involvedOpponents);
    }

    private void getQuickPassEvent(MatchRoleID position, int passReceiver, double opponentDefenceSkill, double goalProbabilityFactor, HashSet<IMatchRoleID> involvedOpponents)
    {
        if ( passReceiver == position.getId()) return;
        Player scorer = analyse.getPlayerByPosition(passReceiver);
        if ( scorer == null ) return;
        Player p = analyse.getPlayer(position.getSpielerId());

        // Compare p passing skill with opponent defence skill
        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(
                position,
                SpecialEventType.QUICK_PASS,
                1, 20, -10,
                min(20, p.getPSskill()) - min(20, opponentDefenceSkill));
        if ( se != null){
            se.setInvolvedOpponentPositions(involvedOpponents);
            se.addInvolvedPosition(analyse.getPosition(passReceiver));
            se.setGoalProbability(goalProbabilityFactor * se.getChanceCreationProbability() * analyse.getGoalProbability(analyse.getPosition(passReceiver)));
            analyse.addSpecialEventPrediction(se);
        }
    }

    private void getQuickScoresEvent( MatchRoleID position, int pos, double goalProbabilityFactor, HashSet<IMatchRoleID> involvedOpponents) {
        Player opp = analyse.getOpponentPlayerByPosition(pos);
        if (opp == null) return;
        Player p = analyse.getPlayer(position.getSpielerId());

        // Compare p scoring skill with opponent defence skill
        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(
                position,
                SpecialEventType.QUICK_SCORES,
                .5, 10, -10,
                p.getSCskill() - opp.getDEFskill());
        if ( se != null ) {
            se.setInvolvedOpponentPositions(involvedOpponents);
            se.addInvolvedOpponentPosition(analyse.getOpponentPosition(pos));
            se.setGoalProbability(goalProbabilityFactor * se.getChanceCreationProbability() * analyse.getGoalProbability(position));
            analyse.addSpecialEventPrediction(se);
        }
    }

}