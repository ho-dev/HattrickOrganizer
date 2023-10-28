package module.nthrf;

import core.file.hrf.HRFStringBuilder;
import core.file.xml.*;
import core.gui.HOMainFrame;
import core.model.match.*;
import core.net.MyConnector;
import core.net.OnlineWorker;
import core.util.HOLogger;
import core.util.Helper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import static core.net.OnlineWorker.downloadLastLineup;
import static core.net.OnlineWorker.downloadNextMatchOrder;

class NthrfConvertXml2Hrf {

	/**
	 * Create the HRF file.
	 */
	final String createHrf(long teamId, MyConnector dh) throws Exception {
		try {
			int progressIncrement = 5;
			HOMainFrame.INSTANCE.setInformation(Helper.getTranslation("ls.update_status.connection"), progressIncrement);

			// leagueId / countryId

			// nt team detail
			String xml = dh.getHattrickXMLFile("/chppxml.axd?file=nationalteamdetails&version=1.9&teamid=" + teamId);
			NtTeamDetails details = new NtTeamDetails();
			var detailsMap = details.parseDetails(xml);

			// world details
			HOMainFrame.INSTANCE.setInformation(Helper.getTranslation("ls.update_status.world_details"), progressIncrement);
			xml = dh.getHattrickXMLFile("/chppxml.axd?file=worlddetails");
			Map<String, String> world = XMLWorldDetailsParser.parseWorldDetailsFromString(xml, String.valueOf(details.getLeagueId()));
			var hrfStringBuilder = new HRFStringBuilder();
			hrfStringBuilder.createBasics(detailsMap, world);

			HOMainFrame.INSTANCE.setInformation(Helper.getTranslation("ls.update_status.team_logo"), progressIncrement);
			OnlineWorker.downloadTeamLogo(detailsMap);

			// nt matches
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(System.currentTimeMillis());
			xml = dh.getHattrickXMLFile("/chppxml.axd?file=matches&version=2.9&teamID=" + teamId + "&LastMatchDate=" + new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
			var matches = XMLMatchesParser.parseMatchesFromString(xml);
//			// last lineup
			HOMainFrame.INSTANCE.setInformation(Helper.getTranslation("ls.update_status.match_lineup"), progressIncrement);
			MatchLineupTeam matchLineupTeam = downloadLastLineup(matches, (int) teamId);

			// TODO nt orders download gives no match data available
			var nextLineupDataMap = downloadNextMatchOrder(matches, (int)teamId);

			HOMainFrame.INSTANCE.setInformation(Helper.getTranslation("ls.update_status.players_information"), progressIncrement);
			xml = dh.getHattrickXMLFile("/chppxml.axd?file=nationalplayers&teamid=" + teamId);
			List<MyHashtable> playersData = NtPlayersParser.parsePlayersFromString(xml);

			if (playersData.size() == 0) {
				// training area closed or all players are released
				return "";
			}

			var empty = new MyHashtable();
			hrfStringBuilder.createPlayers(matchLineupTeam, playersData);
			hrfStringBuilder.createLastLineUp(matchLineupTeam, detailsMap);
			hrfStringBuilder.createLineUp("", (int)teamId, nextLineupDataMap);
			hrfStringBuilder.createTeam(detailsMap);
			hrfStringBuilder.createClub(empty, detailsMap, detailsMap);
			hrfStringBuilder.createArena(detailsMap);
			hrfStringBuilder.createWorld(empty, detailsMap, empty, world);

			return hrfStringBuilder.createHRF().toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * Parse all leagues and (nativeLeagueId) and their countryId.
	 */
	HashMap<Integer, Integer> getCountryMapping(MyConnector dh) {
		HashMap<Integer, Integer> ret = new HashMap<>(100);
		try {
			String str = getWorldDetailString(dh);
			if (str == null || str.equals("")) {
				return ret;
			}
			Document doc = XMLManager.parseString(str);

			Element ele;
			Element root;
			NodeList list;
			if (doc == null) {
				return ret;
			}
			root = doc.getDocumentElement();
			root = (Element) root.getElementsByTagName("LeagueList").item(0);
			list = root.getElementsByTagName("League");
			for (int i = 0; i < list.getLength(); i++) {
				root = (Element) list.item(i);
				ele = (Element) root.getElementsByTagName("LeagueID").item(0);
				String leagueID = XMLManager.getFirstChildNodeValue(ele);
				root = (Element) root.getElementsByTagName("Country").item(0);
				if (XMLManager.getAttributeValue(root, "Available").trim().equalsIgnoreCase("true")) {
					ele = (Element) root.getElementsByTagName("CountryID").item(0);
					String countryID = XMLManager.getFirstChildNodeValue(ele);
					ret.put(Integer.parseInt(leagueID), Integer.parseInt(countryID));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

    private String getWorldDetailString(MyConnector dh) {
        return dh.getHattrickXMLFile("/chppxml.axd?file=worlddetails&version=1.8");
    }

	/**
	 * Save the hrf buffer into a file.
	 */
	final void writeHRF(File file, String hrf) {
		debug("Create NT HRF file: " + (file != null ? file.getAbsolutePath() : "null"));
		if (file == null) return;

		BufferedWriter out;
		//utf-8
		OutputStreamWriter outWrit;

		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			outWrit = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			out = new BufferedWriter(outWrit);
			out.write(hrf);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void debug(String txt) {
		HOLogger.instance().debug(this.getClass(), txt);
	}
}
