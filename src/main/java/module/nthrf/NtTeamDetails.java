package module.nthrf;

import core.db.ColumnDescriptor;
import core.db.DBManager;
import core.file.xml.XMLManager;

import core.model.misc.Basics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

public class NtTeamDetails {

	private int hrfId;


	private int teamId;
	private String teamName;
	private String teamNameShort;
	private long coachId;
	private String coachName;
	private int leagueId;
	private String leagueName;
	private long trainerId;
	private String trainerName;
	private String homePageUrl;
	private int xp253;
	private int xp343;

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public void setTeamNameShort(String teamNameShort) {
		this.teamNameShort = teamNameShort;
	}

	public void setCoachId(long coachId) {
		this.coachId = coachId;
	}

	public void setCoachName(String coachName) {
		this.coachName = coachName;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}

	public void setTrainerId(long trainerId) {
		this.trainerId = trainerId;
	}

	public void setTrainerName(String trainerName) {
		this.trainerName = trainerName;
	}

	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}

	public int getXp253() {
		return xp253;
	}

	public void setXp253(int xp253) {
		this.xp253 = xp253;
	}

	public void setXp343(int xp343) {
		this.xp343 = xp343;
	}

	public void setXp352(int xp352) {
		this.xp352 = xp352;
	}

	public void setXp433(int xp433) {
		this.xp433 = xp433;
	}

	public int getXp442() {
		return xp442;
	}

	public void setXp442(int xp442) {
		this.xp442 = xp442;
	}

	public void setXp451(int xp451) {
		this.xp451 = xp451;
	}

	public void setXp532(int xp532) {
		this.xp532 = xp532;
	}

	public void setXp541(int xp541) {
		this.xp541 = xp541;
	}

	public int getXp550() {
		return xp550;
	}

	public void setXp550(int xp550) {
		this.xp550 = xp550;
	}

	public void setMorale(int morale) {
		this.morale = morale;
	}

	public void setSelfConfidence(int selfConfidence) {
		this.selfConfidence = selfConfidence;
	}

	public void setSupportersPopularity(int supportersPopularity) {
		this.supportersPopularity = supportersPopularity;
	}

	public void setRatingScore(int ratingScore) {
		this.ratingScore = ratingScore;
	}

	public void setFanclubSize(int fanclubSize) {
		this.fanclubSize = fanclubSize;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setFetchedDate(Timestamp fetchedDate) {
		this.fetchedDate = fetchedDate;
	}

	private int xp352;
	private int xp433;
	private int xp442;
	private int xp451;
	private int xp532;
	private int xp541;
	private int xp550;
	private int morale;
	private int selfConfidence;
	private int supportersPopularity;
	private int ratingScore;
	private int fanclubSize;
	private int rank;
	private Timestamp fetchedDate;
	private boolean parsingSuccess;

	public NtTeamDetails(ResultSet rs){
		this.teamId = DBManager.getInteger(rs, "TEAM_ID");
		this.hrfId = DBManager.getInteger(rs, "HRF_ID");
		this.morale = DBManager.getInteger(rs, "MORALE");
		this.selfConfidence = DBManager.getInteger(rs, "SELFCONFIDENCE");
		this.xp253 = DBManager.getInteger(rs, "XP253");
		this.xp343 = DBManager.getInteger(rs, "XP343");
		this.xp352 = DBManager.getInteger(rs, "XP352");
		this.xp433 = DBManager.getInteger(rs, "XP433");
		this.xp442 = DBManager.getInteger(rs, "XP442");
		this.xp451 = DBManager.getInteger(rs, "XP451");
		this.xp532 = DBManager.getInteger(rs, "XP532");
		this.xp541 = DBManager.getInteger(rs, "XP541");
		this.xp550 = DBManager.getInteger(rs, "XP550");
		this.teamName = DBManager.getString(rs,"NAME");
		this.teamNameShort = DBManager.getString(rs,"SHORTNAME");
		this.coachId = DBManager.getInteger(rs, "COACHID");
		this.coachName = DBManager.getString(rs,"COACHNAME");
		this.leagueId = DBManager.getInteger(rs, "LEAGUEID");
		this.leagueName = DBManager.getString(rs,"LEAGUENAME");
		this.trainerId = DBManager.getInteger(rs, "TRAINERID");
		this.trainerName = DBManager.getString(rs,"TRAINERNAME");
		this.supportersPopularity = DBManager.getInteger(rs, "SUPPORTERPOPULARITY");
		this.ratingScore = DBManager.getInteger(rs, "RATING");
		this.fanclubSize = DBManager.getInteger(rs, "FANCLUBSIZE");
		this.rank = DBManager.getInteger(rs, "RANK");
		this.fetchedDate = DBManager.getTimestamp(rs, "FETCHEDDATE");
	}

	NtTeamDetails(String xmlData) {
	    parseDetails(XMLManager.parseString(xmlData));
	}

	private void parseDetails(Document doc) {
        if (doc == null) {
            return;
        }
        try {

            Element root = doc.getDocumentElement();
            Element teamRoot;
            Element ele = (Element)root.getElementsByTagName("FetchedDate").item(0);
            fetchedDate = Basics.parseHattrickDate(XMLManager.getFirstChildNodeValue(ele));

            // root Team
            teamRoot = (Element) root.getElementsByTagName("Team").item(0);
            root = teamRoot;
            ele = (Element) root.getElementsByTagName("TeamID").item(0);
            teamId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("TeamName").item(0);
            teamName = XMLManager.getFirstChildNodeValue(ele);
            ele = (Element) root.getElementsByTagName("ShortTeamName").item(0);
            teamNameShort = XMLManager.getFirstChildNodeValue(ele);

            // root national coach
            root = (Element) teamRoot.getElementsByTagName("NationalCoach").item(0);
            ele = (Element) root.getElementsByTagName("NationalCoachUserID").item(0);
            coachId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("NationalCoachLoginname").item(0);
            coachName = XMLManager.getFirstChildNodeValue(ele);

            // root League
            root = (Element) teamRoot.getElementsByTagName("League").item(0);
            ele = (Element) root.getElementsByTagName("LeagueID").item(0);
            leagueId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("LeagueName").item(0);
            leagueName = XMLManager.getFirstChildNodeValue(ele);

            // root Trainer
            root = (Element) teamRoot.getElementsByTagName("Trainer").item(0);
            ele = (Element) root.getElementsByTagName("PlayerID").item(0);
            trainerId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele));
            ele = (Element) root.getElementsByTagName("PlayerName").item(0);
            trainerName = XMLManager.getFirstChildNodeValue(ele);

            // root HomePage
            root = (Element) teamRoot.getElementsByTagName("HomePage").item(0);
            homePageUrl = XMLManager.getFirstChildNodeValue(root);

            // formation XP
            root = (Element) teamRoot.getElementsByTagName("Experience433").item(0);
            xp433 = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("Experience451").item(0);
            xp451 = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("Experience352").item(0);
            xp352 = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("Experience532").item(0);
            xp532 = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("Experience343").item(0);
            xp343 = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("Experience541").item(0);
            xp541 = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));

            // morale etc.
            root = (Element) teamRoot.getElementsByTagName("Morale").item(0);
            morale = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("SelfConfidence").item(0);
            selfConfidence = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("SupportersPopularity").item(0);
            supportersPopularity = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("RatingScore").item(0);
            ratingScore = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("FanClubSize").item(0);
            fanclubSize = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            root = (Element) teamRoot.getElementsByTagName("Rank").item(0);
            rank = Integer.parseInt(XMLManager.getFirstChildNodeValue(root));
            parsingSuccess = true;
        } catch (Exception e) {
        	parsingSuccess = false;
        	e.printStackTrace();
        }
	}

	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
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

	public long getTrainerId() {
		return trainerId;
	}

	public String getTrainerName() {
		return trainerName;
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

	public int getXp532() {
		return xp532;
	}

	public int getXp343() {
		return xp343;
	}

	public int getXp541() {
		return xp541;
	}

	public int getMorale() {
		return morale;
	}

	public int getSelfConfidence() {
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

	public Timestamp getFetchedDate() {
		return fetchedDate;
	}

	boolean isParsingSuccess() {
		return parsingSuccess;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("NtTeamDetails (from "+fetchedDate+"), parsingSuccess: " + parsingSuccess);
		sb.append("\n\tteam:    " + teamName + " (" + teamId + ") - short: " + teamNameShort);
		sb.append("\n\tcoach:   " + coachName + " (" + coachId + ")");
		sb.append("\n\tleague : " + leagueName + " (" + leagueId + ")");
		sb.append("\n\ttrainer: " + trainerName + " (" + trainerId + ")");
		sb.append("\n\thomePageUrl: " + homePageUrl);
		sb.append("\n\txp433: " + xp433);
		sb.append("\txp451: " + xp451);
		sb.append("\txp352: " + xp352);
		sb.append("\txp532: " + xp532);
		sb.append("\txp343: " + xp343);
		sb.append("\txp541: " + xp541);
		sb.append("\n\tmorale:               " + morale);
		sb.append("\n\tselfConfidence:       " + selfConfidence);
		sb.append("\n\tsupportersPopularity: " + supportersPopularity);
		sb.append("\n\tratingScore:          " + ratingScore);
		sb.append("\n\tfanclubSize:          " + fanclubSize);
		sb.append("\n\trank:                 " + rank);
		return sb.toString();
	}

	public int getHrfId() {
		return hrfId;
	}

	public void setHrfId(int hrfId) {
		this.hrfId = hrfId;
	}
}
