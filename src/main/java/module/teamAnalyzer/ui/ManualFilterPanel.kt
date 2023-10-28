package module.teamAnalyzer.ui;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.enums.MatchType;
import core.net.OnlineWorker;
import core.util.Helper;
import module.teamAnalyzer.ht.HattrickManager;
import module.teamAnalyzer.manager.MatchManager;
import module.teamAnalyzer.ui.model.UiFilterTableModel;
import module.teamAnalyzer.vo.Match;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;


public class ManualFilterPanel extends JPanel {

	private static final Vector<String> COLUMN_NAMES = new Vector<>(Arrays.asList("",
			Helper.getTranslation("RecapPanel.Game"),
			Helper.getTranslation("Type"),
			Helper.getTranslation("ls.match.result"),
			Helper.getTranslation("Week"),
			Helper.getTranslation("Season"), "", ""));

	List<Match> availableMatches = new ArrayList<>();
	private DefaultTableModel tableModel;
	private JTable table;

	/**
	 * Creates a new instance of ManualFilterPanel
	 */
	public ManualFilterPanel() {
		jbInit();
	}

	/**
	 * Re-init the UI components.
	 */
	public void reload() {
		tableModel = new UiFilterTableModel(new Vector<>(), COLUMN_NAMES);
		table.setModel(tableModel);
		availableMatches = MatchManager.getAllMatches();

		Vector<Object> rowData;

		for (Match availableMatch : availableMatches) {

			rowData = new Vector<>();

			boolean isAvailable = DBManager.instance().isMatchInDB(availableMatch.getMatchId(), MatchType.getById(availableMatch.getMatchType().getMatchTypeId()));
			boolean isSelected = TeamAnalyzerPanel.filter.getMatches().contains("" + availableMatch.getMatchId());

			rowData.add(isSelected);

			if (availableMatch.isHome()) {
				rowData.add(availableMatch.getAwayTeam());
				rowData.add(ThemeManager.getIcon(HOIconName.MATCHICONS[availableMatch.getMatchType().getIconArrayIndex()]));
				rowData.add(availableMatch.getHomeGoals() + " - " + availableMatch.getAwayGoals());
			} else {
				rowData.add("*" + availableMatch.getHomeTeam());
				rowData.add(ThemeManager.getIcon(HOIconName.MATCHICONS[availableMatch.getMatchType().getIconArrayIndex()]));
				rowData.add(availableMatch.getAwayGoals() + " - " + availableMatch.getHomeGoals());
			}

			rowData.add(availableMatch.getWeek() + "");
			rowData.add(availableMatch.getSeason() + "");

			if ((HattrickManager.isDownloadAllowed()) || isAvailable) {
				rowData.add("true");
			} else {
				rowData.add("false");
			}

			rowData.add("" + availableMatch.getMatchType().getMatchTypeId());
			tableModel.addRow(rowData);
		}

		addTableListener();

		table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(20);
		table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(90);
		table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(20);
		table.getTableHeader().getColumnModel().getColumn(3).setPreferredWidth(40);
		table.getTableHeader().getColumnModel().getColumn(4).setPreferredWidth(40);
		table.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(40);
		table.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(6).setMinWidth(0);
		table.getTableHeader().getColumnModel().getColumn(6).setPreferredWidth(0);
		table.getTableHeader().getColumnModel().getColumn(6).setWidth(0);
		table.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(7).setMinWidth(0);
		table.getTableHeader().getColumnModel().getColumn(7).setPreferredWidth(0);
		table.getTableHeader().getColumnModel().getColumn(7).setWidth(0);
	}

	/**
	 * Set a match filter.
	 */
	protected void setFilter() {
		List<String> list = new ArrayList<>();
		int i = 0;

		for (Match element : availableMatches) {
			boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
			boolean isAvailable = Boolean.parseBoolean((String) tableModel.getValueAt(i, 6));

			if (isSelected && isAvailable) {
				list.add("" + element.getMatchId());
			}

			i++;
		}

		TeamAnalyzerPanel.filter.setMatches(list);
	}

	private void addTableListener() {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					boolean downloadDone = false;
					int row = table.getSelectedRow();
					String status = (String) tableModel.getValueAt(row, 6);

					if (!status.equalsIgnoreCase("true")) {
						int id = availableMatches.get(row).getMatchId();
						var type = availableMatches.get(row).getMatchType();
						downloadDone = OnlineWorker.downloadMatchData(id, MatchType.getById(type.getMatchTypeId()), false);
					}

					e.consume();

					if (downloadDone) {
						tableModel.setValueAt("true", row, 6);
					}

					updateUI();
				}
			}
		});
	}

	private void jbInit() {
		JPanel main = new ImagePanel();

		main.setLayout(new BorderLayout());
		setLayout(new BorderLayout());
		setOpaque(false);

		Vector<Vector<Object>> data = new Vector<>();

		tableModel = new UiFilterTableModel(data, COLUMN_NAMES);
		table = new JTable(tableModel);
		table.setDefaultRenderer(Object.class, new ManualFilterTableRenderer());

		JScrollPane pane = new JScrollPane(table);

		main.add(pane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(main);

		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane);
	}
}
