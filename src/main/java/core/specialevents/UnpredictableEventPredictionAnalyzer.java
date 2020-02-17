package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Vector;

public class UnpredictableEventPredictionAnalyzer implements  ISpecialEventPredictionAnalyzer {
    private SpecialEventsPredictionManager.Analyse analyse;


    // Unpredictable Long Pass
    // The keeper, wing backs and central defenders can initiate this event. The passing skill of those need to win
    // against the defense of the Inner Midfielders and again the pass receiver (winger or forward) needs some scoring.
    //
    // Example: Your defender has 8 passing and the sum of the Inner Midfielders defending is 35. The forward has 13
    // scoring and the keeper is 15. The success rate is 40% (42% for the winger). But if the passing was 13 instead,
    // the same chance for the forward would be 65%.
    //
    // Unpredictable Scores on His Own
    // One on one with the goalkeeper. This event is for Wingers, Inner Midfielders and Forwards. Keep in mind. A lost
    // chance can create a counter attack for your opponent.
    //
    // Example: Let’s say your Forward has 10 scoring and the opponent keeper 15 goalkeeping. The success ratio will be
    // 78% (86% for wingers)
    //
    // Unpredictable Special Action
    // Any unpredictable player (except keepers) may read their opponents, dribble, and find an opening pass. The player
    // then receiving the pass would be a Winger or Forward. Same as before, a lost chance can lead to a counter attack
    // for your opponent.
    //
    // Example: Your Wing back with 9 passing and 7 experience against the average of defending and experience of the
    // defenders from the opposite site. 13 and 8 for the example. And the ball receiver as a Forward with 12 scoring
    // and keeper with 15 goalkeeping. The success rate will be 62%.
    //
    // Unpredictable Mistake
    // Wing Backs, Defenders and Inner Midfielders can have a bad day and make a fatal mistake. An opposing Winger or
    // Forward can take advantage of this.
    //
    // Example: Your unpredictable defender has 13 Defending and 5 experience, while your opponent forward has 15
    // scoring and 8 experience. The chance to score is ~7% (8% for a Winger)
    //
    // Unpredictable Own Goal
    // A new event where your wingers and forwards may suffer. If they have a low passing skill level, they can make a
    // wrong move towards to your defense and your keeper might not be able to stop it.
    //
    // Example: If your Winger has 8 passing and your Keeper has 15 goalkeeping then the chances to score an own goal
    // are about 31% but if you increase your passing to 15 then you drop it to 18%

    @Override
    public void analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {

        this.analyse = analyse;

        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        if (p.hasSpeciality(Speciality.UNPREDICTABLE)) {
            switch (position.getId()) {
                case IMatchRoleID.keeper:
                    getUnpredictableLongPass( position);
                    break;
                case IMatchRoleID.rightBack:
                    getUnpredictableLongPass( position);
                    getUnpredictableMistake( position);
                    getUnpredictableSpecialAction( position, IMatchRoleID.leftBack, IMatchRoleID.leftCentralDefender, 0);
                    break;
                case IMatchRoleID.leftCentralDefender:
                case IMatchRoleID.middleCentralDefender:
                case IMatchRoleID.rightCentralDefender:
                    getUnpredictableLongPass(position);
                    getUnpredictableMistake( position);
                    getUnpredictableSpecialAction( position, IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender);
                    break;
                case IMatchRoleID.leftBack:
                    getUnpredictableLongPass(position);
                    getUnpredictableMistake( position);
                    getUnpredictableSpecialAction( position, IMatchRoleID.rightCentralDefender, IMatchRoleID.rightBack, 0);
                    break;

                case IMatchRoleID.rightInnerMidfield:
                case IMatchRoleID.centralInnerMidfield:
                case IMatchRoleID.leftInnerMidfield:
                    getUnpredictableMistake(position);
                    getUnpredictableScores( position, 0);
                    getUnpredictableSpecialAction( position, IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender);
                    break;

                case IMatchRoleID.rightWinger:
                    getUnpredictableScores( position, 3);
                    getUnpredictableSpecialAction( position, IMatchRoleID.leftCentralDefender, IMatchRoleID.leftBack,0);
                    getUnpredictableOwnGoal(position);
                    break;
                case IMatchRoleID.leftWinger:
                    getUnpredictableScores( position, 3);
                    getUnpredictableSpecialAction( position, IMatchRoleID.rightCentralDefender, IMatchRoleID.rightBack,0);
                    getUnpredictableOwnGoal( position);
                    break;

                case IMatchRoleID.rightForward:
                    getUnpredictableSpecialAction( position, IMatchRoleID.leftCentralDefender, IMatchRoleID.leftBack,0);
                    getUnpredictableOwnGoal(position);
                    break;
                case IMatchRoleID.leftForward:
                    getUnpredictableSpecialAction( position, IMatchRoleID.rightCentralDefender, IMatchRoleID.rightBack,0);
                    getUnpredictableOwnGoal(position);
                    break;
                case IMatchRoleID.centralForward:
                    getUnpredictableSpecialAction( position, IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender);
                    getUnpredictableOwnGoal(position);
                    break;
                default:
                    break;
            }
        }
    }

    private void getUnpredictableOwnGoal( MatchRoleID position) {
        Player p = this.analyse.getPlayer(position.getSpielerId());
        Player keeper = this.analyse.getPlayerByPosition(IMatchRoleID.keeper);
        double gkSkill=0;
        if (keeper != null){
            gkSkill = keeper.getGKskill();
        }

        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position,
                SpecialEventType.UNPREDICTABLE_OWNGOAL,
                -.1, 10, -10,
                20 - p.getPSskill() - gkSkill
        );

