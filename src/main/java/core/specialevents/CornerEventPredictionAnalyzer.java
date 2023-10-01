package core.specialevents;

/*

Corner to Anyone
 You need a good set pieces taker and your offensive indirect set pieces will be tested against the defensive indirect
 set pieces of your opponent. If the ball receiver has good enough scoring skills to pass the keeper, then it’s a goal.

 Example: Your set piecer needs to collaborate with the rest of your team. This means if your set piecer is not good
 enough, it can reduce your indirect free set pieces during corners or increase it. For example, if your set piecer has
 8 set pieces and your indirect set pieces of your team is at 10, you will have a ~6% drop during the corner. But if he
 is 12 at set pieces, you will have ~4.5% boost on them.

 An example, let’s say your set piecer is 12 and your indirect attacking set pieces at 10. The indirect defending set
 pieces of your opponent is at 11. The ball receiver has 13 scoring and the keeper has 15 goalkeeping. Then you have
 76% chances to score. But if your set piecer is just 6 on skill level, your chances drop to 69%.

Corner to Head
 Same as the previous one, but instead of a good scoring player, you just need to win the fight between the headers of
 the two teams.

 Example: The penalty/boost on indirect attacking set pieces of your kicker is the same as the Corner to Anyone.
 However, in this event, it doesn’t matter if the ball receiver is good at scoring, but the total number of the headers
 in your team against your opponent’s headers. For example, if you have a 12 set piecer with ind. attacking set pieces
 at 10 vs 11 of your opponent, and you have just 1 header against 4, your chances to score is 4%. But if you increase
 your headers to 6 vs 4, your chances are 61%

 Info: Both events might lead to a yellow card for the offensive team, but if your player is already carded he will be
 extra careful so the risk of a second yellow is lower than it would otherwise have been.


► IFK:
off IFK = 0.5*mAtt + 0.3*mSP + 0.09*SPshooter
def IFK = 0.4*mDef + .3*mSP + 0.1*SPgk + 0.08*GKgk

mAtt: outfield players attack skill average
mDEf: outfield players defence skill average

 */

import core.constants.player.Specialty;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

public class CornerEventPredictionAnalyzer  implements ISpecialEventPredictionAnalyzer {
    private SpecialEventsPredictionManager.Analyse analyse;

    @Override
    public void analyzePosition(SpecialEventsPredictionManager.Analyse analyse, MatchRoleID position) {

        int kicker = analyse.getLineup().getKicker();
        int pid = position.getPlayerId();
        if ( pid == kicker) {
            this.analyse = analyse;

            double offIFK = analyse.getRatingIndirectSetPiecesAtt();
            double oppDefIFK = analyse.getOpponentRatingIndirectSetPiecesDef();

            for ( int i = IMatchRoleID.rightBack; i<= IMatchRoleID.leftForward; ++i){
                // for each pass receiver
                MatchRoleID passReceiver = analyse.getPosition(i);
                int id = passReceiver.getPlayerId();
                if ( id != 0 &&  id != position.getPlayerId()){
                    getCornerEvents( position, passReceiver, offIFK, oppDefIFK);
                }
            }
        }
    }

    private void getCornerEvents(MatchRoleID setPiecesTaker, MatchRoleID passReceiver, double offIFK, double oppDefIFK) {
        Player p = analyse.getPlayer(setPiecesTaker.getPlayerId());
        Player scorer = analyse.getPlayer(passReceiver.getPlayerId());
        if ( scorer.hasSpecialty(Specialty.HEAD)) {
            SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(setPiecesTaker, SpecialEventType.CORNER_HEAD,
                    .3, 10, -10,
                    .5*p.getSPskill() + offIFK - oppDefIFK);
            if ( se != null){
                se.setGoalProbability(se.getChanceCreationProbability()*analyse.getGoalProbability(passReceiver,3));
                se.setInvolvedPosition(passReceiver);
                analyse.addSpecialEventPrediction(se);
            }
        }

        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(setPiecesTaker, SpecialEventType.CORNER,
                .3, 10, -10,
                .5*p.getSPskill() + offIFK - oppDefIFK);
        if ( se != null){
            se.setGoalProbability(se.getChanceCreationProbability()*analyse.getGoalProbability(passReceiver));
            se.setInvolvedPosition(passReceiver);
            analyse.addSpecialEventPrediction(se);
        }
    }
}
