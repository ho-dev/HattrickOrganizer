package module.lineup;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.constants.player.PlayerAbility;
import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.rating.RatingPredictionConfig;
import core.util.Helper;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


public final class LineupSettingsPanel extends ImagePanel implements Refreshable, ItemListener {

	private final JComboBox<CBItem> m_jcbTeamConfidence = new JComboBox<>(TeamConfidence.ITEMS);

	private final CBItem[] TRAINER_TYPES = {
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.coachtype.defensive"), 0),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.coachtype.neutral"), 2),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.coachtype.offensive"), 1), };

	private final JComboBox<CBItem> m_jcbTrainerType = new JComboBox<>(TRAINER_TYPES);
	private final JComboBox<CBItem> m_jcbMainTeamSpirit = new JComboBox<>(TeamSpirit.ITEMS);
	private final CBItem[] SUB_TEAM_SPIRIT = {
			new CBItem(HOVerwaltung.instance().getLanguageString("verylow"), 0),
			new CBItem(HOVerwaltung.instance().getLanguageString("low"), 1),
			new CBItem(HOVerwaltung.instance().getLanguageString("Durchschnitt"), 2),
			new CBItem(HOVerwaltung.instance().getLanguageString("high"), 3),
			new CBItem(HOVerwaltung.instance().getLanguageString("veryhigh"), 4) };
	private final JComboBox<CBItem> m_jcbSubTeamSpirit = new JComboBox<>(SUB_TEAM_SPIRIT);
	private final CBItem[] LOCATION = {
			new CBItem(HOVerwaltung.instance().getLanguageString("Heimspiel"),
					IMatchDetails.LOCATION_HOME), //
			new CBItem(HOVerwaltung.instance().getLanguageString("matchlocation.away"),
					IMatchDetails.LOCATION_AWAY), //
			new CBItem(HOVerwaltung.instance().getLanguageString("matchlocation.awayderby"),
					IMatchDetails.LOCATION_AWAYDERBY), //
			new CBItem(HOVerwaltung.instance().getLanguageString("matchlocation.tournament"),
					IMatchDetails.LOCATION_TOURNAMENT) //
	};
	private final JComboBox<CBItem> m_jcbLocation = new JComboBox<>(LOCATION);

	private final CBItem[] PULLBACK_MINUTE = {
			new CBItem(HOVerwaltung.instance().getLanguageString("PullBack.None"), 90),
			new CBItem("85", 85), new CBItem("80", 80), new CBItem("75", 75), new CBItem("70", 70),
			new CBItem("65", 65), new CBItem("60", 60), new CBItem("55", 55), new CBItem("50", 50),
			new CBItem("45", 45), new CBItem("40", 40), new CBItem("35", 35), new CBItem("30", 30),
			new CBItem("25", 25), new CBItem("20", 20), new CBItem("15", 15), new CBItem("10", 10),
			new CBItem("5", 5),
			new CBItem(HOVerwaltung.instance().getLanguageString("PullBack.WholeGame"), 0) };

	private final JComboBox<CBItem> m_jcbPullBackMinute = new JComboBox<>(PULLBACK_MINUTE);

	private final JCheckBox m_jchPullBackOverride = new JCheckBox(HOVerwaltung.instance()
			.getLanguageString("PullBack.Override"), false);

	private final CBItem[] TACTICAL_ASSISTANTS = {
			new CBItem("0", 0),
			new CBItem("1", 1),
			new CBItem("2", 2),
			new CBItem("3", 3),
			new CBItem("4", 4),
			new CBItem("5", 5)
	};
	
	private final JComboBox<CBItem>   m_jcbTacticalAssistants = new JComboBox<>(TACTICAL_ASSISTANTS);


	public LineupSettingsPanel() {
		initComponents();
		core.gui.RefreshManager.instance().registerRefreshable(this);
	}

	private void setLocation(int location) {
		// Set the location
		Helper.setComboBoxFromID(m_jcbLocation, location);

		if (location == IMatchDetails.LOCATION_TOURNAMENT) {
			setTeamSpirit(6, 2); // Set Team Spirit to content (cf: https://www.hattrick.org/Help/Rules/Tournaments.aspx)
			m_jcbMainTeamSpirit.setEnabled(false);
			m_jcbSubTeamSpirit.setEnabled(false);
			setConfidence(6); // Set Team Spirit to wonderful (cf: https://www.hattrick.org/Help/Rules/Tournaments.aspx)
			m_jcbTeamConfidence.setEnabled(false);
		}
		else
		{
			m_jcbMainTeamSpirit.setEnabled(true);
			m_jcbSubTeamSpirit.setEnabled(true);
			m_jcbTeamConfidence.setEnabled(true);
		}
	}


	public void setLabels() {
		final HOModel homodel = HOVerwaltung.instance().getModel();
		final Lineup currentLineup = homodel.getLineup();

		setTeamSpirit(homodel.getTeam().getStimmungAsInt(), homodel.getTeam().getSubStimmung());
		setConfidence(homodel.getTeam().getSelbstvertrauenAsInt());
		setTrainerType(homodel.getTrainer().getTrainerTyp());
		setTacticalAssistants(homodel.getClub().getTacticalAssistantLevels());
		setLocation(currentLineup.getLocation());
		setPullBackMinute(currentLineup.getPullBackMinute());
		m_jcbPullBackMinute.setEnabled(!currentLineup.isPullBackOverride());
		setPullBackOverride(currentLineup.isPullBackOverride());
	}

	public void setConfidence(int iTeamConfidence) {
		Helper.setComboBoxFromID(m_jcbTeamConfidence, iTeamConfidence);
	}

	public void setTeamSpirit(int iTeamSpirit, int iSubTeamSpirit) {
		Helper.setComboBoxFromID(m_jcbMainTeamSpirit, iTeamSpirit);
		Helper.setComboBoxFromID(m_jcbSubTeamSpirit, iSubTeamSpirit);
	}

	public void setTrainerType(int newTrainerType) {
		Helper.setComboBoxFromID(m_jcbTrainerType, newTrainerType);
	}

	public void setTacticalAssistants(int assistants){
		Helper.setComboBoxFromID(m_jcbTacticalAssistants, assistants);
	}

	public void setPullBackMinute(int minute) {
		Helper.setComboBoxFromID(m_jcbPullBackMinute, minute);
	}

	// TODO: find out what this is doing
	private void setPullBackOverride(boolean pullBackOverride) {
		m_jchPullBackOverride.setSelected(pullBackOverride);
	}

	@Override
	public void itemStateChanged(ItemEvent event) {

		if (event.getStateChange() == ItemEvent.DESELECTED) {
			if (event.getSource().equals(m_jchPullBackOverride)) {
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setPullBackOverride(false);
				m_jcbPullBackMinute.setEnabled(true);
				refresh();
			}

		} else if (event.getStateChange() == ItemEvent.SELECTED) {
			if (event.getSource().equals(m_jchPullBackOverride)) {
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setPullBackOverride(true);
				m_jcbPullBackMinute.setEnabled(false);
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbPullBackMinute)) {
				// Pull Back minute changed
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setPullBackMinute(((CBItem) Objects.requireNonNull(m_jcbPullBackMinute.getSelectedItem())).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbMainTeamSpirit)) {
				// team spirit changed
				HOVerwaltung.instance().getModel().getTeam().setStimmungAsInt(((CBItem) Objects.requireNonNull(m_jcbMainTeamSpirit.getSelectedItem())).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbSubTeamSpirit)) {
				// team spirit (sub) changed
				HOVerwaltung.instance().getModel().getTeam().setSubStimmung(((CBItem) Objects.requireNonNull(m_jcbSubTeamSpirit.getSelectedItem())).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbTeamConfidence)) {
				// team confidence changed
				HOVerwaltung.instance().getModel().getTeam().setSelbstvertrauenAsInt(((CBItem) Objects.requireNonNull(m_jcbTeamConfidence.getSelectedItem())).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbTrainerType)) {
				// trainer type changed
				HOVerwaltung.instance().getModel().getTrainer().setTrainerTyp(((CBItem) Objects.requireNonNull(m_jcbTrainerType.getSelectedItem())).getId());
				int iStyleOfPlay = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getStyleOfPlay();
				HOMainFrame.instance().getLineupPanel().getLineupPositionsPanel().updateStyleOfPlayComboBox(iStyleOfPlay);
			} else if (event.getSource().equals(m_jcbLocation)) {
				// location changed
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setLocation((short) ((CBItem) Objects.requireNonNull(m_jcbLocation.getSelectedItem())).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbTacticalAssistants)) {
				HOVerwaltung.instance().getModel().getClub().setTacticalAssistantLevels(((CBItem) Objects.requireNonNull(m_jcbTacticalAssistants.getSelectedItem())).getId());
				// Number of tactical assistants changed
				int iStyleOfPlay = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getStyleOfPlay();
				HOMainFrame.instance().getLineupPanel().getLineupPositionsPanel().updateStyleOfPlayComboBox(iStyleOfPlay);
			}
			refresh();
		}
	}

	@Override
	public void reInit() {
		refresh();
	}

	@Override
	public void refresh() {
		removeItemListeners();
		setLabels();
		addItemListeners();
	}

	private void initComponents() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;

		setLayout(layout);

		int yPos = 0;

		constraints.gridwidth = GridBagConstraints.REMAINDER;

		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		initLabel(constraints, layout, new JLabel(HOVerwaltung.instance()
				.getLanguageString("Venue")), yPos);

		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTeamConfidence.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbTeamConfidence.setMaximumRowCount(4);
		m_jcbLocation.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"tt_AufstellungsDetails_Spielort"));
		m_jcbLocation.setOpaque(false);
		layout.setConstraints(m_jcbLocation, constraints);
		add(m_jcbLocation);
		
		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbMainTeamSpirit.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbMainTeamSpirit.setMaximumRowCount(13);
		layout.setConstraints(m_jcbMainTeamSpirit, constraints);
		add(m_jcbMainTeamSpirit);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("lineup.teamspiritsub")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbSubTeamSpirit.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbSubTeamSpirit.setMaximumRowCount(5);
		layout.setConstraints(m_jcbSubTeamSpirit, constraints);
		add(m_jcbSubTeamSpirit);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.confidence")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTeamConfidence.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbTeamConfidence.setMaximumRowCount(10);
		layout.setConstraints(m_jcbTeamConfidence, constraints);
		add(m_jcbTeamConfidence);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.coachtype")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTrainerType.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbTrainerType.setMaximumRowCount(3);
		layout.setConstraints(m_jcbTrainerType, constraints);
		add(m_jcbTrainerType);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.club.staff.tacticalassistant")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTacticalAssistants.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		layout.setConstraints(m_jcbTacticalAssistants, constraints);
		add(m_jcbTacticalAssistants);
		
		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance()
						.getLanguageString("PullBack.PullBackStartMinute")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		m_jcbPullBackMinute.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbPullBackMinute.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"PullBack.PullBackStartMinute.ToolTip"));
		m_jcbPullBackMinute.setOpaque(false);
		layout.setConstraints(m_jcbPullBackMinute, constraints);
		add(m_jcbPullBackMinute);

		yPos++;
		initLabel(constraints, layout, new JLabel(""), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jchPullBackOverride.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"PullBack.Override.ToolTip"));
		m_jchPullBackOverride.setOpaque(false);
		layout.setConstraints(m_jchPullBackOverride, constraints);
		add(m_jchPullBackOverride);

		// Add all item listeners
		addItemListeners();
	}

	/**
	 * Add all item listeners to the combo boxes
	 */
	private void addItemListeners() {
		m_jcbLocation.addItemListener(this);
		m_jcbMainTeamSpirit.addItemListener(this);
		m_jcbSubTeamSpirit.addItemListener(this);
		m_jcbTeamConfidence.addItemListener(this);
		m_jcbTrainerType.addItemListener(this);
		m_jcbPullBackMinute.addItemListener(this);
		m_jchPullBackOverride.addItemListener(this);
		m_jcbTacticalAssistants.addItemListener(this);
	}

	/**
	 * Remove all item listeners from the combo boxes
	 */
	private void removeItemListeners() {
		m_jcbLocation.removeItemListener(this);
		m_jcbMainTeamSpirit.removeItemListener(this);
		m_jcbSubTeamSpirit.removeItemListener(this);
		m_jcbTeamConfidence.removeItemListener(this);
		m_jcbTrainerType.removeItemListener(this);
		m_jcbPullBackMinute.removeItemListener(this);
		m_jchPullBackOverride.removeItemListener(this);
		m_jcbTacticalAssistants.removeItemListener(this);
	}


	private void initLabel(GridBagConstraints constraints, GridBagLayout layout, JLabel label, int y) {
		constraints.gridx = 1;
		constraints.gridy = y;
		layout.setConstraints(label, constraints);
		add(label);
	}
	

}
