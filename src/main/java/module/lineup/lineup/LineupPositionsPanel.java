package module.lineup.lineup;

import core.datatype.CBItem;
import core.db.user.UserManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.Updatable;
import core.gui.comp.panel.ComboBoxTitled;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.*;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.model.player.TrainerType;
import core.util.HOLogger;
import core.util.Helper;
import module.lineup.AllTeamsPanel;
import module.lineup.Lineup;
import module.lineup.LineupPanel;
import module.lineup.assistant.LineupAssistantPanel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.border.Border;

import static core.model.UserParameter.GOALKEEPER_AT_TOP;

/**
 * Create the panel allowing lineup creation
 */
public class LineupPositionsPanel extends core.gui.comp.panel.RasenPanel implements core.gui.Refreshable, Updatable, ActionListener {

	private final LineupPanel m_clLineupPanel;
	//private MatchAndLineupSelectionPanel m_jpMatchAndLineupSelectionPanel;
	//private MatchBanner m_jpMatchBanner;
	private final JButton m_jbFlipSide = new JButton(ThemeManager.getIcon(HOIconName.RELOAD));
	private PlayerPositionPanel m_clKeeper;
	private PlayerPositionPanel m_clLeftBack;
	private PlayerPositionPanel m_clLeftCentralDefender;
	private PlayerPositionPanel m_clMiddleCentralDefender;
	private PlayerPositionPanel m_clRightCentralDefender;
	private PlayerPositionPanel m_clRightBack;
	private PlayerPositionPanel m_clLeftWinger;
	private PlayerPositionPanel m_clLeftInnerMidfielder;
	private PlayerPositionPanel m_clCentralInnerMidfielder;
	private PlayerPositionPanel m_clRightInnerMidfielder;
	private PlayerPositionPanel m_clRightWinger;
	private PlayerPositionPanel m_clLeftForward;
	private PlayerPositionPanel m_clCentralForward;
	private PlayerPositionPanel m_clRightForward;
	private PlayerPositionPanel m_clSubstKeeper1;
	private PlayerPositionPanel m_clSubstKeeper2;
	private PlayerPositionPanel m_clSubstCD1;
	private PlayerPositionPanel m_clSubstCD2;
	private PlayerPositionPanel m_clSubstWB1;
	private PlayerPositionPanel m_clSubstWB2;
	private PlayerPositionPanel m_clSubstIM1;
	private PlayerPositionPanel m_clSubstIM2;
	private PlayerPositionPanel m_clSubstFwd1;
	private PlayerPositionPanel m_clSubstFwd2;
	private PlayerPositionPanel m_clSubstWI1;
	private PlayerPositionPanel m_clSubstWI2;
	private PlayerPositionPanel m_clSubstXtr1;
	private PlayerPositionPanel m_clSubstXtr2;
	private PlayerPositionPanel m_clCaptain;
	private PlayerPositionPanel m_clSetPieceTaker;
	private javax.swing.JLayeredPane centerPanel;
	private final SwapPositionsManager swapPositionsManager = new SwapPositionsManager(this);
	//private final LineupAssistantPanel assistantPanel;
	// TODO move to MatchSelectionPanel
	private StyleOfPlay m_iStyleOfPlay;
	private MatchTacticType m_iTactic;
	private MatchTeamAttitude m_iAttitude;
	private ComboBoxTitled m_jpTeamAttitude;
	private JComboBox<CBItem> m_jcbTeamAttitude;
	private ComboBoxTitled m_jpTactic;
	private JComboBox<CBItem> m_jcbTactic;
	private ComboBoxTitled m_jpStyleOfPlay;
	private JComboBox<CBItem> m_jcbStyleOfPlay;
	final String offensive_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.offensive");
	final String defensive_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.defensive");
	final String neutral_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.neutral");

	private static ActionListener cbActionListener;
	private Weather m_weather;
	private boolean m_useWeatherImpact;

	public LineupPanel getLineupPanel() {
		return m_clLineupPanel;
	}

	public LineupPositionsPanel(LineupPanel parent, Weather weather, Boolean useWeatherImpact) {
		m_clLineupPanel = parent;
		//assistantPanel = m_clLineupPanel.getLineupAssistantPanel();
		m_weather = weather;
		m_useWeatherImpact = useWeatherImpact;
		initComponents();
		RefreshManager.instance().registerRefreshable(this);
	}

