package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

/*
 Tired Defender Mistake

 During the second half only, if one of your defenders have far lower stamina than an opponent player, he can take the
 chance to pass you and go 1-1 with the goalkeeper.

 Example: If the opponent player is Forward with 13 scoring against your keeper with 15 goalkeeping, the chance to score
 is 41% (57% if same player is a Winger instead)

 */

public class StaminaEventPredictionAnalyzer implements ISpecialEventPredictionAnalyzer {
    private SpecialEventsPredictionManager.Analyse analyse;

    @Override
    public void analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        this.analyse = analyse;

        switch (position.getId()){

            case IMatchRoleID.leftForward:
            case IMatchRoleID.centralForward:
            case IMatchRoleID.rightForward:
            case IMatchRoleID.rightWinger:
            case IMatchRoleID.leftWinger:
                for  ( int i = IMatchRoleID.rightBack; i<= IMatchRoleID.leftBack; i++){
                    getStaminaEvents(position, i);
                }
                break;
        }
    }

    private void getStaminaEvents(MatchRoleID position, int i) {
        Player p = analyse.getPlayer(position.getPlayerId());
        if ( p != null){
            Player op = analyse.getOpponentPlayerByPosition(i);
            if ( op != null){
                SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position, SpecialEventType.STAMINA,
                        .3, 4, 1,
                        p.getKondition() - op.getKondition());
                if ( se != null ){
                    se.addInvolvedOpponentPosition(analyse.getOpponentPosition(i));
                    se.setGoalProbability(se.getChanceCreationProbability()*analyse.getGoalProbability(position));
                    analyse.addSpecialEventPrediction(se);
                }
            }
        }
    }
}
