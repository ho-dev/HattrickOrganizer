// %4061149036:de.hattrickorganizer.gui.playeranalysis%
package module.playeranalysis;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.SpielerCBItem;
import core.gui.model.SpielerCBItemRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.Spieler;
import core.util.HOLogger;
import core.util.Helper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

/**
 * Bietet Übersicht über alle Spieler
 */
public class SpielerAnalysePanel extends LazyImagePanel {
	private static final long serialVersionUID = 7705544952029589545L;
	private JButton printButton;
	private JComboBox playerComboBox;
	private JSplitPane horizontalSplitPane;
	private SpielerMatchesTable m_jtSpielerMatchesTable;
	private SpielerPositionTable m_jtSpielerPositionTable;
	private int columnModelInstance;

	/**
	 * Creates a new SpielerAnalysePanel object.
	 */
	public SpielerAnalysePanel(int instance) {
		columnModelInstance = instance;
	}

	public final void setAktuelleSpieler(int spielerid) {
		Helper.markierenComboBox(playerComboBox, spielerid);
	}

	public void saveColumnOrder() {
		m_jtSpielerMatchesTable.saveColumnOrder();
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		registerRefreshable(true);
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		fillSpielerCB();
		showSelectedPlayer();
	}

	private void addListeners() {
		this.printButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drucken();
			}
		});

		this.playerComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					showSelectedPlayer();
				}
			}
		});
	}

	/**
	 * Drucken der SpielerAnalyse
	 */
	private void drucken() {
		try {
			final JPanel panel = new JPanel(new BorderLayout());
			panel.setBackground(Color.WHITE);

			// Damit nur bestimmte Spalten gedruckt werden ist eine spezielle
			// Tabelle notwendig.
			// Das Scrollpane benötigt man, damit die Spaltenbeschriftung auch
			// angezeigt wird.
			final SpielerMatchesTable table = new SpielerMatchesTable(
					((SpielerCBItem) playerComboBox.getSelectedItem()).getSpieler().getSpielerID(),
					columnModelInstance);
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setPreferredSize(new Dimension(table.getPreferredSize().width + 10, table
					.getPreferredSize().height + 70));
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane.getViewport().setBackground(Color.WHITE);

			panel.add(scrollPane, BorderLayout.NORTH);

			final SpielerPositionTable table2 = new SpielerPositionTable(
					((SpielerCBItem) playerComboBox.getSelectedItem()).getSpieler().getSpielerID());
			scrollPane = new JScrollPane(table2);
			scrollPane.setPreferredSize(new Dimension(table2.getPreferredSize().width + 10, table2
					.getPreferredSize().height + 70));
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane.getViewport().setBackground(Color.WHITE);

			panel.add(scrollPane, BorderLayout.SOUTH);

			final core.gui.print.PrintController printController = core.gui.print.PrintController
					.getInstance();

			final java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());

			final String titel = HOVerwaltung.instance().getLanguageString("SpielerAnalyse")
					+ " - " + HOVerwaltung.instance().getModel().getBasics().getTeamName() + " - "
					+ java.text.DateFormat.getDateTimeInstance().format(calendar.getTime());
			printController.add(new core.gui.print.ComponentPrintObject(printController.getPf(),
					titel, panel, core.gui.print.ComponentPrintObject.NICHTSICHTBAR));

			printController.print();
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private void fillSpielerCB() {
		List<Spieler> players = HOVerwaltung.instance().getModel().getAllSpieler();
		List<SpielerCBItem> spielerCBItems = new ArrayList<SpielerCBItem>(players.size());

		for (Spieler player : players) {
			spielerCBItems.add(new SpielerCBItem(player.getName(), 0f, player));
		}
		Collections.sort(spielerCBItems);

		// Alte Spieler
		List<Spieler> oldPlayers = HOVerwaltung.instance().getModel().getAllOldSpieler();
		List<SpielerCBItem> spielerOldCBItems = new ArrayList<SpielerCBItem>(oldPlayers.size());

		for (Spieler player : oldPlayers) {
			spielerOldCBItems.add(new SpielerCBItem(player.getName(), 0f, player));
		}
		Collections.sort(spielerOldCBItems);

		// Zusammenfügen
		List<SpielerCBItem> cbItems = new ArrayList<SpielerCBItem>(spielerCBItems.size()
				+ spielerOldCBItems.size() + 1);

		cbItems.addAll(spielerCBItems);
		// Fur die Leerzeile;
		cbItems.add(null);
		cbItems.addAll(spielerOldCBItems);
		DefaultComboBoxModel cbModel = new DefaultComboBoxModel(cbItems.toArray());
		playerComboBox.setModel(cbModel);

		// Kein Spieler selektiert
		playerComboBox.setSelectedItem(null);
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		add(initSpielerCB(), BorderLayout.NORTH);

		int spielerid = -1;
		if (playerComboBox.getSelectedItem() != null) {
			spielerid = ((SpielerCBItem) playerComboBox.getSelectedItem()).getSpieler()
					.getSpielerID();
		}

		horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false,
				initSpielerMatchesTabelle(spielerid), initSpielerPositionTabelle(spielerid));

		horizontalSplitPane
				.setDividerLocation((core.model.UserParameter.instance().hoMainFrame_height * 1) / 3);
		add(horizontalSplitPane, BorderLayout.CENTER);
	}

	private Component initSpielerCB() {
		final ImagePanel panel = new ImagePanel(null);

		playerComboBox = new JComboBox();
		playerComboBox.setRenderer(new SpielerCBItemRenderer());
		playerComboBox.setMaximumRowCount(25);
		playerComboBox.setMaximumSize(new Dimension(200, 25));
		playerComboBox.setSize(200, 25);
		playerComboBox.setLocation(10, 5);
		playerComboBox.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

		panel.add(playerComboBox);

		printButton = new JButton(ThemeManager.getIcon(HOIconName.PRINTER));
		printButton.setSize(25, 25);
		printButton.setLocation(220, 5);

		panel.add(printButton);

		panel.setPreferredSize(new Dimension(220, 35));

		fillSpielerCB();

		return panel;
	}

	private Component initSpielerMatchesTabelle(int spielerid) {
		m_jtSpielerMatchesTable = new SpielerMatchesTable(spielerid, columnModelInstance);
		JScrollPane scrollpane = new JScrollPane(m_jtSpielerMatchesTable);
		scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		return scrollpane;
	}

	private Component initSpielerPositionTabelle(int spielerid) {
		m_jtSpielerPositionTable = new SpielerPositionTable(spielerid);
		JScrollPane scrollpane = new JScrollPane(m_jtSpielerPositionTable);
		scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		return scrollpane;
	}

	/**
	 * Aktualisiert die beiden Tabellen mit den Werten des ausgewählten Spielers
	 */
	private void showSelectedPlayer() {
		if (playerComboBox.getSelectedIndex() > -1) {
			// Tabelle updaten
			m_jtSpielerMatchesTable.refresh(((SpielerCBItem) playerComboBox.getSelectedItem())
					.getSpieler().getSpielerID());
			m_jtSpielerPositionTable.refresh(((SpielerCBItem) playerComboBox.getSelectedItem())
					.getSpieler().getSpielerID());
		} else {
			// Tabelle leeren
			m_jtSpielerMatchesTable.refresh(-1);
			m_jtSpielerPositionTable.refresh(-1);
		}
	}
}
