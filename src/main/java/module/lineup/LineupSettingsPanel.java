package module.lineup;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.match.Weather;
import core.util.Helper;
import module.lineup.ratings.LineupRatingPanel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Objects;
import javax.swing.*;
import static core.util.Helper.getTranslation;
import static module.lineup.LineupPanel.TITLE_FG;

public final class LineupSettingsPanel extends ImagePanel implements Refreshable, ItemListener {

	private final LineupRatingPanel ratingPanel;

	private final JComboBox<CBItem> m_jcbTeamConfidence = new JComboBox<>(TeamConfidence.ITEMS);

	private final CBItem[] TRAINER_TYPES = {
			new CBItem(getTranslation("ls.module.lineup.coachtype.defensive"), 0),
			new CBItem(getTranslation("ls.module.lineup.coachtype.neutral"), 2),
			new CBItem(getTranslation("ls.module.lineup.coachtype.offensive"), 1), };

	private final JComboBox<CBItem> m_jcbTrainerType = new JComboBox<>(TRAINER_TYPES);
	private final JComboBox<CBItem> m_jcbMainTeamSpirit = new JComboBox<>(TeamSpirit.ITEMS);
	private final CBItem[] SUB_TEAM_SPIRIT = {
			new CBItem(getTranslation("verylow"), 0),
			new CBItem(getTranslation("low"), 1),
			new CBItem(getTranslation("Durchschnitt"), 2),
			new CBItem(getTranslation("high"), 3),
			new CBItem(getTranslation("veryhigh"), 4) };
	private final JComboBox<CBItem> m_jcbSubTeamSpirit = new JComboBox<>(SUB_TEAM_SPIRIT);

	/** weather combo boxes */
	public static final CBItem[] WEATHER =
			{
					new CBItem("", Weather.RAINY.getId()),
					new CBItem("", Weather.OVERCAST.getId()),
					new CBItem("", Weather.PARTIALLY_CLOUDY.getId()),
					new CBItem("", Weather.SUNNY.getId())
			};
	private final JComboBox<CBItem> m_jcbWeather = new JComboBox<>(WEATHER);

	private final CBItem[] LOCATION = {
			new CBItem(getTranslation("ls.module.lineup.matchlocation.home"),IMatchDetails.LOCATION_HOME),
			new CBItem(getTranslation("ls.module.lineup.matchlocation.away"), IMatchDetails.LOCATION_AWAY),
			new CBItem(getTranslation("ls.module.lineup.matchlocation.awayderby"),IMatchDetails.LOCATION_AWAYDERBY),
			new CBItem(getTranslation("ls.module.lineup.matchlocation.tournament"),IMatchDetails.LOCATION_TOURNAMENT)
	};
	private final JComboBox<CBItem> m_jcbLocation = new JComboBox<>(LOCATION);

	private final CBItem[] PULLBACK_MINUTE = {
			new CBItem(getTranslation("PullBack.None"), 90),
			new CBItem("85", 85), new CBItem("80", 80), new CBItem("75", 75), new CBItem("70", 70),
			new CBItem("65", 65), new CBItem("60", 60), new CBItem("55", 55), new CBItem("50", 50),
			new CBItem("45", 45), new CBItem("40", 40), new CBItem("35", 35), new CBItem("30", 30),
			new CBItem("25", 25), new CBItem("20", 20), new CBItem("15", 15), new CBItem("10", 10),
			new CBItem("5", 5),
			new CBItem(getTranslation("PullBack.WholeGame"), 0) };

	private final JComboBox<CBItem> m_jcbPullBackMinute = new JComboBox<>(PULLBACK_MINUTE);

	private final JCheckBox m_jchPullBackOverride = new JCheckBox(getTranslation("PullBack.Override"), false);

	private final CBItem[] TACTICAL_ASSISTANTS = {
			new CBItem("0", 0),
			new CBItem("1", 1),
			new CBItem("2", 2),
			new CBItem("3", 3),
			new CBItem("4", 4),
			new CBItem("5", 5)
	};
	
	private final JComboBox<CBItem>   m_jcbTacticalAssistants = new JComboBox<>(TACTICAL_ASSISTANTS);


