package module.specialEvents;

import core.model.match.MatchHighlight;

public class MatchRow {

	private Match match;
	private MatchHighlight matchHighlight;
	private boolean isMatchHeaderLine;
	private int matchCount;	

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public boolean isMatchHeaderLine() {
		return isMatchHeaderLine;
	}

	public void setMatchHeaderLine(boolean isMatchHeaderLine) {
		this.isMatchHeaderLine = isMatchHeaderLine;
	}

	public MatchHighlight getMatchHighlight() {
		return matchHighlight;
	}

	public void setMatchHighlight(MatchHighlight matchHighlight) {
		this.matchHighlight = matchHighlight;
	}

	public int getMatchCount() {
		return matchCount;
	}

	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}
}
