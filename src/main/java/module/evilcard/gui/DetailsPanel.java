package module.evilcard.gui;

import module.evilcard.Model;
import module.evilcard.ModelChangeAdapter;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableColumnModel;

public class DetailsPanel extends JPanel {

	private static final long serialVersionUID = 331958180198333728L;
	private DetailsTableModel detailsTableModel;
	private final Model model;

	DetailsPanel(Model model) {
		this.model = model;
		initComponents();
		addListeners();
	}

	private void addListeners() {
		this.model.addModelChangeListener(new ModelChangeAdapter() {
			@Override
			public void selectedPlayerChanged() {
				detailsTableModel.refresh(model.getSelectedPlayer());
			}
		});
	}

	private void initComponents() {
		this.detailsTableModel = new DetailsTableModel();
		DetailsTable detailsTable = new DetailsTable();
		detailsTable.setModel(this.detailsTableModel);

		detailsTable.setAutoCreateRowSorter(true);
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(DetailsTableModel.COL_MATCH_ID, SortOrder.DESCENDING));
		detailsTable.getRowSorter().setSortKeys(sortKeys);

		TableColumnModel columnModel = detailsTable.getColumnModel();
		columnModel.getColumn(DetailsTableModel.COL_WARNINGS_TYPE1).setPreferredWidth(20);
		columnModel.getColumn(DetailsTableModel.COL_WARNINGS_TYPE2).setPreferredWidth(20);
		columnModel.getColumn(DetailsTableModel.COL_WARNINGS_TYPE3).setPreferredWidth(20);
		columnModel.getColumn(DetailsTableModel.COL_WARNINGS_TYPE4).setPreferredWidth(20);
		columnModel.getColumn(DetailsTableModel.COL_DIRECT_RED_CARDS).setPreferredWidth(20);
		columnModel.getColumn(DetailsTableModel.COL_MATCH_ID).setPreferredWidth(70);
		columnModel.getColumn(DetailsTableModel.COL_MATCH_HOME).setPreferredWidth(120);
		columnModel.getColumn(DetailsTableModel.COL_MATCH_GUEST).setPreferredWidth(120);
		columnModel.getColumn(DetailsTableModel.COL_MATCH_RESULT).setPreferredWidth(50);
		columnModel.getColumn(DetailsTableModel.COL_EVENT).setPreferredWidth(500);

		detailsTable.setDefaultRenderer(Object.class, new DetailsTableCellRenderer());
		columnModel.getColumn(DetailsTableModel.COL_EVENT).setCellRenderer(new TextAreaRenderer());

		setLayout(new BorderLayout());
		add(new JScrollPane(detailsTable), BorderLayout.CENTER);
	}

}
