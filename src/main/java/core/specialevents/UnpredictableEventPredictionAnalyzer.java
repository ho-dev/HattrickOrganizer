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
    public List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {

        this.analyse = analyse;

        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        if (p.hasSpeciality(Speciality.UNPREDICTABLE)) {
            switch (position.getId()) {
                case IMatchRoleID.keeper:
                    getUnpredictableLongPass(ret, position);
                    break;
                case IMatchRoleID.rightBack:
                    getUnpredictableLongPass(ret, position);
                    getUnpredictableMistake(ret, position);
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.leftBack, IMatchRoleID.leftCentralDefender, 0);
                    break;
                case IMatchRoleID.leftCentralDefender:
                case IMatchRoleID.middleCentralDefender:
                case IMatchRoleID.rightCentralDefender:
                    getUnpredictableLongPass(ret, position);
                    getUnpredictableMistake(ret, position);
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender);
                    break;
                case IMatchRoleID.leftBack:
                    getUnpredictableLongPass(ret, position);
                    getUnpredictableMistake(ret, position);
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.rightCentralDefender, IMatchRoleID.rightBack, 0);
                    break;

                case IMatchRoleID.rightInnerMidfield:
                case IMatchRoleID.centralInnerMidfield:
                case IMatchRoleID.leftInnerMidfield:
                    getUnpredictableMistake(ret, position);
                    getUnpredictableScores(ret, position, 0);
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender);
                    break;

                case IMatchRoleID.rightWinger:
                    getUnpredictableScores(ret, position, 3);
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.leftCentralDefender, IMatchRoleID.leftBack,0);
                    getUnpredictableOwnGoal(ret, position);
                    break;
                case IMatchRoleID.leftWinger:
                    getUnpredictableScores(ret, position, 3);
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.rightCentralDefender, IMatchRoleID.rightBack,0);
                    getUnpredictableOwnGoal(ret, position);
                    break;

                case IMatchRoleID.rightForward:
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.leftCentralDefender, IMatchRoleID.leftBack,0);
                    getUnpredictableOwnGoal(ret, position);
                    break;
                case IMatchRoleID.leftForward:
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.rightCentralDefender, IMatchRoleID.rightBack,0);
                    getUnpredictableOwnGoal(ret, position);
                    break;
                case IMatchRoleID.centralForward:
                    getUnpredictableSpecialAction(ret, position, IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender);
                    getUnpredictableOwnGoal(ret, position);
                    break;
                default:
                    break;
            }
        }
        return ret;
    }

    private void getUnpredictableOwnGoal(Vector<SpecialEventsPrediction> ret, MatchRoleID position) {
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
            ret.add(se);
        }
    }

    private void getUnpredictableSpecialAction(Vector<SpecialEventsPrediction> ret, MatchRoleID position, int leftCentralDefender, int middleCentralDefender, int rightCentralDefender) {
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
            getPassReceivers(ret, se);
        }
    }

    private void getUnpredictableScores(Vector<SpecialEventsPrediction> ret, MatchRoleID position, double wingerScoreBonus) {
        SpecialEventsPrediction scorerSpecialEvent = this.analyse.getScorerSpecialEvent(position,
                SpecialEventType.UNPREDICTABLE_SCORES,
                wingerScoreBonus);
        if ( scorerSpecialEvent != null){
            ret.add(scorerSpecialEvent);
        }
    }

    private void getUnpredictableMistake(Vector<SpecialEventsPrediction> ret, MatchRoleID position) {
        getUnpredictableMistake(ret, position, IMatchRoleID.leftWinger, 3);
        getUnpredictableMistake(ret, position, IMatchRoleID.rightWinger, 3);
        getUnpredictableMistake(ret, position, IMatchRoleID.leftForward,0);
        getUnpredictableMistake(ret, position, IMatchRoleID.centralForward,0);
        getUnpredictableMistake(ret, position, IMatchRoleID.rightForward,0);
    }

    private void getUnpredictableMistake(Vector<SpecialEventsPrediction> ret, MatchRoleID position, int opponentPos, double wingerScoreBonus) {
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
            }
        }
    }
    
    private void getUnpredictableLongPass(Vector<SpecialEventsPrediction> ret, MatchRoleID position) {
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
                    getPassReceivers(ret, se);
                }
            }
        }
    }

    private void getPassReceivers(Vector<SpecialEventsPrediction> ret, SpecialEventsPrediction se) {
        final double wingerScoreBonus = 3;
        getPassReceiver(ret, se, IMatchRoleID.rightWinger, wingerScoreBonus);
        getPassReceiver(ret, se, IMatchRoleID.leftWinger, wingerScoreBonus);
        getPassReceiver(ret, se, IMatchRoleID.leftForward, 0);
        getPassReceiver(ret, se, IMatchRoleID.centralForward, 0);
        getPassReceiver(ret, se, IMatchRoleID.rightForward, 0);
    }

    private void getPassReceiver(Vector<SpecialEventsPrediction> ret, SpecialEventsPrediction se, int pos, double wingerScoreBonus) {
        Player passReceiver = this.analyse.getPlayerByPosition(pos);
        if (passReceiver != null) {
            double score = analyse.getGoalProbability(analyse.getPosition(pos), wingerScoreBonus);
            SpecialEventsPrediction e = new SpecialEventsPrediction(se); // make a copy
            e.setGoalProbability(e.getChanceCreationProbability() * score);
            e.addInvolvedPosition(analyse.getPosition(pos));
            ret.add(e);
        }
    }
}