        if ( se != null){
            analyse.addSpecialEventPrediction(se);
        }
    }

    private void getUnpredictableSpecialAction( MatchRoleID position, int leftCentralDefender, int middleCentralDefender, int rightCentralDefender) {
        // Calculate opponent Defence skill
        Player p = this.analyse.getPlayer(position.getSpielerId());
        double opponentDefenceSkill = 0;
        double opponentExperience = 0;
        int n = 0;
        Player opp = analyse.getOpponentPlayerByPosition(leftCentralDefender);
        if (opp != null) {
            n++;
            opponentDefenceSkill += opp.getDEFskill();
            opponentExperience += opp.getErfahrung();
        }
        opp = analyse.getOpponentPlayerByPosition(middleCentralDefender);
        if (opp != null) {
            n++;
            opponentDefenceSkill += opp.getDEFskill();
            opponentExperience += opp.getErfahrung();
        }
        if (rightCentralDefender != 0) {
            opp = analyse.getOpponentPlayerByPosition(rightCentralDefender);
            if (opp != null) {
                n++;
                opponentDefenceSkill += opp.getDEFskill();
                opponentExperience += opp.getErfahrung();
            }
        }
        if (n > 1) {
            opponentDefenceSkill /= n;
            opponentExperience /= n;
        }

        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position, SpecialEventType.UNPREDICTABLE,
                .5, 10, -10,
                p.getPSskill() + p.getErfahrung() - opponentDefenceSkill - opponentExperience);
        if ( se != null){
            getPassReceivers(se);
        }
    }

    private void getUnpredictableScores( MatchRoleID position, double wingerScoreBonus) {
        SpecialEventsPrediction scorerSpecialEvent = this.analyse.getScorerSpecialEvent(position,
                SpecialEventType.UNPREDICTABLE_SCORES,
                wingerScoreBonus);
        if ( scorerSpecialEvent != null){
            analyse.addSpecialEventPrediction(scorerSpecialEvent);
        }
    }

    private void getUnpredictableMistake( MatchRoleID position) {
        getUnpredictableMistake( position, IMatchRoleID.leftWinger, 3);
        getUnpredictableMistake( position, IMatchRoleID.rightWinger, 3);
        getUnpredictableMistake( position, IMatchRoleID.leftForward,0);
        getUnpredictableMistake( position, IMatchRoleID.centralForward,0);
        getUnpredictableMistake( position, IMatchRoleID.rightForward,0);
    }

    private void getUnpredictableMistake( MatchRoleID position, int opponentPos, double wingerScoreBonus) {
        Player p = this.analyse.getPlayer(position.getSpielerId());
        Player op = this.analyse.getOpponentPlayerByPosition(opponentPos);
        if ( op != null){
            SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position,
                    SpecialEventType.UNPREDICTABLE_MISTAKE,
                    -.5,10, -10,
                    op.getSCskill()+op.getErfahrung()-p.getDEFskill()-p.getErfahrung()
                    );

            if ( se != null){
                MatchRoleID omid = analyse.getOpponentPosition(opponentPos);
                double scores = analyse.getOpponentGoalProbability(omid,wingerScoreBonus);
                se.setGoalProbability(se.getChanceCreationProbability()*scores);
                se.addInvolvedOpponentPosition(omid);
                analyse.addSpecialEventPrediction(se);
            }
        }
    }
    
    private void getUnpredictableLongPass( MatchRoleID position) {
        Player p = this.analyse.getPlayer(position.getSpielerId());
        for (int i = IMatchRoleID.rightInnerMidfield; i <= IMatchRoleID.leftInnerMidfield; i++) {
            Player opp = this.analyse.getOpponentPlayerByPosition(i);
            if (opp != null) {
                // compare pass skill with opponents defence skill
                SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position,
                        SpecialEventType.UNPREDICTABLE_LONGPASS,
                        .1, 10, -10,
                        p.getPSskill() - opp.getDEFskill()
                );

                if (se != null) {
                    se.addInvolvedOpponentPosition(this.analyse.getOpponentPosition(i));
                    getPassReceivers(se);
                }
            }
        }
    }

    private void getPassReceivers( SpecialEventsPrediction se) {
        final double wingerScoreBonus = 3;
        getPassReceiver( se, IMatchRoleID.rightWinger, wingerScoreBonus);
        getPassReceiver( se, IMatchRoleID.leftWinger, wingerScoreBonus);
        getPassReceiver( se, IMatchRoleID.leftForward, 0);
        getPassReceiver( se, IMatchRoleID.centralForward, 0);
        getPassReceiver( se, IMatchRoleID.rightForward, 0);
    }

    private void getPassReceiver( SpecialEventsPrediction se, int pos, double wingerScoreBonus) {
        Player passReceiver = this.analyse.getPlayerByPosition(pos);
        if (passReceiver != null) {
            double score = analyse.getGoalProbability(analyse.getPosition(pos), wingerScoreBonus);
            SpecialEventsPrediction e = new SpecialEventsPrediction(se); // make a copy
            e.setGoalProbability(e.getChanceCreationProbability() * score);
            e.addInvolvedPosition(analyse.getPosition(pos));
            analyse.addSpecialEventPrediction(e);
        }
    }
}