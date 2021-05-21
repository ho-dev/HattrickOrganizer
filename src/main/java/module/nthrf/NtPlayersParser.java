package module.nthrf;

import core.file.xml.XMLManager;
import core.net.MyConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class NtPlayersParser {

	private String fetchedDate;
	private long teamId;
	private String teamName;
	private List<Long> playerIds = new ArrayList<Long>();
	private List<NtPlayer> players = new ArrayList<NtPlayer>();
	private boolean parsingSuccess;

	/**
	 * Parse player details and store the IDs and Players in local objects.
	 */
	NtPlayersParser(String xmlData, MyConnector dh, HashMap<Integer, Integer> countryMapping) {
	    parseBasics(XMLManager.parseString(xmlData));
	    parsePlayerDetails(dh, countryMapping);
	}

	private void parsePlayerDetails(MyConnector dh, HashMap<Integer, Integer> countryMapping) {
		try {
			for (Iterator<Long> i = playerIds.iterator(); i.hasNext(); ) {
				Long playerId = i.next();
				String xmlData = dh.getHattrickXMLFile("/chppxml.axd?file=playerdetails&version=2.8&playerId=" + playerId);
				Document doc = XMLManager.parseString(xmlData);
				Element root = doc.getDocumentElement();
				Element ele = (Element)root.getElementsByTagName("Player").item(0);
                players.add(createPlayer(ele, countryMapping));
            }
		} catch (Exception e) {
        	parsingSuccess = false;
        	e.printStackTrace();
        }
	}

	protected final NtPlayer createPlayer(Element ele, HashMap<Integer, Integer> countryMapping) throws Exception {
		Element tmp = null;
		final NtPlayer player = new NtPlayer();

		player.setPlayerId(Long.parseLong(getXMLValue(ele, "PlayerID")));
		player.setFirstName(getXMLValue(ele, "FirstName"));
		player.setLastName(getXMLValue(ele, "LastName"));
		player.setNickName(getXMLValue(ele, "NickName"));

		try {
			player.setShirtNumber(Integer.parseInt(getXMLValue(ele,"PlayerNumber")));
		} catch (Exception e) {
			//no supporter
		}

		player.setTsi(Integer.parseInt(getXMLValue(ele, "TSI")));
		player.setForm(Integer.parseInt(getXMLValue(ele, "PlayerForm")));
		player.setAgeYears(Integer.parseInt(getXMLValue(ele, "Age")));
		player.setAgeDays(Integer.parseInt(getXMLValue(ele, "AgeDays")));
		player.setXp(Integer.parseInt(getXMLValue(ele, "Experience")));
		player.setLeaderShip(Integer.parseInt(getXMLValue(ele, "Leadership")));
		player.setSpeciality(Integer.parseInt(getXMLValue(ele, "Specialty")));
		player.setTranferlisted(getXMLValue(ele, "TransferListed").equals("True"));
		int nativeLeagueId = Integer.parseInt(getXMLValue(ele, "NativeLeagueID"));
		player.setNativeLeagueId(nativeLeagueId);
		player.setCountryId(NthrfUtil.getCountryId(nativeLeagueId, countryMapping));

		player.setTrainer(false);
		player.setTrainerSkill(7);	// solid trainer
		player.setTrainerType(2);	// normal trainer
		try {
			if (ele.getElementsByTagName("TrainerType") != null && ele.getElementsByTagName("TrainerType").getLength()>0) {
				player.setTrainerType(Integer.parseInt(getXMLValue(ele, "TrainerType")));
				player.setTrainerSkill(Integer.parseInt(getXMLValue(ele, "TrainerSkill")));
				player.setTrainer(true);
			}
		} catch (Exception e) {
			System.out.println("Error in Trainercheck: " + player.getPlayerId() + ", " + player.getName() + " - " + e);
		}

		player.setSalary(Integer.parseInt(getXMLValue(ele, "Salary")));
		player.setAgreeability(Integer.parseInt(getXMLValue(ele, "Agreeability")));
		player.setAggressiveness(Integer.parseInt(getXMLValue(ele, "Aggressiveness")));
		player.setHonesty(Integer.parseInt(getXMLValue(ele, "Honesty")));
		player.setCaps(Integer.parseInt(getXMLValue(ele, "Caps")));
		player.setCapsU20(Integer.parseInt(getXMLValue(ele, "CapsU20")));
		try {
			player.setYellowCards(Integer.parseInt(getXMLValue(ele, "Cards")));
		} catch (Exception e) {
			System.out.println("Cant get cards: " + player.getPlayerId() + ", " + player.getName() + " - " + e);
			player.setYellowCards(0);
		}
		try {
			player.setInjury(Integer.parseInt(getXMLValue(ele, "InjuryLevel")));
		} catch (Exception e) {
			System.out.println("Cant get injury: " + player.getPlayerId() + ", " + player.getName() + " - " + e);
			player.setInjury(0);
		}

		try {
			player.setCareerGoals(Integer.parseInt(getXMLValue(ele, "CareerGoals")));
			player.setCareerHattricks(Integer.parseInt(getXMLValue(ele, "CareerHattricks")));
			player.setLeagueGoals(Integer.parseInt(getXMLValue(ele, "LeagueGoals")));
		} catch (Exception e) {
			System.out.println("Cant get goals++: " + player.getPlayerId() + ", " + player.getName() + " - " + e);
			player.setCareerGoals(0);
			player.setCareerHattricks(0);
			player.setLeagueGoals(0);
		}

		try {
			player.setStaminaSkill(Integer.parseInt(getXMLValue(ele, "StaminaSkill")));
			player.setKeeperSkill(Integer.parseInt(getXMLValue(ele, "KeeperSkill")));
			player.setPlaymakerSkill(Integer.parseInt(getXMLValue(ele, "PlaymakerSkill")));
			player.setScorerSkill(Integer.parseInt(getXMLValue(ele, "ScorerSkill")));
			player.setPassingSkill(Integer.parseInt(getXMLValue(ele, "PassingSkill")));
			player.setWingerSkill(Integer.parseInt(getXMLValue(ele, "WingerSkill")));
			player.setDefenderSkill(Integer.parseInt(getXMLValue(ele, "DefenderSkill")));
			player.setSetPiecesSkill(Integer.parseInt(getXMLValue(ele, "SetPiecesSkill")));
		} catch (Exception e) {
			System.out.println("Cant get skills: " + player.getPlayerId() + ", " + player.getName() + " - " + e);
			e.printStackTrace();
		}

		return player;
	}

	private String getXMLValue(Element ele, String key) {
		var tmp = (Element) ele.getElementsByTagName(key).item(0);
		if ( tmp != null){
			var child = tmp.getFirstChild();
			if ( child != null){
				return child.getNodeValue();
			}
		}
		return "";
	}

	private void parseBasics(Document doc) {
        if (doc == null) {
            return;
        }
        try {
            Element root = doc.getDocumentElement();
            Element ele = (Element)root.getElementsByTagName("FetchedDate").item(0);
            fetchedDate = XMLManager.getFirstChildNodeValue(ele);
            ele = (Element)root.getElementsByTagName("TeamID").item(0);
            teamId = Long.parseLong(XMLManager.getFirstChildNodeValue(ele));
            ele = (Element)root.getElementsByTagName("TeamName").item(0);
            teamName = XMLManager.getFirstChildNodeValue(ele);

            // players
            root = (Element)root.getElementsByTagName("Players").item(0);
            NodeList playersNode = root.getElementsByTagName("Player");
        	for (int m=0; (playersNode != null && m<playersNode.getLength()); m++) {
        		ele = (Element)playersNode.item(m);
        		ele = (Element)ele.getElementsByTagName("PlayerID").item(0);
        		playerIds.add(new Long(Long.parseLong(XMLManager.getFirstChildNodeValue(ele))));
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

	public long getTeamId() {
		return teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public List<Long> getPlayerIds() {
		return playerIds;
	}

	public List<NtPlayer> getAllPlayers() {
		return players;
	}

	public boolean isParsingSuccess() {
		return parsingSuccess;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("NtPlayers (from "+fetchedDate+"), parsingSuccess: " + parsingSuccess);
		sb.append("\n\tTeam: " + teamName + " (" + teamId + ")");
		sb.append("\n\tPlayer IDs("+playerIds.size()+"):");
		int m = 1;
		for (Iterator<Long> i=playerIds.iterator(); i.hasNext(); m++) {
			sb.append("\n\t\t" + m + ". " + i.next());
		}
		sb.append("\n---------------------------------------------");
		m = 1;
		for (Iterator<NtPlayer> i=players.iterator(); i.hasNext(); m++) {
			sb.append("\n\t" + m + ". " + i.next());
		}
		return sb.toString();
	}
}
