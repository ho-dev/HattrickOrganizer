package module.ifa;

import core.util.HODateTime;

import java.util.Date;

public class IfaMatch {

	private int matchId;
	private int matchTyp;
	private HODateTime playedDate;
	private String playedDateString;
	private int homeTeamId;
	private int awayTeamId;
	private int homeLeagueId;
	private int awayLeagueId;
	private int awayTeamGoals;
	private int homeTeamGoals;

	public IfaMatch(int matchTyp){
		this.matchTyp=matchTyp;
	}

	public int getMatchTyp(){
		return matchTyp;
	}

	public final int getMatchId() {
		return matchId;
	}

	public final void setMatchId(int matchId) {
		this.matchId = matchId;
	}
	
	public HODateTime getPlayedDate() {
		return playedDate;
	}

	public void setPlayedDate(HODateTime playedDate) {
		// be defensive, java.util.Date is not immutable
		this.playedDate = playedDate;
	}

	public final int getHomeTeamId() {
		return homeTeamId;
	}

	public final void setHomeTeamId(int homeTeamId) {
		this.homeTeamId = homeTeamId;
	}

	public final int getAwayTeamId() {
		return awayTeamId;
	}

	public final void setAwayTeamId(int awayTeamId) {
		this.awayTeamId = awayTeamId;
	}

	public final int getHomeLeagueId() {
		return homeLeagueId;
	}

	public final void setHomeLeagueId(int homeLeagueId) {
		this.homeLeagueId = homeLeagueId;
	}

	public final int getAwayLeagueId() {
		return awayLeagueId;
	}

	public final void setAwayLeagueId(int awayLeagueId) {
		this.awayLeagueId = awayLeagueId;
	}

	public final int getAwayTeamGoals() {
		return awayTeamGoals;
	}

	public final void setAwayTeamGoals(int awayTeamGoals) {
		this.awayTeamGoals = awayTeamGoals;
	}

	public final int getHomeTeamGoals() {
		return homeTeamGoals;
	}

	public final void setHomeTeamGoals(int homeTeamGoals) {
		this.homeTeamGoals = homeTeamGoals;
	}
}
