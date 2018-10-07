//package module.opponentspy.OpponentTeam;
//
//import core.file.xml.TeamInfo;
//import core.file.xml.XMLTeamDetailsParser;
//import core.gui.HOMainFrame;
//import core.model.HOModel;
//import core.model.match.MatchKurzInfo;
//import core.model.match.MatchLineup;
//import core.model.match.MatchLineupPlayer;
//import core.model.match.MatchLineupTeam;
//import core.model.player.Spieler;
//import core.net.MyConnector;
//import core.net.OnlineWorker;
//import core.util.Helper;
//import module.lineup.Lineup;
//import module.opponentspy.CalcPlayerBaseProvider;
//import module.opponentspy.CalcVariables;
//import module.opponentspy.OppPlayerSkillEstimator;
//import module.opponentspy.OpponentPlayer;
//import module.opponentspy.RoleAssigner;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.Vector;
//
//import javax.swing.JOptionPane;
//
//public class OpponentTeamManager {
//
//	private int teamId;
//	private TeamInfo teamInfo;
//	private List<MatchKurzInfo> matchList;
//	private HashMap<Integer, OpponentPlayer> playerMap;
//	private List<MatchLineup> lineups;
//
//	public int getTeamId() {
//		return teamId;
//	}
//
//	public void setTeamId(int teamId) {
//		this.teamId = teamId;
//	}
//
//	public HOModel getOpponentModel() {
//		HOModel model = new HOModel();
//
//		if (teamId <= 0) {
//			return null;
//		}
//		try {
//			MyConnector connector = MyConnector.instance();
//
//			String teamDetails = connector.getTeamdetails(teamId);
//
//			if (teamDetails == null) {
//				return null;
//			}
//
//			List<TeamInfo> teamInfos = XMLTeamDetailsParser.getTeamInfoFromString(teamDetails);
//
//			if (! (teamInfos.size() > 0))
//				return null;
//
//			for (TeamInfo info : teamInfos) {
//				if (info.getTeamId() == teamId) {
//					teamInfo = info;
//					break;
//				}
//			}
//
//			if (teamInfo == null)
//				return null;
//
//			getPlayers(connector, teamId);
//			getLineupInfo(connector, teamId);
//			addPlayedPositions();
//
//			OppPlayerSkillEstimator estimator = new OppPlayerSkillEstimator();
//			Vector<Spieler> playerVector = new Vector<Spieler>();
//
//			for (Entry<Integer, OpponentPlayer> entry : playerMap.entrySet()) {
//				try {
//
//
//					OpponentPlayer player = entry.getValue();
//
//					player.setCalculationRole(RoleAssigner.getOpponentPlayerRole(player));
//
//					estimator.CalculateSkillsForPlayer(player);
//					playerVector.add(player);
//
//				} catch (Exception e) {
//					System.out.println("Error: " + entry.getValue().getSpielerID() + " " + entry.getValue().getName());
//					int i = 2;
//				}
//			}
//
//
//			model.setSpieler(playerVector);
//			model.setAufstellung(new Lineup());
//
//		}
//		catch (Exception ex) {
//			Helper.showMessage(HOMainFrame.instance(), "Error happened", "Error happened",
//					JOptionPane.ERROR_MESSAGE);
//			return null;
//		}
//
//		return model;
//	}
//
//	private void getPlayers(MyConnector connector, int teamId) {
//
//		if (playerMap == null)
//			playerMap = new HashMap<Integer, OpponentPlayer>();
//
//		List<Spieler> players = OnlineWorker.getTeamPlayers(teamId);
//
//		for (Spieler player : players) {
//
//			OpponentPlayer opponentPlayer;
//
//			if (!playerMap.containsKey(player.getSpielerID())) {
//				opponentPlayer = new OpponentPlayer();
//				playerMap.put(player.getSpielerID(), opponentPlayer);
//			} else {
//				opponentPlayer = playerMap.get(player.getSpielerID());
//			}
//
//			opponentPlayer.setName(player.getName());
//			opponentPlayer.setSpielerID(player.getSpielerID());
//			opponentPlayer.setTSI(player.getTSI());
//			opponentPlayer.setGehalt(player.getGehalt());
//			opponentPlayer.setAlter(player.getAlter());
//			opponentPlayer.setAgeDays(player.getAgeDays());
//			opponentPlayer.setVerletzt(player.getVerletzt());
//			opponentPlayer.setSpezialitaet(player.getSpezialitaet());
//			opponentPlayer.setForm(player.getForm());
//			opponentPlayer.setKondition(player.getKondition());
//
//			if (teamInfo != null)
//				opponentPlayer.setPlayingAbroad(teamInfo.getLeagueId() != player.getNationalitaet());
//
//		}
//	}
//
//	private void getLineupInfo(MyConnector connector, int teamId) throws Exception {
//
//		matchList = OnlineWorker.getMatches(teamId, new Date());
//
//		if (playerMap == null)
//			playerMap = new HashMap<Integer, OpponentPlayer>();
//
//		for(MatchKurzInfo match : matchList) {
//
//			if (match.getMatchStatus() == 1) { // Finished
//
//				MatchLineup lineup = OnlineWorker.fetchLineup(match.getMatchID(), teamId, match.getMatchTyp());
//
//				if (lineups == null)
//					lineups = new ArrayList<MatchLineup>();
//
//				lineups.add(lineup);
//
//			}
//		}
//	}
//
//	private void addPlayedPositions() {
//		for (MatchLineup lineup : lineups) {
//
//			MatchLineupTeam teamLineup = null;
//
//			if (lineup.getHeim() != null)
//				teamLineup = lineup.getHeim();
//			else if (lineup.getGast() != null)
//				teamLineup = lineup.getGast();
//
//			if (teamLineup != null) {
//
//				for (MatchLineupPlayer player : teamLineup.getAufstellung()) {
//
//					// Only add playerPosition if on the team currently
//
//					if (playerMap.containsKey(player.getSpielerId())) {
//
//						playerMap.get(player.getSpielerId()).addPlayedPosition(
//								new PlayedPosition(player.getFieldPos(), player.getTaktik(), lineup.getMatchTyp(),
//										player.getRating(), player.getRatingStarsEndOfMatch()));
//					}
//				}
//			}
//		}
//	}
//}
