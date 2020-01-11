package module.specialEvents;

import core.model.match.MatchEvent;
import core.model.match.MatchType;
import core.model.match.Weather;

import java.util.Date;

public class Match {

	private Date matchDate;
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

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
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

	public void setWeather(Weather weather) {
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
}
