package module.ifa.table;

import java.text.Collator;
import java.util.Comparator;
import javax.swing.DefaultRowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SummaryTableSorter<M extends TableModel> extends TableRowSorter<M> {
	public SummaryTableSorter(M model) {
		super(model);
	}

	// overridden to use SummaryModelWrapper
	@Override
	public void setModel(M model) {
		super.setModel(model);
		setModelWrapper(new SummaryModelWrapper<M>(getModelWrapper()));
	}

	// overridden to use TableCellValueComparator always
	@Override
	protected boolean useToString(int column) {
		return false;
	}

	// overridden to returns TableCellValueComparator
	@Override
	public Comparator<?> getComparator(int column) {
		Comparator<?> comparator = super.getComparator(column);
		if (comparator instanceof Collator)
			comparator = null;
		return new TableCellValueComparator(comparator);
	}

	@SuppressWarnings("hiding")
	class SummaryModelWrapper<M extends TableModel> extends DefaultRowSorter.ModelWrapper<M, Integer> {
		private DefaultRowSorter.ModelWrapper<M, Integer> delegate;

		public SummaryModelWrapper(DefaultRowSorter.ModelWrapper<M, Integer> delegate) {
			this.delegate = delegate;
		}

		@Override
		public M getModel() {
			return delegate.getModel();
		}

		@Override
		public int getColumnCount() {
			return delegate.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return delegate.getRowCount();
		}

		@Override
		public String getStringValueAt(int row, int column) {
			return delegate.getStringValueAt(row, column);
		}

		// returns TableCellValue instances always
		@Override
		public Object getValueAt(int row, int column) {
			return new TableCellValue(row, column, delegate.getValueAt(row, column));
		}

		@Override
		public Integer getIdentifier(int row) {
			return delegate.getIdentifier(row);
		}
	}

	class TableCellValue {
		public int row;
		public int column;
		public Object value;

		public TableCellValue(int row, int column, Object value) {
			this.row = row;
			this.column = column;
			this.value = value;
		}
	}

	class TableCellValueComparator implements Comparator<TableCellValue> {
		@SuppressWarnings("unchecked")
		private Comparator delegate;

		@SuppressWarnings("unchecked")
		public TableCellValueComparator(Comparator delegate) {
			this.delegate = delegate;
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compare(TableCellValue cell1, TableCellValue cell2) {
			SortOrder order = SortOrder.ASCENDING;
			for (SortKey sortKey : getSortKeys()) {
				if (sortKey.getColumn() == cell1.column) {
					order = sortKey.getSortOrder();
					break;
				}
			}
			int modifier = order == SortOrder.ASCENDING ? 1 : -1;
			if (cell1.row == getModelWrapper().getRowCount() - 1)
				return modifier;
			else if (cell2.row == getModelWrapper().getRowCount() - 1)
				return -modifier;
			else {
				if (delegate != null)
					return delegate.compare(cell1.value, cell2.value);
				else {
					String v1 = getModelWrapper().getStringValueAt(cell1.row, cell1.column);
					String v2 = getModelWrapper().getStringValueAt(cell2.row, cell2.column);
					if (v1 == null)
						return v2 == null ? 0 : -1;
					else
						return v2 == null ? 1 : v1.compareTo(v2);
				}
			}
		}
	}
}