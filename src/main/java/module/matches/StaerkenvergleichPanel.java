// %1751165603:de.hattrickorganizer.gui.matches%
package module.matches;

import core.constants.player.PlayerAbility;
import core.db.DBManager;
import core.gui.comp.entry.RatingTableEntry;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupPlayer;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.util.Helper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Zeigt die Stärken eines Matches an
 */
class StaerkenvergleichPanel extends LazyImagePanel {

	private static final long serialVersionUID = -4203763992583137178L;

	private JLabel gastEinstellungLabel;
	private JLabel gastSelbstvertrauenLabel;
	private JLabel gastSterneLabel;
	private JLabel gastStimmungLabel;
	private JLabel gastTaktikLabel;
	private JLabel gastTaktikskillLabel;
	private JLabel gastTeamNameLabel;
	private JLabel gastTeamToreLabel;
	private JLabel gastTeamHatstatsLabel;
	private JLabel gastTeamLoddarLabel;
	private JLabel heimEinstellungLabel;
	private JLabel heimSelbstvertrauenLabel;
	private JLabel heimSterneLabel;
	private JLabel heimStimmungLabel;
	private JLabel heimTaktikLabel;
	private JLabel heimTaktikskillLabel;
	private JLabel homeStyleOfPlayLabel;
	private JLabel awayStyleOfPlayLabel;
	private JLabel heimTeamNameLabel;
	private JLabel heimTeamToreLabel;
	private JLabel heimTeamHatstatsLabel;
	private JLabel heimTeamLoddarLabel;
	private JLabel matchtypLabel;
	private JLabel wetterLabel;
	private JLabel zuschauerLabel;
	private RatingTableEntry gastTeamRatingTableEntry;
	private RatingTableEntry heimTeamRatingTableEntry;
	private final MatchesModel matchesModel;

	/**
	 * Creates a new StaerkenvergleichPanel object.
	 */
	StaerkenvergleichPanel(MatchesModel matchesModel) {
		this(matchesModel, false);
	}

