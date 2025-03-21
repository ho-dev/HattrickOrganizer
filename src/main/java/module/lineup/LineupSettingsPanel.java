package module.lineup;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.datatype.CBItem;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.IMatchDetails;
import core.model.match.Weather;
import core.model.player.Player;
import core.model.player.TrainerType;
import core.util.Helper;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Objects;
import javax.swing.*;
import static core.util.Helper.getTranslation;
import static module.lineup.LineupPanel.TITLE_FG;

public final class LineupSettingsPanel extends ImagePanel implements Refreshable, ItemListener {

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
			new CBItem(getTranslation("high"), 2),
			new CBItem(getTranslation("veryhigh"), 3) };
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

	private final CBItem[] ManMarkingPositions = {
			new CBItem(getTranslation("ls.module.lineup.manmarkingposition.none"),0),
			new CBItem(getTranslation("ls.module.lineup.manmarkingposition.opposite"), Player.ManMarkingPosition.Opposite.getValue()),
			new CBItem(getTranslation("ls.module.lineup.manmarkingposition.notopposite"), Player.ManMarkingPosition.NotOpposite.getValue()),
			new CBItem(getTranslation("ls.module.lineup.manmarkingposition.notinlineup"), Player.ManMarkingPosition.NotInLineup.getValue())
	};
	private final JComboBox<CBItem> m_jcbManMarkingPosition = new JComboBox<>(ManMarkingPositions);

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
			m_iRealTeamSpirit = homodel.getTeam().getTeamSpiritLevel();
			m_iRealSubTeamSpirit = homodel.getTeam().getSubTeamSpirit();
			m_iRealConfidence = homodel.getTeam().getConfidence();
			m_iRealTrainerType = homodel.getTrainer().getTrainerType().toInt();
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

	private void setManMarking(boolean enabled, Player.ManMarkingPosition manMarkingPosition){
		m_jcbManMarkingPosition.setEnabled(enabled);
		var id = 0;
		if ( enabled ){
			if ( manMarkingPosition != null){
				id = manMarkingPosition.getValue();
			}
		}
		Helper.setComboBoxFromID(m_jcbManMarkingPosition,id);
	}

	private void setLocation(int location) {
		// Set the location
		Helper.setComboBoxFromID(m_jcbLocation, location);

		if (location == IMatchDetails.LOCATION_TOURNAMENT) {

			m_jcbMainTeamSpirit.setEnabled(false);
			m_jcbSubTeamSpirit.setEnabled(false);
			m_jcbTeamConfidence.setEnabled(false);

			homodel.getTeam().setTeamSpiritLevel(6); // Set Team Spirit to content (cf: https://www.hattrick.org/Help/Rules/Tournaments.aspx)
			homodel.getTeam().setSubTeamSpirit(2);
			homodel.getTeam().setConfidence(6);  // Set Team Spirit to wonderful (cf: https://www.hattrick.org/Help/Rules/Tournaments.aspx)

		}
		else
		{
			m_jcbMainTeamSpirit.setEnabled(true);
			m_jcbSubTeamSpirit.setEnabled(true);
			m_jcbTeamConfidence.setEnabled(true);
		}
	}

	public void setLabels() {

		m_jcbLocation.setEnabled(true);
		m_jcbWeather.setEnabled(true);
		m_jcbMainTeamSpirit.setEnabled(true);
		m_jcbSubTeamSpirit.setEnabled(true);
		m_jcbTeamConfidence.setEnabled(true);
		m_jcbTrainerType.setEnabled(true);
		m_jcbTacticalAssistants.setEnabled(true);
		m_jcbPullBackMinute.setEnabled(true);
		m_jbReset.setEnabled(true);

		var team = homodel.getTeam();
		if ( team != null) {
			setTeamSpirit(team.getTeamSpiritLevel(), team.getSubTeamSpirit());
			setConfidence(team.getConfidence());
			setTrainerType(homodel.getTrainer().getTrainerType().toInt());
		}

		var club = homodel.getClub();
		if ( club != null) {
			setTacticalAssistants(club.getTacticalAssistantLevels());
		}

		final Lineup currentLineup = homodel.getCurrentLineup();
        setManMarking(currentLineup.getManMarkingOrder() != null, currentLineup.getManMarkingPosition());
        setLocation(currentLineup.getLocation());
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
				var lineup = homodel.getCurrentLineup();
                lineup.setPullBackMinute(((CBItem) Objects.requireNonNull(m_jcbPullBackMinute.getSelectedItem())).getId());
            } else if (event.getSource().equals(m_jcbMainTeamSpirit)) {
				homodel.getTeam().setTeamSpiritLevel(((CBItem) Objects.requireNonNull(m_jcbMainTeamSpirit.getSelectedItem())).getId());
			} else if (event.getSource().equals(m_jcbSubTeamSpirit)) {
				homodel.getTeam().setSubTeamSpirit(((CBItem) Objects.requireNonNull(m_jcbSubTeamSpirit.getSelectedItem())).getId());
			} else if (event.getSource().equals(m_jcbTeamConfidence)) {
				homodel.getTeam().setConfidence(((CBItem) Objects.requireNonNull(m_jcbTeamConfidence.getSelectedItem())).getId());
			} else if (event.getSource().equals(m_jcbTrainerType)) {
				var trainerType = ((CBItem) Objects.requireNonNull(m_jcbTrainerType.getSelectedItem())).getId();
				homodel.getTrainer().setTrainerType(TrainerType.fromInt(trainerType));
				lineupPanel.updateStyleOfPlayComboBox();
			} else if (event.getSource().equals(m_jcbLocation)) {
				var lineup = homodel.getCurrentLineup();
                lineup.setLocation((short) ((CBItem) Objects.requireNonNull(m_jcbLocation.getSelectedItem())).getId());
            } else if (event.getSource().equals(m_jcbTacticalAssistants)) {
				var tacticalAssistantLevel = ((CBItem) Objects.requireNonNull(m_jcbTacticalAssistants.getSelectedItem())).getId();
				homodel.getClub().setTacticalAssistantLevels(tacticalAssistantLevel);
				lineupPanel.updateStyleOfPlayComboBox();
			} else if (event.getSource().equals(m_jcbWeather)) {
				Lineup lineup = homodel.getCurrentLineup();
                lineup.setWeatherForecast(Weather.Forecast.TODAY); // weather forecast is override
                lineup.setWeather(getWeather());
                lineupPanel.refreshLineupPositionsPanel();
            } else if (event.getSource().equals(this.m_jcbPredictionModel)) {
				var ratingPredictionManager = homodel.getRatingPredictionManager();
				var selected = (CBItem)m_jcbPredictionModel.getSelectedItem();
				if ( selected != null) {
					ratingPredictionManager.getRatingPredictionModel(selected.getText(), homodel.getTeam());
				}
			} else if (event.getSource().equals(m_jcbManMarkingPosition)) {
				var lineup = homodel.getCurrentLineup();
                lineup.setManMarkingPosition(Player.ManMarkingPosition.fromId(((CBItem) Objects.requireNonNull(m_jcbManMarkingPosition.getSelectedItem())).getId()));
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

		yPos++;
		initLabel(constraints, layout, new JLabel(getTranslation("ls.module.lineup.manmarkingposition")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		m_jcbManMarkingPosition.setToolTipText(getTranslation("ls.module.lineup.manmarkingposition.ToolTip"));
		m_jcbManMarkingPosition.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		layout.setConstraints(m_jcbManMarkingPosition, constraints);
		add(m_jcbManMarkingPosition);

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

		var fontSize = UserParameter.instance().fontSize;

		yPos++;
		initLabel(constraints, layout, new JLabel(""), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		m_jbReset.setToolTipText(getTranslation("ls.module.lineup.reset_settings.tt"));
		m_jbReset.setIcon(ImageUtilities.getSvgIcon(HOIconName.RESET, Map.of("lineColor", HOColorName.RESET_COLOR), fontSize, fontSize));
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
		var ratingPredictionManager = homodel.getRatingPredictionManager();
		var allPredictionNames = ratingPredictionManager.getAllPredictionModelNames();
		CBItem[] allItems = new CBItem[allPredictionNames.size()];
		for (int i = 0; i < allItems.length; i++) {
			String predictionName = allPredictionNames.get(i);
			allItems[i] = new CBItem(predictionName, i);
		}
		return allItems;
	}

	public void resetSettings() {
		homodel.getTeam().setTeamSpiritLevel(m_iRealTeamSpirit);
		homodel.getTeam().setSubTeamSpirit(m_iRealSubTeamSpirit);
		homodel.getTeam().setConfidence(m_iRealConfidence);
		homodel.getTrainer().setTrainerType(TrainerType.fromInt(m_iRealTrainerType));
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
		m_jcbManMarkingPosition.addItemListener(this);
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
		m_jcbManMarkingPosition.removeItemListener(this);
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
