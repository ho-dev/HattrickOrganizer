package module.lineup;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.model.AufstellungCBItem;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * Create the main panel of Lineup module
 */
public class LineupPositionsPanel extends core.gui.comp.panel.RasenPanel implements
		core.gui.Refreshable, core.gui.Updateable, ActionListener {

	private final LineupPanel m_clLineupPanel;
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
	private final IAufstellungsAssistentPanel assistantPanel;
	

	public LineupPositionsPanel(LineupPanel panel) {
		m_clLineupPanel = panel;
		assistantPanel = panel.getAufstellungsAssistentPanel();
		initComponents();
		RefreshManager.instance().registerRefreshable(this);
	}

	public javax.swing.JLayeredPane getCenterPanel() {
		return centerPanel;
	}

	@Override
	public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(m_jbFlipSide)) {
			HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().flipSide();
			HOMainFrame.instance().getAufstellungsPanel().update();
		}
	}

	@Override
	public final void reInit() {
		refresh();
	}

	@Override
	public final void refresh() {
		boolean gruppenfilter = m_clLineupPanel.getAufstellungsAssistentPanel()
				.isGroupFilter();
		String gruppe = m_clLineupPanel.getAufstellungsAssistentPanel().getGroup();
		boolean gruppenegieren = m_clLineupPanel.getAufstellungsAssistentPanel().isNotGroup();

		boolean exludeLast = m_clLineupPanel.getAufstellungsAssistentPanel()
				.isExcludeLastMatch();

		// All Player Positions Inform First 11
		List<Player> selectedPlayers = new ArrayList<>();
		List<Player> assitPlayers = new ArrayList<>();
		List<Player> allPlayers = HOVerwaltung.instance().getModel().getCurrentPlayers();
		List<Player> filteredPlayers = new ArrayList<>();
		Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

		for (Player player: allPlayers) {
			// the first 11
			if (lineup.isPlayerInStartingEleven(player.getSpielerID())) {
				selectedPlayers.add(player);
			}
			else if (lineup.isSpielerInReserve(player.getSpielerID())) {
				assitPlayers.add(player);
			}
		}

		// Apply the Group Filter
		for (Player player: allPlayers) {
			// No Filter
			if (!gruppenfilter || (gruppe.equals(player.getTeamInfoSmilie()) && !gruppenegieren)
					|| (!gruppe.equals(player.getTeamInfoSmilie()) && gruppenegieren)) {
				boolean include = true;
				final AufstellungCBItem lastLineup = AufstellungsVergleichHistoryPanel
						.getLastLineup();

				if (exludeLast
						&& (lastLineup != null)
						&& lastLineup.getAufstellung()
								.isPlayerInStartingEleven(player.getSpielerID())) {
					include = false;
					HOLogger.instance().log(getClass(), "Exclude: " + player.getFullName());
				}

				if (include) {
					filteredPlayers.add(player);
				}
			}
		}

		m_clKeeper.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clLeftBack.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clLeftCentralDefender.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clMiddleCentralDefender.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clRightCentralDefender.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clRightBack.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clLeftWinger.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clLeftInnerMidfielder.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clCentralInnerMidfielder.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clRightInnerMidfielder.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clRightWinger.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clLeftForward.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clCentralForward.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clRightForward.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clSubstKeeper1.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clSubstKeeper2.refresh2(filteredPlayers, m_clSubstKeeper1.getPlayerId());
		m_clSubstCD1.refresh(filteredPlayers, selectedPlayers, assitPlayers);
	    m_clSubstCD2.refresh2(filteredPlayers, m_clSubstCD1.getPlayerId());
		m_clSubstWB1.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clSubstWB2.refresh2(filteredPlayers, m_clSubstWB1.getPlayerId());
		m_clSubstIM1.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clSubstIM2.refresh2(filteredPlayers, m_clSubstIM1.getPlayerId());
		m_clSubstFwd1.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clSubstFwd2.refresh2(filteredPlayers, m_clSubstFwd1.getPlayerId());
		m_clSubstWI1.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clSubstWI2.refresh2(filteredPlayers, m_clSubstWI1.getPlayerId());
		m_clSubstXtr1.refresh(filteredPlayers, selectedPlayers, assitPlayers);
		m_clSubstXtr2.refresh2(filteredPlayers, m_clSubstXtr1.getPlayerId());
		m_clSetPieceTaker.refresh(selectedPlayers, null, null);
	 	m_clCaptain.refresh(selectedPlayers, null, null);

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
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(1, 1, 1, 1);

		centerPanel.setLayout(layout);

		constraints.gridx = 3;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		m_clKeeper = new PlayerPositionPanel(this, IMatchRoleID.keeper);
		layout.setConstraints(m_clKeeper, constraints);
		centerPanel.add(m_clKeeper);
		swapPositionsManager.addSwapCapabilityTo(m_clKeeper);
		
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clRightBack = new PlayerPositionPanel(this, IMatchRoleID.rightBack);
		layout.setConstraints(m_clRightBack, constraints);
		centerPanel.add(m_clRightBack);
		swapPositionsManager.addSwapCapabilityTo(m_clRightBack);
		assistantPanel.addToAssistant(m_clRightBack);

		// Defense line

		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clRightCentralDefender = new PlayerPositionPanel(this,
				IMatchRoleID.rightCentralDefender);
		layout.setConstraints(m_clRightCentralDefender, constraints);
		centerPanel.add(m_clRightCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clRightCentralDefender);
		assistantPanel.addToAssistant(m_clRightCentralDefender);

		constraints.gridx = 3;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clMiddleCentralDefender = new PlayerPositionPanel(this,
				IMatchRoleID.middleCentralDefender);
		layout.setConstraints(m_clMiddleCentralDefender, constraints);
		centerPanel.add(m_clMiddleCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clMiddleCentralDefender);
		assistantPanel.addToAssistant(m_clMiddleCentralDefender);

		constraints.gridx = 4;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clLeftCentralDefender = new PlayerPositionPanel(this,
				IMatchRoleID.leftCentralDefender);
		layout.setConstraints(m_clLeftCentralDefender, constraints);
		centerPanel.add(m_clLeftCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftCentralDefender);
		assistantPanel.addToAssistant(m_clLeftCentralDefender);

		constraints.gridx = 5;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clLeftBack = new PlayerPositionPanel(this, IMatchRoleID.leftBack);
		layout.setConstraints(m_clLeftBack, constraints);
		centerPanel.add(m_clLeftBack);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftBack);
		assistantPanel.addToAssistant(m_clLeftBack);

		// Midfield Line

		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clRightWinger = new PlayerPositionPanel(this, IMatchRoleID.rightWinger);
		layout.setConstraints(m_clRightWinger, constraints);
		centerPanel.add(m_clRightWinger);
		swapPositionsManager.addSwapCapabilityTo(m_clRightWinger);
		assistantPanel.addToAssistant(m_clRightWinger);

		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clRightInnerMidfielder = new PlayerPositionPanel(this,
				IMatchRoleID.rightInnerMidfield);
		layout.setConstraints(m_clRightInnerMidfielder, constraints);
		centerPanel.add(m_clRightInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clRightInnerMidfielder);
		assistantPanel.addToAssistant(m_clRightInnerMidfielder);

		constraints.gridx = 3;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clCentralInnerMidfielder = new PlayerPositionPanel(this,
				IMatchRoleID.centralInnerMidfield);
		layout.setConstraints(m_clCentralInnerMidfielder, constraints);
		centerPanel.add(m_clCentralInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clCentralInnerMidfielder);
		assistantPanel.addToAssistant(m_clCentralInnerMidfielder);

		constraints.gridx = 4;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clLeftInnerMidfielder = new PlayerPositionPanel(this, IMatchRoleID.leftInnerMidfield);
		layout.setConstraints(m_clLeftInnerMidfielder, constraints);
		centerPanel.add(m_clLeftInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftInnerMidfielder);
		assistantPanel.addToAssistant(m_clLeftInnerMidfielder);

		constraints.gridx = 5;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clLeftWinger = new PlayerPositionPanel(this, IMatchRoleID.leftWinger);
		layout.setConstraints(m_clLeftWinger, constraints);
		centerPanel.add(m_clLeftWinger);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftWinger);
		assistantPanel.addToAssistant(m_clLeftWinger);

		// Forward line

		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		m_clRightForward = new PlayerPositionPanel(this, IMatchRoleID.rightForward);
		layout.setConstraints(m_clRightForward, constraints);
		centerPanel.add(m_clRightForward);
		swapPositionsManager.addSwapCapabilityTo(m_clRightForward);
		assistantPanel.addToAssistant(m_clRightForward);

		constraints.gridx = 3;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		m_clCentralForward = new PlayerPositionPanel(this, IMatchRoleID.centralForward);
		layout.setConstraints(m_clCentralForward, constraints);
		centerPanel.add(m_clCentralForward);
		swapPositionsManager.addSwapCapabilityTo(m_clCentralForward);
		assistantPanel.addToAssistant(m_clCentralForward);

		constraints.gridx = 4;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		m_clLeftForward = new PlayerPositionPanel(this, IMatchRoleID.leftForward);
		layout.setConstraints(m_clLeftForward, constraints);
		centerPanel.add(m_clLeftForward);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftForward);
		assistantPanel.addToAssistant(m_clLeftForward);

		// A spacer between forwards and captain

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 7;
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createVerticalStrut(10));
		layout.setConstraints(box, constraints);
		centerPanel.add(box);

		// Captain and setpieces

		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clCaptain = new PlayerPositionPanel(this, IMatchRoleID.captain);
		layout.setConstraints(m_clCaptain, constraints);
		centerPanel.add(m_clCaptain);

		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clSetPieceTaker = new PlayerPositionPanel(this, IMatchRoleID.setPieces);
		layout.setConstraints(m_clSetPieceTaker, constraints);
		centerPanel.add(m_clSetPieceTaker);

		// Buttons to allocate team number to players currently on the line up

		constraints.gridx = 4;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		centerPanel.add(new AufstellungsGruppenPanel(), constraints);

		// buttons to toggle MiniLineup, reverse lineup ...
		final JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setOpaque(false);

		final JPanel panel = new JPanel();
		panel.setOpaque(false);
		m_jbFlipSide.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString("tt_AufstellungsPosition_FlipSide"));
		m_jbFlipSide.addActionListener(this);
		m_jbFlipSide.setMargin(new Insets(0, 0, 0, 0));
		panel.add(m_jbFlipSide);
		buttonPanel.add(panel, BorderLayout.NORTH);

		constraints.gridx = 6;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(panel, constraints);
		centerPanel.add(panel);

		// A spacer between captain and reserves.
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 7;
		Box box2 = new Box(BoxLayout.Y_AXIS);
		box2.add(Box.createVerticalStrut(35));
		layout.setConstraints(box2, constraints);
		centerPanel.add(box2);

		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		JLabel label1 = new JLabel(HOVerwaltung.instance().getLanguageString("moduleLineup.LineupPanel.SubstitutesLabel"), JLabel.CENTER);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
		label1.setBorder(border);
		Font labelFont = label1.getFont();
		label1.setFont(new Font(labelFont.getName(), Font.BOLD, 15));
		label1.setOpaque(true);
		label1.setBackground(Color.GRAY);
		layout.setConstraints(label1, constraints);
		centerPanel.add(label1);

		// The reserves
		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		m_clSubstKeeper1 = new PlayerPositionPanel(this, IMatchRoleID.substGK1);
		layout.setConstraints(m_clSubstKeeper1, constraints);
		centerPanel.add(m_clSubstKeeper1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstKeeper1);

		constraints.gridx = 0;
		constraints.gridy = 9;
		constraints.gridwidth = 1;
		m_clSubstKeeper2 = new PlayerPositionPanel(this, IMatchRoleID.substGK2);
		layout.setConstraints(m_clSubstKeeper2, constraints);
		centerPanel.add(m_clSubstKeeper2);

		constraints.gridx = 1;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		m_clSubstCD1 = new PlayerPositionPanel(this, IMatchRoleID.substCD1);
		layout.setConstraints(m_clSubstCD1, constraints);
		centerPanel.add(m_clSubstCD1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstCD1);

		constraints.gridx = 1;
		constraints.gridy = 9;
		constraints.gridwidth = 1;
		m_clSubstCD2 = new PlayerPositionPanel(this, IMatchRoleID.substCD2);
		layout.setConstraints(m_clSubstCD2, constraints);
		centerPanel.add(m_clSubstCD2);

		constraints.gridx = 2;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		m_clSubstWB1 = new PlayerPositionPanel(this, IMatchRoleID.substWB1);
		layout.setConstraints(m_clSubstWB1, constraints);
		centerPanel.add(m_clSubstWB1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstWB1);

		constraints.gridx = 2;
		constraints.gridy = 9;
		constraints.gridwidth = 1;
		m_clSubstWB2 = new PlayerPositionPanel(this, IMatchRoleID.substWB2);
		layout.setConstraints(m_clSubstWB2, constraints);
		centerPanel.add(m_clSubstWB2);

		constraints.gridx = 3;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		m_clSubstIM1 = new PlayerPositionPanel(this, IMatchRoleID.substIM1);
		layout.setConstraints(m_clSubstIM1, constraints);
		centerPanel.add(m_clSubstIM1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstIM1);

		constraints.gridx = 3;
		constraints.gridy = 9;
		constraints.gridwidth = 1;
		m_clSubstIM2 = new PlayerPositionPanel(this, IMatchRoleID.substIM2);
		layout.setConstraints(m_clSubstIM2, constraints);
		centerPanel.add(m_clSubstIM2);

		constraints.gridx = 4;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		m_clSubstFwd1= new PlayerPositionPanel(this, IMatchRoleID.substFW1);
		layout.setConstraints(m_clSubstFwd1, constraints);
		centerPanel.add(m_clSubstFwd1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstFwd1);

		constraints.gridx = 4;
		constraints.gridy = 9;
		constraints.gridwidth = 1;
		m_clSubstFwd2= new PlayerPositionPanel(this, IMatchRoleID.substFW2);
		layout.setConstraints(m_clSubstFwd2, constraints);
		centerPanel.add(m_clSubstFwd2);

		constraints.gridx = 5;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		m_clSubstWI1 = new PlayerPositionPanel(this, IMatchRoleID.substWI1);
		layout.setConstraints(m_clSubstWI1, constraints);
		centerPanel.add(m_clSubstWI1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstWI1);

		constraints.gridx = 5;
		constraints.gridy = 9;
		constraints.gridwidth = 1;
		m_clSubstWI2 = new PlayerPositionPanel(this, IMatchRoleID.substWI2);
		layout.setConstraints(m_clSubstWI2, constraints);
		centerPanel.add(m_clSubstWI2);

		constraints.gridx = 6;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		m_clSubstXtr1 = new PlayerPositionPanel(this, IMatchRoleID.substXT1);
		layout.setConstraints(m_clSubstXtr1, constraints);
		centerPanel.add(m_clSubstXtr1);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstXtr1);

		constraints.gridx = 6;
		constraints.gridy = 9;
		constraints.gridwidth = 1;
		m_clSubstXtr2 = new PlayerPositionPanel(this, IMatchRoleID.substXT2);
		layout.setConstraints(m_clSubstXtr2, constraints);
		centerPanel.add(m_clSubstXtr2);

		add(centerPanel, BorderLayout.CENTER);
	}
	// get all positions
	public ArrayList<PlayerPositionPanel> getAllPositions() {
		ArrayList<PlayerPositionPanel> pos = new ArrayList<PlayerPositionPanel>(14);
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
}
