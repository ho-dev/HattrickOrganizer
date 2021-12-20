package module.lineup;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.datatype.CBItem;
import core.gui.Refreshable;
import core.gui.comp.panel.ComboBoxTitled;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.match.StyleOfPlay;
import core.model.match.Weather;
import core.model.player.TrainerType;
import core.rating.RatingPredictionConfig;
import core.util.Helper;
import module.lineup.lineup.MatchAndLineupSelectionPanel;
import module.lineup.ratings.LineupRatingPanel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.swing.*;
import static core.util.Helper.getTranslation;
import static module.lineup.LineupPanel.TITLE_FG;

public final class LineupSettingsPanel extends ImagePanel implements Refreshable, ItemListener {

	private LineupRatingPanel ratingPanel;
	//private MatchAndLineupSelectionPanel matchAndLineupPanel;
	private final LineupPanel lineupPanel;

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
					new CBItem("", Weather.SUNNY.getId()),
					new CBItem("", Weather.UNKNOWN.getId())
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

	private JComboBox<CBItem> m_jcbPredictionModel;

	private final CBItem[] TACTICAL_ASSISTANTS = {
			new CBItem("0", 0),
			new CBItem("1", 1),
			new CBItem("2", 2),
			new CBItem("3", 3),
			new CBItem("4", 4),
			new CBItem("5", 5)
	};
	
	private final JComboBox<CBItem>   m_jcbTacticalAssistants = new JComboBox<>(TACTICAL_ASSISTANTS);

	private final JButton m_jbReset = new JButton("");

	private int m_iRealTeamSpirit;
	private int m_iRealSubTeamSpirit;
	private int m_iRealConfidence;
	private int m_iRealTrainerType;
	private int m_iRealTacticalAssistantsLevel;

	private HOModel homodel;


	public void backupRealGameSettings(){

		homodel = HOVerwaltung.instance().getModel();

		//the following values are stored to allow reverting to real value after playing with the various lineup settings
		if (homodel.getTeam() != null) {
			m_iRealTeamSpirit = homodel.getTeam().getTeamSpirit();
			m_iRealSubTeamSpirit = homodel.getTeam().getSubTeamSpirit();
			m_iRealConfidence = homodel.getTeam().getConfidence();
			m_iRealTrainerType = homodel.getTrainer().getTrainerTyp().toInt();
			m_iRealTacticalAssistantsLevel = homodel.getClub().getTacticalAssistantLevels();
		}
		else{
			m_iRealTeamSpirit = 4;
			m_iRealSubTeamSpirit = 2;
			m_iRealConfidence = 4;
			m_iRealTrainerType = 2;
			m_iRealTacticalAssistantsLevel = 0;
		}
	}

	public LineupSettingsPanel(LineupPanel parent) {
		lineupPanel = parent;
		backupRealGameSettings();
		initComponents();
		core.gui.RefreshManager.instance().registerRefreshable(this);
	}

	private boolean isLineupSimulator(){
/*		if (matchAndLineupPanel == null){
			matchAndLineupPanel = lineupPanel.getLineupPositionsPanel().getMatchAndLineupSelectionPanel();
		}
		if (matchAndLineupPanel != null) {
			return matchAndLineupPanel.isLineupSimulator();
		}
		else{
			return false;
		}*/
		return true;
	}

	public Weather getWeather() {
		int id = ((CBItem) Objects.requireNonNull(m_jcbWeather.getSelectedItem(), "Weather CB can't be null")).getId();
		return Weather.getById(id);
	}

	public void setWeather(Weather weather, Weather.Forecast weatherForecast) {
		if ((weather==Weather.NULL) || (weatherForecast== Weather.Forecast.UNSURE) || (weatherForecast== Weather.Forecast.NULL)) weather=Weather.UNKNOWN;
		if (m_jcbWeather.getSelectedIndex() != weather.getId()){
			m_jcbWeather.setSelectedIndex(weather.getId());
		}
	}

