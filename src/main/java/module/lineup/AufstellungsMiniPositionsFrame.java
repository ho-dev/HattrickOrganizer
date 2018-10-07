// %80307481:de.hattrickorganizer.gui.lineup%
package module.lineup;

import core.gui.HOMainFrame;
import core.gui.Refreshable;
import core.gui.Updateable;
import core.gui.comp.panel.RasenPanel;
import core.gui.model.SpielerCBItem;
import core.gui.print.ComponentPrintObject;
import core.gui.print.PrintController;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.model.player.SpielerPosition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

/**
 * Darstellung der Aufstellung in einem kleinen Frame
 */
public class AufstellungsMiniPositionsFrame extends JFrame implements WindowListener, Refreshable,
		Updateable, ActionListener {

	private static final long serialVersionUID = 7505316315597313881L;
	private LineupPanel m_clAufstellungsPanel;
	private JButton m_jbMaxFrame = new JButton(ThemeManager.getIcon(HOIconName.MAXLINEUP));
	private PlayerPositionPanel m_clMiddleCentralDefender;
	private PlayerPositionPanel m_clCentralInnerMidfielder;
	private PlayerPositionPanel m_clCentralForward;
	private PlayerPositionPanel m_clLeftBack;
	private PlayerPositionPanel m_clLeftWinger;
	private PlayerPositionPanel m_clLeftCentralDefender;
	private PlayerPositionPanel m_clLeftInnerMidfielder;
	private PlayerPositionPanel m_clLeftForward;
	private PlayerPositionPanel m_clRightBack;
	private PlayerPositionPanel m_clRightWinger;
	private PlayerPositionPanel m_clRightCentralDefender;
	private PlayerPositionPanel m_clRightInnerMidfielder;
	private PlayerPositionPanel m_clRightForward;
	private PlayerPositionPanel m_clReserveWinger;
	private PlayerPositionPanel m_clReserveMidfielder;
	private PlayerPositionPanel m_clReserveForward;
	private PlayerPositionPanel m_clReserveKeeper;
	private PlayerPositionPanel m_clReserveDefender;
	private PlayerPositionPanel m_clCaptain;
	private PlayerPositionPanel m_clSetPieces;
	private PlayerPositionPanel m_clKeeper;
	private boolean m_bMinimize;
	private boolean m_bPrint;

	public AufstellungsMiniPositionsFrame(LineupPanel aufstellungsPanel, boolean print,
			boolean minimized) {
		super("Mini" + core.model.HOVerwaltung.instance().getLanguageString("Aufstellung"));

		m_bPrint = print;
		m_bMinimize = minimized;

		m_clAufstellungsPanel = aufstellungsPanel;

		core.gui.RefreshManager.instance().registerRefreshable(this);

		initComponentes();

		this.setIconImage(HOMainFrame.instance().getIconImage());
		this.addWindowListener(this);
	}

	/**
	 * Position des MiniScouts speichern
	 * 
	 */
	@Override
	public final void setVisible(boolean sichtbar) {
		super.setVisible(sichtbar);

		if (!sichtbar && !m_bPrint) {
			core.model.UserParameter.instance().miniscout_PositionX = this.getLocation().x;
			core.model.UserParameter.instance().miniscout_PositionY = this.getLocation().y;
			core.gui.RefreshManager.instance().unregisterRefreshable(this);
		}
	}

	@Override
	public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		setVisible(false);
		core.gui.HOMainFrame.instance().setVisible(true);
		dispose();
	}

	/**
	 * Drucken der Aufstellung
	 */
	public final void doPrint() {
		try {
			final PrintController printController = PrintController.getInstance();

			final java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());

			final String titel = HOVerwaltung.instance().getLanguageString("Aufstellung") + " - "
					+ HOVerwaltung.instance().getModel().getBasics().getTeamName() + " - "
					+ java.text.DateFormat.getDateTimeInstance().format(calendar.getTime());
			printController.add(new ComponentPrintObject(printController.getPf(), titel,
					getContentPane(), ComponentPrintObject.SICHTBAR));

			printController.print();
		} catch (Exception e) {
		}

		setVisible(false);
	}

	@Override
	public final void reInit() {
		refresh();
	}

	@Override
	public final void refresh() {
		final boolean gruppenfilter = m_clAufstellungsPanel.getAufstellungsAssistentPanel()
				.isGroupFilter();
		final String gruppe = m_clAufstellungsPanel.getAufstellungsAssistentPanel().getGroup();
		final boolean gruppenegieren = m_clAufstellungsPanel.getAufstellungsAssistentPanel()
				.isNotGroup();

		// Alle SpielerPositionen Informieren
		// erste 11
		final Vector<Spieler> aufgestellteSpieler = new Vector<Spieler>();

		final Vector<Spieler> alleSpieler = HOVerwaltung.instance().getModel().getAllSpieler();
		final Vector<Spieler> gefilterteSpieler = new Vector<Spieler>();
		final Lineup aufstellung = HOVerwaltung.instance().getModel().getAufstellung();

		for (int i = 0; i < alleSpieler.size(); i++) {
			final Spieler spieler = (Spieler) alleSpieler.get(i);

			// ein erste 11
			if (aufstellung.isSpielerInAnfangsElf(spieler.getSpielerID())) {
				aufgestellteSpieler.add(spieler);
			}
		}

		// Den Gruppenfilter anwenden
		for (int i = 0; i < alleSpieler.size(); i++) {
			final Spieler spieler = (Spieler) alleSpieler.get(i);

			// Kein Filter
			if (!gruppenfilter || (gruppe.equals(spieler.getTeamInfoSmilie()) && !gruppenegieren)
					|| (!gruppe.equals(spieler.getTeamInfoSmilie()) && gruppenegieren)) {
				gefilterteSpieler.add(spieler);
			}
		}

		// SpielerPositionsPanels aktualisieren
		SpielerPosition position;

		position = aufstellung.getPositionById(m_clKeeper.getPositionsID());
		m_clKeeper.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clLeftBack.getPositionsID());
		m_clLeftBack.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clLeftCentralDefender.getPositionsID());
		m_clLeftCentralDefender.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clMiddleCentralDefender.getPositionsID());
		m_clMiddleCentralDefender.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clRightCentralDefender.getPositionsID());
		m_clRightCentralDefender.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clRightBack.getPositionsID());
		m_clRightBack.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clLeftWinger.getPositionsID());
		m_clLeftWinger.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clLeftInnerMidfielder.getPositionsID());
		m_clLeftInnerMidfielder.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clCentralInnerMidfielder.getPositionsID());
		m_clCentralInnerMidfielder.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clRightInnerMidfielder.getPositionsID());
		m_clRightInnerMidfielder.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clRightWinger.getPositionsID());
		m_clRightWinger.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clLeftForward.getPositionsID());
		m_clLeftForward.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clCentralForward.getPositionsID());
		m_clCentralForward.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clRightForward.getPositionsID());
		m_clRightForward.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clReserveKeeper.getPositionsID());
		m_clReserveKeeper.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clReserveDefender.getPositionsID());
		m_clReserveDefender.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clReserveMidfielder.getPositionsID());
		m_clReserveMidfielder.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clReserveWinger.getPositionsID());
		m_clReserveWinger.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clReserveForward.getPositionsID());
		m_clReserveForward.refresh(gefilterteSpieler);

		position = aufstellung.getPositionById(m_clSetPieces.getPositionsID());
		m_clSetPieces.refresh(aufgestellteSpieler);

		position = aufstellung.getPositionById(m_clCaptain.getPositionsID());
		m_clCaptain.refresh(aufgestellteSpieler);

		// Check
		aufstellung.checkAufgestellteSpieler();
	}

	@Override
	public final void update() {
		m_clAufstellungsPanel.update();
		refresh();
	}

	@Override
	public void windowActivated(java.awt.event.WindowEvent windowEvent) {
	}

	@Override
	public void windowClosed(java.awt.event.WindowEvent windowEvent) {
	}

	@Override
	public final void windowClosing(java.awt.event.WindowEvent windowEvent) {
		setVisible(false);
		HOMainFrame.instance().setVisible(true);
		dispose();
	}

	@Override
	public void windowDeactivated(java.awt.event.WindowEvent windowEvent) {
	}

	@Override
	public void windowDeiconified(java.awt.event.WindowEvent windowEvent) {
	}

	@Override
	public void windowIconified(java.awt.event.WindowEvent windowEvent) {
	}

	@Override
	public void windowOpened(java.awt.event.WindowEvent windowEvent) {
	}

	private SpielerCBItem createSpielerLabel(int spielerID) {
		final Spieler spieler = HOVerwaltung.instance().getModel().getSpieler(spielerID);

		if (spieler != null) {
			return new SpielerCBItem(spieler.getName(), 0f, spieler);
		} else {
			return new SpielerCBItem("", 0f, null);
		}
	}

	/**
	 * Erstellt die Komponenten
	 */
	private void initComponentes() {
		setContentPane(new RasenPanel(new BorderLayout(), m_bPrint));

		final javax.swing.JPanel centerPanel = new javax.swing.JPanel();
		centerPanel.setOpaque(false);

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 2, 2, 2);

		centerPanel.setLayout(layout);

		m_clKeeper = new MiniPosPlayer(this, centerPanel, ISpielerPosition.keeper, m_bPrint,
				m_bMinimize, 2, 0);

		m_clRightBack = new MiniPosPlayer(this, centerPanel, ISpielerPosition.rightBack, m_bPrint,
				m_bMinimize, 0, 1);

		m_clRightCentralDefender = new MiniPosPlayer(this, centerPanel,
				ISpielerPosition.rightCentralDefender, m_bPrint, m_bMinimize, 1, 1);

		m_clMiddleCentralDefender = new MiniPosPlayer(this, centerPanel,
				ISpielerPosition.middleCentralDefender, m_bPrint, m_bMinimize, 2, 1);

		m_clLeftCentralDefender = new MiniPosPlayer(this, centerPanel,
				ISpielerPosition.leftCentralDefender, m_bPrint, m_bMinimize, 3, 1);

		m_clLeftBack = new MiniPosPlayer(this, centerPanel, ISpielerPosition.leftBack, m_bPrint,
				m_bMinimize, 4, 1);

		m_clRightWinger = new MiniPosPlayer(this, centerPanel, ISpielerPosition.rightWinger,
				m_bPrint, m_bMinimize, 0, 2);

		m_clRightInnerMidfielder = new MiniPosPlayer(this, centerPanel,
				ISpielerPosition.rightInnerMidfield, m_bPrint, m_bMinimize, 1, 2);

		m_clCentralInnerMidfielder = new MiniPosPlayer(this, centerPanel,
				ISpielerPosition.centralInnerMidfield, m_bPrint, m_bMinimize, 2, 2);

		m_clLeftInnerMidfielder = new MiniPosPlayer(this, centerPanel,
				ISpielerPosition.leftInnerMidfield, m_bPrint, m_bMinimize, 3, 2);

		m_clLeftWinger = new MiniPosPlayer(this, centerPanel, ISpielerPosition.leftWinger,
				m_bPrint, m_bMinimize, 4, 2);

		m_clRightForward = new MiniPosPlayer(this, centerPanel, ISpielerPosition.rightForward,
				m_bPrint, m_bMinimize, 1, 3);

		m_clCentralForward = new MiniPosPlayer(this, centerPanel, ISpielerPosition.centralForward,
				m_bPrint, m_bMinimize, 2, 3);

		m_clLeftForward = new MiniPosPlayer(this, centerPanel, ISpielerPosition.leftForward,
				m_bPrint, m_bMinimize, 3, 3);

		// A spacer between forwards and reserves.

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 5;
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createRigidArea(new Dimension(10, 6)));
		layout.setConstraints(box, constraints);
		centerPanel.add(box);

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clReserveKeeper = new PlayerPositionPanel(this, ISpielerPosition.substKeeper, m_bPrint,
				m_bMinimize);
		layout.setConstraints(m_clReserveKeeper, constraints);
		centerPanel.add(m_clReserveKeeper);

		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clReserveDefender = new PlayerPositionPanel(this, ISpielerPosition.substDefender,
				m_bPrint, m_bMinimize);
		layout.setConstraints(m_clReserveDefender, constraints);
		centerPanel.add(m_clReserveDefender);

		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clReserveMidfielder = new PlayerPositionPanel(this, ISpielerPosition.substInnerMidfield,
				m_bPrint, m_bMinimize);
		layout.setConstraints(m_clReserveMidfielder, constraints);
		centerPanel.add(m_clReserveMidfielder);

		constraints.gridx = 3;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clReserveForward = new PlayerPositionPanel(this, ISpielerPosition.substForward, m_bPrint,
				m_bMinimize);
		layout.setConstraints(m_clReserveForward, constraints);
		centerPanel.add(m_clReserveForward);

		constraints.gridx = 4;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		m_clReserveWinger = new PlayerPositionPanel(this, ISpielerPosition.substWinger, m_bPrint,
				m_bMinimize);
		layout.setConstraints(m_clReserveWinger, constraints);
		centerPanel.add(m_clReserveWinger);

		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		m_clCaptain = new PlayerPositionPanel(this, ISpielerPosition.captain, m_bPrint, m_bMinimize);
		layout.setConstraints(m_clCaptain, constraints);
		centerPanel.add(m_clCaptain);

		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		m_clSetPieces = new PlayerPositionPanel(this, ISpielerPosition.setPieces, m_bPrint,
				m_bMinimize);
		layout.setConstraints(m_clSetPieces, constraints);
		centerPanel.add(m_clSetPieces);

		getContentPane().add(centerPanel, BorderLayout.CENTER);

		if (!m_bPrint) {
			// MiniLineup
			final JPanel panel = new JPanel(new BorderLayout());
			panel.setOpaque(false);
			m_jbMaxFrame.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString(
					"tt_AufstellungsMiniPosFrame_zurueck"));
			m_jbMaxFrame.setPreferredSize(new Dimension(25, 25));
			m_jbMaxFrame.addActionListener(this);
			panel.add(m_jbMaxFrame, BorderLayout.EAST);
			getContentPane().add(panel, BorderLayout.SOUTH);
		} else {
			// Aufstellungsratingspanel
			final AufstellungsDetailPanel detailpanel = new AufstellungsDetailPanel();
			detailpanel.refresh();
			detailpanel.setPreferredSize(new Dimension(HOMainFrame.instance()
					.getAufstellungsPanel().getAufstellungsDetailPanel().getWidth(), 100));
			getContentPane().add(detailpanel, BorderLayout.WEST);
		}

		// Elfmeterschützen
		final JPanel sidePanel = new JPanel(new BorderLayout());
		sidePanel.setOpaque(false);

		final JLabel label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString(
				"lineup.penaltytakers.takerstable.title"));
		label.setFont(label.getFont().deriveFont(Font.BOLD));

		if (!m_bPrint) {
			label.setForeground(Color.WHITE);
		} else {
			label.setForeground(Color.BLACK);
		}

		sidePanel.add(label, BorderLayout.NORTH);

		final JList liste = new JList();
		liste.setOpaque(false);
		liste.setCellRenderer(new core.gui.model.SpielerCBItemRenderer());
		liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final DefaultListModel listmodel = new DefaultListModel();
		List<SpielerPosition> shooters = HOVerwaltung.instance().getModel().getAufstellung()
				.getPenaltyTakers();
		for (SpielerPosition pos : shooters) {
			listmodel.addElement(createSpielerLabel(pos.getSpielerId()));
		}

		liste.setModel(listmodel);

		sidePanel.add(liste, BorderLayout.CENTER);

		getContentPane().add(sidePanel, BorderLayout.EAST);

		refresh();

		pack();
		setSize(getSize().width + 20, getSize().height + 30);

		if (!m_bPrint) {
			setLocation(core.model.UserParameter.instance().miniscout_PositionX,
					core.model.UserParameter.instance().miniscout_PositionY);
			core.gui.HOMainFrame.instance().setVisible(false);
		} else {
			try {
				final Toolkit kit = Toolkit.getDefaultToolkit();
				setLocation(kit.getScreenSize().width, kit.getScreenSize().height);
			} catch (Exception e) {
				// NIX
			}
		}

		setVisible(true);
	}

	/**
	 * A private, very specific extension to the PlayerPositionPanel that takes
	 * care of adding and removing the player panel depending on the presence of
	 * a player in that position.
	 * 
	 */
	private class MiniPosPlayer extends PlayerPositionPanel {

		private GridBagConstraints m_gbcConstraints;
		private JPanel m_cpParent;
		private boolean m_bCurrentlyAdded = true;
		private Box m_clDummy = new Box(BoxLayout.X_AXIS);

		protected MiniPosPlayer(Updateable updater, JPanel parent, int positionsID, int x, int y) {

			this(updater, parent, positionsID, false, false, x, y);

		}

		protected MiniPosPlayer(Updateable updater, JPanel parent, int positionsID, boolean print,
				boolean minimize, int x, int y) {

			super(updater, positionsID, print, minimize);

			m_cpParent = parent;
			m_gbcConstraints = new GridBagConstraints();
			m_gbcConstraints.gridx = x;
			m_gbcConstraints.gridy = y;
			m_gbcConstraints.gridwidth = 1;
			m_gbcConstraints.gridheight = 1;
			m_gbcConstraints.anchor = GridBagConstraints.CENTER;
			m_gbcConstraints.fill = GridBagConstraints.NONE;
			m_gbcConstraints.weightx = 0.0;
			m_gbcConstraints.weighty = 0.0;
			m_gbcConstraints.insets = new Insets(2, 2, 2, 2);
			m_cpParent.add(this, m_gbcConstraints);

			m_clDummy.setPreferredSize(new Dimension(MINI_PLAYER_POSITION_PANEL_WIDTH,
					MINI_PLAYER_POSITION_PANEL_HEIGHT));
		}

		/**
		 *
		 */
		private static final long serialVersionUID = 8546453871916507058L;

		@Override
		public void refresh(List<Spieler> spieler) {
			super.refresh(spieler);
			/*
			 * If this Panel is present and contains no player, remove it. If
			 * this Panel is not present and contains a player, add it. If the
			 * panel is removed, add the dummy panel
			 */

			if (HOVerwaltung.instance().getModel().getAufstellung().hasFreePosition() == true) {
				m_clDummy.setBorder(new LineBorder(Color.white));
			} else {
				m_clDummy.setBorder(null);
			}

			if ((((SpielerCBItem) this.getPlayerComboBox().getSelectedItem()).getSpieler() == null)
					&& (m_bCurrentlyAdded == true)) {
				removePanel();
			} else if ((((SpielerCBItem) this.getPlayerComboBox().getSelectedItem()).getSpieler() != null)
					&& (m_bCurrentlyAdded == false)) {
				addPanel();
			}
		}

		private void addPanel() {
			m_cpParent.remove(m_clDummy);
			m_cpParent.add(this, m_gbcConstraints);
			m_bCurrentlyAdded = true;

			m_cpParent.revalidate();
		}

		private void removePanel() {
			m_cpParent.remove(this);
			m_cpParent.add(m_clDummy, m_gbcConstraints);
			m_bCurrentlyAdded = false;

			m_cpParent.revalidate();
		}
	}
}
