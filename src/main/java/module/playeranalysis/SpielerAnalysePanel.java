// %4061149036:de.hattrickorganizer.gui.playeranalysis%
package module.playeranalysis;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.PlayerCBItem;
import core.gui.model.PlayerCBItemRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.net.HattrickLink;
import core.util.Helper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Bietet Übersicht über alle Player
 */
public class SpielerAnalysePanel extends LazyImagePanel {
	private JComboBox<PlayerCBItem> playerComboBox;
	private SpielerMatchesTable m_jtSpielerMatchesTable;
	private SpielerPositionTable m_jtSpielerPositionTable;
	private final int columnModelInstance;

	/**
	 * Creates a new SpielerAnalysePanel object.
	 */
	public SpielerAnalysePanel(int instance) {
		columnModelInstance = instance;
	}

	public final void setAktuelleSpieler(int spielerid) {
		Helper.setComboBoxFromID(playerComboBox, spielerid);
	}

	public void storeUserSettings() {
		m_jtSpielerMatchesTable.storeUserSettings();
		m_jtSpielerPositionTable.storeUserSettings();
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
		this.playerComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                showSelectedPlayer();
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
						// TODO: get match type
						MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(matchId, null);
						HattrickLink.showMatch(matchId+"",info.getMatchType().isOfficial());
					}
				}catch (Exception ignored){

				}

			}
		});
	}

	private void fillSpielerCB() {
		// Current Players
		final List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();

		// Former Players
		final List<Player> oldPlayers = HOVerwaltung.instance().getModel().getFormerPlayers();
		final var duplicateLastNames = Player.getDuplicateLastNames(Stream.concat(players.stream(), oldPlayers.stream()).toList());

		final List<PlayerCBItem> playerCBItems = players.stream()
				.map(player -> createPlayerCBItem(player, duplicateLastNames))
				.sorted()
				.toList();

		final List<PlayerCBItem> spielerOldCBItems = oldPlayers.stream()
				.map(player -> createPlayerCBItem(player, duplicateLastNames))
				.sorted()
				.toList();

		// Create a common list
		List<PlayerCBItem> cbItems = new ArrayList<>(playerCBItems.size() + spielerOldCBItems.size() + 1);
		cbItems.addAll(playerCBItems);
		cbItems.add(null); // for a blank line
		cbItems.addAll(spielerOldCBItems);

		DefaultComboBoxModel<PlayerCBItem> cbModel = new DefaultComboBoxModel<>(cbItems.toArray(new PlayerCBItem[0]));
		playerComboBox.setModel(cbModel);

		// no player selected
		playerComboBox.setSelectedItem(null);
	}

	private static PlayerCBItem createPlayerCBItem(Player player, Set<String> duplicateLastNames) {
		final boolean lastNameUnique = !duplicateLastNames.contains(player.getLastName());
		return new PlayerCBItem(player.getFullNameSmart(lastNameUnique), 0f, player);
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		add(initSpielerCB(), BorderLayout.NORTH);

		int spielerid = -1;
		if (playerComboBox.getSelectedItem() != null) {
			spielerid = ((PlayerCBItem) playerComboBox.getSelectedItem()).getPlayer().getPlayerId();
		}

		JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false,
				initSpielerMatchesTabelle(spielerid), initSpielerPositionTabelle(spielerid));

		horizontalSplitPane.setDividerLocation((core.model.UserParameter.instance().hoMainFrame_height) / 3);
		add(horizontalSplitPane, BorderLayout.CENTER);
	}

	private Component initSpielerCB() {
		final ImagePanel panel = new ImagePanel(null);
		var fontSize = UserParameter.instance().fontSize;
		playerComboBox = new JComboBox<>();
		playerComboBox.setRenderer(new PlayerCBItemRenderer());
		playerComboBox.setMaximumRowCount(25);
		playerComboBox.setMaximumSize(new Dimension(20*fontSize, 2* fontSize));
		playerComboBox.setSize(20 * fontSize, 2*fontSize);
		playerComboBox.setLocation(10, 5);
		playerComboBox.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

		panel.add(playerComboBox);

		panel.setPreferredSize(new Dimension(20 * fontSize, 3*fontSize));

		fillSpielerCB();

		return panel;
	}

	private Component initSpielerMatchesTabelle(int spielerid) {
		m_jtSpielerMatchesTable = new SpielerMatchesTable(spielerid, columnModelInstance);
		return new JScrollPane(m_jtSpielerMatchesTable);
	}

	private Component initSpielerPositionTabelle(int spielerid) {
		m_jtSpielerPositionTable = new SpielerPositionTable(spielerid);
		return new JScrollPane(m_jtSpielerPositionTable);
	}

	/**
	 * Update both tables with current player's values
	 */
	private void showSelectedPlayer() {
		if (playerComboBox.getSelectedIndex() > -1) {
			// Tabelle updaten
			m_jtSpielerMatchesTable.refresh(((PlayerCBItem) playerComboBox.getSelectedItem())
					.getPlayer().getPlayerId());
			m_jtSpielerPositionTable.refresh(((PlayerCBItem) playerComboBox.getSelectedItem())
					.getPlayer().getPlayerId());
		} else {
			// Tabelle leeren
			m_jtSpielerMatchesTable.refresh(-1);
			m_jtSpielerPositionTable.refresh(-1);
		}
	}
}
