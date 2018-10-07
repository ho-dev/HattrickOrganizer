package core.model;

public class WorldDetailLeague {
	private int leagueId;
	private int countryId;
	private String countryName;
	private int activeUsers;
	
	public WorldDetailLeague(){
		
	}
	
	public WorldDetailLeague(int leagueId, String countryName){
		this.leagueId = leagueId;
		this.countryId = leagueId;
		this.countryName = countryName;
	}
	
	public WorldDetailLeague(int leagueId, int countryId, String countryName){
		this.leagueId = leagueId;
		this.countryId = countryId;
		this.countryName = countryName;
	}
	
	public final int getLeagueId() {
		return leagueId;
	}
	public final void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	public final int getCountryId() {
		return countryId;
	}
	public final void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	public final String getCountryName() {
		return countryName;
	}
	public final void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public final int getActiveUsers() {
		return activeUsers;
	}
	public final void setActiveUsers(int activeUsers) {
		this.activeUsers = activeUsers;
	}

	@Override
	public String toString(){
		return getCountryName();
	}
}
