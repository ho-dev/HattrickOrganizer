package module.nthrf;

import core.file.xml.XMLManager;
import core.model.misc.Basics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.sql.Timestamp;

public class NtTeamDetails {

	private int hrfId;
	private int teamId;
	private String teamName;
	private String teamNameShort;
	private int coachId;
	private String coachName;
	private int leagueId;
	private String leagueName;
	private int trainerId;
	private String trainerName;
	private String homePageUrl;
	private int xp253;
	private int xp343;
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
	public void setTrainerId(Integer trainerId) {
		if ( trainerId!=null) this.trainerId = trainerId;
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
	public void setMorale(Integer morale) {
		if ( morale != null) this.morale = morale;
	}
	public void setSelfConfidence(Integer selfConfidence) {
		if ( selfConfidence!=null) this.selfConfidence = selfConfidence;
	}
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
	public void setFetchedDate(Timestamp fetchedDate) {
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
	public int getHrfId() {
		return hrfId;
	}

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


	boolean isParsingSuccess() {
		return parsingSuccess;
	}

	@Override
	public String toString() {
		return "NtTeamDetails (from " + fetchedDate + "), parsingSuccess: " + parsingSuccess + "\n\tteam:    " + teamName + " (" + teamId + ") - short: " + teamNameShort +
				"\n\tcoach:   " + coachName + " (" + coachId + ")" +
				"\n\tleague : " + leagueName + " (" + leagueId + ")" +
				"\n\ttrainer: " + trainerName + " (" + trainerId + ")" +
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
