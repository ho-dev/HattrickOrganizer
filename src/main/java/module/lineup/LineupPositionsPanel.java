// %2976388207:de.hattrickorganizer.gui.lineup%
package module.lineup;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.model.AufstellungCBItem;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;

import static javax.swing.SwingConstants.HORIZONTAL;

/**
 * Enthält die einzelnen Positionen
 */
public class LineupPositionsPanel extends core.gui.comp.panel.RasenPanel implements
		core.gui.Refreshable, core.gui.Updateable, ActionListener {

	private static final long serialVersionUID = -9098199182886069003L;
	private LineupPanel m_clLineupPanel;
	private JButton m_jbDrucken = new JButton(ThemeManager.getIcon(HOIconName.PRINTER));
	private JButton m_jbFlipSide = new JButton(ThemeManager.getIcon(HOIconName.RELOAD));
	private JButton m_jbMidiFrame = new JButton(ThemeManager.getIcon(HOIconName.MIDLINEUPFRAME));
	private JButton m_jbMiniFrame = new JButton(ThemeManager.getIcon(HOIconName.MINLINEUPFRAME));
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
	
//	private static final String EMPTY = "";
//	private static final String SPACE =" ";
//
//	public PlayerPositionPanel getM_clCentralForward() {
//		return m_clCentralForward;
//	}
//
//	public void setM_clCentralForward(PlayerPositionPanel m_clCentralForward) {
//		this.m_clCentralForward = m_clCentralForward;
//	}
//
//	public PlayerPositionPanel getM_clCentralInnerMidfielder() {
//		return m_clCentralInnerMidfielder;
//	}
//
//	public void setM_clCentralInnerMidfielder(
//			PlayerPositionPanel m_clCentralInnerMidfielder) {
//		this.m_clCentralInnerMidfielder = m_clCentralInnerMidfielder;
//	}
//
//	public PlayerPositionPanel getM_clLeftBack() {
//		return m_clLeftBack;
//	}
//
//	public void setM_clLeftBack(PlayerPositionPanel m_clLeftBack) {
//		this.m_clLeftBack = m_clLeftBack;
//	}
//
//	public PlayerPositionPanel getM_clLeftWinger() {
//		return m_clLeftWinger;
//	}
//
//	public void setM_clLeftWinger(PlayerPositionPanel m_clLeftWinger) {
//		this.m_clLeftWinger = m_clLeftWinger;
//	}
//
//	public PlayerPositionPanel getM_clLeftCentralDefender() {
//		return m_clLeftCentralDefender;
//	}
//
//	public void setM_clLeftCentralDefender(
//			PlayerPositionPanel m_clLeftCentralDefender) {
//		this.m_clLeftCentralDefender = m_clLeftCentralDefender;
//	}
//
//	public PlayerPositionPanel getM_clLeftInnerMidfielder() {
//		return m_clLeftInnerMidfielder;
//	}
//
//	public void setM_clLeftInnerMidfielder(
//			PlayerPositionPanel m_clLeftInnerMidfielder) {
//		this.m_clLeftInnerMidfielder = m_clLeftInnerMidfielder;
//	}
//
//	public PlayerPositionPanel getM_clLeftForward() {
//		return m_clLeftForward;
//	}
//
//	public void setM_clLeftForward(PlayerPositionPanel m_clLeftForward) {
//		this.m_clLeftForward = m_clLeftForward;
//	}
//
//	public PlayerPositionPanel getM_clMiddleCentralDefender() {
//		return m_clMiddleCentralDefender;
//	}
//
//	public void setM_clMiddleCentralDefender(
//			PlayerPositionPanel m_clMiddleCentralDefender) {
//		this.m_clMiddleCentralDefender = m_clMiddleCentralDefender;
//	}
//
//	public PlayerPositionPanel getM_clRightBack() {
//		return m_clRightBack;
//	}
//
//	public void setM_clRightBack(PlayerPositionPanel m_clRightBack) {
//		this.m_clRightBack = m_clRightBack;
//	}
//
//	public PlayerPositionPanel getM_clRightWinger() {
//		return m_clRightWinger;
//	}
//
//	public void setM_clRightWinger(PlayerPositionPanel m_clRightWinger) {
//		this.m_clRightWinger = m_clRightWinger;
//	}
//
//	public PlayerPositionPanel getM_clRightCentralDefender() {
//		return m_clRightCentralDefender;
//	}
//
//	public void setM_clRightCentralDefender(
//			PlayerPositionPanel m_clRightCentralDefender) {
//		this.m_clRightCentralDefender = m_clRightCentralDefender;
//	}
//
//	public PlayerPositionPanel getM_clRightInnerMidfielder() {
//		return m_clRightInnerMidfielder;
//	}
//
//	public void setM_clRightInnerMidfielder(
//			PlayerPositionPanel m_clRightInnerMidfielder) {
//		this.m_clRightInnerMidfielder = m_clRightInnerMidfielder;
//	}
//
//	public PlayerPositionPanel getM_clRightForward() {
//		return m_clRightForward;
//	}
//
//	public void setM_clRightForward(PlayerPositionPanel m_clRightForward) {
//		this.m_clRightForward = m_clRightForward;
//	}
//
//	public PlayerPositionPanel getM_clKeeper() {
//		return m_clKeeper;
//	}
//
//	public void setM_clKeeper(PlayerPositionPanel m_clKeeper) {
//		this.m_clKeeper = m_clKeeper;
//	}

	public LineupPositionsPanel(LineupPanel panel) {
		m_clLineupPanel = panel;
		assistantPanel = panel.getAufstellungsAssistentPanel();
		initComponentes();
		RefreshManager.instance().registerRefreshable(this);
	}

	public javax.swing.JLayeredPane getCenterPanel() {
		return centerPanel;
	}

	@Override
	public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(m_jbMiniFrame)) {
			new AufstellungsMiniPositionsFrame(m_clLineupPanel, false, true);
		} else if (actionEvent.getSource().equals(m_jbMidiFrame)) {
			new AufstellungsMiniPositionsFrame(m_clLineupPanel, false, false);
		} else if (actionEvent.getSource().equals(m_jbFlipSide)) {
			HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().flipSide();
			HOMainFrame.instance().getAufstellungsPanel().update();
		} else {
			final AufstellungsMiniPositionsFrame frame = new AufstellungsMiniPositionsFrame(
					m_clLineupPanel, true, false);
			frame.setVisible(true);

			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}

			frame.doPrint();
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
		List<Player> selectedPlayers = new ArrayList<Player>();
		List<Player> allPlayers = HOVerwaltung.instance().getModel().getAllSpieler();
		List<Player> filteredPlayers = new ArrayList<Player>();
		Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

		for (Player player: allPlayers) {
			// the first 11
			if (lineup.isPlayerInStartingEleven(player.getSpielerID())) {
				selectedPlayers.add(player);
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
					HOLogger.instance().log(getClass(), "Exclude: " + player.getName());
				}

				if (include) {
					filteredPlayers.add(player);
				}
			}
		}

		m_clKeeper.refresh(filteredPlayers);
		m_clLeftBack.refresh(filteredPlayers);
		m_clLeftCentralDefender.refresh(filteredPlayers);
		m_clMiddleCentralDefender.refresh(filteredPlayers);
		m_clRightCentralDefender.refresh(filteredPlayers);
		m_clRightBack.refresh(filteredPlayers);
		m_clLeftWinger.refresh(filteredPlayers);
		m_clLeftInnerMidfielder.refresh(filteredPlayers);
		m_clCentralInnerMidfielder.refresh(filteredPlayers);
		m_clRightInnerMidfielder.refresh(filteredPlayers);
		m_clRightWinger.refresh(filteredPlayers);
		m_clLeftForward.refresh(filteredPlayers);
		m_clCentralForward.refresh(filteredPlayers);
		m_clRightForward.refresh(filteredPlayers);
		m_clSubstKeeper1.refresh(filteredPlayers);
		m_clSubstKeeper2.refresh2(filteredPlayers, m_clSubstKeeper1.getPlayerId());
		m_clSubstCD1.refresh(filteredPlayers);
	    m_clSubstCD2.refresh2(filteredPlayers, m_clSubstCD1.getPlayerId());
		m_clSubstWB1.refresh(filteredPlayers);
		m_clSubstWB2.refresh2(filteredPlayers, m_clSubstWB1.getPlayerId());
		m_clSubstIM1.refresh(filteredPlayers);
		m_clSubstIM2.refresh2(filteredPlayers, m_clSubstIM1.getPlayerId());
		m_clSubstFwd1.refresh(filteredPlayers);
		m_clSubstFwd2.refresh2(filteredPlayers, m_clSubstFwd1.getPlayerId());
		m_clSubstWI1.refresh(filteredPlayers);
		m_clSubstWI2.refresh2(filteredPlayers, m_clSubstWI1.getPlayerId());
		m_clSubstXtr1.refresh(filteredPlayers);
		m_clSubstXtr2.refresh2(filteredPlayers, m_clSubstXtr1.getPlayerId());
		m_clSetPieceTaker.refresh(selectedPlayers);
	 	m_clCaptain.refresh(selectedPlayers);

		// Check
		lineup.checkAufgestellteSpieler();
	}

	public void exportOldLineup(String name) {
		File dir = new File("Lineups/"
				+ HOVerwaltung.instance().getModel().getBasics().getManager());
		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			File f = new File(dir, name + ".dat");
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write("<lineup>");
			bw.newLine();
			saveDetail(bw, m_clKeeper);
			saveDetail(bw, m_clRightBack);
			saveDetail(bw, m_clRightCentralDefender);
			saveDetail(bw, m_clMiddleCentralDefender);
			saveDetail(bw, m_clLeftCentralDefender);
			saveDetail(bw, m_clLeftBack);
			saveDetail(bw, m_clRightWinger);
			saveDetail(bw, m_clRightInnerMidfielder);
			saveDetail(bw, m_clCentralInnerMidfielder);
			saveDetail(bw, m_clLeftInnerMidfielder);
			saveDetail(bw, m_clLeftWinger);
			saveDetail(bw, m_clRightForward);
			saveDetail(bw, m_clCentralForward);
			saveDetail(bw, m_clLeftForward);
			saveDetail(bw, m_clSubstKeeper1);
			saveDetail(bw, m_clSubstKeeper2);
			saveDetail(bw, m_clSubstCD1);
			saveDetail(bw, m_clSubstCD2);
			saveDetail(bw, m_clSubstWB1);
			saveDetail(bw, m_clSubstWB2);
			saveDetail(bw, m_clSubstIM1);
			saveDetail(bw, m_clSubstIM2);
			saveDetail(bw, m_clSubstFwd1);
			saveDetail(bw, m_clSubstFwd2);
			saveDetail(bw, m_clSubstWI1);
			saveDetail(bw, m_clSubstWI2);
			saveDetail(bw, m_clSubstXtr1);
			saveDetail(bw, m_clSubstXtr2);
			saveDetail(bw, m_clSetPieceTaker);
			saveDetail(bw, m_clCaptain);

			bw.write("<tacticType>" + m_clLineupPanel.getAufstellungsDetailPanel().getTaktik()
					+ "</tacticType>");
			bw.newLine();
			bw.write("<matchType>" + m_clLineupPanel.getAufstellungsDetailPanel().getEinstellung()
					+ "</matchType>");
			bw.newLine();
			bw.write("</lineup>");
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private void saveDetail(BufferedWriter bw, PlayerPositionPanel positionPanel)
			throws IOException {
		bw.write("<position>");
		bw.newLine();
		bw.write("<code>" + positionPanel.getPositionsID() + "</code>");
		bw.newLine();
		bw.write("<player>" + positionPanel.getPlayerId() + "</player>");
		bw.newLine();
		bw.write("<tactic>" + positionPanel.getTacticOrder() + "</tactic>");
		bw.newLine();
		bw.write("</position>");
		bw.newLine();
	}
	
////	private void savePenaltyTakers(BufferedWriter bw) {
////
//	}

	@Override
	public final void update() {
		m_clLineupPanel.update();
	}

	/**
	 * Erstellt die Komponenten
	 */
	private void initComponentes() {
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
		m_jbDrucken.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString(
				"tt_AufstellungsPosition_Drucken"));
		m_jbDrucken.addActionListener(this);
		m_jbDrucken.setPreferredSize(new Dimension(25, 25));
		panel.add(m_jbDrucken);
		m_jbMiniFrame.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString(
				"tt_AufstellungsPosition_MiniFrame"));
		m_jbMiniFrame.addActionListener(this);
		m_jbMiniFrame.setPreferredSize(new Dimension(25, 25));
		panel.add(m_jbMiniFrame);
		m_jbMidiFrame.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString(
				"tt_AufstellungsPosition_MidiFrame"));
		m_jbMidiFrame.addActionListener(this);
		m_jbMidiFrame.setPreferredSize(new Dimension(25, 25));
		panel.add(m_jbMidiFrame);
		m_jbFlipSide.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString(
				"tt_AufstellungsPosition_FlipSide"));
		m_jbFlipSide.addActionListener(this);
		m_jbFlipSide.setPreferredSize(new Dimension(25, 25));
		panel.add(m_jbFlipSide);
		m_jbDrucken.setPreferredSize(new Dimension(m_jbDrucken.getPreferredSize().width, 25));
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

		// Test
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		JLabel label1 = new JLabel("SUBSTITUTES", JLabel.CENTER);
		// create a line border with the specified color and width
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
		swapPositionsManager.addSwapCapabilityTo(m_clSubstKeeper2);

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
		swapPositionsManager.addSwapCapabilityTo(m_clSubstCD2);

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
		swapPositionsManager.addSwapCapabilityTo(m_clSubstWB2);

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
		swapPositionsManager.addSwapCapabilityTo(m_clSubstIM2);

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
		swapPositionsManager.addSwapCapabilityTo(m_clSubstFwd2);

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
		swapPositionsManager.addSwapCapabilityTo(m_clSubstWI2);

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
		swapPositionsManager.addSwapCapabilityTo(m_clSubstXtr2);

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
