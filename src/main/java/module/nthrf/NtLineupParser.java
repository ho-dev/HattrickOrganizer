package module.nthrf;

import core.file.xml.XMLManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class NtLineupParser {
	private String fetchedDate;
	private long matchId;
	private int matchType;
	private boolean isYouth;
	private String matchDate;

	private long teamId;
	private String teamName;
	private int xpLevel;

	private List<NtPlayerPosition> players = new ArrayList<NtPlayerPosition>(); // <NtPlayerPosition>
	private boolean parsingSuccess;

	NtLineupParser(String xmlData) {
		parseDetails(XMLManager.parseString(xmlData));
	}

	protected final NtPlayerPosition createPlayer(Element ele) throws Exception {
		Element tmp = null;
		final NtPlayerPosition pp = new NtPlayerPosition();

		tmp = (Element) ele.getElementsByTagName("PlayerID").item(0);
		pp.setPlayerId(Long.parseLong(tmp.getFirstChild().getNodeValue()));
		tmp = (Element) ele.getElementsByTagName("PlayerName").item(0);
		pp.setName(tmp.getFirstChild().getNodeValue());
		tmp = (Element) ele.getElementsByTagName("RoleID").item(0);
		int roleId = Integer.parseInt(tmp.getFirstChild().getNodeValue());
		pp.setRoleId(roleId);
		if (roleId > 0 && roleId < 12) {
			tmp = (Element) ele.getElementsByTagName("PositionCode").item(0);
			pp.setPositionCode(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
			tmp = (Element) ele.getElementsByTagName("Behaviour").item(0);
			pp.setBehaviour(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
		}
		if ((roleId < 12) || (roleId > 18)) {
			tmp = (Element) ele.getElementsByTagName("RatingStars").item(0);
			pp.setRatingStars(Float.parseFloat(tmp.getFirstChild().getNodeValue()));
		}
		return pp;
	}

	private void parseDetails(Document doc) {
        if (doc == null) {
            return;
        }
        try {
            Element root = doc.getDocumentElement();
            Element ele = (Element)root.getElementsByTagName("FetchedDate").item(0);
            fetchedDate = XMLManager.getFirstChildNodeValue(ele);
            ele = (Element)root.getElementsByTagName("MatchID").item(0);
            matchId = Long.parseLong(XMLManager.getFirstChildNodeValue(ele));
            ele = (Element)root.getElementsByTagName("IsYouth").item(0);
            isYouth = Boolean.getBoolean(XMLManager.getFirstChildNodeValue(ele).toLowerCase(java.util.Locale.ENGLISH));
            ele = (Element)root.getElementsByTagName("MatchType").item(0);
            matchType = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele));
            ele = (Element)root.getElementsByTagName("MatchDate").item(0);
            matchDate = XMLManager.getFirstChildNodeValue(ele);

            // skip home team
            // skip away team
            // skip arena

            // team
            root = (Element)root.getElementsByTagName("Team").item(0);
            ele = (Element)root.getElementsByTagName("TeamID").item(0);
            teamId = Long.parseLong(XMLManager.getFirstChildNodeValue(ele));
            ele = (Element)root.getElementsByTagName("TeamName").item(0);
            teamName = XMLManager.getFirstChildNodeValue(ele);
            ele = (Element)root.getElementsByTagName("ExperienceLevel").item(0);
            xpLevel = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele));

            // lineup players
            root = (Element)root.getElementsByTagName("Lineup").item(0);
            NodeList playersNode = root.getElementsByTagName("Player");
        	for (int m=0; (playersNode != null && m<playersNode.getLength()); m++) {
        		ele = (Element)playersNode.item(m);
        		players.add(createPlayer(ele));
        	}
        	parsingSuccess = true;
        } catch (Exception e) {
        	parsingSuccess = false;
        	e.printStackTrace();
        }
	}

	public String getFetchedDate() {
		return fetchedDate;
	}
	public boolean isParsingSuccess() {
		return parsingSuccess;
	}
	public List<NtPlayerPosition> getAllPlayers() {
		return players;
	}
	public long getMatchId() {
		return matchId;
	}
	public int getMatchType() {
		return matchType;
	}
	public boolean isYouth() {
		return isYouth;
	}
	public String getMatchDate() {
		return matchDate;
	}
	public long getTeamId() {
		return teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public int getXpLevel() {
		return xpLevel;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("NtPlayers (from "+fetchedDate+"), parsingSuccess: " + parsingSuccess);
		sb.append("\n\tTeam: " + teamName + " (" + teamId + ")");
		sb.append("\n\tPlayer IDs("+players.size()+"):");
		int m = 1;
		for (Iterator<NtPlayerPosition> i=players.iterator(); i.hasNext(); m++) {
			sb.append("\n\t\t" + m + ". " + i.next());
		}
		return sb.toString();
	}
}
