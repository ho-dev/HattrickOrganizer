package module.lineup;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.match.StyleOfPlay;
import core.model.player.Player;
import core.util.HOLogger;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import module.lineup.lineup.PlayerPositionPanel;
import module.lineup.ratings.LineupRatingPanel;
import static core.model.player.IMatchRoleID.*;


/**
 * Listener for the "copy ratings to clipboard" feature at the lineup screen.
 *
 * @author aik
 */
public class CopyListener implements ActionListener {

	private final LineupRatingPanel lineupRatingPanel;
	private static final String LF = System.getProperty("line.separator", "\n");
	private static final String EMPTY = "";
	private static final String SPACE = " ";
	private final JMenuItem miPlaintext = new JMenuItem(HOVerwaltung.instance().getLanguageString("Lineup.CopyRatings.PlainText"));
	private final JMenuItem miHattickMLDef = new JMenuItem(HOVerwaltung.instance().getLanguageString("Lineup.CopyRatings.HattrickML"));
	private final JMenuItem miLineup = new JMenuItem(HOVerwaltung.instance().getLanguageString("Aufstellung"));
	private final JMenuItem miLineupAndRatings = new JMenuItem(HOVerwaltung.instance().getLanguageString("Lineup.CopyRatings.LineupAndRatings"));
	
	final JPopupMenu menu = new JPopupMenu();

	/**
	 * Create the CopyListener and initialize the gui components.
	 */
	public CopyListener(LineupRatingPanel lineupRatingPanel) {
		this.lineupRatingPanel = lineupRatingPanel;
		miPlaintext.addActionListener(this);
		miHattickMLDef.addActionListener(this);
		miLineup.addActionListener(this);
		miLineupAndRatings.addActionListener(this);
		menu.add(miPlaintext);
		menu.add(miHattickMLDef);
		menu.add(miLineup);
		menu.add(miLineupAndRatings);
		
	}

