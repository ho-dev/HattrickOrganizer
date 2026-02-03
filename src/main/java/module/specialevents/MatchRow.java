package module.specialevents;

import core.model.match.MatchEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class MatchRow {

    private Match match;
    private MatchEvent matchHighlight;
    private boolean matchHeaderLine;
    private int matchCount;

    public Optional<MatchEvent> getMatchHighlight() {
        return Optional.ofNullable(matchHighlight);
    }
}
