package module.matches;

import java.util.Optional;

public record MatchHighlights(MatchHighlightsRegularAndExtraTime regularAndExtraTime,
                              MatchHighlightPenaltyContest penaltyContest) {

    public Optional<MatchHighlightPenaltyContest> getPenaltyContest() {
        return Optional.ofNullable(penaltyContest);
    }
}
