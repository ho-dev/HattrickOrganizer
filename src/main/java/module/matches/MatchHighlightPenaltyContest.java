package module.matches;

import java.util.List;

public record MatchHighlightPenaltyContest(MatchScore penalitiesScored, List<MatchHighlight> highlights) {
}
