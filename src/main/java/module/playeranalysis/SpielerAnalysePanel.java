// %4061149036:de.hattrickorganizer.gui.playeranalysis%
package module.playeranalysis;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.SpielerCBItem;
import core.gui.model.PlayerCBItemRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.net.HattrickLink;
import core.util.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bietet Übersicht über alle Player
 */
public class SpielerAnalysePanel extends LazyImagePanel {
	private static final long serialVersionUID = 7705544952029589545L;
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
						HattrickLink.showMatch(matchId+"",info.getMatchTyp().isOfficial());
					}
				}catch (Exception ex){

				}

			}
		});
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
		playerComboBox.setRenderer(new PlayerCBItemRenderer());
		playerComboBox.setMaximumRowCount(25);
		playerComboBox.setMaximumSize(new Dimension(200, 25));
		playerComboBox.setSize(200, 25);
		playerComboBox.setLocation(10, 5);
		playerComboBox.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

		panel.add(playerComboBox);

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