	private void setLocation(int location) {
		// Set the location
		Helper.setComboBoxFromID(m_jcbLocation, location);

		if (location == IMatchDetails.LOCATION_TOURNAMENT) {

			m_jcbMainTeamSpirit.setEnabled(false);
			m_jcbSubTeamSpirit.setEnabled(false);
			m_jcbTeamConfidence.setEnabled(false);

			homodel.getTeam().setTeamSpirit(6); // Set Team Spirit to content (cf: https://www.hattrick.org/Help/Rules/Tournaments.aspx)
			homodel.getTeam().setSubTeamSpirit(2);
			homodel.getTeam().setConfidence(6);  // Set Team Spirit to wonderful (cf: https://www.hattrick.org/Help/Rules/Tournaments.aspx)

		}
		else
		{
			boolean bLineupSimulation = isLineupSimulator();
			m_jcbMainTeamSpirit.setEnabled(bLineupSimulation);
			m_jcbSubTeamSpirit.setEnabled(bLineupSimulation);
			m_jcbTeamConfidence.setEnabled(bLineupSimulation);
		}
	}

	public void setLabels() {

		// Lineup settings are editable only if in Lineup Simulator mode
		boolean bLineupSimulation = isLineupSimulator();
		m_jcbLocation.setEnabled(bLineupSimulation);
		m_jcbWeather.setEnabled(bLineupSimulation);
		m_jcbMainTeamSpirit.setEnabled(bLineupSimulation);
		m_jcbSubTeamSpirit.setEnabled(bLineupSimulation);
		m_jcbTeamConfidence.setEnabled(bLineupSimulation);
		m_jcbTrainerType.setEnabled(bLineupSimulation);
		m_jcbTacticalAssistants.setEnabled(bLineupSimulation);
		m_jcbPullBackMinute.setEnabled(bLineupSimulation);
		m_jbReset.setEnabled(bLineupSimulation);

		final Lineup currentLineup = homodel.getLineupWithoutRatingRecalc();
		setLocation(currentLineup.getLocation());
		var team = homodel.getTeam();
		if ( team != null){
			if(bLineupSimulation) {
				setTeamSpirit(team.getTeamSpirit(), team.getSubTeamSpirit());
				setConfidence(team.getConfidence());
				setTrainerType(homodel.getTrainer().getTrainerTyp().toInt());
			}
			else{

				if (m_jcbLocation.getSelectedItem() != null &&
						((CBItem)m_jcbLocation.getSelectedItem()).getId() == IMatchDetails.LOCATION_TOURNAMENT){
					setTeamSpirit(6, 2);
					setConfidence(6);
				}
				else{
					setTeamSpirit(m_iRealTeamSpirit, m_iRealSubTeamSpirit);
					setConfidence(m_iRealConfidence);
				}
				setTrainerType(m_iRealTrainerType);
			}
		}

		var club = homodel.getClub();
		if ( club != null){
			if(bLineupSimulation) {
				setTacticalAssistants(club.getTacticalAssistantLevels());
			}
			else{
				setTacticalAssistants(m_iRealTacticalAssistantsLevel);
			}
		}
		setWeather(currentLineup.getWeather(), currentLineup.getWeatherForecast());
		setPullBackMinute(currentLineup.getPullBackMinute());
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

	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getStateChange() == ItemEvent.SELECTED) {
			if (event.getSource().equals(m_jcbPullBackMinute)) {
				homodel.getLineupWithoutRatingRecalc().setPullBackMinute(((CBItem) Objects.requireNonNull(m_jcbPullBackMinute.getSelectedItem())).getId());
			} else if (event.getSource().equals(m_jcbMainTeamSpirit)) {
				homodel.getTeam().setTeamSpirit(((CBItem) Objects.requireNonNull(m_jcbMainTeamSpirit.getSelectedItem())).getId());
			} else if (event.getSource().equals(m_jcbSubTeamSpirit)) {
				homodel.getTeam().setSubTeamSpirit(((CBItem) Objects.requireNonNull(m_jcbSubTeamSpirit.getSelectedItem())).getId());
			} else if (event.getSource().equals(m_jcbTeamConfidence)) {
				homodel.getTeam().setConfidence(((CBItem) Objects.requireNonNull(m_jcbTeamConfidence.getSelectedItem())).getId());
			} else if (event.getSource().equals(m_jcbTrainerType)) {
				var trainerType = ((CBItem) Objects.requireNonNull(m_jcbTrainerType.getSelectedItem())).getId();
				homodel.getTrainer().setTrainerTyp(TrainerType.fromInt(trainerType));
				var newStyleOfPlay = lineupPanel.updateStyleOfPlayComboBox();
				homodel.getLineupWithoutRatingRecalc().setStyleOfPlay(newStyleOfPlay);
			} else if (event.getSource().equals(m_jcbLocation)) {
				homodel.getLineupWithoutRatingRecalc().setLocation((short) ((CBItem) Objects.requireNonNull(m_jcbLocation.getSelectedItem())).getId());
			} else if (event.getSource().equals(m_jcbTacticalAssistants)) {
				var tacticalAssistantLevel = ((CBItem) Objects.requireNonNull(m_jcbTacticalAssistants.getSelectedItem())).getId();
				homodel.getClub().setTacticalAssistantLevels(tacticalAssistantLevel);
				var newStyleOfPlay = lineupPanel.updateStyleOfPlayComboBox();
				homodel.getLineupWithoutRatingRecalc().setStyleOfPlay(newStyleOfPlay);
			} else if (event.getSource().equals(m_jcbWeather)) {
				Lineup lineup = homodel.getLineupWithoutRatingRecalc();
				lineup.setWeatherForecast(Weather.Forecast.TODAY); // weather forecast is overriden
				lineup.setWeather(getWeather());
				lineupPanel.refreshLineupPositionsPanel();
			}
			else if (event.getSource().equals(this.m_jcbPredictionModel)){
				RatingPredictionConfig.setInstancePredictionType(((CBItem) Objects.requireNonNull(m_jcbPredictionModel.getSelectedItem())).getId());
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
		lineupPanel.refreshLineupRatingPanel();
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
		m_jcbPullBackMinute.setToolTipText(getTranslation("PullBack.PullBackStartMinute.ToolTip"));
		m_jcbPullBackMinute.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbPullBackMinute, constraints);
		add(m_jcbPullBackMinute);

		m_jcbPredictionModel = new JComboBox<>(getPredictionItems());
		yPos++;
		initLabel(constraints, layout, new JLabel(getTranslation("PredictionType")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		m_jcbPredictionModel.setToolTipText(getTranslation("Lineup.PredictionModel.ToolTip"));
		m_jcbPredictionModel.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbPredictionModel, constraints);
		add(m_jcbPredictionModel);

		yPos++;
		initLabel(constraints, layout, new JLabel(""), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		m_jbReset.setToolTipText(getTranslation("ls.module.lineup.reset_settings.tt"));
		m_jbReset.setIcon(ImageUtilities.getSvgIcon(HOIconName.RESET, Map.of("lineColor", HOColorName.RESET_COLOR), 18, 18));
		m_jbReset.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));

		m_jbReset.addActionListener(e -> {
			resetSettings();
			refresh();
		});

		layout.setConstraints(m_jbReset, constraints);
		add(m_jbReset);

		// Add all item listeners
		addItemListeners();
	}

