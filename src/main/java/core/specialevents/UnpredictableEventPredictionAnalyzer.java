package core.specialevents;

import core.constants.player.Speciality;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Vector;

public class UnpredictableEventPredictionAnalyzer implements  ISpecialEventPredictionAnalyzer {
    public static final String eventName = "Unpredictable";
    private SpecialEventsPredictionManager theManager = null;

    public UnpredictableEventPredictionAnalyzer(SpecialEventsPredictionManager specialEventsPredictionManager) {
        theManager = specialEventsPredictionManager;
    }


    // Spieler können ihre Passspiel-Fähigkeit für überraschende Steilpässe einsetzen und ihre Torschuss-Fähigkeit,
    // um den Ball abzufangen. Ihre Unberechenbarkeit kann zudem zu einer Torgelegenheit aus eigentlich aussichtsloser
    // Position führen. Wenn ein unberechenbarer Verteidiger oder zentraler Mittelfeldspieler nur über eine recht
    // niedrige Verteidigungs-Fähigkeit verfügt, kann es passieren, dass ein unvorsichtiger Pass von ihnen beim Gegner
    // landet – und sich diesem eine gute Einschussmöglichkeit eröffnet.
    @Override
    public List<SpecialEventsPrediction> analyzePosition(MatchRoleID position) {

        Vector<SpecialEventsPrediction> ret = new Vector<SpecialEventsPrediction>();
        int id = position.getSpielerId();
        Player p = theManager.getPlayer(id);
        Speciality speciality = Speciality.values()[p.getPlayerSpecialty()];
        if (speciality.equals(Speciality.UNPREDICTABLE)) {

            SpecialEventsPrediction se = SpecialEventsPrediction.createIfInRange(
                    position,
                    eventName,
                    .5, 0, 20, 10,
                    Math.max(p.getPSskill(), p.getSCskill()));

            if (se != null) {
                ret.add(se);
            }

            switch (position.getId()) {
                case IMatchRoleID.rightBack:
                case IMatchRoleID.leftCentralDefender:
                case IMatchRoleID.middleCentralDefender:
                case IMatchRoleID.rightCentralDefender:
                case IMatchRoleID.leftBack:
                case IMatchRoleID.centralInnerMidfield:
                case IMatchRoleID.leftInnerMidfield:
                case IMatchRoleID.rightInnerMidfield:
                    se = SpecialEventsPrediction.createIfInRange(position, eventName,
                            -.5, 0, 0, 4,
                            p.getDEFskill());
                    if (se != null) {
                        ret.add(se);
                    }

                    break;
            }
        }
        return ret;
    }
}
