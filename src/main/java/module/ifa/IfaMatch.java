package module.ifa;

import java.util.Date;

public class IfaMatch {

	private int matchId;
	private int matchTyp;
	private Date playedDate;
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
	
	public Date getPlayedDate() {
		// be defensive, java.util.Date is not immutable
		return new Date(this.playedDate.getTime());
	}

	public void setPlayedDate(Date playedDate) {
		// be defensive, java.util.Date is not immutable
		this.playedDate = new Date(playedDate.getTime());
	}

	/**
	 * @deprecated use {@link IfaMatch#getPlayedDate()} instead
	 * @return
	 */
	@Deprecated
	public final String getPlayedDateString() {
		return playedDateString;
	}

	/**
	 * @deprecated use {@link IfaMatch#setPlayedDate(Date)} instead
	 * @param playedDateString
	 */
	@Deprecated
	public final void setPlayedDateString(String playedDateString) {
		this.playedDateString = playedDateString;
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
