package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Vector;

// Winger to Anyone
// If your winger manages to break through the defense on his side of the pitch, he might pass the ball to the other
// Winger, Forward, or an Inner Midfielder.
//
// Example: Your Winger has 13 winger skill level and the average defending of the Defenders on the same side is 15.
// The ball receiver has 13 scoring and the keeper has 15 goalkeeping. The chances to score are 69%. But if you increase
// your Winger skill to 16, your chances increase also to 73%
//
// Winger to Head
// Same as the previous event, but if the receiver has a head spec, then it will be far easier for him to score.
//
// Example: Same skills as before. The difference is that the ball receiver is easier to score now since he is a header.
// The chances are 80% and 84% respectively.


public class WingerEventPredictionAnalyzer implements ISpecialEventPredictionAnalyzer {

    private SpecialEventsPredictionManager.Analyse analyse;

    @Override
    public void analyzePosition(SpecialEventsPredictionManager.Analyse analyse,MatchRoleID position) {
        this.analyse = analyse;
        int id = position.getSpielerId();
        if ( id != 0) {
            switch (position.getId()) {
                case IMatchRoleID.leftWinger:
                    getWingerEvents(position, IMatchRoleID.rightBack, IMatchRoleID.rightCentralDefender);
                    break;
                case IMatchRoleID.rightWinger:
                    getWingerEvents( position, IMatchRoleID.leftBack, IMatchRoleID.leftCentralDefender);
                    break;
            }
        }
    }

    private void getWingerEvents( MatchRoleID position, int back, int centralDefender) {
        double defence = 0;
        double ndefence = 0;
        Player oppDefender = this.analyse.getOpponentPlayerByPosition(back);
        if (oppDefender != null) {
            defence += oppDefender.getDEFskill();
            ndefence++;
        }
        oppDefender = this.analyse.getOpponentPlayerByPosition(centralDefender);
        if (oppDefender != null) {
            defence += oppDefender.getDEFskill();
            ndefence++;
        }
        if (ndefence > 1) {
            defence /= ndefence;
        }

        // for each pass receiver
        for ( int i = IMatchRoleID.rightWinger; i<= IMatchRoleID.leftForward; i++){
            if ( i != position.getId()){ // passReceiver is not the winger himself
                getWingerEvents( position, i, defence);
            }
        }
    }

    private void getWingerEvents( MatchRoleID position, int passReceiver, double defence) {
        MatchRoleID scorerId = analyse.getPosition(passReceiver);
        Player scorer = analyse.getPlayer(scorerId.getSpielerId());
        if ( scorer != null) {
            if (scorer.hasSpeciality(Speciality.HEAD)) {
                getWingerEvent( position, scorerId, defence, SpecialEventType.WINGER_HEAD, 3);
            } else {
                getWingerEvent( position, scorerId, defence, SpecialEventType.WINGER_SCORER, 0);
            }
        }
    }

    private void getWingerEvent( MatchRoleID position, MatchRoleID scorer, double defence, SpecialEventType type, double bonus) {
        if (position.getSpielerId() == 0 || scorer.getSpielerId() ==0) return;
        Player winger = analyse.getPlayer(position.getSpielerId());
        SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(position, type,
                .2, 10, -10,
                winger.getWIskill() - defence
        );
        if (se != null) {
            Player involvedPlayer = analyse.getPlayer(scorer.getSpielerId());
            se.setGoalProbability(se.getChanceCreationProbability() * analyse.getGoalProbability(scorer, bonus));
            se.setInvolvedPosition(scorer);
            analyse.addSpecialEventPrediction(se);
        }
    ]
}