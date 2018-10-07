package core.file.xml;

public class TeamInfo {

	private int teamId;
	private String name;
	private String country;
	private String league;
	private int leagueId;
	private String currencyRate;
	private String currencyName;
	private boolean primaryTeam;
	
	public int getTeamId() {
		return teamId;
	}
	
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getLeague() {
		return league;
	}
	
	public void setLeague(String league) {
		this.league = league;
	}

	public int getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public String getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(String currencyRate) {
		this.currencyRate = currencyRate;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public boolean isPrimaryTeam() {
		return primaryTeam;
	}

	public void setPrimaryTeam(boolean primaryTeam) {
		this.primaryTeam = primaryTeam;
	}

	
}
