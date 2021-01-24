package module.lineup.substitution;

import core.constants.UIConstants;
import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.util.GUIUtils;
import module.lineup.Lineup;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.Substitution;
import module.lineup.substitution.plausibility.Error;
import module.lineup.substitution.plausibility.PlausibilityCheck;
import module.lineup.substitution.plausibility.Problem;
import module.lineup.substitution.plausibility.Uncertainty;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Main Panel for Match Orders (i.e. substitutions) in the Lineup tab.
 */
public class SubstitutionOverview extends JPanel {

	private static final long serialVersionUID = -625638866350314110L;
	private JTable substitutionTable;
	private MessageBox messageBox;
	private DetailsView detailsView;
	private EditAction editAction;
	private RemoveAction removeAction;
	private RemoveAllAction removeAllAction;
	private BehaviorAction behaviorAction;
	private PositionSwapAction positionSwapAction;
	private SubstitutionAction substitutionAction;
	private ManMarkingAction manMarkingAction;
	private Lineup lineup;
	private List<Substitution> substitutionBackup;

	public SubstitutionOverview(Lineup lineup) {
		this.lineup = lineup;
		createActions();
		initComponents();
		addListeners();
		refresh();
		GUIUtils.selectFirstRow(this.substitutionTable);
	}

	public void setLineup(Lineup lineup) {
		this.lineup = lineup;
		refresh();
		GUIUtils.selectFirstRow(this.substitutionTable);
	}

	private void createActions() {
		this.editAction = new EditAction();
		this.editAction.setEnabled(false);
		this.removeAction = new RemoveAction();
		this.removeAction.setEnabled(false);
		this.removeAllAction = new RemoveAllAction();
		this.removeAllAction.setEnabled(false);
		this.behaviorAction = new BehaviorAction();
		this.positionSwapAction = new PositionSwapAction();
		this.substitutionAction = new SubstitutionAction();
		this.manMarkingAction = new ManMarkingAction();
	}

	private void refresh() {
		SubstitutionsTableModel model = (SubstitutionsTableModel) this.substitutionTable.getModel();
		model.setData(this.lineup.getSubstitutionList());

		// Max order is 5 + the level of the tactical assistant.
		int maxOrders = 5 + HOVerwaltung.instance().getModel().getClub().getTacticalAssistantLevels();
		int nSubstitutions = 0;	// no limit
		int nManMarkings = 0;	// limit 1
		int nOther = 0;			// limit maxOrders - nSubstitutions

		for (var row : model.rows) {
			var subs = row.getSubstitution();
			switch (subs.getOrderType()) {
				case SUBSTITUTION -> nSubstitutions++;
				case MAN_MARKING -> nManMarkings++;
				default -> nOther++;
			}
			row.setProblem(PlausibilityCheck.checkForProblem(this.lineup, row.getSubstitution()));
			if ( nSubstitutions+nOther>maxOrders){
				row.setProblem(Error.TOO_MANY_ORDERS);
			}
			else if ( nManMarkings > 1){
				row.setProblem(Error.TOO_MANY_ORDERS);
			}
		}
		detailsView.refresh();
		((SubstitutionsTableModel) this.substitutionTable.getModel()).sort();

		boolean enableNewMatchOrders = maxOrders-nOther-nSubstitutions>0;
		this.behaviorAction.setEnabled(enableNewMatchOrders);
		this.positionSwapAction.setEnabled(enableNewMatchOrders);
		this.substitutionAction.setEnabled(enableNewMatchOrders);
		this.manMarkingAction.setEnabled(nManMarkings<1);
	}