	/**
	 * Handle action events (shop popup menu or copy ratings).
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e != null && e.getSource().equals(miPlaintext)) {
			menu.setVisible(false);
			copyToClipboard(getRatingsAsText());
		} else if (e != null && e.getSource().equals(miHattickMLDef)) {
			copyToClipboard(getRatingsAsHattrickML_DefTop());
			menu.setVisible(false);
		} else if (e != null && e.getSource().equals(miLineup)) {
			copyToClipboard(getLineupRatingPanel());
			menu.setVisible(false);
		} else if (e != null && e.getSource().equals(miLineupAndRatings)) {
			copyToClipboard(getLineupAndRatings());
			menu.setVisible(false);
		} else if (e != null && e.getSource() != null && e.getSource() instanceof Component) {
			menu.show((Component)e.getSource(), 1, 1);
		}
	}

	/**
	 * Copy the given text into the system clip board.
	 */
	public static void copyToClipboard(final String txt) {
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txt), null);
		} catch (Exception e) {
			HOLogger.instance().error(CopyListener.class, e);
		}
	}

	/**
	 * Get ratings as normal text, ordered like in HT.
	 */
	private String getRatingsAsText() {
		var sb = new StringBuilder();
		if (lineupRatingPanel != null) {
			sb.append(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield")).append(": ").append(lineupRatingPanel.getMidfieldRating()).append(LF);
			sb.append(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence")).append(": ").append(lineupRatingPanel.getRightDefenseRating()).append(LF);
			sb.append(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence")).append(": ").append(lineupRatingPanel.getCentralDefenseRating()).append(LF);
			sb.append(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence")).append(": ").append(lineupRatingPanel.getLeftDefenseRating()).append(LF);
			sb.append(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack")).append(": ").append(lineupRatingPanel.getRightAttackRating()).append(LF);
			sb.append(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack")).append(": ").append(lineupRatingPanel.getCentralAttackRating()).append(LF);
			sb.append(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack")).append(": ").append(lineupRatingPanel.getLeftAttackRating()).append(LF);
		}
		return sb.toString();
	}

	/**
	 * Get ratings in a HT-ML style table.
	 */
	private String getRatingsAsHattrickML_DefTop() {
		var sb = new StringBuilder();
		if (lineupRatingPanel != null) {
			sb.append("[table]");
			sb.append("[tr][th][/th][th]").append(HOVerwaltung.instance().getLanguageString("Rechts"));
			sb.append("[/th][th]").append(HOVerwaltung.instance().getLanguageString("Mitte"));
			sb.append("[/th][th]").append(HOVerwaltung.instance().getLanguageString("Links")).append("[/th][/tr]").append(LF);
			sb.append("[tr][th]").append(HOVerwaltung.instance().getLanguageString("match.sector.defence"));
			sb.append("[/th][td align=center]").append(lineupRatingPanel.getRightDefenseRating());
			sb.append("[/td][td align=center]").append(lineupRatingPanel.getCentralDefenseRating());
			sb.append("[/td][td align=center]").append(lineupRatingPanel.getLeftDefenseRating());
			sb.append("[/td][/tr]").append(LF);
			sb.append("[tr][th]").append(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
			sb.append("[/th][td colspan=3 align=center]");
			sb.append(lineupRatingPanel.getMidfieldRating()).append("[/td][/tr]").append(LF);
			sb.append("[tr][th]").append(HOVerwaltung.instance().getLanguageString("Attack"));
			sb.append("[/th][td align=center]").append(lineupRatingPanel.getRightAttackRating());
			sb.append("[/td][td align=center]").append(lineupRatingPanel.getCentralAttackRating());
			sb.append("[/td][td align=center]").append(lineupRatingPanel.getLeftAttackRating());
			sb.append("[/td][/tr]").append(LF);
			sb.append("[/table]");
			sb.append(LF);
		}
		return sb.toString();
	}
	
	private String getLineupRatingPanel() {
		if (lineupRatingPanel == null) return EMPTY;
	  	//LineupPositionsPanel lPanel = HOMainFrame.INSTANCE.getLineupPanel().getLineupPositionsPanel();
		ArrayList<PlayerPositionPanel> pos = Objects.requireNonNull(HOMainFrame.INSTANCE.getLineupPanel()).getAllPositions();
		String goalie, rightWB, rightCD, middleCD, leftCD, leftWB, rightW, rightIM, middleIM, leftIM, leftW, rightFW, middleFW, leftFW;
		goalie = rightWB = rightCD = middleCD = leftCD = leftWB = rightW = rightIM = middleIM = leftIM = leftW = rightFW = middleFW = leftFW = EMPTY;
		for (PlayerPositionPanel p : pos) {
			int positionID = p.getPositionsID();
			int playerID = p.getiSelectedPlayerId();
			if (playerID != -1) {
				Player player = p.getSelectedPlayer();
				switch (positionID) {
					// ugly that the imported static constants from IMatchRoleID are named in lower case
					case keeper -> goalie = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportNameForKeeper();
					case rightBack -> rightWB = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case rightCentralDefender -> rightCD = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case middleCentralDefender -> middleCD = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftCentralDefender -> leftCD = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftBack -> leftWB = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case rightWinger -> rightW = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case rightInnerMidfield -> rightIM = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case centralInnerMidfield -> middleIM = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftInnerMidfield -> leftIM = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftWinger -> leftW = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case rightForward -> rightFW = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case centralForward -> middleFW = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftForward -> leftFW = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					default -> HOLogger.instance().warning(getClass(), "positionID: " + positionID + " not found");
				}
			}
		}
		HOVerwaltung hov = HOVerwaltung.instance();
		var lineupData = hov.getModel().getCurrentLineup();
		byte system = lineupData.getCurrentTeamFormationCode();
		String systemName = lineupData.getSystemName(system);

		String header = "[table][tr][th colspan=5 align=center]" + hov.getLanguageString("Aufstellung") + 
					SPACE + systemName + "[/th][/tr]" + LF;
		String keeper = "[tr][td colspan=5 align=center]" + goalie + "[/td][/tr]" + LF;
		String defence = "[tr][td align=center]" + rightWB + "[/td]" + "[td align=center]" + rightCD + "[/td]" + "[td align=center]" + middleCD + "[/td]" +
				"[td align=center]" + leftCD + "[/td]" + "[td align=center]" + leftWB + "[/td][/tr]" + LF;
		String middle = "[tr][td align=center]" + rightW + "[/td]" + "[td align=center]" + rightIM + "[/td]" + "[td align=center]" + middleIM + "[/td]" +
				"[td align=center]" + leftIM + "[/td]" + "[td align=center]" + leftW + "[/td][/tr]" + LF;
		String attack = "[tr][td][/td][td align=center]" + rightFW + "[/td]" + "[td align=center]" + middleFW + "[/td]" + "[td align=center]" + leftFW + "[/td]" +
				"[td][/td][/tr]" + LF + "[/table]";

		return header + keeper + defence + middle + attack;
	}
	
	private String getLineupAndRatings() {
		if (lineupRatingPanel == null) return EMPTY;
		//LineupPositionsPanel lPanel = HOMainFrame.INSTANCE.getLineupPanel().getLineupPositionsPanel();
		ArrayList<PlayerPositionPanel> pos = HOMainFrame.INSTANCE.getLineupPanel().getAllPositions();
		String goalie, rightWB, rightCD, middleCD, leftCD, leftWB, rightW, rightIM, middleIM, leftIM, leftW, rightFW, middleFW, leftFW;
		goalie = rightWB = rightCD = middleCD = leftCD = leftWB = rightW = rightIM = middleIM = leftIM = leftW = rightFW = middleFW = leftFW = EMPTY;
		for (PlayerPositionPanel p : pos) {
			int positionID = p.getPositionsID();
			int playerID = p.getiSelectedPlayerId();
			if (playerID != -1) {
				Player player = p.getSelectedPlayer();
				switch (positionID) {
					// ugly that the imported static constants from IMatchRoleID are named in lower case
					case keeper -> goalie = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportNameForKeeper();
					case rightBack -> rightWB = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case rightCentralDefender -> rightCD = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case middleCentralDefender -> middleCD = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftCentralDefender -> leftCD = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftBack -> leftWB = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case rightWinger -> rightW = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case rightInnerMidfield -> rightIM = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case centralInnerMidfield -> middleIM = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftInnerMidfield -> leftIM = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftWinger -> leftW = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case rightForward -> rightFW = p.getTacticSymbol() + SPACE + player.getShortName() + player.getSpecialtyExportName();
					case centralForward -> middleFW = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					case leftForward -> leftFW = player.getShortName() + SPACE + p.getTacticSymbol() + player.getSpecialtyExportName();
					default -> HOLogger.instance().warning(getClass(), "positionID: " + positionID + " not found");
				}
			}
		}
		HOVerwaltung hov = HOVerwaltung.instance();
		Lineup lineupData = hov.getModel().getCurrentLineup();
		byte system = lineupData.getCurrentTeamFormationCode();
		String systemName = lineupData.getSystemName(system);
		int tacticType = lineupData.getTacticType();
		String tacticName = Matchdetails.getShortTacticName(tacticType);
		var ratingPredictionModel = hov.getModel().getRatingPredictionModel();
		var tacticLevel = ratingPredictionModel.getTacticRating(lineupData, 0);
		String level = (tacticLevel != 0) ? " (" + tacticLevel + ")" : "";
		int attitude = lineupData.getAttitude();
		String attitudeName = lineupData.getAttitudeName(attitude);
		var styleOfPlay = StyleOfPlay.fromInt(lineupData.getCoachModifier());
		String styleOfPlayName = MatchLineupTeam.getStyleOfPlayName(styleOfPlay);
		
		String header = "[table][tr][th colspan=8 align=center]" + hov.getLanguageString("Aufstellung") + 
				SPACE + systemName + " // " + hov.getLanguageString("ls.team.teamattitude") + ": " +
				attitudeName + " // " + hov.getLanguageString("ls.team.tactic") + ": " + tacticName + 
				level + " // " + hov.getLanguageString("ls.team.styleofPlay") + ": " + styleOfPlayName + "[/th][/tr]" + LF;
		String keeper = "[tr][td colspan=5 align=center]" + goalie + "[/td]" + "[th align=center]"+HOVerwaltung.instance().getLanguageString("Rechts") +
				"[/th][th align=center]"+HOVerwaltung.instance().getLanguageString("Mitte") +
				"[/th][th align=center]"+HOVerwaltung.instance().getLanguageString("Links")+"[/th][/tr]" + LF;
		String defence = "[tr][td align=center]" + rightWB + "[/td]" + "[td align=center]" + rightCD + "[/td]" + "[td align=center]" + middleCD + "[/td]" +
				"[td align=center]" + leftCD + "[/td]" + "[td align=center]" + leftWB + "[/td]" + "[td align=center]"+ lineupRatingPanel.getRightDefenseRating() +
				"[/td][td align=center]"+ lineupRatingPanel.getCentralDefenseRating() + "[/td][td align=center]"+ lineupRatingPanel.getLeftDefenseRating() + "[/td][/tr]" + LF;
		String middle = "[tr][td align=center]" + rightW + "[/td]" + "[td align=center]" + rightIM + "[/td]" + "[td align=center]" + middleIM + "[/td]" +
				"[td align=center]" + leftIM + "[/td]" + "[td align=center]" + leftW + "[/td]" + "[td][/td]" + "[td align=center]" +
				lineupRatingPanel.getMidfieldRating()+ "[/td][td]" + "[/td][/tr]" + LF;
		String attack = "[tr][td][/td][td align=center]" + rightFW + "[/td]" + "[td align=center]" + middleFW + "[/td]" + "[td align=center]" + leftFW + "[/td]" +
				"[td][/td]" + "[td align=center]"+ lineupRatingPanel.getRightAttackRating() + "[/td][td align=center]"+ lineupRatingPanel.getCentralAttackRating() +
				"[/td][td align=center]"+ lineupRatingPanel.getLeftAttackRating() + "[/td][/tr]" + LF + "[/table]";
		return header + keeper + defence + middle + attack;
	}
}
