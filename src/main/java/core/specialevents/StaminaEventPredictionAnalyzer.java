package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

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
        Player p = analyse.getPlayer(position.getSpielerId());
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