	private CBItem[] getPredictionItems() {
		final ResourceBundle bundle = HOVerwaltung.instance().getResource();
		String[] allPredictionNames = RatingPredictionConfig.getAllPredictionNames();
		CBItem[] allItems = new CBItem[allPredictionNames.length];
		for (int i = 0; i < allItems.length; i++) {
			String predictionName = allPredictionNames[i];
			if (bundle.containsKey("prediction." + predictionName))
				predictionName = HOVerwaltung.instance().getLanguageString(
						"prediction." + predictionName);
			allItems[i] = new CBItem(predictionName, i);
		}
		return allItems;
	}

	public void resetSettings() {
		homodel.getTeam().setTeamSpirit(m_iRealTeamSpirit);
		homodel.getTeam().setSubTeamSpirit(m_iRealSubTeamSpirit);
		homodel.getTeam().setConfidence(m_iRealConfidence);
		homodel.getTrainer().setTrainerTyp(TrainerType.fromInt(m_iRealTrainerType));
		homodel.getClub().setTacticalAssistantLevels(m_iRealTacticalAssistantsLevel);
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
		m_jcbTacticalAssistants.addItemListener(this);
		m_jcbPredictionModel.addItemListener(this);
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
		m_jcbTacticalAssistants.removeItemListener(this);
		m_jcbPredictionModel.removeItemListener(this);
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
