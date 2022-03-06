package module.specialEvents;

import core.model.match.MatchEvent;
import core.model.enums.MatchType;
import core.model.match.Weather;
import core.util.HODateTime;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Date;

public class Match {

	private HODateTime matchDate;
	private int matchId;
	private int hostingTeamTactic;
	private int visitingTeamTactic;
	private int hostingTeamId;
	private int visitingTeamId;
	private Weather weather;
	private String hostingTeam;
	private String visitingTeam;
	private String matchResult;
	private MatchEvent matchHighlight;
	private MatchType matchType;

	public HODateTime getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(HODateTime matchDate) {
		this.matchDate = matchDate;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public int getHostingTeamTactic() {
		return hostingTeamTactic;
	}

	public void setHostingTeamTactic(int hostingTeamTactic) {
		this.hostingTeamTactic = hostingTeamTactic;
	}

	public int getVisitingTeamTactic() {
		return visitingTeamTactic;
	}

	public void setVisitingTeamTactic(int visitingTeamTactic) {
		this.visitingTeamTactic = visitingTeamTactic;
	}

	public String getHostingTeam() {
		return hostingTeam;
	}

	public void setHostingTeam(String hostingTeam) {
		this.hostingTeam = hostingTeam;
	}

	public String getVisitingTeam() {
		return visitingTeam;
	}

	public void setVisitingTeam(String visitingTeam) {
		this.visitingTeam = visitingTeam;
	}

	public String getMatchResult() {
		return matchResult;
	}

	public boolean isWinningTeam(int iTeamID) {
		Integer iWinningTeamID = getWinningTeamID();
		if (iWinningTeamID == null) return false;
		return iWinningTeamID == iTeamID;
	}

	public boolean isWinningTeam(String teamName) {
		int iTeamID = hostingTeamId;
		if (visitingTeam.equals(teamName)) iTeamID = visitingTeamId;
		else if (! hostingTeam.equals(teamName)) return false; // should never occur
		return isWinningTeam(iTeamID);
	}

	public @Nullable Integer getWinningTeamID() {
		String[] aResult = matchResult.split("-");
		int homeScore = Integer.parseInt(aResult[0].trim());
		int visitorScore = Integer.parseInt(aResult[1].trim());
		if (homeScore > visitorScore) return hostingTeamId;
		else if (homeScore < visitorScore) return visitingTeamId;
		return null;
	}

	public @Nullable String getWinningTeamName() {
		Integer iWinningTeamID = getWinningTeamID();
		if (iWinningTeamID == null) return null;
		else if (iWinningTeamID == hostingTeamId) return hostingTeam;
		else return hostingTeam;
	}

	public void setMatchResult(String matchResult) {
		this.matchResult = matchResult;
	}

	public MatchEvent getMatchHighlight() {
		return matchHighlight;
	}

	public void setMatchHighlight(MatchEvent matchHighlight) {
		this.matchHighlight = matchHighlight;
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(@Nullable Weather weather) {
		this.weather = weather;
	}

	public int getHostingTeamId() {
		return hostingTeamId;
	}

	public void setHostingTeamId(int hostingTeamId) {
		this.hostingTeamId = hostingTeamId;
	}

	public int getVisitingTeamId() {
		return visitingTeamId;
	}

	public void setVisitingTeamId(int visitingTeamId) {
		this.visitingTeamId = visitingTeamId;
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}

	public URI getHTURL(){
		if (matchType.isOfficial()) {
			return URI.create(String.format("http://www.hattrick.org/Club/Matches/Match.aspx?matchID=%s", matchId));
		}
		return URI.create(String.format("https://www80.hattrick.org/Club/Matches/Match.aspx?matchID=%s&SourceSystem=HTOIntegrated", matchId));
	}

}
