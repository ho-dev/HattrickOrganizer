package module.lineup.penalties;

import core.constants.UIConstants;
import core.constants.player.PlayerAbility;
import core.gui.comp.table.RowNumberTable;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchLineupPosition;
import core.model.player.Player;
import core.model.player.MatchRoleID;
import core.util.GUIUtils;
import module.lineup.Lineup;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

public class PenaltyTakersView extends JPanel {

	@Serial
	private static final long serialVersionUID = -5089904466636200088L;
	private JTable playersTable;
	private JTable takersTable;
	private Lineup lineup;
	private JButton autoButton;
	private JButton clearButton;
	private JButton moveUpButton;
	private JButton moveDownButton;
	private JButton addToTakersButton;
	private JButton removeFromTakersButton;
	private JCheckBox showAnfangsElfCheckBox;
	private JCheckBox showReserveCheckBox;
	private JCheckBox showOthersCheckBox;
	private List<PenaltyTaker> players;

	public PenaltyTakersView() {
		initComponents();
		addListeners();
	}

	public List<PenaltyTaker> getPenaltyTakers() {
		PenaltyTakersTableModel model = getTakersTableModel();
		List<PenaltyTaker> list = new ArrayList<>(model.getRowCount());
		for (int i = 0; i < model.getRowCount(); i++) {
			list.add(model.getPenaltyTaker(i));
		}
		return list;
	}

	public void setPlayers(List<Player> players) {
		this.players = new ArrayList<>();
		for (Player player : players) {
			if (!player.isLineupDisabled()) {
				this.players.add(new PenaltyTaker(player));
			}
		}
		getPlayersTableModel().setPenaltyTakers(this.players);
	}

	public void setLineup(Lineup lineup) {
		this.lineup = lineup;
		reset();
		if ( lineup == null) return;

		// get positions already set as penalty takers in the lineup
		List<MatchLineupPosition> positions = this.lineup.getPenaltyTakers();
		List<PenaltyTaker> takers = new ArrayList<>();
		for (MatchRoleID pos : positions) {
			if (pos.getPlayerId() != 0) {
				PenaltyTaker taker = getPenaltyTaker(pos.getPlayerId());
				if (taker != null) {
					takers.add(taker);
				}
			}
		}
		getPlayersTableModel().removeAll(takers);
		getTakersTableModel().addAll(takers);
	}

	private PenaltyTaker getPenaltyTaker(int playerId) {
		for (PenaltyTaker taker : this.players) {
			if (taker.getPlayer().getPlayerID() == playerId) {
				return taker;
			}
		}
		return null;
	}

	private void reset() {
		getPlayersTableModel().setPenaltyTakers(this.players);
		getTakersTableModel().setPenaltyTakers(Collections.emptyList());
	}