	public LineupSettingsPanel(LineupRatingPanel ratingPanel) {
		this.ratingPanel = ratingPanel;
		initComponents();
		core.gui.RefreshManager.instance().registerRefreshable(this);
	}

	public final Weather getWeather() {
		int id = ((CBItem) Objects.requireNonNull(m_jcbWeather.getSelectedItem(), "Weather CB can't be null")).getId();
		return Weather.getById(id);
	}

	public void setWeather(Weather weather) {
		if (weather==Weather.NULL) weather=Weather.PARTIALLY_CLOUDY;
		if (m_jcbWeather.getSelectedIndex() != weather.getId()){
			m_jcbWeather.setSelectedIndex(weather.getId());
		}
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
		setWeather(currentLineup.getWeather());
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

		HOModel model = HOVerwaltung.instance().getModel();

		if (event.getStateChange() == ItemEvent.DESELECTED) {
			if (event.getSource().equals(m_jchPullBackOverride)) {
				model.getLineupWithoutRatingRecalc().setPullBackOverride(false);
				m_jcbPullBackMinute.setEnabled(true);
			}
		}
		else if (event.getStateChange() == ItemEvent.SELECTED) {
			if (event.getSource().equals(m_jchPullBackOverride)) {
				model.getLineupWithoutRatingRecalc().setPullBackOverride(true);
				m_jcbPullBackMinute.setEnabled(false);
			}
			else if (event.getSource().equals(m_jcbPullBackMinute)) {
				model.getLineupWithoutRatingRecalc().setPullBackMinute(((CBItem) Objects.requireNonNull(m_jcbPullBackMinute.getSelectedItem())).getId());
			}
			else if (event.getSource().equals(m_jcbMainTeamSpirit)) {
				model.getTeam().setStimmungAsInt(((CBItem) Objects.requireNonNull(m_jcbMainTeamSpirit.getSelectedItem())).getId());
			}
			else if (event.getSource().equals(m_jcbSubTeamSpirit)) {
				model.getTeam().setSubStimmung(((CBItem) Objects.requireNonNull(m_jcbSubTeamSpirit.getSelectedItem())).getId());
			}
			else if (event.getSource().equals(m_jcbTeamConfidence)) {
				model.getTeam().setSelbstvertrauenAsInt(((CBItem) Objects.requireNonNull(m_jcbTeamConfidence.getSelectedItem())).getId());
			}
			else if (event.getSource().equals(m_jcbTrainerType)) {
				model.getTrainer().setTrainerTyp(((CBItem) Objects.requireNonNull(m_jcbTrainerType.getSelectedItem())).getId());
				int iStyleOfPlay = model.getLineupWithoutRatingRecalc().getStyleOfPlay();
				HOMainFrame.instance().getLineupPanel().getLineupPositionsPanel().updateStyleOfPlayComboBox(iStyleOfPlay);
			}
			else if (event.getSource().equals(m_jcbLocation)) {
				model.getLineupWithoutRatingRecalc().setLocation((short) ((CBItem) Objects.requireNonNull(m_jcbLocation.getSelectedItem())).getId());
			}
			else if (event.getSource().equals(m_jcbTacticalAssistants)) {
				model.getClub().setTacticalAssistantLevels(((CBItem) Objects.requireNonNull(m_jcbTacticalAssistants.getSelectedItem())).getId());
				int iStyleOfPlay = model.getLineupWithoutRatingRecalc().getStyleOfPlay();
				HOMainFrame.instance().getLineupPanel().getLineupPositionsPanel().updateStyleOfPlayComboBox(iStyleOfPlay);
			}
			else if (event.getSource().equals(m_jcbWeather))
			{
				HOMainFrame.instance().getLineupPanel().getLineupPositionsPanel().refresh();
				model.getLineupWithoutRatingRecalc().setWeather(getWeather());
			}
		}
		refresh();
	}

	@Override
	public void reInit() {
		refresh();
	}

	@Override
	public void refresh() {
		removeItemListeners();
		setLabels();
		ratingPanel.refresh();
		addItemListeners();
	}

	public void refresh(Boolean bIncludeRatingPanel) {
		if(bIncludeRatingPanel) {
			refresh();
		}
		else{
			removeItemListeners();
			setLabels();
			addItemListeners();
		}
	}


