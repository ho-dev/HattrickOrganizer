// %929884203:de.hattrickorganizer.net%
/*
 * ConvertXml2Hrf.java
 *
 * Created on 12. Januar 2004, 09:44
 */
package core.file.xml;

import core.db.DBManager;
import core.db.user.UserManager;
import core.file.hrf.HRFStringBuilder;
import core.gui.CursorToolkit;
import core.gui.HOMainFrame;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.*;
import core.model.enums.MatchType;
import core.model.player.PlayerAvatar;
import core.net.OnlineWorker;
import core.util.Helper;
import core.module.config.ModuleConfig;
import core.net.MyConnector;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Convert the necessary xml data into a HRF file.
 * 
 * @author thomas.werth
 */
public class ConvertXml2Hrf {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private ConvertXml2Hrf() {
	}

	/**
	 * Create the HRF data and return it in one string.
	 */
	public static @Nullable String createHrf() throws IOException {
		int progressIncrement = 3;
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.connection"), progressIncrement);
		final MyConnector mc = MyConnector.instance();
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		Integer youthTeamId = HOVerwaltung.instance().getModel().getBasics().getYouthTeamId();

		String teamDetails = mc.getTeamdetails(-1);

		if (teamDetails == null) {
			return null;
		}

		var teamInfoList = XMLTeamDetailsParser.getTeamInfoFromString(teamDetails);
		var usersPremierTeamInfo = teamInfoList.stream().filter(TeamInfo::isPrimaryTeam).findFirst().get();
		var usersPremierTeamId = usersPremierTeamInfo.getTeamId();
		if (teamId <= 0 || youthTeamId == null) {
			// We have no team selected or the youth team information is never downloaded before
			if (teamInfoList.size() == 1) {
				// user has only one single team
				teamId = teamInfoList.get(0).getTeamId();
				youthTeamId = teamInfoList.get(0).getYouthTeamId();
			} else if (teamInfoList.size() >= 2){
				// user has more than one team
				if ( teamId <=0) {
					// Select one of user's teams, if not done before
					CursorToolkit.stopWaitCursor(HOMainFrame.instance().getRootPane());
					TeamSelectionDialog selection = new TeamSelectionDialog(HOMainFrame.instance(), teamInfoList);
					selection.setVisible(true);
					if (selection.getCancel()) {
						return null;
					}
					teamId = selection.getSelectedTeam().getTeamId();
					youthTeamId = selection.getSelectedTeam().getYouthTeamId();
				}
				else {
					// team id is in DB and this is the first time we download youth team information
					int finalTeamId = teamId;
					var teaminfo = teamInfoList.stream()
							.filter(x -> x.getTeamId() == finalTeamId)
							.findAny()
							.orElse(null);
					if ( teaminfo != null){
						youthTeamId = teaminfo.getYouthTeamId();
					}
				}
			} else {
				return null;
			}
		}

		Map<String, String> teamdetailsDataMap = XMLTeamDetailsParser.parseTeamdetailsFromString(teamDetails, teamId);
		if (teamdetailsDataMap.size() == 0) return null;

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.team_logo"), progressIncrement);
		DBManager.instance().storeTeamLogoInfo(teamId, OnlineWorker.getLogoURL(teamdetailsDataMap), null);
		var logoFilename = ThemeManager.instance().getTeamLogoFilename(teamId);
		if (logoFilename!= null && !logoFilename.equals(UserManager.instance().getCurrentUser().getClubLogo())){
			UserManager.instance().getCurrentUser().setClubLogo(logoFilename);
			UserManager.instance().save();
		}

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.club_info"), progressIncrement);
		Map<String, String> clubDataMap = XMLClubParser.parseClubFromString(mc.getVerein(teamId));

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.league_details"), progressIncrement);
		Map<String, String> ligaDataMap = XMLLeagueDetailsParser.parseLeagueDetailsFromString(mc.getLeagueDetails(teamdetailsDataMap.get("LeagueLevelUnitID")),
				String.valueOf(teamId));

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.world_details"), progressIncrement);
		Map<String, String> worldDataMap = XMLWorldDetailsParser.parseWorldDetailsFromString(
				mc.getWorldDetails(Integer.parseInt(teamdetailsDataMap.get("LeagueID"))), teamdetailsDataMap.get("LeagueID"));

		// Currency fix
		var lastPremierId = ModuleConfig.instance().getInteger("UsersPremierTeamId");
		if ( lastPremierId != null && lastPremierId == usersPremierTeamId ){
		//if (ModuleConfig.instance().containsKey("CurrencyRate")) {
			worldDataMap.put("CurrencyRate", ModuleConfig.instance().getString("CurrencyRate"));
			worldDataMap.put("CountryId", ModuleConfig.instance().getString("CountryId"));
		} else {
			// We need to get hold of the currency info for the primary team, no matter which team we download.
			usersPremierTeamInfo = XMLWorldDetailsParser.updateTeamInfoWithCurrency(usersPremierTeamInfo, mc.getWorldDetails(usersPremierTeamInfo.getLeagueId()));
			ModuleConfig.instance().setString("CurrencyRate", usersPremierTeamInfo.getCurrencyRate().trim());
			ModuleConfig.instance().setString("CountryId", usersPremierTeamInfo.getCountryId());
			ModuleConfig.instance().setInteger("UsersPremierTeamId", usersPremierTeamInfo.getTeamId());
			worldDataMap.put("CurrencyRate", ModuleConfig.instance().getString("CurrencyRate"));
			worldDataMap.put("CountryId", ModuleConfig.instance().getString("CountryId"));
		}

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.players_information"), progressIncrement);
		List<MyHashtable> playersData = new XMLPlayersParser().parsePlayersFromString(mc.downloadPlayers(teamId));

		// Download players' avatar
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.players_avatars"), progressIncrement);
		List<PlayerAvatar> playersAvatar = XMLAvatarsParser.parseAvatarsFromString(mc.getAvatars(teamId));
		ThemeManager.instance().generateAllPlayerAvatar(playersAvatar, 1);

		List<MyHashtable> youthplayers=null;
		if ( youthTeamId != null && youthTeamId > 0 ){
			youthplayers = new XMLPlayersParser().parseYouthPlayersFromString(mc.downloadYouthPlayers(youthTeamId));
		}
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.economy"), progressIncrement);
		Map<String, String> economyDataMap = XMLEconomyParser.parseEconomyFromString(mc.getEconomy(teamId));

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.training"), progressIncrement);
		Map<String, String> trainingDataMap = XMLTrainingParser.parseTrainingFromString(mc.getTraining(teamId));

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.staff"), progressIncrement);
		List<MyHashtable> staffData = XMLStaffParser.parseStaffFromString(mc.getStaff(teamId));

		int arenaId = 0;
		try {
			arenaId = Integer.parseInt(teamdetailsDataMap.get("ArenaID"));
		} catch (Exception ignored) {

		}
		Map<String, String> arenaDataMap = XMLArenaParser.parseArenaFromString(mc.downloadArena(arenaId));

		// MatchOrder
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.match_orders"), progressIncrement);
		List<MatchKurzInfo> matches = XMLMatchesParser
				.parseMatchesFromString(mc.getMatches(Integer
						.parseInt(teamdetailsDataMap.get("TeamID")),
						false, true));

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.match_info"), progressIncrement);

		// Automatisch alle MatchLineups runterladen
		Map<String, String> nextLineupDataMap = null;
		MatchKurzInfo upcomingMatch = null;
		for (var match : matches) {
			if ((match.getMatchStatus() == MatchKurzInfo.UPCOMING)){
				upcomingMatch = match;
				// Match is always from the normal system, and league will do
				// the trick as the type.
				nextLineupDataMap = XMLMatchOrderParser
						.parseMatchOrderFromString(mc.getMatchOrder(
								match.getMatchID(), match.getMatchType(), teamId));
				break;
			}
		}

		MatchLineup matchLineup = null;
		var finishedMatchesAvailable = matches.stream().anyMatch(f->f.getMatchStatus()==MatchKurzInfo.FINISHED);
		if ( finishedMatchesAvailable) {
			HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.match_lineup"), progressIncrement);
			var matchLineupString = mc.downloadMatchLineup(-1, teamId, MatchType.LEAGUE);
			if (!matchLineupString.isEmpty()) {
				matchLineup = XMLMatchLineupParser.parseMatchLineupFromString(matchLineupString);
			}
		}

		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.match_details"), progressIncrement);

		MatchLineupTeam matchLineupTeam = null;
		int lastAttitude = 0;
		int lastTactic = 0;
		// Identify team, important for player ratings
		if (matchLineup != null) {
			Matchdetails md = XMLMatchdetailsParser
					.parseMatchdetailsFromString(
							mc.downloadMatchdetails(matchLineup.getMatchID(),
									matchLineup.getMatchTyp()), null);

			if (matchLineup.getHomeTeamId() == Integer.parseInt(teamdetailsDataMap.get("TeamID"))) {
				matchLineupTeam = matchLineup.getHomeTeam();
				if (md != null) {
					lastAttitude = md.getHomeEinstellung();
					lastTactic = md.getHomeTacticType();
				}
			} else {
				matchLineupTeam = matchLineup.getGuestTeam();
				if (md != null) {
					lastAttitude = md.getGuestEinstellung();
					lastTactic = md.getGuestTacticType();
				}
			}
			matchLineupTeam.setMatchTeamAttitude(MatchTeamAttitude.fromInt(lastAttitude));
			matchLineupTeam.setMatchTacticType(MatchTacticType.fromInt(lastTactic));
		}

		var hrfSgtringBuilder = new HRFStringBuilder();
		// Abschnitte erstellen
		// basics
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_basics"), progressIncrement);
		hrfSgtringBuilder.createBasics(teamdetailsDataMap, worldDataMap);

		// Liga
		hrfSgtringBuilder.createLeague(ligaDataMap);
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_league"), progressIncrement);

		// Club
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_club"), progressIncrement);
		hrfSgtringBuilder.createClub(clubDataMap, economyDataMap, teamdetailsDataMap);

		// team
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_team"), progressIncrement);
		hrfSgtringBuilder.createTeam(trainingDataMap);

		// lineup
		var trainerId = String.valueOf(teamdetailsDataMap.get("TrainerID"));
		int matchId = -1;
		int matchType = 0;
		if ( upcomingMatch != null){
			matchId = upcomingMatch.getMatchID();
			matchType = upcomingMatch.getMatchType().getId();
		}
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_lineups"), progressIncrement);
		hrfSgtringBuilder.createLineUp(trainerId, teamId, matchType, matchId, nextLineupDataMap);

		// economy
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_economy"), progressIncrement);
		hrfSgtringBuilder.createEconomy(economyDataMap);

		// Arena
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_arena"), progressIncrement);
		hrfSgtringBuilder.createArena(arenaDataMap);

		// players
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_players"), progressIncrement);
		hrfSgtringBuilder.createPlayers(matchLineupTeam, playersData);

		// youth players
		if ( youthplayers != null){
			HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_youth_players"), progressIncrement);
			hrfSgtringBuilder.appendYouthPlayers(youthplayers);
		}

		// xtra Data
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_world"), progressIncrement);
		hrfSgtringBuilder.createWorld(clubDataMap, teamdetailsDataMap, trainingDataMap, worldDataMap);

		// lineup of the last match
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_last_lineup"), progressIncrement);
		hrfSgtringBuilder.createLastLineUp(teamdetailsDataMap, matchLineupTeam);

		// staff
		HOMainFrame.instance().setInformation(Helper.getTranslation("ls.update_status.create_staff"), progressIncrement);
		hrfSgtringBuilder.createStaff(staffData);

		return hrfSgtringBuilder.createHRF().toString();
	}

	// //////////////////////////////////////////////////////////////////////////////
	// Helper
	// //////////////////////////////////////////////////////////////////////////////

}
