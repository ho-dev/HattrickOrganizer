// %4061149036:de.hattrickorganizer.gui.playeranalysis%
package module.playeranalysis;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.SpielerCBItem;
import core.gui.model.SpielerCBItemRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.util.HOLogger;
import core.util.Helper;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
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
 * Bietet Übersicht über alle Player
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
		m_jtSpielerMatchesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try{
					int columnMATCHID=54;
					core.gui.comp.entry.ColorLabelEntry colorLabelEntry = (ColorLabelEntry) m_jtSpielerMatchesTable.getValueAt(m_jtSpielerMatchesTable.getSelectedRow(),columnMATCHID);
					int matchId = Integer.parseInt(colorLabelEntry.getText());
					if(e.getClickCount()==2  && m_jtSpielerMatchesTable.getSelectedRow()>=0){
						HOMainFrame.instance().showMatch(Integer.parseInt(colorLabelEntry.getText()));
					}else if(e.getClickCount()==1 && e.isShiftDown()){
						MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(matchId);
						getHTURL(matchId+"",info.getMatchTyp().isOfficial());
					}
				}catch (Exception ex){

				}

			}
		});
	}

	public void getHTURL(String matchId, boolean isOfficial){
		URI url;
		if (isOfficial) {
			url = URI.create(String.format("http://www.hattrick.org/Club/Matches/Match.aspx?matchID=%s", matchId));
		}else
			url= URI.create(String.format("https://www.hattrick.org/Club/Matches/Match.aspx?matchID=%s&SourceSystem=HTOIntegrated", matchId));

		if(Desktop.isDesktopSupported()){
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(url);
			} catch (IOException   e) {}
		}else{
			String os = System.getProperty("os.name").toLowerCase();
			Runtime runtime = Runtime.getRuntime();
			try {
				if(os.indexOf("win") >= 0)
					runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
				else if(os.indexOf("mac") >= 0)
					runtime.exec("open " + url);
				else
					runtime.exec("firefox " + url);
			} catch (IOException e) {}
		}
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
		List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		List<SpielerCBItem> spielerCBItems = new ArrayList<SpielerCBItem>(players.size());

		for (Player player : players) {
			spielerCBItems.add(new SpielerCBItem(player.getFullName(), 0f, player));
		}
		Collections.sort(spielerCBItems);

		// Alte Player
		List<Player> oldPlayers = HOVerwaltung.instance().getModel().getFormerPlayers();
		List<SpielerCBItem> spielerOldCBItems = new ArrayList<SpielerCBItem>(oldPlayers.size());

		for (Player player : oldPlayers) {
			spielerOldCBItems.add(new SpielerCBItem(player.getFullName(), 0f, player));
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

		// Kein Player selektiert
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
