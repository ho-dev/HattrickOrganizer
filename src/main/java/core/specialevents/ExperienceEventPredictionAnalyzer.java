package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.Lineup;

import java.util.List;
import java.util.Vector;

import static java.lang.Math.abs;


/*
 Experienced Forward Scores

 If your forward has great experience compared to the average experience of the opponents, and also high enough scoring
 to outwit the keeper, you might end up with a goal.

 Example: If your forward has 10 experience against an average of 8 and his scoring is 13 against a 15 goalkeeper, your
 chances are 33% to score. But if the experience was at 15, your chances increase to 38%.

 Inexperienced Defender

 If your defender has low experience compared to the average experience of the opposing team, an opposing player may be
 able to win the ball and then get a crack at scoring.

 Example: A defender with 10 experience and 15 defending against a forward with 13 scoring (against a 15 keeper) and an
 average experience of 12, will give the opponent 20% to score. But if you increase your defender experience to 12, you
 will drop this to 16%.

 */
public class ExperienceEventPredictionAnalyzer implements ISpecialEventPredictionAnalyzer {

    private SpecialEventsPredictionManager.Analyse analyse;

    @Override
    public void analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {

        this.analyse = analyse;

        //double experienceAverage = getExperienceAverage();
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        SpecialEventsPrediction se=null;
        switch (position.getId()){
            /*
            case IMatchRoleID.leftCentralDefender:
            case IMatchRoleID.middleCentralDefender:
            case IMatchRoleID.rightCentralDefender:
                 se = SpecialEventsPrediction.createIfInRange(
                        position,
                        SpecialEventType.EXPERIENCE,
                        -.5,-5,0,
                        p.getErfahrung() - experienceAverage);
                 if ( se != null){
                     se.setGoalProbability(.4*se.getChanceCreationProbability());
                 }
                break;


             */

            case IMatchRoleID.leftForward:
            case IMatchRoleID.centralForward:
            case IMatchRoleID.rightForward:
                for  ( int i = IMatchRoleID.rightCentralDefender; i<= IMatchRoleID.leftCentralDefender; i++){
                    getExperienceEvents(position, i);
                }
                break;
        }
    }

    private void getExperienceEvents(MatchRoleID position, int i) {
        Player oppDefender = analyse.getOpponentPlayerByPosition(i);
        if ( oppDefender!=null && position.getSpielerId() != 0){
            Player p = analyse.getPlayer(position.getSpielerId());
            SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(
                    position,
                    SpecialEventType.EXPERIENCE,
                    .5, 10, 5,
                    p.getErfahrung() - oppDefender.getErfahrung());
            if ( se != null){
                se.addInvolvedOpponentPosition(analyse.getOpponentPosition(i));
                se.setGoalProbability(analyse.getGoalProbability(position)*se.getChanceCreationProbability());
                analyse.addSpecialEventPrediction(se);
            }
        }
    }
/*
    private double getExperienceAverage() {
        Lineup oppLineup = analyse.getOpponentLineup();
        int sum=0;
        int n=0;
        for (int i = MatchRoleID.keeper; i<= MatchRoleID.leftForward; i++){
            Player p = analyse.getPlayerByPosition(i);
            if ( p != null){
                sum += p.getErfahrung();
                n++;
            }
        }
        if ( n > 1){
            return (double)sum/n;
        }
        return (double)sum;
    }*/

}