	private void addListeners() {
		this.substitutionTable.getSelectionModel().addListSelectionListener(
				e -> {
					if (!e.getValueIsAdjusting()) {
						tableSelectionChanged();
					}
				});

		this.substitutionTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					editSelectedSubstitution();
				}
			}
		});

		this.substitutionTable.getModel().addTableModelListener(e -> removeAllAction.setEnabled(substitutionTable.getRowCount() > 0));

		this.substitutionTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "edit");
		this.substitutionTable.getActionMap().put("edit", this.editAction);
	}

	private void tableSelectionChanged() {
		int selectedRowIndex = this.substitutionTable.getSelectedRow();
		boolean enable = false;
		TableRow tableRow = null;
		if (selectedRowIndex != -1) {
			tableRow = ((SubstitutionsTableModel) this.substitutionTable.getModel())
					.getRow(selectedRowIndex);
			enable = true;
		}
		this.editAction.setEnabled(enable);
		this.removeAction.setEnabled(enable);
		if (tableRow != null) {
			this.detailsView.setSubstitution(tableRow.getSubstitution());
		} else {
			this.detailsView.setSubstitution(null);
		}

		Icon icon = null;
		if (tableRow != null && tableRow.getProblem() != null) {
			this.messageBox.setMessage(PlausibilityCheck.getComment(tableRow.getProblem(),
					tableRow.getSubstitution()));
			if (tableRow.isUncertain()) {
				icon = ThemeManager.getIcon(HOIconName.EXCLAMATION);
			} else if (tableRow.isError()) {
				icon = ThemeManager.getIcon(HOIconName.EXCLAMATION_RED);
			}
		} else {
			this.messageBox.setMessage("");
		}
		this.messageBox.setIcon(icon);
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		this.substitutionTable = new JTable();
		this.substitutionTable.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
		this.substitutionTable.setModel(new SubstitutionsTableModel());
		this.substitutionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumn warningCol = this.substitutionTable.getColumnModel().getColumn(
				SubstitutionsTableModel.WARNING_COL_IDX);
		warningCol.setCellRenderer(new WarningRenderer());
		warningCol.setPreferredWidth(25);
		warningCol.setMaxWidth(25);

		TableColumn orderTypeIconCol = this.substitutionTable.getColumnModel().getColumn(
				SubstitutionsTableModel.ORDERTYPE_ICON_COL_IDX);
		orderTypeIconCol.setCellRenderer(new OrderTypeRenderer());
		orderTypeIconCol.setPreferredWidth(25);
		orderTypeIconCol.setMaxWidth(25);

		JPanel lowerPanel = new JPanel(new GridBagLayout());
		this.detailsView = new DetailsView();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weighty = 1.0;
		lowerPanel.add(this.detailsView, gbc);

		this.messageBox = new MessageBox();
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 10);
		lowerPanel.add(this.messageBox, gbc);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.add(new JScrollPane(this.substitutionTable), 0);
		splitPane.add(lowerPanel, 1);
		splitPane.setDividerLocation(200);

		add(splitPane, BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.EAST);
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		JButton editButton = new JButton();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(10, 10, 2, 10);
		editButton.setAction(this.editAction);
		buttonPanel.add(editButton, gbc);

		JButton removeButton = new JButton();
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 10);
		removeButton.setAction(this.removeAction);
		buttonPanel.add(removeButton, gbc);

		JButton removeAllButton = new JButton();
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 10, 10);
		removeAllButton.setAction(this.removeAllAction);
		buttonPanel.add(removeAllButton, gbc);

		JButton substitutionButton = new JButton();
		gbc.gridy++;
		gbc.insets = new Insets(10, 10, 2, 10);
		buttonPanel.add(substitutionButton, gbc);
		substitutionButton.setAction(this.substitutionAction);

		JButton behaviorButton = new JButton();
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 10);
		buttonPanel.add(behaviorButton, gbc);
		behaviorButton.setAction(this.behaviorAction);

		JButton positionSwapButton = new JButton();
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 10);
		buttonPanel.add(positionSwapButton, gbc);
		positionSwapButton.setAction(this.positionSwapAction);

		JButton manMarkingButton = new JButton();
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 10);
		gbc.weighty = 1.0;
		buttonPanel.add(manMarkingButton, gbc);
		manMarkingButton.setAction(this.manMarkingAction);

		GUIUtils.equalizeComponentSizes(editButton, removeButton, removeAllButton, substitutionButton,
				behaviorButton, positionSwapButton, manMarkingButton);

		return buttonPanel;
	}

	private void doNewOrder(MatchOrderType orderType) {
		SubstitutionEditDialog dlg = getSubstitutionEditDialog(orderType);
		dlg.setLocationRelativeTo(SubstitutionOverview.this);
		backupLineupSubstitutions();
		Substitution newSub = new Substitution(orderType);
		lineup.getSubstitutionList().add(newSub);
		dlg.init(lineup, newSub);
		dlg.setVisible(true);

		if (!dlg.isCanceled()) {
			newSub.setPlayerOrderId(getNextOrderID());
			updateOrderIDs();
			refresh();
			selectSubstitution(newSub);
			HOMainFrame.instance().getLineupPanel().getLineupSettingsPanel().setLabels();
		}
		else {
			restoreLineupSubstitutions();
		}
	}

	private SubstitutionEditDialog getSubstitutionEditDialog(MatchOrderType orderType) {
		SubstitutionEditDialog dlg;
		Window windowAncestor = SwingUtilities.getWindowAncestor(SubstitutionOverview.this);
		if (windowAncestor instanceof Frame) {
			dlg = new SubstitutionEditDialog((Frame) windowAncestor, orderType);
		} else {
			dlg = new SubstitutionEditDialog((Dialog) windowAncestor, orderType);
		}
		return dlg;
	}

	private void selectSubstitution(Substitution sub) {
		SubstitutionsTableModel tblModel = (SubstitutionsTableModel) this.substitutionTable.getModel();
		for (int i = 0; i < tblModel.getRowCount(); i++) {
			if (tblModel.getRow(i).getSubstitution() == sub) {
				this.substitutionTable.getSelectionModel().setSelectionInterval(i, i);
			}
		}
	}

	private void editSelectedSubstitution() {
		int selectedRowIndex = substitutionTable.getSelectedRow();
		if (selectedRowIndex != -1) {
			backupLineupSubstitutions();
			final SubstitutionsTableModel tableModel = (SubstitutionsTableModel) substitutionTable.getModel();
			final Substitution sub = tableModel.getRow(selectedRowIndex).getSubstitution();

			SubstitutionEditDialog dlg = getSubstitutionEditDialog(sub.getOrderType());
			dlg.setLocationRelativeTo(SubstitutionOverview.this);
			dlg.init(lineup, sub);
			dlg.setVisible(true);

			if (!dlg.isCanceled()) {
				tableModel.fireTableRowsUpdated(selectedRowIndex, selectedRowIndex);
				refresh();
				selectSubstitution(sub);
			}
			else {
				restoreLineupSubstitutions();
			}
		}
	}

	private void backupLineupSubstitutions() {
		this.substitutionBackup = new ArrayList<>();
		for (Substitution s : this.lineup.getSubstitutionList()) {
			Substitution backup = new Substitution(s.getPlayerOrderId(),
					s.getObjectPlayerID(),
					s.getSubjectPlayerID(),
					s.getOrderType().getId(),
					s.getMatchMinuteCriteria(),
					s.getRoleId(),
					s.getBehaviour(),
					s.getRedCardCriteria(),
					s.getStanding()
					);
			this.substitutionBackup.add(backup);
		}
	}

	private void restoreLineupSubstitutions() {
		this.lineup.setSubstitionList(this.substitutionBackup);
	}

	private void updateOrderIDs() {
		List<Substitution> list = new ArrayList<>(this.lineup.getSubstitutionList());
		list.sort(Comparator.comparingInt(Substitution::getPlayerOrderId));
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setPlayerOrderId(i);
		}
	}

	private int getNextOrderID() {
		List<Substitution> list = this.lineup.getSubstitutionList();
		int max = -1;
		for (Substitution sub : list) {
			if (sub.getPlayerOrderId() > max) {
				max = sub.getPlayerOrderId();
			}
		}
		return ++max;
	}

	/**
	 * TableModel for the overview table where existing substitutions are
	 * listed.
	 *
	 */
	private class SubstitutionsTableModel extends AbstractTableModel {

		public static final int WARNING_COL_IDX = 0;
		public static final int ORDERTYPE_COL_IDX = 1;
		public static final int SUBJECTPLAYER_COL_IDX = 2;
		public static final int ORDERTYPE_ICON_COL_IDX = 3;
		public static final int OBJECTPLAYER_COL_IDX = 4;
		public static final int WHEN_COL_IDX = 5;
		public static final int STANDING_COL_IDX = 6;
		public static final int CARDS_COL_IDX = 7;
		// number of columns
		public static final int COLUMN_COUNT = 8;

		private static final long serialVersionUID = 6969656858380680460L;
		private List<TableRow> rows = new ArrayList<>();
		private String[] columnNames;
		private Comparator<TableRow> rowComparator;

		public SubstitutionsTableModel() {
			this.columnNames = new String[COLUMN_COUNT];
			this.columnNames[WARNING_COL_IDX] = "";
			this.columnNames[ORDERTYPE_COL_IDX] = HOVerwaltung.instance().getLanguageString(
					"subs.orders.colheadline.order");
			this.columnNames[SUBJECTPLAYER_COL_IDX] = HOVerwaltung.instance().getLanguageString(
					"subs.orders.colheadline.player1");
			this.columnNames[ORDERTYPE_ICON_COL_IDX] = "";
			this.columnNames[OBJECTPLAYER_COL_IDX] = HOVerwaltung.instance().getLanguageString(
					"subs.orders.colheadline.player2");
			this.columnNames[WHEN_COL_IDX] = HOVerwaltung.instance().getLanguageString(
					"subs.orders.colheadline.when");
			this.columnNames[STANDING_COL_IDX] = HOVerwaltung.instance().getLanguageString(
					"subs.orders.colheadline.standing");
			this.columnNames[CARDS_COL_IDX] = HOVerwaltung.instance().getLanguageString(
					"subs.orders.colheadline.cards");
		}

		public void sort() {
			if (this.rowComparator == null) {
				this.rowComparator = (o1, o2) -> {
					Substitution s1 = o1.getSubstitution();
					Substitution s2 = o2.getSubstitution();

					int ret = s1.getMatchMinuteCriteria() - s2.getMatchMinuteCriteria();
					if (ret == 0) {
						ret = s1.getPlayerOrderId() - s2.getPlayerOrderId();
					}
					return ret;
				};
			}
			this.rows.sort(this.rowComparator);
			fireTableDataChanged();
		}

		public void setData(List<Substitution> data) {
			this.rows.clear();
			for (Substitution sub : data) {
				TableRow row = new TableRow();
				row.setSub(sub);
				this.rows.add(row);
			}
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return this.rows.size();
		}

		@Override
		public int getColumnCount() {
			return this.columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Substitution sub = this.rows.get(rowIndex).getSubstitution();

			switch (columnIndex) {
			case ORDERTYPE_COL_IDX:
				return LanguageStringLookup.getOrderType(sub.getOrderType());
			case SUBJECTPLAYER_COL_IDX:
				return sub.getSubjectPlayerName();
			case ORDERTYPE_ICON_COL_IDX:
				return sub.getBehaviour();
			case OBJECTPLAYER_COL_IDX:
				return sub.getObjectPlayerName();
			case WHEN_COL_IDX:
				if (sub.getMatchMinuteCriteria() > 0) {
					return HOVerwaltung.instance().getLanguageString("subs.MinuteAfterX", (int) sub.getMatchMinuteCriteria());
				}
				return HOVerwaltung.instance().getLanguageString("subs.MinuteAnytime");
			case STANDING_COL_IDX:
				return LanguageStringLookup.getStanding(sub.getStanding());
			case CARDS_COL_IDX:
				return LanguageStringLookup.getRedCard(sub.getRedCardCriteria());
			}

			return "";
		}

		@Override
		public String getColumnName(int column) {
			return this.columnNames[column];
		}

		public TableRow getRow(int rowIndex) {
			return this.rows.get(rowIndex);
		}
	}

	/*
	 * This class is a simple container for row data.
	 */
	private class TableRow {

		private Substitution sub;
		private Problem problem;

		public Substitution getSubstitution() {
			return sub;
		}

		public void setSub(Substitution sub) {
			this.sub = sub;
		}

		public boolean isUncertain() {
			return this.problem != null && this.problem instanceof Uncertainty;
		}

		public boolean isError() {
			return this.problem != null && this.problem instanceof Error;
		}

		public Problem getProblem() {
			return problem;
		}

		public void setProblem(Problem problem) {
			this.problem = problem;
		}
	}

	private class BehaviorAction extends AbstractAction {

		private static final long serialVersionUID = 3753611559396928213L;

		public BehaviorAction() {
			super(HOVerwaltung.instance().getLanguageString("subs.Behavior"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doNewOrder(MatchOrderType.NEW_BEHAVIOUR);
		}
	}

	private class PositionSwapAction extends AbstractAction {

		private static final long serialVersionUID = 3753611559396928213L;

		public PositionSwapAction() {
			super(HOVerwaltung.instance().getLanguageString("subs.TypeSwap"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doNewOrder(MatchOrderType.POSITION_SWAP);
		}
	}

	private class SubstitutionAction extends AbstractAction {

		private static final long serialVersionUID = 2005264416271904159L;

		public SubstitutionAction() {
			super(HOVerwaltung.instance().getLanguageString("subs.TypeSub"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doNewOrder(MatchOrderType.SUBSTITUTION);
		}
	}

	private class ManMarkingAction extends AbstractAction {

		private static final long serialVersionUID = 2005264416271904159L;

		public ManMarkingAction() {
			super(HOVerwaltung.instance().getLanguageString("subs.TypeManMarking"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doNewOrder(MatchOrderType.MAN_MARKING);
		}
	}

	private class RemoveAction extends AbstractAction {

		private static final long serialVersionUID = 715531467612457L;

		public RemoveAction() {
			super(HOVerwaltung.instance().getLanguageString("ls.button.delete"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SubstitutionsTableModel model = (SubstitutionsTableModel) substitutionTable.getModel();
			TableRow row = model.getRow(substitutionTable.getSelectedRow());
			lineup.getSubstitutionList().remove(row.getSubstitution());
			updateOrderIDs();
			refresh();
		}
	}

	private class RemoveAllAction extends AbstractAction {

		private static final long serialVersionUID = 715531467617L;

		public RemoveAllAction() {
			super(HOVerwaltung.instance().getLanguageString("ls.button.deleteall"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			List<Substitution> subs = new ArrayList<>(lineup.getSubstitutionList());
			lineup.getSubstitutionList().removeAll(subs);
			refresh();
		}
	}

	private class EditAction extends AbstractAction {

		private static final long serialVersionUID = 715531467677812457L;

		public EditAction() {
			super(HOVerwaltung.instance().getLanguageString("ls.button.edit"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			editSelectedSubstitution();
		}
	}

	private class OrderTypeRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 5422073852994253027L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected, boolean hasFocus, int row, int column) {

			JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
					hasFocus, row, column);
			SubstitutionsTableModel tblModel = (SubstitutionsTableModel) table.getModel();
			Substitution sub = tblModel.getRow(row).getSubstitution();
			Icon icon = switch (sub.getOrderType()) {
				case SUBSTITUTION -> ThemeManager.getIcon(HOIconName.SUBSTITUTION);
				case NEW_BEHAVIOUR -> ThemeManager.getIcon(HOIconName.ARROW_MOVE);
				case POSITION_SWAP -> ThemeManager.getIcon(HOIconName.ARROW_CIRCLE);
				case MAN_MARKING -> ThemeManager.getIcon(HOIconName.MAN_MARKING);
			};
			component.setIcon(icon);
			return component;
		}
	}

	private class WarningRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 7013869782046646283L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

			JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
					hasFocus, row, column);
			SubstitutionsTableModel tblModel = (SubstitutionsTableModel) table.getModel();
			TableRow tblRow = tblModel.getRow(row);
			if (tblRow.isUncertain()) {
				component.setIcon(ThemeManager.getIcon(HOIconName.EXCLAMATION));
			} else if (tblRow.isError()) {
				component.setIcon(ThemeManager.getIcon(HOIconName.EXCLAMATION_RED));
			} else {
				component.setIcon(null);
			}
			return component;
		}
	}
}
