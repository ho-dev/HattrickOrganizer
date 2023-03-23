package module.nthrf;

import core.db.AbstractTable;
import core.file.xml.XMLManager;
import core.util.HODateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NtTeamDetails extends AbstractTable.Storable {

	private int hrfId;
	private int teamId;
	private String teamName;
	private String teamNameShort;
	private int coachId;
	private String coachName;
	private int leagueId;
	private String leagueName;
	private String homePageUrl;
	private String logo;
	private int xp253=0;
	private int xp343=0;
	private int xp352=0;
	private int xp433=0;
	private int xp442=0;
	private int xp451=0;
	private int xp523=0;
	private int xp532=0;
	private int xp541=0;
	private int xp550=0;
	private Integer morale;
	private Integer selfConfidence;
	private int supportersPopularity;
	private int ratingScore;
	private int fanclubSize;
	private int rank;
	private HODateTime fetchedDate;
	private boolean parsingSuccess;

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public void setTeamNameShort(String teamNameShort) {
		this.teamNameShort = teamNameShort;
	}
	public void setCoachId(Integer coachId) {
		if ( coachId!=null )this.coachId = coachId;
	}
	public void setCoachName(String coachName) {
		this.coachName = coachName;
	}
	public void setLeagueId(Integer leagueId) {
		if ( leagueId!=null) this.leagueId = leagueId;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}
	public int getXp253() {
		return xp253;
	}
	public void setXp253(Integer xp253) {
		if ( xp253 != null )this.xp253 = xp253;
	}
	public void setXp343(Integer xp343) {
		if ( xp343 != null) this.xp343 = xp343;
	}
	public void setXp352(Integer xp352) {
		if (xp352!=null)this.xp352 = xp352;
	}
	public void setXp433(Integer xp433) {
		if ( xp433 != null) this.xp433 = xp433;
	}
	public int getXp442() {
		return xp442;
	}
	public void setXp442(Integer xp442) {
		if ( xp442 != null ) this.xp442 = xp442;
	}
	public void setXp451(Integer xp451) {
		if ( xp451!=null) this.xp451 = xp451;
	}
	public void setXp523(Integer xp523) {
		if ( xp523!= null) this.xp523 = xp523;
	}
	public void setXp532(Integer xp532) {
		if ( xp532!= null) this.xp532 = xp532;
	}
	public void setXp541(Integer xp541) {
		if ( xp541 != null ) this.xp541 = xp541;
	}
	public int getXp550() {
		return xp550;
	}
	public void setXp550(Integer xp550) {
		if ( xp550!= null) this.xp550 = xp550;
	}
	public void setMorale(Integer morale) { this.morale = morale; }
	public void setSelfConfidence(Integer selfConfidence) { this.selfConfidence = selfConfidence; }
	public void setSupportersPopularity(Integer supportersPopularity) {
		if ( supportersPopularity!= null) this.supportersPopularity = supportersPopularity;
	}
	public void setRatingScore(Integer ratingScore) {
		if ( ratingScore!= null) this.ratingScore = ratingScore;
	}
	public void setFanclubSize(Integer fanclubSize) {
		if ( fanclubSize!= null) this.fanclubSize = fanclubSize;
	}
	public void setRank(Integer rank) {
		if ( rank!=null) this.rank = rank;
	}
	public void setFetchedDate(HODateTime fetchedDate) {
		this.fetchedDate = fetchedDate;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(Integer teamId) {
		if ( teamId != null ) this.teamId = teamId;
	}
	public void setHrfId(Integer hrfId) {
		if ( hrfId!=null) this.hrfId = hrfId;
	}

	public String getTeamName() {
		return teamName;
	}
	public long getCoachId() {
		return coachId;
	}
	public String getCoachName() {
		return coachName;
	}
	public int getLeagueId() {
		return leagueId;
	}
	public String getLeagueName() {
		return leagueName;
	}
	public String getHomePageUrl() {
		return homePageUrl;
	}
	public int getXp433() {
		return xp433;
	}
	public int getXp451() {
		return xp451;
	}
	public int getXp352() {
		return xp352;
	}
	public int getXp523() {
		return this.xp523;
	}
	public int getXp532() {
		return xp532;
	}
	public int getXp343() {
		return xp343;
	}
	public int getXp541() {
		return xp541;
	}
	public Integer getMorale() {
		return morale;
	}
	public Integer getSelfConfidence() {
		return selfConfidence;
	}
	public int getSupportersPopularity() {
		return supportersPopularity;
	}
	public int getRatingScore() {
		return ratingScore;
	}
	public int getFanclubSize() {
		return fanclubSize;
	}
	public int getRank() {
		return rank;
	}
	public String getTeamNameShort() {
		return teamNameShort;
	}
	public HODateTime getFetchedDate() {
		return fetchedDate;
	}
	public int getHrfId() {
		return hrfId;
	}

	/**
	 * constructor is used by AbstractTable.load
	 */
	public NtTeamDetails(){}

	public NtTeamDetails(String xmlData) {
	    parseDetails(XMLManager.parseString(xmlData));
	}

	private void parseDetails(Document doc) {
        if (doc == null) {
            return;
        }
        try {

            Element root = doc.getDocumentElement();
            Element ele = (Element)root.getElementsByTagName("FetchedDate").item(0);
            fetchedDate = HODateTime.fromHT(XMLManager.getFirstChildNodeValue(ele));

            // root Team
            var teamRoot = (Element) root.getElementsByTagName("Team").item(0);

			teamId = getInteger(teamRoot, "TeamID", -1);
			teamName = getString(teamRoot, "TeamName");
			teamNameShort = getString(teamRoot, "ShortTeamName");

            // root national coach
            root = (Element) teamRoot.getElementsByTagName("NationalCoach").item(0);
			coachId = getInteger(root, "NationalCoachUserID");
			coachName = getString(root, "NationalCoachLoginname");

            // root League
            root = (Element) teamRoot.getElementsByTagName("League").item(0);
			leagueId = getInteger(root, "LeagueID", -1);
			leagueName = getString(root, "LeagueName");

            // root HomePage
			homePageUrl = getString(teamRoot, "HomePage");
			logo = getString(teamRoot, "Logo");

            // formation XP
			xp253 = getInteger(teamRoot, "Experience253", 0);
			xp352 = getInteger(teamRoot, "Experience352",0);
			xp451 = getInteger(teamRoot, "Experience451",0);
			xp442 = getInteger(teamRoot, "Experience442",0);
			xp433 = getInteger(teamRoot, "Experience433",0);
			xp343 = getInteger(teamRoot, "Experience343",0);
			xp523 = getInteger(teamRoot, "Experience523",0);
			xp532 = getInteger(teamRoot, "Experience532",0);
			xp541 = getInteger(teamRoot, "Experience541",0);
			xp550 = getInteger(teamRoot, "Experience550",0);

			// morale etc.
			morale = getInteger(teamRoot, "Morale");
			selfConfidence = getInteger(teamRoot, "SelfConfidence");
			supportersPopularity = getInteger(teamRoot, "SupportersPopularity",0);
			ratingScore = getInteger(teamRoot, "RatingScore",0);
			fanclubSize = getInteger(teamRoot, "FanClubSize",0);
			rank = getInteger(teamRoot, "Rank",0);

            parsingSuccess = true;
        } catch (Exception e) {
        	parsingSuccess = false;
        	e.printStackTrace();
        }
	}

	private int getInteger(Element root, String tag, int def) {
		var ret = getInteger(root, tag);
		if ( ret != null) return ret;
		return def;
	}

	private String getString(Element root, String tag) {
		var ele = (Element) root.getElementsByTagName(tag).item(0);
		return XMLManager.getFirstChildNodeValue(ele);
	}

	private Integer getInteger(Element root, String tag) {
		try {
			return Integer.parseInt(getString(root, tag));
		} catch (NumberFormatException ignored) {
			//e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "NtTeamDetails (from " + fetchedDate + "), parsingSuccess: " + parsingSuccess + "\n\tteam:    " + teamName + " (" + teamId + ") - short: " + teamNameShort +
				"\n\tcoach:   " + coachName + " (" + coachId + ")" +
				"\n\tleague : " + leagueName + " (" + leagueId + ")" +
				"\n\thomePageUrl: " + homePageUrl +
				"\n\txp433: " + xp433 +
				"\txp451: " + xp451 +
				"\txp352: " + xp352 +
				"\txp532: " + xp532 +
				"\txp343: " + xp343 +
				"\txp541: " + xp541 +
				"\n\tmorale:               " + morale +
				"\n\tselfConfidence:       " + selfConfidence +
				"\n\tsupportersPopularity: " + supportersPopularity +
				"\n\tratingScore:          " + ratingScore +
				"\n\tfanclubSize:          " + fanclubSize +
				"\n\trank:                 " + rank;
	}


}
