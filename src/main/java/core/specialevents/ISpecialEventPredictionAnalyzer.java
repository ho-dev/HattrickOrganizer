package core.specialevents;

import core.model.player.MatchRoleID;

import java.util.List;

public interface ISpecialEventPredictionAnalyzer {

    List<SpecialEventsPrediction> analyzePosition(MatchRoleID position);
}
