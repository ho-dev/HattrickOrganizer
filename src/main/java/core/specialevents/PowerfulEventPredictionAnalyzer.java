package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.substitution.model.MatchOrderType;

import java.util.List;
import java.util.Vector;

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
    public List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {
        this.analyse = analyse;
        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p = analyse.getPlayer(id);
        if (p.hasSpeciality(Speciality.POWERFUL)) {

            switch (position.getId()) {
                case IMatchRoleID.leftForward:
                case IMatchRoleID.centralForward:
                case IMatchRoleID.rightForward:

                    if( position.getTaktik() == IMatchRoleID.NORMAL){
                        getPowerfulNormalForward(ret, position);
                    }
                    break;

                case IMatchRoleID.rightInnerMidfield:
                case IMatchRoleID.centralInnerMidfield:
                case IMatchRoleID.leftInnerMidfield:
                    if ( position.getTaktik() == IMatchRoleID.DEFENSIVE){
                        getSittingMidfielder(ret, position);
                    }
                    break;
            }
        }
        return ret;
    }

    private void getSittingMidfielder(Vector<SpecialEventsPrediction> ret, MatchRoleID position) {


    }

    private void getPowerfulNormalForward(Vector<SpecialEventsPrediction> ret, MatchRoleID position) {
        int overcrowding=0;
        for (  int i = IMatchRoleID.rightForward; i <= IMatchRoleID.leftForward; i++){
            if ( i != position.getId()){
                MatchRoleID mid = this.analyse.getPosition(i);
                if ( mid.getTaktik() == IMatchRoleID.DEFENSIVE){
                    Player p = analyse.getPlayer(mid.getSpielerId());
                    if ( p.hasSpeciality(Speciality.POWERFUL) ) {
                        overcrowding++;
                    }
                }
            }
        }

        double overcrowdingFactor = 1;
        if ( overcrowding == 1){
            overcrowdingFactor = 1.6/2;
        }
        else if ( overcrowding == 2){
            overcrowdingFactor = 2./3.;
        }

        double defence=0;
        for ( int i = IMatchRoleID.rightCentralDefender; i <= IMatchRoleID.leftCentralDefender; i++){
            Player opponentDefender = analyse.getOpponentPlayerByPosition(i);
            if ( opponentDefender != null ) {
                defence+=opponentDefender.getDEFskill();
            }
        }

        Player p = analyse.getPlayer(position.getSpielerId());
        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position, SpecialEventType.PNF,
                0.5, 10, -20,
                p.getPMskill() - defence);

        if ( se != null){
            se.setChanceCreationProbability(se.getChanceCreationProbability()*overcrowdingFactor);
            se.setGoalProbability(analyse.getGoalProbability(position)*se.getChanceCreationProbability());
            ret.add(se);
        }
    }
}
