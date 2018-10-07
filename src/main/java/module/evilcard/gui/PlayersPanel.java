package module.evilcard.gui;

import core.gui.CursorToolkit;
import module.evilcard.Model;
import module.evilcard.ModelChangeAdapter;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

class PlayersPanel extends JPanel {

	private static final long serialVersionUID = 5173473921072367115L;
	private PlayersTable playersTable;
	private PlayersTableModel playersTableModel;
	private final Model model;

	PlayersPanel(Model model) {
		super();
		this.model = model;
		initComponents();
		addListeners();
	}

	private void addListeners() {
		this.playersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					model.setSelectedPlayer(getSelectedPlayer());
				}
			}
		});

		this.model.addModelChangeListener(new ModelChangeAdapter() {
			@Override
			public void playerFilterChanged() {
				load();
			}

			@Override
			public void completeDataChanged() {
				load();
			}
		});
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());
		this.playersTableModel = new PlayersTableModel();
		this.playersTable = new PlayersTable();
		this.playersTable.setModel(this.playersTableModel);

		this.playersTable.setAutoCreateRowSorter(true);
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(PlayersTableModel.COL_NAME, SortOrder.ASCENDING));
		this.playersTable.getRowSorter().setSortKeys(sortKeys);

		TableColumnModel columnModel = this.playersTable.getColumnModel();
		columnModel.getColumn(PlayersTableModel.COL_ID).setMinWidth(0);
		columnModel.getColumn(PlayersTableModel.COL_ID).setMaxWidth(0);
		columnModel.getColumn(PlayersTableModel.COL_ID).setWidth(0);
		columnModel.getColumn(PlayersTableModel.COL_NAME).setPreferredWidth(120);
		columnModel.getColumn(PlayersTableModel.COL_AGGRESSIVITY).setPreferredWidth(120);
		columnModel.getColumn(PlayersTableModel.COL_HONESTY).setPreferredWidth(120);
		columnModel.getColumn(PlayersTableModel.COL_DIRECT_RED_CARDS).setPreferredWidth(20);
		columnModel.getColumn(PlayersTableModel.COL_CARDS).setPreferredWidth(20);
		columnModel.getColumn(PlayersTableModel.COL_WARNINGS).setPreferredWidth(20);
		columnModel.getColumn(PlayersTableModel.COL_WARNINGS_TYPE1).setPreferredWidth(20);
		columnModel.getColumn(PlayersTableModel.COL_WARNINGS_TYPE2).setPreferredWidth(20);
		columnModel.getColumn(PlayersTableModel.COL_WARNINGS_TYPE3).setPreferredWidth(20);
		columnModel.getColumn(PlayersTableModel.COL_WARNINGS_TYPE4).setPreferredWidth(20);
		columnModel.getColumn(PlayersTableModel.COL_RAW_AVERAGE).setPreferredWidth(50);
		columnModel.getColumn(PlayersTableModel.COL_WEIGHTED_AVERAGE).setPreferredWidth(50);
		columnModel.getColumn(PlayersTableModel.COL_MATCHES).setPreferredWidth(35);

		this.playersTable.setDefaultRenderer(String.class, new PlayersTableCellRenderer());
		this.playersTable.setDefaultRenderer(Integer.class, new PlayersTableCellRenderer());
		this.playersTable.setDefaultRenderer(Double.class, new PlayersTableCellRenderer());

		add(new JScrollPane(this.playersTable));

		this.playersTableModel.refresh(this.model.getPlayerFilter());
	}

	private void load() {
		CursorToolkit.startWaitCursor(PlayersPanel.this);
		try {
			playersTableModel.refresh(model.getPlayerFilter());
		} finally {
			CursorToolkit.stopWaitCursor(PlayersPanel.this);
		}

	}

	private int getSelectedPlayer() {
		int row = this.playersTable.getSelectedRow();
		if (row >= 0) {
			Integer playerId = (Integer) playersTable.getValueAt(row, PlayersTableModel.COL_ID);
			return playerId.intValue();
		}
		return 0;
	}
}
