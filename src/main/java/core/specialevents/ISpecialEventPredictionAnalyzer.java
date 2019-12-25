package core.specialevents;

import core.model.player.MatchRoleID;

import java.util.List;

public interface ISpecialEventPredictionAnalyzer {

    enum SpecialEventType {
        EXPERIENCE,
        PNF,
        PDIM,
        UNPREDICTABLE,
        WINGER_SCORER,
        WINGER_HEAD
    }

    List<SpecialEventsPrediction> analyzePosition(MatchRoleID position);
}