	public void setEnabledTeamAttitudeCB(boolean enabled) {
		if (!enabled){
			m_iAttitude = MatchTeamAttitude.Normal; // core.model.match.IMatchDetails.EINSTELLUNG_NORMAL;
		}
		Helper.setComboBoxFromID(m_jcbTeamAttitude, MatchTeamAttitude.toInt(m_iAttitude));
		m_jcbTeamAttitude.setEnabled(enabled);
	}

	public javax.swing.JLayeredPane getCenterPanel() {
		return centerPanel;
	}

	@Override
	public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(m_jbFlipSide)) {
			HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().flipSide();
			HOMainFrame.instance().getLineupPanel().update();
		}
	}

	@Override
	public final void reInit() {
		refresh();
	}

	@Override
	public final void refresh() {

		HOLogger.instance().log(getClass(), "refresh() has been called");

		boolean bGroupFiltered = m_clLineupPanel.isAssistantGroupFilter();
		String sGroup = m_clLineupPanel.getAssistantGroup();
		boolean bSelectedGroupExcluded = m_clLineupPanel.isAssistantSelectedGroupExcluded();
		boolean bExcludeLast = m_clLineupPanel.isAssistantExcludeLastMatch();

		m_weather = m_clLineupPanel.getWeather();

		// All Player Positions Inform First 11
		List<Player> selectedPlayers = new ArrayList<>();
		List<Player> substitutes = new ArrayList<>();
		List<Player> allPlayers = HOVerwaltung.instance().getModel().getCurrentPlayers();
		List<Player> filteredPlayers = new ArrayList<>();
		Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

		// refresh lineup settings
		m_iTactic = MatchTacticType.fromInt(lineup.getTacticType());
		Helper.setComboBoxFromID(m_jcbTactic, lineup.getTacticType());
		m_iAttitude = MatchTeamAttitude.fromInt(lineup.getAttitude());
		Helper.setComboBoxFromID(m_jcbTeamAttitude, lineup.getAttitude());
		m_iStyleOfPlay = StyleOfPlay.fromInt(lineup.getStyleOfPlay());
		updateStyleOfPlayComboBox(m_iStyleOfPlay);

		for (Player player: allPlayers) {
			// the first 11
			if (lineup.isPlayerInStartingEleven(player.getPlayerID())) {
				selectedPlayers.add(player);
			}
			else if (lineup.isSpielerInReserve(player.getPlayerID())) {
				substitutes.add(player);
			}
		}

		// Apply the Group Filter
		for (Player player: allPlayers) {
			// No Filter
			if (!bGroupFiltered || (sGroup.equals(player.getTeamInfoSmilie()) && !bSelectedGroupExcluded)
					|| (!sGroup.equals(player.getTeamInfoSmilie()) && bSelectedGroupExcluded)) {
				boolean include = true;
				if ( bExcludeLast) {
					var previousLineup = HOVerwaltung.instance().getModel().getPreviousLineup();
					if (previousLineup != null && previousLineup.isPlayerInStartingEleven(player.getPlayerID())) {
						include = false;
					}
				}
				if (include) {
					filteredPlayers.add(player);
				}
			}
		}

		m_clKeeper.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clLeftBack.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clLeftCentralDefender.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clMiddleCentralDefender.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clRightCentralDefender.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clRightBack.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clLeftWinger.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clLeftInnerMidfielder.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clCentralInnerMidfielder.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clRightInnerMidfielder.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clRightWinger.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clLeftForward.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clCentralForward.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clRightForward.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clSubstKeeper1.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clSubstKeeper2.refresh2(filteredPlayers, m_clSubstKeeper1.getiSelectedPlayerId(), m_weather, m_useWeatherImpact);
		m_clSubstCD1.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
	    m_clSubstCD2.refresh2(filteredPlayers, m_clSubstCD1.getiSelectedPlayerId(), m_weather, m_useWeatherImpact);
		m_clSubstWB1.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clSubstWB2.refresh2(filteredPlayers, m_clSubstWB1.getiSelectedPlayerId(), m_weather, m_useWeatherImpact);
		m_clSubstIM1.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clSubstIM2.refresh2(filteredPlayers, m_clSubstIM1.getiSelectedPlayerId(), m_weather, m_useWeatherImpact);
		m_clSubstFwd1.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clSubstFwd2.refresh2(filteredPlayers, m_clSubstFwd1.getiSelectedPlayerId(), m_weather, m_useWeatherImpact);
		m_clSubstWI1.refresh(filteredPlayers, selectedPlayers, substitutes, m_weather, m_useWeatherImpact);
		m_clSubstWI2.refresh2(filteredPlayers, m_clSubstWI1.getiSelectedPlayerId(), m_weather, m_useWeatherImpact);
		m_clSubstXtr1.refresh(filteredPlayers, selectedPlayers, substitutes, null, false);
		m_clSubstXtr2.refresh2(filteredPlayers, m_clSubstXtr1.getiSelectedPlayerId(), null, false);
		m_clSetPieceTaker.refresh(selectedPlayers, null, null, m_weather, m_useWeatherImpact);
	 	m_clCaptain.refresh(selectedPlayers, null, null, null, false);

		// Check
		lineup.checkAufgestellteSpieler();
	}


	@Override
	public final void update() {
		m_clLineupPanel.update();
	}


	private void initComponents() {
		setLayout(new BorderLayout());

		centerPanel = new javax.swing.JLayeredPane();
		centerPanel.setOpaque(false);

		final GridBagLayout layout = new GridBagLayout();
		centerPanel.setLayout(layout);

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(3, 3, 3, 3);

		// Keeper
		constraints.gridx = 1 + getLineupColumnNumber(2); //3;
		constraints.gridy = getLineupRowNumber(0);
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		//constraints.anchor = GridBagConstraints.SOUTH;
		m_clKeeper = new PlayerPositionPanel(this, IMatchRoleID.keeper, m_weather, m_useWeatherImpact);
		swapPositionsManager.addSwapCapabilityTo(m_clKeeper);
		layout.setConstraints(m_clKeeper, constraints);
		centerPanel.add(m_clKeeper);

		// TEAM ATTITUDE ================================================================
		m_jcbTeamAttitude = new JComboBox<>(new CBItem[]{
				new CBItem(
						HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.playitcool"),
						IMatchDetails.EINSTELLUNG_PIC),
				new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.normal"),
						IMatchDetails.EINSTELLUNG_NORMAL),
				new CBItem(HOVerwaltung.instance().getLanguageString(
						"ls.team.teamattitude.matchoftheseason"), IMatchDetails.EINSTELLUNG_MOTS)});

		m_jpTeamAttitude = new ComboBoxTitled(getLangStr("ls.team.teamattitude"), m_jcbTeamAttitude, true);

		constraints.gridx = 4;
		layout.setConstraints(m_jpTeamAttitude, constraints);
		centerPanel.add(m_jpTeamAttitude);
		// Initialize attitude CB
		var isCompetitive = this.m_clLineupPanel.isSelectedMatchCompetitive();
		var lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
		if (isCompetitive) {
			m_iAttitude = MatchTeamAttitude.fromInt(lineup.getAttitude());
		}

		//After initialization this is set via listener on MatchAndLineupSelectionPanel.m_jcbUpcomingGames
		setEnabledTeamAttitudeCB(isCompetitive);

		// TACTIC ================================================================
		m_jcbTactic = new JComboBox<>(new CBItem[]{
				new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_NORMAL),
						IMatchDetails.TAKTIK_NORMAL),
				new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_PRESSING),
						IMatchDetails.TAKTIK_PRESSING),
				new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_KONTER),
						IMatchDetails.TAKTIK_KONTER),
				new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_MIDDLE),
						IMatchDetails.TAKTIK_MIDDLE),
				new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_WINGS),
						IMatchDetails.TAKTIK_WINGS),
				new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_CREATIVE),
						IMatchDetails.TAKTIK_CREATIVE),
				new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_LONGSHOTS),
						IMatchDetails.TAKTIK_LONGSHOTS)});

		m_jpTactic = new ComboBoxTitled(getLangStr("ls.team.tactic"), m_jcbTactic, true);

		constraints.gridx = 5;
		layout.setConstraints(m_jpTactic, constraints);
		centerPanel.add(m_jpTactic);

		// Initialize tactic CB
		m_iTactic = MatchTacticType.fromInt(lineup.getTacticType());
		Helper.setComboBoxFromID(m_jcbTactic, lineup.getTacticType());

		// Style of Play ================================================================
		m_jcbStyleOfPlay = new JComboBox<>();
		m_iStyleOfPlay = StyleOfPlay.fromInt(lineup.getStyleOfPlay());
		updateStyleOfPlayComboBox(m_iStyleOfPlay);

		m_jpStyleOfPlay = new ComboBoxTitled(getLangStr("ls.team.styleofPlay"), m_jcbStyleOfPlay, true);

		constraints.gridx = 6;
		layout.setConstraints(m_jpStyleOfPlay, constraints);
		centerPanel.add(m_jpStyleOfPlay);

		// Initialize Style of play CB
		Helper.setComboBoxFromID(m_jcbStyleOfPlay, lineup.getStyleOfPlay());


		// WBr ==========================================================================
		constraints.gridx = 1 + getLineupColumnNumber(0);
		constraints.gridy = getLineupRowNumber(1);
		constraints.insets = new Insets(3, 3, 3, 3);
		//constraints.anchor = GridBagConstraints.CENTER;
		m_clRightBack = new PlayerPositionPanel(this, IMatchRoleID.rightBack, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clRightBack, constraints);
		centerPanel.add(m_clRightBack);
		swapPositionsManager.addSwapCapabilityTo(m_clRightBack);
		m_clLineupPanel.addToAssistant(m_clRightBack);

		// Defense line
		constraints.gridx = 1 + getLineupColumnNumber(1); //2;
		m_clRightCentralDefender = new PlayerPositionPanel(this,
				IMatchRoleID.rightCentralDefender, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clRightCentralDefender, constraints);
		centerPanel.add(m_clRightCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clRightCentralDefender);
		m_clLineupPanel.addToAssistant(m_clRightCentralDefender);

		constraints.gridx = 1 + getLineupColumnNumber(2); //3;
		m_clMiddleCentralDefender = new PlayerPositionPanel(this,
				IMatchRoleID.middleCentralDefender, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clMiddleCentralDefender, constraints);
		centerPanel.add(m_clMiddleCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clMiddleCentralDefender);
		m_clLineupPanel.addToAssistant(m_clMiddleCentralDefender);

		constraints.gridx = 1 + getLineupColumnNumber(3); //4;
		m_clLeftCentralDefender = new PlayerPositionPanel(this,
				IMatchRoleID.leftCentralDefender, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clLeftCentralDefender, constraints);
		centerPanel.add(m_clLeftCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftCentralDefender);
		m_clLineupPanel.addToAssistant(m_clLeftCentralDefender);

		constraints.gridx = 1 + getLineupColumnNumber(4);//5;
		m_clLeftBack = new PlayerPositionPanel(this, IMatchRoleID.leftBack, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clLeftBack, constraints);
		centerPanel.add(m_clLeftBack);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftBack);
		m_clLineupPanel.addToAssistant(m_clLeftBack);

		// Midfield Line
		constraints.gridx = 1 + getLineupColumnNumber(0); //1;
		constraints.gridy = getLineupRowNumber(2);
		m_clRightWinger = new PlayerPositionPanel(this, IMatchRoleID.rightWinger, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clRightWinger, constraints);
		centerPanel.add(m_clRightWinger);
		swapPositionsManager.addSwapCapabilityTo(m_clRightWinger);
		m_clLineupPanel.addToAssistant(m_clRightWinger);

		constraints.gridx = 1 + getLineupColumnNumber(1); //2;
		m_clRightInnerMidfielder = new PlayerPositionPanel(this,
				IMatchRoleID.rightInnerMidfield, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clRightInnerMidfielder, constraints);
		centerPanel.add(m_clRightInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clRightInnerMidfielder);
		m_clLineupPanel.addToAssistant(m_clRightInnerMidfielder);

		constraints.gridx = 1 + getLineupColumnNumber(2); //3;
		m_clCentralInnerMidfielder = new PlayerPositionPanel(this,
				IMatchRoleID.centralInnerMidfield, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clCentralInnerMidfielder, constraints);
		centerPanel.add(m_clCentralInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clCentralInnerMidfielder);
		m_clLineupPanel.addToAssistant(m_clCentralInnerMidfielder);

		constraints.gridx = 1 + getLineupColumnNumber(3); //4;
		m_clLeftInnerMidfielder = new PlayerPositionPanel(this, IMatchRoleID.leftInnerMidfield, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clLeftInnerMidfielder, constraints);
		centerPanel.add(m_clLeftInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftInnerMidfielder);
		m_clLineupPanel.addToAssistant(m_clLeftInnerMidfielder);

		constraints.gridx = 1 + getLineupColumnNumber(4); //5;
		m_clLeftWinger = new PlayerPositionPanel(this, IMatchRoleID.leftWinger, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clLeftWinger, constraints);
		centerPanel.add(m_clLeftWinger);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftWinger);
		m_clLineupPanel.addToAssistant(m_clLeftWinger);

		// Forward line
		constraints.gridx = 1 + getLineupColumnNumber(1); //2;
		constraints.gridy = getLineupRowNumber(3);
		m_clRightForward = new PlayerPositionPanel(this, IMatchRoleID.rightForward, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clRightForward, constraints);
		centerPanel.add(m_clRightForward);
		swapPositionsManager.addSwapCapabilityTo(m_clRightForward);
		m_clLineupPanel.addToAssistant(m_clRightForward);

		constraints.gridx = 1 + getLineupColumnNumber(2); //3;
		m_clCentralForward = new PlayerPositionPanel(this, IMatchRoleID.centralForward, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clCentralForward, constraints);
		centerPanel.add(m_clCentralForward);
		swapPositionsManager.addSwapCapabilityTo(m_clCentralForward);
		m_clLineupPanel.addToAssistant(m_clCentralForward);

		constraints.gridx = 1 + getLineupColumnNumber(3); //4;
		m_clLeftForward = new PlayerPositionPanel(this, IMatchRoleID.leftForward, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clLeftForward, constraints);
		centerPanel.add(m_clLeftForward);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftForward);
		m_clLineupPanel.addToAssistant(m_clLeftForward);

		// Captain and setpieces
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.SOUTH;
		m_clCaptain = new PlayerPositionPanel(this, IMatchRoleID.captain, null, false);
		m_clCaptain.addCaptainIcon();
		layout.setConstraints(m_clCaptain, constraints);
		centerPanel.add(m_clCaptain);

		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.NORTH;
		m_clSetPieceTaker = new PlayerPositionPanel(this, IMatchRoleID.setPieces, m_weather, m_useWeatherImpact);
		m_clSetPieceTaker.addSetPiecesIcon();
		layout.setConstraints(m_clSetPieceTaker, constraints);
		centerPanel.add(m_clSetPieceTaker);

		// Buttons to allocate team number to players currently on the line up
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		centerPanel.add(new AllTeamsPanel(), constraints);

		// Reserves Separation
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		JLabel label1 = new JLabel(HOVerwaltung.instance().getLanguageString("moduleLineup.LineupPanel.SubstitutesLabel"), JLabel.CENTER);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
		label1.setBorder(border);
		Font labelFont = label1.getFont();
		label1.setBackground(Color.GRAY);
		label1.setForeground(ImageUtilities.getColorForContrast(Color.GRAY));
		label1.setFont(new Font(labelFont.getName(), Font.BOLD, 15));
		label1.setOpaque(true);
		layout.setConstraints(label1, constraints);
		centerPanel.add(label1);

		// The reserves
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clSubstKeeper1 = new PlayerPositionPanel(this, IMatchRoleID.substGK1, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstKeeper1, constraints);
		centerPanel.add(m_clSubstKeeper1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstKeeper1);

		constraints.gridx = 1;
		m_clSubstCD1 = new PlayerPositionPanel(this, IMatchRoleID.substCD1, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstCD1, constraints);
		centerPanel.add(m_clSubstCD1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstCD1);

		constraints.gridx = 2;
		m_clSubstWB1 = new PlayerPositionPanel(this, IMatchRoleID.substWB1, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstWB1, constraints);
		centerPanel.add(m_clSubstWB1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstWB1);

		constraints.gridx = 3;
		m_clSubstIM1 = new PlayerPositionPanel(this, IMatchRoleID.substIM1, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstIM1, constraints);
		centerPanel.add(m_clSubstIM1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstIM1);

		constraints.gridx = 4;
		m_clSubstFwd1 = new PlayerPositionPanel(this, IMatchRoleID.substFW1, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstFwd1, constraints);
		centerPanel.add(m_clSubstFwd1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstFwd1);

		constraints.gridx = 5;
		m_clSubstWI1 = new PlayerPositionPanel(this, IMatchRoleID.substWI1, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstWI1, constraints);
		centerPanel.add(m_clSubstWI1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstWI1);

		constraints.gridx = 6;
		m_clSubstXtr1 = new PlayerPositionPanel(this, IMatchRoleID.substXT1);
		layout.setConstraints(m_clSubstXtr1, constraints);
		centerPanel.add(m_clSubstXtr1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstXtr1);

		constraints.gridx = 0;
		constraints.gridy = 6;
		m_clSubstKeeper2 = new PlayerPositionPanel(this, IMatchRoleID.substGK2, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstKeeper2, constraints);
		centerPanel.add(m_clSubstKeeper2);

		constraints.gridx = 1;
		m_clSubstCD2 = new PlayerPositionPanel(this, IMatchRoleID.substCD2, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstCD2, constraints);
		centerPanel.add(m_clSubstCD2);

		constraints.gridx = 2;
		m_clSubstWB2 = new PlayerPositionPanel(this, IMatchRoleID.substWB2, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstWB2, constraints);
		centerPanel.add(m_clSubstWB2);

		constraints.gridx = 3;
		m_clSubstIM2 = new PlayerPositionPanel(this, IMatchRoleID.substIM2, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstIM2, constraints);
		centerPanel.add(m_clSubstIM2);

		constraints.gridx = 4;
		m_clSubstFwd2 = new PlayerPositionPanel(this, IMatchRoleID.substFW2, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstFwd2, constraints);
		centerPanel.add(m_clSubstFwd2);

		constraints.gridx = 5;
		m_clSubstWI2 = new PlayerPositionPanel(this, IMatchRoleID.substWI2, m_weather, m_useWeatherImpact);
		layout.setConstraints(m_clSubstWI2, constraints);
		centerPanel.add(m_clSubstWI2);

		constraints.gridx = 6;
		m_clSubstXtr2 = new PlayerPositionPanel(this, IMatchRoleID.substXT2);
		layout.setConstraints(m_clSubstXtr2, constraints);
		centerPanel.add(m_clSubstXtr2);

		add(centerPanel, BorderLayout.CENTER);

		cbActionListener = e -> {
			if (e.getSource().equals(m_jcbStyleOfPlay)) {
				// StyleOfPlay changed (directly or indirectly)
				var styleOfPlay = ((CBItem) Objects.requireNonNull(m_jcbStyleOfPlay.getSelectedItem(), "ERROR: Style Of Play is null")).getId();
				this.m_iStyleOfPlay = StyleOfPlay.fromInt(styleOfPlay);
				lineup.setStyleOfPlay(styleOfPlay);
				m_clLineupPanel.refreshLineupRatingPanel();
			} else if (e.getSource().equals(m_jcbTeamAttitude)) {
				// Attitude changed
				var attitude = ((CBItem) Objects.requireNonNull(m_jcbTeamAttitude.getSelectedItem(), "ERROR: Attitude is null")).getId();
				m_iAttitude = MatchTeamAttitude.fromInt(attitude);
				lineup.setAttitude(attitude);
				m_clLineupPanel.refreshLineupRatingPanel();
			} else if (e.getSource().equals(m_jcbTactic)) {
				// Tactic changed
				var tactic = ((CBItem) Objects.requireNonNull(m_jcbTactic.getSelectedItem(), "ERROR: Tactic type is null")).getId();
				this.m_iTactic = MatchTacticType.fromInt(tactic);
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setTacticType(tactic);
				m_clLineupPanel.refreshLineupRatingPanel();
			}
		};

		addListeners();
	}

	/**
	 * swap the lineup row numbers, if goalkeeper should be displayed at the bottom
	 * @param i 0, goalkeeper at the top (no swap)
	 *          1, goalkeeper at the bottom (swap)
	 * @return int
	 */
	private int getLineupRowNumber(int i) {
		if (UserParameter.instance().lineupOrientationSetting == GOALKEEPER_AT_TOP) return i;
		return 3 - i;
	}

	/**
	 * swap the lineup column numbers, if goalkeeper should be displayed at the bottom
	 * @param i 0, goalkeeper at the top (no swap)
	 *          1, goalkeeper at the bottom (swap)
	 * @return int
	 */
	private int getLineupColumnNumber(int i) {
		if (UserParameter.instance().lineupOrientationSetting == GOALKEEPER_AT_TOP) return i;
		return 4 - i;
	}

	private void addListeners() {
		m_jcbStyleOfPlay.addActionListener(cbActionListener);
		m_jcbTeamAttitude.addActionListener(cbActionListener);
		m_jcbTactic.addActionListener(cbActionListener);
	}

	private void removeListeners() {
		m_jcbStyleOfPlay.removeActionListener(cbActionListener);
		m_jcbTeamAttitude.removeActionListener(cbActionListener);
		m_jcbTactic.removeActionListener(cbActionListener);
	}


	public ArrayList<PlayerPositionPanel> getAllPositions() {
		ArrayList<PlayerPositionPanel> pos = new ArrayList<>(14);
		pos.add(m_clCentralForward);
		pos.add(m_clCentralInnerMidfielder);
		pos.add(m_clLeftBack);
		pos.add(m_clLeftWinger);
		pos.add(m_clLeftCentralDefender);
		pos.add(m_clLeftInnerMidfielder);
		pos.add(m_clLeftForward);
		pos.add(m_clMiddleCentralDefender);
		pos.add(m_clRightBack);
		pos.add(m_clRightWinger);
		pos.add(m_clRightCentralDefender);
		pos.add(m_clRightInnerMidfielder);
		pos.add(m_clRightForward);
		pos.add(m_clKeeper);
		return pos;
	}

	private String getLangStr(String key) {return HOVerwaltung.instance().getLanguageString(key);}

	private List<Integer> getValidStyleOfPlayValues()
	{
		TrainerType trainer;
		int tacticalAssistants;
		try {
			trainer = HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp();
			tacticalAssistants = HOVerwaltung.instance().getModel().getClub().getTacticalAssistantLevels();

		} catch (Exception e) {
			trainer = TrainerType.Balanced;
			tacticalAssistants = 0;
			HOLogger.instance().error(getClass(), "Model not ready, put default value " + trainer + " for trainer and "  + tacticalAssistants + " for tactical Assistants.");
		}

		int min=-10, max=10;

		switch (trainer) {
			case Defensive -> max = -10 + 2 * tacticalAssistants;  // Defensive
			case Offensive -> min = 10 - 2 * tacticalAssistants;   // Offensive
			case Balanced -> {     			                   // Neutral
				min = - tacticalAssistants;
				max = tacticalAssistants;
			}
			default -> HOLogger.instance().error(getClass(), "Illegal trainer type found: " + trainer);
		}

		return IntStream.rangeClosed(min, max).boxed().collect(Collectors.toList());
	}

	// each time updateStyleOfPlayBox gets called we need to add all elements back so that we can load stored lineups
	// so we need addAllStyleOfPlayItems() after every updateStyleOfPlayBox()
	public int updateStyleOfPlayComboBox(StyleOfPlay oldValue)
	{
		// NT Team can select whatever Style of Play they like
		if (!UserManager.instance().getCurrentUser().isNtTeam()) {

			removeListeners();

			// remove all combo box items and add new ones.
			List<Integer> legalValues = getValidStyleOfPlayValues();

			m_jcbStyleOfPlay.removeAllItems();

			for (int value : legalValues) {
				CBItem cbItem;
				if (value == 0) {
					cbItem = new CBItem(neutral_sop, value);
				} else if (value > 0) {
					cbItem = new CBItem((value * 10) + "% " + offensive_sop, value);
				} else {
					cbItem = new CBItem((Math.abs(value) * 10) + "% " + defensive_sop, value);
				}
				m_jcbStyleOfPlay.addItem(cbItem);
			}

			addListeners();

			// Set trainer default value
			setStyleOfPlay(getDefaultTrainerStyleOfPlay());
			// Attempt to set the old value. If it is not possible it will do nothing.
			setStyleOfPlay(oldValue);
		}
		var item = (CBItem)(m_jcbStyleOfPlay.getSelectedItem());
		if ( item != null) return item.getId();
		return 0;
	}

	public void setStyleOfPlay(StyleOfPlay style){
		Helper.setComboBoxFromID(m_jcbStyleOfPlay, StyleOfPlay.toInt(style));
	}

	private StyleOfPlay getDefaultTrainerStyleOfPlay() {
		TrainerType trainer;
		try {
			trainer = HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp();
		} catch (Exception e) {
			return StyleOfPlay.Neutral();  // Happens for instance with empty db
		}

		return switch (trainer) {
			case Defensive -> StyleOfPlay.Defensive(); // Defensive
			case Offensive -> StyleOfPlay.Offensive(); // Offensive
			default -> StyleOfPlay.Neutral();  // Neutral
		};
	}

	public boolean is_jcbTeamAttitudeInitialized() {
		return m_jcbTeamAttitude != null;
	}
}