	private void initComponents() {
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(createTablesPanel(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.insets = new Insets(20, 4, 4, 4);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(createButtonsPanel(), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(createFilterPanel(), gbc);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		setRowFilter();
	}

	/**
	 * The panel containing the two tables and the button to move players
	 * between the tables.
	 *
	 * @return the panel
	 */
	private JPanel createTablesPanel() {
		JPanel tablesPanel = new JPanel(new GridBagLayout());

		JLabel playersTableLabel = new JLabel(getLangStr("lineup.penaltytakers.playerstable.title"));
		playersTableLabel.setFont(playersTableLabel.getFont().deriveFont(Font.BOLD));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(8, 8, 4, 8);
		tablesPanel.add(playersTableLabel, gbc);

		this.playersTable = new JTable();
		this.playersTable.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
		this.playersTable.setModel(new PenaltyTakersTableModel());
		this.playersTable.setAutoCreateRowSorter(true);
		// as default, sort by if in lineup, than by ability
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(5, SortOrder.DESCENDING));
		this.playersTable.getRowSorter().setSortKeys(sortKeys);

		TableColumn inLineupColumn = this.playersTable.getColumnModel().getColumn(0);
		InLineupRenderer inLineupRenderer = new InLineupRenderer();
		inLineupColumn.setCellRenderer(inLineupRenderer);
		inLineupColumn.setMaxWidth(20);
		PlayerNameRenderer playerNameRenderer = new PlayerNameRenderer();
		this.playersTable.getColumnModel().getColumn(1).setCellRenderer(playerNameRenderer);
		SkillRenderer skillRenderer = new SkillRenderer();
		this.playersTable.getColumnModel().getColumn(2).setCellRenderer(skillRenderer);
		this.playersTable.getColumnModel().getColumn(3).setCellRenderer(skillRenderer);
		this.playersTable.getColumnModel().getColumn(4).setCellRenderer(skillRenderer);
		this.playersTable.getColumnModel().getColumn(5).setCellRenderer(new DoubleRenderer());

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.4;
		gbc.weighty = 1.0;
		tablesPanel.add(new JScrollPane(this.playersTable), gbc);

		JPanel moveButtonsPanel = new JPanel(new GridBagLayout());
		this.addToTakersButton = new JButton(getLangStr("ls.button.add"));
		this.addToTakersButton.setIcon(ThemeManager.getIcon(HOIconName.MOVE_RIGHT));
		this.addToTakersButton.setEnabled(false);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.insets = new Insets(15, 8, 4, 8);
		moveButtonsPanel.add(this.addToTakersButton, gbc);

		this.removeFromTakersButton = new JButton(getLangStr("ls.button.remove"));
		this.removeFromTakersButton.setIcon(ThemeManager.getIcon(HOIconName.MOVE_LEFT));
		this.removeFromTakersButton.setEnabled(false);
		gbc.insets = new Insets(4, 8, 15, 8);
		gbc.gridy = 1;
		gbc.weighty = 1.0;
		moveButtonsPanel.add(this.removeFromTakersButton, gbc);

		GUIUtils.equalizeComponentSizes(this.addToTakersButton, this.removeFromTakersButton);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		tablesPanel.add(moveButtonsPanel, gbc);

		JLabel takersTableLabel = new JLabel(getLangStr("lineup.penaltytakers.takerstable.title"));
		takersTableLabel.setFont(takersTableLabel.getFont().deriveFont(Font.BOLD));
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(8, 8, 4, 8);
		tablesPanel.add(takersTableLabel, gbc);

		this.takersTable = new JTable();
		this.takersTable.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
		this.takersTable.setModel(new PenaltyTakersTableModel());

		inLineupColumn = this.takersTable.getColumnModel().getColumn(0);
		inLineupColumn.setCellRenderer(inLineupRenderer);
		inLineupColumn.setMaxWidth(20);
		this.takersTable.getColumnModel().getColumn(1).setCellRenderer(playerNameRenderer);
		this.takersTable.getColumnModel().getColumn(2).setCellRenderer(skillRenderer);
		this.takersTable.getColumnModel().getColumn(3).setCellRenderer(skillRenderer);
		this.takersTable.getColumnModel().getColumn(4).setCellRenderer(skillRenderer);
		this.takersTable.getColumnModel().getColumn(5).setCellRenderer(new DoubleRenderer());

		JScrollPane scrollPane = new JScrollPane(this.takersTable);
		RowNumberTable rowTable = new RowNumberTable(this.takersTable);
		scrollPane.setRowHeaderView(rowTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.4;
		gbc.weighty = 1.0;
		tablesPanel.add(scrollPane, gbc);

		return tablesPanel;
	}

	private JPanel createFilterPanel() {
		JPanel filterPanel = new JPanel(new GridBagLayout());
		filterPanel.setBorder(BorderFactory
				.createTitledBorder(getLangStr("lineup.penaltytakers.filter.title")));

		this.showAnfangsElfCheckBox = new JCheckBox(
				getLangStr("lineup.penaltytakers.filter.starting"));
		this.showAnfangsElfCheckBox.setSelected(true);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(10, 10, 2, 10);
		filterPanel.add(this.showAnfangsElfCheckBox, gbc);

		this.showReserveCheckBox = new JCheckBox(getLangStr("lineup.penaltytakers.filter.reserves"));
		this.showReserveCheckBox.setSelected(true);
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 10, 2, 10);
		filterPanel.add(this.showReserveCheckBox, gbc);

		this.showOthersCheckBox = new JCheckBox(getLangStr("lineup.penaltytakers.filter.other"));
		this.showOthersCheckBox.setSelected(true);
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 10, 10, 10);
		gbc.weightx = 1;
		gbc.weighty = 1;
		filterPanel.add(this.showOthersCheckBox, gbc);

		return filterPanel;
	}

	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new GridBagLayout());
		this.autoButton = new JButton(getLangStr("lineup.penaltytakers.button.auto"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(10, 8, 4, 10);
		buttonsPanel.add(this.autoButton, gbc);

		this.clearButton = new JButton(getLangStr("ls.button.reset"));
		this.clearButton.setEnabled(false);
		gbc.gridy = 1;
		gbc.insets = new Insets(4, 8, 4, 10);
		buttonsPanel.add(this.clearButton, gbc);

		this.moveUpButton = new JButton(getLangStr("lineup.penaltytakers.button.moveUp"));
		this.moveUpButton.setIcon(ThemeManager.getIcon(HOIconName.MOVE_UP));
		this.moveUpButton.setEnabled(false);
		gbc.gridy = 2;
		gbc.insets = new Insets(16, 8, 4, 10);
		buttonsPanel.add(this.moveUpButton, gbc);

		this.moveDownButton = new JButton(getLangStr("lineup.penaltytakers.button.moveDown"));
		this.moveDownButton.setIcon(ThemeManager.getIcon(HOIconName.MOVE_DOWN));
		this.moveDownButton.setEnabled(false);
		gbc.gridy = 3;
		gbc.insets = new Insets(4, 8, 4, 10);
		gbc.weighty = 1.0;
		buttonsPanel.add(this.moveDownButton, gbc);

		GUIUtils.equalizeComponentSizes(this.autoButton, this.clearButton, this.moveUpButton,
				this.moveDownButton);

		return buttonsPanel;
	}

	@SuppressWarnings("unchecked")
	private void setRowFilter() {
		TableRowSorter<PenaltyTakersTableModel> rowSorter = ((TableRowSorter<PenaltyTakersTableModel>) this.playersTable
				.getRowSorter());

		rowSorter.setRowFilter(new RowFilter<>() {

			@Override
			public boolean include(
					RowFilter.Entry<? extends PenaltyTakersTableModel, ? extends Integer> entry) {
				PenaltyTakersTableModel personModel = entry.getModel();
				PenaltyTaker taker = personModel.getPenaltyTaker(entry.getIdentifier());
				if (showAnfangsElfCheckBox.isSelected()
						&& getInLineupVal(taker.getPlayer()) == 1) {
					return true;
				}
				if (showReserveCheckBox.isSelected()
						&& getInLineupVal(taker.getPlayer()) == 2) {
					return true;
				}
				return showOthersCheckBox.isSelected() && getInLineupVal(taker.getPlayer()) == 3;
			}
		});
	}

	private PenaltyTakersTableModel getPlayersTableModel() {
		return (PenaltyTakersTableModel) this.playersTable.getModel();
	}

	private PenaltyTakersTableModel getTakersTableModel() {
		return (PenaltyTakersTableModel) this.takersTable.getModel();
	}

	private void addListeners() {
		this.playersTable.getSelectionModel().addListSelectionListener(evt -> {
			if (!evt.getValueIsAdjusting()) {
				int selectedRow = playersTable.getSelectedRow();
				if (selectedRow == -1) {
					addToTakersButton.setEnabled(false);
				} else {
					takersTable.clearSelection();
					addToTakersButton.setEnabled(takersTable.getRowCount() < 11);
				}
			}
		});

		this.takersTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int[] selectedRows = takersTable.getSelectedRows();
				if (selectedRows.length > 0) {
					playersTable.clearSelection();
				}
				if (selectedRows.length != 1) {
					moveUpButton.setEnabled(false);
					moveDownButton.setEnabled(false);
				} else {
					moveUpButton.setEnabled((selectedRows[0] > 0));
					moveDownButton.setEnabled(selectedRows[0] < takersTable.getRowCount() - 1);
				}
				removeFromTakersButton.setEnabled(selectedRows.length > 0);
			}
		});

		this.takersTable.getModel().addTableModelListener(arg0 -> clearButton.setEnabled(takersTable.getRowCount() > 0));

		this.autoButton.addActionListener(e -> bestFit());

		this.clearButton.addActionListener(e -> reset());

		this.moveUpButton.addActionListener(arg0 -> moveTaker(Move.UP));

		this.moveDownButton.addActionListener(arg0 -> moveTaker(Move.DOWN));

		this.addToTakersButton.addActionListener(e -> {
			List<PenaltyTaker> players = getSelected(playersTable);
			for (PenaltyTaker player : players) {
				getPlayersTableModel().remove(player);
				getTakersTableModel().add(player);
			}
			selectTakers(players);
		});

		this.removeFromTakersButton.addActionListener(e -> {
			List<PenaltyTaker> takers = getSelected(takersTable);
			List<PenaltyTaker> activetakers = new ArrayList<>();
			int iTaker;
			for (PenaltyTaker taker : takers) {
				getTakersTableModel().remove(taker);
				iTaker = getInLineupVal(taker.getPlayer());
				if ( (showAnfangsElfCheckBox.isSelected() && iTaker == 1) ||
					 (showReserveCheckBox.isSelected() && iTaker == 2) ||
						(showOthersCheckBox.isSelected() && iTaker == 3))
				{getPlayersTableModel().add(taker);
				 activetakers.add(taker);
				}
			}
			selectPlayers(activetakers);
		});

		ItemListener filterCheckBoxListener = e -> getPlayersTableModel().fireTableDataChanged();
		this.showAnfangsElfCheckBox.addItemListener(filterCheckBoxListener);
		this.showReserveCheckBox.addItemListener(filterCheckBoxListener);
		this.showOthersCheckBox.addItemListener(filterCheckBoxListener);
	}

	private List<PenaltyTaker> getSelected(JTable table) {
		int[] viewRowIndexes = table.getSelectedRows();
		List<PenaltyTaker> selectedPlayers = new ArrayList<>(viewRowIndexes.length);
		if (viewRowIndexes.length > 0) {
			PenaltyTakersTableModel model = (PenaltyTakersTableModel) table.getModel();
			for (int viewRowIndex : viewRowIndexes) {
				int modelRowIndex = table.convertRowIndexToModel(viewRowIndex);
				selectedPlayers.add(model.getPenaltyTaker(modelRowIndex));
			}
		}
		return selectedPlayers;
	}

	private void moveTaker(Move move) {
		int viewRowIndex = this.takersTable.getSelectedRow();
		if ((move == Move.UP && viewRowIndex > 0)
				|| (move == Move.DOWN && viewRowIndex < this.takersTable.getRowCount() - 1)) {

			PenaltyTaker taker = getSelected(this.takersTable).get(0);
			PenaltyTakersTableModel model = getTakersTableModel();
			List<PenaltyTaker> list = new ArrayList<>(model.getRowCount());
			for (int i = 0; i < this.takersTable.getRowCount(); i++) {
				list.add(model.getPenaltyTaker(this.takersTable.convertRowIndexToModel(i)));
			}
			list.remove(taker);
			if (move == Move.UP) {
				list.add(viewRowIndex - 1, taker);
			} else {
				list.add(viewRowIndex + 1, taker);
			}

			model.setPenaltyTakers(list);
			List<PenaltyTaker> takers = new ArrayList<>();
			takers.add(taker);
			selectTakers(takers);
		}
	}

	private void select(List<PenaltyTaker> takers, JTable table) {
		PenaltyTakersTableModel model = (PenaltyTakersTableModel) table.getModel();
		for (PenaltyTaker taker : takers) {
			int viewIndex = table.convertRowIndexToView(model.getModelIndex(taker));
			table.addRowSelectionInterval(viewIndex, viewIndex);
		}
	}

	private void selectTakers(List<PenaltyTaker> takers) {
		select(takers, this.takersTable);
	}

	private void selectPlayers(List<PenaltyTaker> players) {
		select(players, this.playersTable);
	}

	private Integer getInLineupVal(Player player) {
		if (lineup != null) {
			int playerId = player.getPlayerID();
			if (lineup.isPlayerInStartingEleven(playerId)) {
				return 1;
			} else if (lineup.isSpielerInReserve(playerId)) {
				return 2;
			}
		}
		return 3;
	}

	private void bestFit() {
		boolean checkInEleven = showAnfangsElfCheckBox.isSelected();
		boolean checkInReserve = showReserveCheckBox.isSelected();

		List<PenaltyTaker> list = new ArrayList<>();
		for (PenaltyTaker player : this.players) {
			int lineupValue = getInLineupVal(player.getPlayer());
			if (lineupValue != 3) {
				if (checkInEleven && lineupValue == 1) {
					list.add(player);
				}

				if (checkInReserve && lineupValue == 2) {
					list.add(player);
				}
			}
		}

		Comparator<PenaltyTaker> comparator = (o1, o2) -> {
			if (o1.getAbility() > o2.getAbility()) {
				return -1;
			} else if (o1.getAbility() < o2.getAbility()) {
				return 1;
			}
			return 0;
		};

		list.sort(comparator);
		List<PenaltyTaker> takers;
		if (list.size() > 11) {
			takers = new ArrayList<>(list.subList(0, 11));
		} else {
			takers = list;
		}

		getTakersTableModel().setPenaltyTakers(takers);
		getPlayersTableModel().removeAll(takers);
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}

	private class PenaltyTakersTableModel extends AbstractTableModel {

		@Serial
		private static final long serialVersionUID = 3044881352777003621L;
		private final String[] columnNames;
		private List<PenaltyTaker> data = new ArrayList<>();

		public PenaltyTakersTableModel() {
			this.columnNames = new String[6];
			this.columnNames[0] = "";
			this.columnNames[1] = getLangStr("ls.player.name");
			this.columnNames[2] = getLangStr("ls.player.experience");
			this.columnNames[3] = getLangStr("ls.player.skill.setpieces");
			this.columnNames[4] = getLangStr("ls.player.skill.scoring");
			this.columnNames[5] = getLangStr("lineup.penaltytakers.colheadline.ability");
		}

		public void add(PenaltyTaker taker) {
			this.data.add(taker);
			fireTableDataChanged();
		}

		public void remove(PenaltyTaker taker) {
			this.data.remove(taker);
			fireTableDataChanged();
		}

		public void removeAll(Collection<PenaltyTaker> takers) {
			for (PenaltyTaker taker : takers) {
				this.data.remove(taker);
			}
			fireTableDataChanged();
		}

		public void addAll(Collection<PenaltyTaker> takers) {
			this.data.addAll(takers);
			fireTableDataChanged();
		}

		public void setPenaltyTakers(List<PenaltyTaker> takers) {
			this.data = new ArrayList<>(takers);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return this.data.size();
		}

		@Override
		public int getColumnCount() {
			return this.columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PenaltyTaker taker = this.data.get(rowIndex);
			return switch (columnIndex) {
				case 0 -> getInLineupVal(taker.getPlayer());
				case 1 -> taker.getPlayer().getFullName();
				case 2 -> taker.getExperience();
				case 3 -> taker.getSetPieces();
				case 4 -> taker.getScoring();
				case 5 -> taker.getAbility();
				default -> "";
			};
		}

		@Override
		public String getColumnName(int column) {
			return this.columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return switch (columnIndex) {
				case 0 -> Integer.class;
				case 1 -> String.class;
				case 2, 3, 4, 5 -> Double.class;
				default -> Object.class;
			};
		}

		public PenaltyTaker getPenaltyTaker(int rowIndex) {
			return this.data.get(rowIndex);
		}

		public int getModelIndex(PenaltyTaker player) {
			return this.data.indexOf(player);
		}
	}

	private static class InLineupRenderer extends DefaultTableCellRenderer {

		@Serial
		private static final long serialVersionUID = 2815809080926324953L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

			JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
					hasFocus, row, column);
			int val = (Integer) value;
			switch (val) {
				case 1 -> component.setIcon(ThemeManager.getIcon(HOIconName.PLAYS_AT_BEGINNING));
				case 2 -> component.setIcon(ThemeManager.getIcon(HOIconName.IS_RESERVE));
				case 3 -> component.setIcon(ThemeManager.getIcon(HOIconName.NOT_IN_LINEUP));
				default -> component.setIcon(null);
			}
			return component;
		}

	}

	private static class DoubleRenderer extends DefaultTableCellRenderer {

		@Serial
		private static final long serialVersionUID = -9094435304652745951L;
		private final NumberFormat format;

		public DoubleRenderer() {
			this.format = NumberFormat.getNumberInstance();
			this.format.setMinimumFractionDigits(2);
			this.format.setMaximumFractionDigits(2);
			setHorizontalAlignment(SwingConstants.RIGHT);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			Double val = (Double) value;
			String str = this.format.format(val);
			return super.getTableCellRendererComponent(table, str, isSelected, hasFocus, row,
					column);
		}
	}

	private class SkillRenderer extends DoubleRenderer {

		@Serial
		private static final long serialVersionUID = 3943598594307257068L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			Double val = (Double) value;
			((JComponent) c).setToolTipText(PlayerAbility.getNameForSkill(val.floatValue()));
			return c;
		}
	}

	private static class PlayerNameRenderer extends DefaultTableCellRenderer {

		@Serial
		private static final long serialVersionUID = 1970459130002883259L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			((JComponent) c).setToolTipText(String.valueOf(value));
			return c;
		}
	}

	private enum Move {
		UP,
		DOWN
	}
}
