package core.specialevents;

import core.model.player.MatchRoleID;

import java.util.List;

public interface ISpecialEventPredictionAnalyzer {

    enum SpecialEventType {
        EXPERIENCE,
        PNF,
        PDIM,
        UNPREDICTABLE,              // special action
        UNPREDICTABLE_LONGPASS,
        UNPREDICTABLE_MISTAKE,
        UNPREDICTABLE_OWNGOAL,
        UNPREDICTABLE_SCORES,
        WINGER_SCORER,
        WINGER_HEAD,
        QUICK_PASS,
        QUICK_SCORES,
        TECHNICAL_HEAD
    }

    List<SpecialEventsPrediction> analyzePosition(SpecialEventsPredictionManager.Analyse analyse,  MatchRoleID position);
}
