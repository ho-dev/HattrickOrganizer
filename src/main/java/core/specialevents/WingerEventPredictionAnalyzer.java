package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Vector;


// Flügelspieler mit ausreichend gutem Flügelspielwert ermöglichen manchmal eine zusätzliche Torgelegenheit,
// die durch einen Mannschaftskameraden (Stürmer oder Flügelspieler) vollendet wird. Wenn dieser zweite Spieler ein
// Kopfballspezialist ist oder über eine genügend gute Torschuss-Fähigkeit verfügt, wird er die Chance mit höherer
// Wahrscheinlichkeit verwerten. Flügel-Kopf und Flügel-Schuss sind dabei zwei separate SE, die also auch beide im
// selben Spiel auftreten können.

public class WingerEventPredictionAnalyzer implements ISpecialEventPredictionAnalyzer {
    public static final String eventNameHead = "Winger-Head";
    public static final String eventNameScorer = "Winger-Scorer";
    private SpecialEventsPredictionManager theManager = null;

    public WingerEventPredictionAnalyzer(SpecialEventsPredictionManager specialEventsPredictionManager) {
        theManager = specialEventsPredictionManager;
    }

    @Override
    public List<SpecialEventsPrediction> analyzePosition(MatchRoleID position) {
        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p  = theManager.getPlayer(id);
        switch (position.getId()) {
            case IMatchRoleID.leftWinger:
            case IMatchRoleID.rightWinger:
                for ( IMatchRoleID imid : theManager.getLineup().getFieldPositions()){
                    MatchRoleID mid = (MatchRoleID) imid;
                    if (mid.getId()==position.getId())continue; // same player again
                    switch (mid.getId()) {
                        case IMatchRoleID.leftWinger:
                        case IMatchRoleID.rightWinger:
                        case IMatchRoleID.leftForward:
                        case IMatchRoleID.centralForward:
                        case IMatchRoleID.rightForward:
                            Player involvedPlayer = theManager.getPlayer(mid.getSpielerId());
                            Speciality speciality = Speciality.values()[involvedPlayer.getPlayerSpecialty()];
                            if (speciality.equals(Speciality.HEAD)) {
                                SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(
                                        position, eventNameHead,
                                        .5, 0, 20, 8,
                                        p.getWIskill());
                                if (se != null) {
                                    se.setInvolvedPosition(mid);
                                    ret.add(se);
                                }
                            }

                            SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(
                                    position, eventNameScorer,
                                    .5,0, 20*20, 8*8,
                                    p.getWIskill()*involvedPlayer.getSCskill());
                            if ( se != null){
                                se.setInvolvedPosition(mid);
                                ret.add(se);
                            }
                            break;
                    }
                }
                break;
        }
        return ret;
    }
}