	StaerkenvergleichPanel(MatchesModel matchesModel, boolean print) {
		super(print);
		this.matchesModel = matchesModel;
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		MatchKurzInfo info = this.matchesModel.getMatch();
		if (info == null) {
			clear();
			return;
		}

		if (info.getMatchStatus() != MatchKurzInfo.FINISHED) {
			clear();
		}

		Matchdetails details = this.matchesModel.getDetails();


		matchtypLabel.setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[info.getMatchTyp()
				.getIconArrayIndex()]));
		matchtypLabel.setText(info.getMatchTyp().getName());

		// Teams
		heimTeamNameLabel.setText(info.getHeimName());
		gastTeamNameLabel.setText(info.getGastName());

		int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		if (info.getHeimID() == teamid) {
			heimTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.TEAM_FG));
		} else {
			heimTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
		}

		if (info.getGastID() == teamid) {
			gastTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.TEAM_FG));
		} else {
			gastTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
		}

		if (info.getMatchStatus() == MatchKurzInfo.FINISHED) {

			if (details.getHomeHalfTimeGoals() >= 0) {
				heimTeamToreLabel.setText(info.getHeimTore() + " ("
						+ details.getHomeHalfTimeGoals() + ") ");
				gastTeamToreLabel.setText(info.getGastTore() + " ("
						+ details.getGuestHalfTimeGoals() + ") ");
			} else {
				heimTeamToreLabel.setText(String.valueOf(info.getHeimTore()));
				gastTeamToreLabel.setText(String.valueOf(info.getGastTore()));
			}

			String name4matchtyp = info.getMatchTyp().getName();
			if ((details.getZuschauer() <= 0) && (info.getMatchTyp().getSourceString() == "hattrick")) {
				name4matchtyp += (" ( " + HOVerwaltung.instance().getLanguageString("Reload_Match") + " )");
			}
			matchtypLabel.setText(name4matchtyp);

			zuschauerLabel.setText(String.valueOf(details.getZuschauer()));
			if (details.getWetterId() != -1) {
				wetterLabel
						.setIcon(ThemeManager.getIcon(HOIconName.WEATHER[details.getWetterId()]));
			} else {
				wetterLabel.setIcon(null);
			}
			
			// Sterne für Sieger!
			if (info.getHeimTore() > info.getGastTore()) {
				heimTeamNameLabel.setIcon(ImageUtilities.getStarIcon());
				gastTeamNameLabel.setIcon(null);
			} else if (info.getHeimTore() < info.getGastTore()) {
				heimTeamNameLabel.setIcon(null);
				gastTeamNameLabel.setIcon(ImageUtilities.getStarIcon());
			} else {
				heimTeamNameLabel.setIcon(ImageUtilities.getStarIcon());
				gastTeamNameLabel.setIcon(ImageUtilities.getStarIcon());
			}

			// Sterneanzahl
			double heimSterne = getStars(DBManager.instance().getMatchLineupPlayers(
					info.getMatchID(), info.getHeimID()));
			double gastSterne = getStars(DBManager.instance().getMatchLineupPlayers(
					info.getMatchID(), info.getGastID()));
			heimSterneLabel.setText(Helper.round(heimSterne, 1) + " ");
			gastSterneLabel.setText(Helper.round(gastSterne, 1) + " ");

			heimTeamRatingTableEntry.setRating((float) heimSterne * 2);
			gastTeamRatingTableEntry.setRating((float) gastSterne * 2);

			heimTeamHatstatsLabel.setText(String.valueOf(details.getHomeHatStats()));
			gastTeamHatstatsLabel.setText(String.valueOf(details.getAwayHatStats()));

			heimTeamLoddarLabel
					.setText(String.valueOf(Helper.round(details.getHomeLoddarStats(), 2)));
			gastTeamLoddarLabel
					.setText(String.valueOf(Helper.round(details.getAwayLoddarStats(), 2)));

			// Einstellung
			heimEinstellungLabel.setText(getEinstellungText(details.getHomeEinstellung()));
			gastEinstellungLabel.setText(getEinstellungText(details.getGuestEinstellung()));

			// Taktik
			heimTaktikLabel.setText(Matchdetails.getNameForTaktik(details.getHomeTacticType()));
			gastTaktikLabel.setText(Matchdetails.getNameForTaktik(details.getGuestTacticType()));
			
			// style of play
			int homeStyleOfPlay = matchesModel.getHomeTeamInfo().getStyleOfPlay();
			int awayStyleOfPlay = matchesModel.getAwayTeamInfo().getStyleOfPlay();
			
			// old matches don't have style of play, use string output method from Lineup
			if (homeStyleOfPlay != -1000) {
				homeStyleOfPlayLabel.setText(MatchLineupTeam.getStyleOfPlayName(homeStyleOfPlay));
			} else {
				homeStyleOfPlayLabel.setText("");
			}
			
			if (awayStyleOfPlay != -1000) {
				awayStyleOfPlayLabel.setText(MatchLineupTeam.getStyleOfPlayName(awayStyleOfPlay));
			} else {
				awayStyleOfPlayLabel.setText("");
			}

			// Skill
			if (details.getHomeTacticType() != 0) {
				heimTaktikskillLabel.setText(PlayerAbility.getNameForSkill(details
						.getHomeTacticSkill()));
			} else {
				heimTaktikskillLabel.setText("");
			}

			if (details.getGuestTacticType() != 0) {
				gastTaktikskillLabel.setText(PlayerAbility.getNameForSkill(details
						.getGuestTacticSkill()));
			} else {
				gastTaktikskillLabel.setText("");
			}

			// Stimmung und Selbstvertrauen
			int hrfid = DBManager.instance().getHRFID4Date(info.getMatchDateAsTimestamp());
			String[] stimmungSelbstvertrauen = DBManager.instance().getStimmmungSelbstvertrauen(
					hrfid);

			if (info.getHeimID() == teamid) {
				heimStimmungLabel.setText(stimmungSelbstvertrauen[0]);
				gastStimmungLabel.setText("");
				heimSelbstvertrauenLabel.setText(stimmungSelbstvertrauen[1]);
				gastSelbstvertrauenLabel.setText("");
			} else if (info.getGastID() == teamid) {
				heimStimmungLabel.setText("");
				gastStimmungLabel.setText(stimmungSelbstvertrauen[0]);
				heimSelbstvertrauenLabel.setText("");
				gastSelbstvertrauenLabel.setText(stimmungSelbstvertrauen[1]);
			} else {
				heimStimmungLabel.setText("");
				gastStimmungLabel.setText("");
				heimSelbstvertrauenLabel.setText("");
				gastSelbstvertrauenLabel.setText("");
			}
		}
	}

	private void addListeners() {
		this.matchesModel.addMatchModelChangeListener(new MatchModelChangeListener() {

			@Override
			public void matchChanged() {
				setNeedsRefresh(true);
			}
		});
	}

	private void initComponents() {
		setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

		final GridBagLayout mainlayout = new GridBagLayout();
		final GridBagConstraints mainconstraints = new GridBagConstraints();
		mainconstraints.anchor = GridBagConstraints.NORTH;
		mainconstraints.fill = GridBagConstraints.HORIZONTAL;
		mainconstraints.weighty = 0.1;
		mainconstraints.weightx = 1.0;
		mainconstraints.insets = new Insets(4, 6, 4, 6);

		setLayout(mainlayout);

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.weighty = 0.0;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 3, 2, 2);

		final JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager
				.getColor(HOColorName.PANEL_BORDER)));
		panel.setBackground(getBackground());

		// Match
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 5;
		matchtypLabel = new JLabel();
		matchtypLabel.setFont(matchtypLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(matchtypLabel, constraints);
		panel.add(matchtypLabel);

		// Platzhalter
		JLabel label = new JLabel("  ");
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 3;
		constraints.gridy = 1;
		constraints.gridheight = 20;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		label = new JLabel(" ");
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Zuschauer"));
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		zuschauerLabel = new JLabel();
		zuschauerLabel.setFont(zuschauerLabel.getFont().deriveFont(Font.BOLD));
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.2;
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		layout.setConstraints(zuschauerLabel, constraints);
		panel.add(zuschauerLabel);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.weather"));
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 4;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 5;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		wetterLabel = new JLabel();
		wetterLabel.setPreferredSize(new Dimension(28, 28));
		layout.setConstraints(wetterLabel, constraints);
		panel.add(wetterLabel);

		// Platzhalter
		label = new JLabel(" ");
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		constraints.gridwidth = 6;
		layout.setConstraints(label, constraints);
		panel.add(label);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Heim"));
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
		label.setHorizontalAlignment(SwingConstants.LEFT);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		layout.setConstraints(label, constraints);
		panel.add(label);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Gast"));
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
		label.setHorizontalAlignment(SwingConstants.LEFT);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 4;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		// Teams mit Ergebnis
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.result"));
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 0;
		constraints.gridy = 4;
		layout.setConstraints(label, constraints);
		panel.add(label);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 1;
		constraints.gridy = 4;
		heimTeamNameLabel = new JLabel();
		heimTeamNameLabel.setPreferredSize(new Dimension(140, 14));
		heimTeamNameLabel.setFont(heimTeamNameLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimTeamNameLabel, constraints);
		panel.add(heimTeamNameLabel);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 2;
		constraints.gridy = 4;
		heimTeamToreLabel = new JLabel();
		heimTeamToreLabel.setFont(heimTeamToreLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimTeamToreLabel, constraints);
		panel.add(heimTeamToreLabel);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 4;
		constraints.gridy = 4;
		gastTeamNameLabel = new JLabel();
		gastTeamNameLabel.setPreferredSize(new Dimension(140, 14));
		gastTeamNameLabel.setFont(gastTeamNameLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastTeamNameLabel, constraints);
		panel.add(gastTeamNameLabel);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 5;
		constraints.gridy = 4;
		gastTeamToreLabel = new JLabel();
		gastTeamToreLabel.setFont(gastTeamToreLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastTeamToreLabel, constraints);
		panel.add(gastTeamToreLabel);

		// Sterne
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Rating"));
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 1;
		constraints.gridy = 5;
		heimTeamRatingTableEntry = new RatingTableEntry();
		layout.setConstraints(heimTeamRatingTableEntry.getComponent(false), constraints);
		panel.add(heimTeamRatingTableEntry.getComponent(false));

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 2;
		constraints.gridy = 5;
		heimSterneLabel = new JLabel();
		heimSterneLabel.setFont(heimSterneLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimSterneLabel, constraints);
		panel.add(heimSterneLabel);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 4;
		constraints.gridy = 5;
		gastTeamRatingTableEntry = new RatingTableEntry();
		layout.setConstraints(gastTeamRatingTableEntry.getComponent(false), constraints);
		panel.add(gastTeamRatingTableEntry.getComponent(false));

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 5;
		constraints.gridy = 5;
		gastSterneLabel = new JLabel();
		gastSterneLabel.setFont(gastSterneLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastSterneLabel, constraints);
		panel.add(gastSterneLabel);

		// HatStats
		label = new JLabel(HOVerwaltung.instance()
				.getLanguageString("ls.match.ratingtype.hatstats"));
		add(panel, label, layout, constraints, 0, 6);
		heimTeamHatstatsLabel = new JLabel();
		add(panel, heimTeamHatstatsLabel, layout, constraints, 1, 6);
		gastTeamHatstatsLabel = new JLabel();
		add(panel, gastTeamHatstatsLabel, layout, constraints, 4, 6);

		// LoddarStats
		label = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ls.match.ratingtype.loddarstats"));
		add(panel, label, layout, constraints, 0, 7);
		heimTeamLoddarLabel = new JLabel();
		add(panel, heimTeamLoddarLabel, layout, constraints, 1, 7);
		gastTeamLoddarLabel = new JLabel();
		add(panel, gastTeamLoddarLabel, layout, constraints, 4, 7);

		// Einstellung
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.teamattitude"));
		add(panel, label, layout, constraints, 0, 8);
		heimEinstellungLabel = new JLabel();
		add(panel, heimEinstellungLabel, layout, constraints, 1, 8);
		gastEinstellungLabel = new JLabel();
		add(panel, gastEinstellungLabel, layout, constraints, 4, 8);

		// Taktiktyp
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.tactic"));
		add(panel, label, layout, constraints, 0, 9);
		heimTaktikLabel = new JLabel();
		add(panel, heimTaktikLabel, layout, constraints, 1, 9);
		gastTaktikLabel = new JLabel();
		add(panel, gastTaktikLabel, layout, constraints, 4, 9);

		// Taktikskill
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.tacticalskill"));
		add(panel, label, layout, constraints, 0, 10);
		heimTaktikskillLabel = new JLabel();
		add(panel, heimTaktikskillLabel, layout, constraints, 1, 10);
		gastTaktikskillLabel = new JLabel();
		add(panel, gastTaktikskillLabel, layout, constraints, 4, 10);
		
		// StyleOfPlay
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.styleofPlay"));
		add(panel, label, layout, constraints, 0, 11);
		homeStyleOfPlayLabel = new JLabel();
		add(panel, homeStyleOfPlayLabel, layout, constraints, 1, 11);
		awayStyleOfPlayLabel = new JLabel();
		add(panel, awayStyleOfPlayLabel, layout, constraints, 4, 11);

		// Stimmung
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit"));
		add(panel, label, layout, constraints, 0, 12);
		heimStimmungLabel = new JLabel();
		add(panel, heimStimmungLabel, layout, constraints, 1, 12);
		gastStimmungLabel = new JLabel();
		add(panel, gastStimmungLabel, layout, constraints, 4, 12);

		// Selbstvertrauen
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.confidence"));
		label.setPreferredSize(new Dimension(label.getPreferredSize().width + 10, label
				.getPreferredSize().height));
		add(panel, label, layout, constraints, 0, 13);
		heimSelbstvertrauenLabel = new JLabel();
		add(panel, heimSelbstvertrauenLabel, layout, constraints, 1, 13);
		gastSelbstvertrauenLabel = new JLabel();
		add(panel, gastSelbstvertrauenLabel, layout, constraints, 4, 13);

		mainconstraints.gridx = 0;
		mainconstraints.gridy = 0;
		mainlayout.setConstraints(panel, mainconstraints);
		add(panel);

		clear();
	}

	private void add(JPanel panel, JLabel label, GridBagLayout layout,
			GridBagConstraints constraints, int x, int y) {
		if (x == 0) {
			constraints.weightx = 0.0;
			constraints.gridwidth = 1;
		} else {
			constraints.weightx = 1.0;
			constraints.gridwidth = 2;
		}

		constraints.gridx = x;
		constraints.gridy = y;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);
	}

	private void clear() {
		zuschauerLabel.setText(" ");
		wetterLabel.setIcon(null);
		heimTeamNameLabel.setText(" ");
		gastTeamNameLabel.setText(" ");
		matchtypLabel.setIcon(null);
		matchtypLabel.setText(" ");
		heimTeamRatingTableEntry.setRating(0, true);
		gastTeamRatingTableEntry.setRating(0, true);
		heimTeamToreLabel.setText(" ");
		gastTeamToreLabel.setText(" ");
		heimTeamNameLabel.setIcon(null);
		gastTeamNameLabel.setIcon(null);
		heimSterneLabel.setText(" ");
		gastSterneLabel.setText(" ");
		heimTeamHatstatsLabel.setText(" ");
		gastTeamHatstatsLabel.setText(" ");
		heimTeamLoddarLabel.setText(" ");
		gastTeamLoddarLabel.setText(" ");
		heimEinstellungLabel.setText("");
		gastEinstellungLabel.setText("");
		heimTaktikLabel.setText("");
		homeStyleOfPlayLabel.setText("");
		awayStyleOfPlayLabel.setText("");
		gastTaktikLabel.setText("");
		heimTaktikskillLabel.setText("");
		gastTaktikskillLabel.setText("");
		heimStimmungLabel.setText("");
		gastStimmungLabel.setText("");
		heimSelbstvertrauenLabel.setText("");
		gastSelbstvertrauenLabel.setText("");
	}

	private double getStars(List<MatchLineupPlayer> players) {
		double stars = 0;
		for (MatchLineupPlayer player : players) {
			if ((player.getId() < IMatchRoleID.startReserves)
					&& (player.getId() >= IMatchRoleID.startLineup)) {
				double rating = (double) player.getRating();

				if (rating > 0) {
					stars += rating;
				}
			}
		}
		return stars;
	}

	private String getEinstellungText(int einstellung) {
		switch (einstellung) {
		case IMatchDetails.EINSTELLUNG_NORMAL:
			return HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.normal");
		case IMatchDetails.EINSTELLUNG_PIC:
			return HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.playitcool");
		case IMatchDetails.EINSTELLUNG_MOTS:
			return HOVerwaltung.instance().getLanguageString(
					"ls.team.teamattitude.matchoftheseason");
		default:
			return "";
		}
	}
}
