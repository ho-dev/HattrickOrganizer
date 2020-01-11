package module.specialEvents;

import core.model.match.MatchEvent;

public class MatchRow {

	private Match match;
	private MatchEvent matchHighlight;
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

	public MatchEvent getMatchHighlight() {
		return matchHighlight;
	}

	public void setMatchHighlight(MatchEvent matchHighlight) {
		this.matchHighlight = matchHighlight;
	}

	public int getMatchCount() {
		return matchCount;
	}

	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}
}
