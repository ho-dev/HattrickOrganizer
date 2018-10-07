// %2976388207:de.hattrickorganizer.gui.lineup%
package module.lineup;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.model.AufstellungCBItem;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

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
	private PlayerPositionPanel m_clCentralForward;
	private PlayerPositionPanel m_clCentralInnerMidfielder;
	private PlayerPositionPanel m_clLeftBack;
	private PlayerPositionPanel m_clLeftWinger;
	private PlayerPositionPanel m_clLeftCentralDefender;
	private PlayerPositionPanel m_clLeftInnerMidfielder;
	private PlayerPositionPanel m_clLeftForward;
	private PlayerPositionPanel m_clMiddleCentralDefender;
	private PlayerPositionPanel m_clRightBack;
	private PlayerPositionPanel m_clRightWinger;
	private PlayerPositionPanel m_clRightCentralDefender;
	private PlayerPositionPanel m_clRightInnerMidfielder;
	private PlayerPositionPanel m_clRightForward;
	private PlayerPositionPanel m_clSubstWinger;
	private PlayerPositionPanel m_clSubstMidfield;
	private PlayerPositionPanel m_clSubstForward;
	private PlayerPositionPanel m_clSubstKeeper;
	private PlayerPositionPanel m_clSubstDefender;
	private PlayerPositionPanel m_clCaptain;
	private PlayerPositionPanel m_clSetPieceTaker;
	private PlayerPositionPanel m_clKeeper;
	private javax.swing.JLayeredPane centerPanel;
	private final SwapPositionsManager swapPositionsManager = new SwapPositionsManager(this);
	private final IAufstellungsAssistentPanel assistantPanel;
	
	private static final String EMPTY = "";
	private static final String SPACE =" ";

	public PlayerPositionPanel getM_clCentralForward() {
		return m_clCentralForward;
	}

	public void setM_clCentralForward(PlayerPositionPanel m_clCentralForward) {
		this.m_clCentralForward = m_clCentralForward;
	}

	public PlayerPositionPanel getM_clCentralInnerMidfielder() {
		return m_clCentralInnerMidfielder;
	}

	public void setM_clCentralInnerMidfielder(
			PlayerPositionPanel m_clCentralInnerMidfielder) {
		this.m_clCentralInnerMidfielder = m_clCentralInnerMidfielder;
	}

	public PlayerPositionPanel getM_clLeftBack() {
		return m_clLeftBack;
	}

	public void setM_clLeftBack(PlayerPositionPanel m_clLeftBack) {
		this.m_clLeftBack = m_clLeftBack;
	}

	public PlayerPositionPanel getM_clLeftWinger() {
		return m_clLeftWinger;
	}

	public void setM_clLeftWinger(PlayerPositionPanel m_clLeftWinger) {
		this.m_clLeftWinger = m_clLeftWinger;
	}

	public PlayerPositionPanel getM_clLeftCentralDefender() {
		return m_clLeftCentralDefender;
	}

	public void setM_clLeftCentralDefender(
			PlayerPositionPanel m_clLeftCentralDefender) {
		this.m_clLeftCentralDefender = m_clLeftCentralDefender;
	}

	public PlayerPositionPanel getM_clLeftInnerMidfielder() {
		return m_clLeftInnerMidfielder;
	}

	public void setM_clLeftInnerMidfielder(
			PlayerPositionPanel m_clLeftInnerMidfielder) {
		this.m_clLeftInnerMidfielder = m_clLeftInnerMidfielder;
	}

	public PlayerPositionPanel getM_clLeftForward() {
		return m_clLeftForward;
	}

	public void setM_clLeftForward(PlayerPositionPanel m_clLeftForward) {
		this.m_clLeftForward = m_clLeftForward;
	}

	public PlayerPositionPanel getM_clMiddleCentralDefender() {
		return m_clMiddleCentralDefender;
	}

	public void setM_clMiddleCentralDefender(
			PlayerPositionPanel m_clMiddleCentralDefender) {
		this.m_clMiddleCentralDefender = m_clMiddleCentralDefender;
	}

	public PlayerPositionPanel getM_clRightBack() {
		return m_clRightBack;
	}

	public void setM_clRightBack(PlayerPositionPanel m_clRightBack) {
		this.m_clRightBack = m_clRightBack;
	}

	public PlayerPositionPanel getM_clRightWinger() {
		return m_clRightWinger;
	}

	public void setM_clRightWinger(PlayerPositionPanel m_clRightWinger) {
		this.m_clRightWinger = m_clRightWinger;
	}

	public PlayerPositionPanel getM_clRightCentralDefender() {
		return m_clRightCentralDefender;
	}

	public void setM_clRightCentralDefender(
			PlayerPositionPanel m_clRightCentralDefender) {
		this.m_clRightCentralDefender = m_clRightCentralDefender;
	}

	public PlayerPositionPanel getM_clRightInnerMidfielder() {
		return m_clRightInnerMidfielder;
	}

	public void setM_clRightInnerMidfielder(
			PlayerPositionPanel m_clRightInnerMidfielder) {
		this.m_clRightInnerMidfielder = m_clRightInnerMidfielder;
	}

	public PlayerPositionPanel getM_clRightForward() {
		return m_clRightForward;
	}

	public void setM_clRightForward(PlayerPositionPanel m_clRightForward) {
		this.m_clRightForward = m_clRightForward;
	}

	public PlayerPositionPanel getM_clKeeper() {
		return m_clKeeper;
	}

	public void setM_clKeeper(PlayerPositionPanel m_clKeeper) {
		this.m_clKeeper = m_clKeeper;
	}

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
			HOVerwaltung.instance().getModel().getAufstellung().flipSide();
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

		// Alle SpielerPositionen Informieren erste 11
		List<Spieler> aufgestellteSpieler = new ArrayList<Spieler>();
		List<Spieler> alleSpieler = HOVerwaltung.instance().getModel().getAllSpieler();
		List<Spieler> gefilterteSpieler = new ArrayList<Spieler>();
		Lineup aufstellung = HOVerwaltung.instance().getModel().getAufstellung();

		for (Spieler player: alleSpieler) {
			// ein erste 11
			if (aufstellung.isSpielerInAnfangsElf(player.getSpielerID())) {
				aufgestellteSpieler.add(player);
			}
		}

		// Den Gruppenfilter anwenden
		for (Spieler player: alleSpieler) {
			// Kein Filter
			if (!gruppenfilter || (gruppe.equals(player.getTeamInfoSmilie()) && !gruppenegieren)
					|| (!gruppe.equals(player.getTeamInfoSmilie()) && gruppenegieren)) {
				boolean include = true;
				final AufstellungCBItem lastLineup = AufstellungsVergleichHistoryPanel
						.getLastLineup();

				if (exludeLast
						&& (lastLineup != null)
						&& lastLineup.getAufstellung()
								.isSpielerInAnfangsElf(player.getSpielerID())) {
					include = false;
					HOLogger.instance().log(getClass(), "Exclude: " + player.getName());
				}

				if (include) {
					gefilterteSpieler.add(player);
				}
			}
		}

		m_clKeeper.refresh(gefilterteSpieler);
		m_clLeftBack.refresh(gefilterteSpieler);
		m_clLeftCentralDefender.refresh(gefilterteSpieler);
		m_clMiddleCentralDefender.refresh(gefilterteSpieler);
		m_clRightCentralDefender.refresh(gefilterteSpieler);
		m_clRightBack.refresh(gefilterteSpieler);
		m_clLeftWinger.refresh(gefilterteSpieler);
		m_clLeftInnerMidfielder.refresh(gefilterteSpieler);
		m_clCentralInnerMidfielder.refresh(gefilterteSpieler);
		m_clRightInnerMidfielder.refresh(gefilterteSpieler);
		m_clRightWinger.refresh(gefilterteSpieler);
		m_clLeftForward.refresh(gefilterteSpieler);
		m_clCentralForward.refresh(gefilterteSpieler);
		m_clRightForward.refresh(gefilterteSpieler);
		m_clSubstKeeper.refresh(gefilterteSpieler);
		m_clSubstDefender.refresh(gefilterteSpieler);
		m_clSubstMidfield.refresh(gefilterteSpieler);
		m_clSubstWinger.refresh(gefilterteSpieler);
		m_clSubstForward.refresh(gefilterteSpieler);
		m_clSetPieceTaker.refresh(aufgestellteSpieler);
		m_clCaptain.refresh(aufgestellteSpieler);

		// Check
		aufstellung.checkAufgestellteSpieler();
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
			saveDetail(bw, m_clSetPieceTaker);
			saveDetail(bw, m_clSubstKeeper);
			saveDetail(bw, m_clCaptain);
			saveDetail(bw, m_clSubstDefender);
			saveDetail(bw, m_clSubstMidfield);
			saveDetail(bw, m_clSubstWinger);
			saveDetail(bw, m_clSubstForward);
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
	
	private void savePenaltyTakers(BufferedWriter bw) {
		
	}

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
		constraints.insets = new Insets(2, 2, 2, 2);

		centerPanel.setLayout(layout);

		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		m_clKeeper = new PlayerPositionPanel(this, ISpielerPosition.keeper);
		layout.setConstraints(m_clKeeper, constraints);
		centerPanel.add(m_clKeeper);
		swapPositionsManager.addSwapCapabilityTo(m_clKeeper);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clRightBack = new PlayerPositionPanel(this, ISpielerPosition.rightBack);
		layout.setConstraints(m_clRightBack, constraints);
		centerPanel.add(m_clRightBack);
		swapPositionsManager.addSwapCapabilityTo(m_clRightBack);
		assistantPanel.addToAssistant(m_clRightBack);

		// Defense line

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clRightCentralDefender = new PlayerPositionPanel(this,
				ISpielerPosition.rightCentralDefender);
		layout.setConstraints(m_clRightCentralDefender, constraints);
		centerPanel.add(m_clRightCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clRightCentralDefender);
		assistantPanel.addToAssistant(m_clRightCentralDefender);

		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clMiddleCentralDefender = new PlayerPositionPanel(this,
				ISpielerPosition.middleCentralDefender);
		layout.setConstraints(m_clMiddleCentralDefender, constraints);
		centerPanel.add(m_clMiddleCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clMiddleCentralDefender);
		assistantPanel.addToAssistant(m_clMiddleCentralDefender);

		constraints.gridx = 3;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clLeftCentralDefender = new PlayerPositionPanel(this,
				ISpielerPosition.leftCentralDefender);
		layout.setConstraints(m_clLeftCentralDefender, constraints);
		centerPanel.add(m_clLeftCentralDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftCentralDefender);
		assistantPanel.addToAssistant(m_clLeftCentralDefender);

		constraints.gridx = 4;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		m_clLeftBack = new PlayerPositionPanel(this, ISpielerPosition.leftBack);
		layout.setConstraints(m_clLeftBack, constraints);
		centerPanel.add(m_clLeftBack);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftBack);
		assistantPanel.addToAssistant(m_clLeftBack);

		// Midfield Line

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clRightWinger = new PlayerPositionPanel(this, ISpielerPosition.rightWinger);
		layout.setConstraints(m_clRightWinger, constraints);
		centerPanel.add(m_clRightWinger);
		swapPositionsManager.addSwapCapabilityTo(m_clRightWinger);
		assistantPanel.addToAssistant(m_clRightWinger);

		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clRightInnerMidfielder = new PlayerPositionPanel(this,
				ISpielerPosition.rightInnerMidfield);
		layout.setConstraints(m_clRightInnerMidfielder, constraints);
		centerPanel.add(m_clRightInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clRightInnerMidfielder);
		assistantPanel.addToAssistant(m_clRightInnerMidfielder);

		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clCentralInnerMidfielder = new PlayerPositionPanel(this,
				ISpielerPosition.centralInnerMidfield);
		layout.setConstraints(m_clCentralInnerMidfielder, constraints);
		centerPanel.add(m_clCentralInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clCentralInnerMidfielder);
		assistantPanel.addToAssistant(m_clCentralInnerMidfielder);

		constraints.gridx = 3;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clLeftInnerMidfielder = new PlayerPositionPanel(this, ISpielerPosition.leftInnerMidfield);
		layout.setConstraints(m_clLeftInnerMidfielder, constraints);
		centerPanel.add(m_clLeftInnerMidfielder);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftInnerMidfielder);
		assistantPanel.addToAssistant(m_clLeftInnerMidfielder);

		constraints.gridx = 4;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		m_clLeftWinger = new PlayerPositionPanel(this, ISpielerPosition.leftWinger);
		layout.setConstraints(m_clLeftWinger, constraints);
		centerPanel.add(m_clLeftWinger);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftWinger);
		assistantPanel.addToAssistant(m_clLeftWinger);

		// Forward line

		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		m_clRightForward = new PlayerPositionPanel(this, ISpielerPosition.rightForward);
		layout.setConstraints(m_clRightForward, constraints);
		centerPanel.add(m_clRightForward);
		swapPositionsManager.addSwapCapabilityTo(m_clRightForward);
		assistantPanel.addToAssistant(m_clRightForward);

		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		m_clCentralForward = new PlayerPositionPanel(this, ISpielerPosition.centralForward);
		layout.setConstraints(m_clCentralForward, constraints);
		centerPanel.add(m_clCentralForward);
		swapPositionsManager.addSwapCapabilityTo(m_clCentralForward);
		assistantPanel.addToAssistant(m_clCentralForward);

		constraints.gridx = 3;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		m_clLeftForward = new PlayerPositionPanel(this, ISpielerPosition.leftForward);
		layout.setConstraints(m_clLeftForward, constraints);
		centerPanel.add(m_clLeftForward);
		swapPositionsManager.addSwapCapabilityTo(m_clLeftForward);
		assistantPanel.addToAssistant(m_clLeftForward);

		// A spacer between forwards and reserves.

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 5;
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createRigidArea(new Dimension(10, 6)));
		layout.setConstraints(box, constraints);
		centerPanel.add(box);

		// The reserves

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clSubstKeeper = new PlayerPositionPanel(this, ISpielerPosition.substKeeper);
		layout.setConstraints(m_clSubstKeeper, constraints);
		centerPanel.add(m_clSubstKeeper);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstKeeper);

		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clSubstDefender = new PlayerPositionPanel(this, ISpielerPosition.substDefender);
		layout.setConstraints(m_clSubstDefender, constraints);
		centerPanel.add(m_clSubstDefender);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstDefender);

		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clSubstMidfield = new PlayerPositionPanel(this, ISpielerPosition.substInnerMidfield);
		layout.setConstraints(m_clSubstMidfield, constraints);
		centerPanel.add(m_clSubstMidfield);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstMidfield);

		constraints.gridx = 3;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clSubstForward = new PlayerPositionPanel(this, ISpielerPosition.substForward);
		layout.setConstraints(m_clSubstForward, constraints);
		centerPanel.add(m_clSubstForward);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstForward);

		constraints.gridx = 4;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clSubstWinger = new PlayerPositionPanel(this, ISpielerPosition.substWinger);
		layout.setConstraints(m_clSubstWinger, constraints);
		centerPanel.add(m_clSubstWinger);
		swapPositionsManager.addSwapCapabilityTo(m_clSubstWinger);

		// Captain and setpieces

		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		m_clCaptain = new PlayerPositionPanel(this, ISpielerPosition.captain);
		layout.setConstraints(m_clCaptain, constraints);
		centerPanel.add(m_clCaptain);

		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		m_clSetPieceTaker = new PlayerPositionPanel(this, ISpielerPosition.setPieces);
		layout.setConstraints(m_clSetPieceTaker, constraints);
		centerPanel.add(m_clSetPieceTaker);

		// Gruppenzuordnung des aufgestellten

		constraints.gridx = 3;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		centerPanel.add(new AufstellungsGruppenPanel(), constraints);

		// MiniLineup
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

		constraints.gridx = 4;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(panel, constraints);
		centerPanel.add(panel);

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
