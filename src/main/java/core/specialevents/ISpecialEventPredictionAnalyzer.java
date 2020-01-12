package core.specialevents;

import core.model.player.MatchRoleID;

import java.util.List;

public interface ISpecialEventPredictionAnalyzer {

    enum SpecialEventType {
        CORNER,
        CORNER_HEAD,
        EXPERIENCE,
        QUICK_PASS,
        QUICK_SCORES,
        PNF,
        PDIM,
        TECHNICAL_HEAD,
        UNPREDICTABLE,              // special action
        UNPREDICTABLE_LONGPASS,
        UNPREDICTABLE_MISTAKE,
        UNPREDICTABLE_OWNGOAL,
        UNPREDICTABLE_SCORES,
        WINGER_SCORER,
        WINGER_HEAD
    }

    void analyzePosition(SpecialEventsPredictionManager.Analyse analyse,  MatchRoleID position);
}
