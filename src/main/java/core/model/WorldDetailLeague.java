package core.model;

import core.db.AbstractTable;
import core.util.HODateTime;

import java.util.List;

public class WorldDetailLeague  extends AbstractTable.Storable {

	private static final HOConfigurationParameter latestDownload = new HOConfigurationParameter("LatestWorldDetailsDownload", null);

	private int leagueId;
	private int countryId;
	private String countryName;
	private int activeUsers;

	//	CurrencyName : String
	//	The name of the currency in this country.
	private String currencyName;

	//	CurrencyRate : Decimal
	//	Decimal value specifying the relative currency rate to SEK (swedish krona).
	private Double currencyRate;

	//	CountryCode : String
	//	The country code for this country.
	private String countryCode;

	//	DateFormat : String
	//	The date format for users of this country using ISO_8601
	private String dateFormat;

	//	TimeFormat : String
	//  The time format for users of this country using ISO_8601
	private String timeFormat;

	public static List<WorldDetailLeague> allLeagues;

	public WorldDetailLeague() {
	}

	public WorldDetailLeague(int leagueId, int countryId, String countryName) {
		this.leagueId = leagueId;
		this.countryId = countryId;
		this.countryName = countryName;
	}

	/**
	 * Check if world details should be downloaded
	 * World details never changes during a hattrick season, so the download only should be downloaded once per season
	 *
	 * @return boolean, True if world details were not downloaded before or only during a previous season.
	 * False if world details were already downloaded during the current season
	 */
	public static boolean checkWorldDetailsDownload() {
		var model = HOVerwaltung.instance().getModel();
		if (model == null) return false; // Without model do NOT download world Details
		if (allLeagues == null || allLeagues.isEmpty()) return true;
		if (latestDownload.getValue() != null) {
			var latestDownloadSeason = HODateTime.fromHT(latestDownload.getValue()).toHTWeek().season;
			return HODateTime.now().toHTWeek().season > latestDownloadSeason;
		}
		return true;
	}

	public static void setLatestDownloadNow(){
		latestDownload.setValue(HODateTime.now().toHT());
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
	public String toString() {
		return getCountryName();
	}

	public boolean isComplete() {
		return this.currencyRate != null;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public Double getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(String currencyRate) {
		try {
			this.currencyRate = Double.parseDouble(currencyRate.replace(',', '.'));
		} catch (NumberFormatException ignored) {
		}
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	/**
	 * Get the league of user's premier team
	 */
	public static WorldDetailLeague getWorldDetailsLeagueOfPremierTeam() {
		var xtraData = HOVerwaltung.instance().getModel().getXtraDaten();
		if (xtraData != null) {
			var countryId = xtraData.getCountryId();
			return WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(countryId);
		}
		return null;
	}
}