	private void initComponents() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0,0,0 ,5);

		setLayout(layout);

		int yPos = 0;

		// Venue ===============================
		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		initLabel(constraints, layout, new JLabel(getTranslation("Venue")), yPos);

		constraints.gridx = 2;
		m_jcbLocation.setMaximumRowCount(4);
		m_jcbLocation.setToolTipText(getTranslation("tt_AufstellungsDetails_Spielort"));
		m_jcbLocation.setOpaque(false);
		m_jcbLocation.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbLocation, constraints);
		add(m_jcbLocation);

		constraints.gridx = 3;
		constraints.weightx = 1.0;
		add(new JLabel(""));

		yPos++;

		// Weather ===============================
		constraints.weightx = 0.0;
		initLabel(constraints, layout, new JLabel(getTranslation("ls.match.weather")), yPos);
		m_jcbWeather.setToolTipText(getTranslation("tt_AufstellungsAssistent_Wetter"));
		m_jcbWeather.setRenderer(new core.gui.comp.renderer.WeatherListCellRenderer());
		m_jcbWeather.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		constraints.gridx = 2;
		constraints.gridy = yPos;
		layout.setConstraints(m_jcbWeather, constraints);
		add(m_jcbWeather);


		yPos++;
		initLabel(constraints, layout, new JLabel(getTranslation("ls.team.teamspirit")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbMainTeamSpirit.setMaximumRowCount(13);
		m_jcbMainTeamSpirit.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbMainTeamSpirit, constraints);
		add(m_jcbMainTeamSpirit);

		yPos++;
		initLabel(constraints, layout,	new JLabel(getTranslation("lineup.teamspiritsub")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbSubTeamSpirit.setMaximumRowCount(5);
		m_jcbSubTeamSpirit.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbSubTeamSpirit, constraints);
		add(m_jcbSubTeamSpirit);

		yPos++;
		initLabel(constraints, layout, new JLabel(getTranslation("ls.team.confidence")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTeamConfidence.setMaximumRowCount(10);
		m_jcbTeamConfidence.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbTeamConfidence, constraints);
		add(m_jcbTeamConfidence);

		yPos++;
		initLabel(constraints, layout, new JLabel(getTranslation("ls.team.coachtype")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTrainerType.setMaximumRowCount(3);
		m_jcbTrainerType.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbTrainerType, constraints);
		add(m_jcbTrainerType);

		yPos++;
		initLabel(constraints, layout, new JLabel(getTranslation("ls.club.staff.tacticalassistant")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTacticalAssistants.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbTacticalAssistants, constraints);
		add(m_jcbTacticalAssistants);
		
		yPos++;
		initLabel(constraints, layout, new JLabel(getTranslation("PullBack.PullBackStartMinute")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		m_jcbPullBackMinute.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"PullBack.PullBackStartMinute.ToolTip"));
		m_jcbPullBackMinute.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbPullBackMinute, constraints);
		add(m_jcbPullBackMinute);

		yPos++;
		initLabel(constraints, layout, new JLabel(""), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jchPullBackOverride.setToolTipText(getTranslation("PullBack.Override.ToolTip"));
		m_jchPullBackOverride.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
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
		m_jcbWeather.addItemListener(this);
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
		m_jcbWeather.removeItemListener(this);
		m_jcbMainTeamSpirit.removeItemListener(this);
		m_jcbSubTeamSpirit.removeItemListener(this);
		m_jcbTeamConfidence.removeItemListener(this);
		m_jcbTrainerType.removeItemListener(this);
		m_jcbPullBackMinute.removeItemListener(this);
		m_jchPullBackOverride.removeItemListener(this);
		m_jcbTacticalAssistants.removeItemListener(this);
	}


	private void initLabel(GridBagConstraints constraints, GridBagLayout layout, JLabel label, int y) {
		label.setForeground(TITLE_FG);
		label.setFont(getFont().deriveFont(Font.BOLD));
		label.setHorizontalAlignment(SwingConstants.LEFT);
		constraints.gridx = 1;
		constraints.gridy = y;
		layout.setConstraints(label, constraints);
		add(label);
	}


}
