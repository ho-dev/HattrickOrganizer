package core.db;

import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnFactory;
import java.sql.Types;

class UserColumnsTable extends AbstractTable {
	final static String TABLENAME = "USERCOLUMNS";

	protected UserColumnsTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("COLUMN_ID").setGetter((p) -> ((_UserColumn) p).getId()).setSetter((p, v) -> ((_UserColumn) p).setId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MODELL_INDEX").setGetter((p) -> ((_UserColumn) p).getModelIndex()).setSetter((p, v) -> ((_UserColumn) p).setModelIndex((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TABLE_INDEX").setGetter((p) -> ((_UserColumn) p).getIndex()).setSetter((p, v) -> ((_UserColumn) p).setIndex((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COLUMN_WIDTH").setGetter((p) -> ((_UserColumn) p).getPreferredWidth()).setSetter((p, v) -> ((_UserColumn) p).setPreferredWidth((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
		};
	}

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder() {
		return new PreparedDeleteStatementBuilder(this, "WHERE COLUMN_ID BETWEEN ? AND ?");
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder() {
		return new PreparedSelectStatementBuilder(this, "WHERE COLUMN_ID BETWEEN ? AND ?");
	}

	void deleteModel(int modelId) {
		executePreparedDelete(modelId * 1000, modelId * 1000 + 999);
	}

	void saveModel(HOTableModel model) {
		deleteModel(model.getId());
		UserColumn[] dbcolumns = model.getColumns();
		for (int i = 0; i < dbcolumns.length; i++) {

			if (model.getId() == 2 && dbcolumns[i].getId() == UserColumnFactory.ID) {
				dbcolumns[i].setDisplay(true); // force ID column
			}

			if (dbcolumns[i].isDisplay()) {
				var _userColumn = new _UserColumn();
				_userColumn.setModelIndex(i);
				_userColumn.setId(model.getId() * 1000 + dbcolumns[i].getId());
				_userColumn.setPreferredWidth(dbcolumns[i].getPreferredWidth());
				_userColumn.setIndex(dbcolumns[i].getIndex());
				store(_userColumn);
			}
		}
	}

	void insertDefault(HOTableModel model) {
		UserColumn[] dbcolumns = model.getColumns();
		for (int i = 0; i < dbcolumns.length; i++) {
			dbcolumns[i].setIndex(i);
		}
	}

	void loadModel(HOTableModel model) {
		int count = 0;
		var userColumns = load(_UserColumn.class, model.getId() * 1000, model.getId() * 1000 + 999);
		UserColumn[] dbcolumns = model.getColumns();
		for (var userColumn : userColumns) {
			var modelIndex = userColumn.getModelIndex();
			if (modelIndex < dbcolumns.length) {
				var dbColumn = dbcolumns[modelIndex];
				dbColumn.setPreferredWidth(userColumn.getPreferredWidth());
				dbColumn.setDisplay(true);
				dbColumn.setIndex(userColumn.getIndex());
				count++;
			}
		}

		if (count == 0) {
			insertDefault(model);
		}
	}

	/**
	 * kind of a clone of abstract class UserColumn used to load and store user column information
	 */
	private static class _UserColumn extends AbstractTable.Storable{

		private int id;
		private int modelIndex;
		private int index;
		private Integer preferredWidth;

		public _UserColumn(){}
		/**
		 * set index
		 * if columnModel should be saved index will set, or column is loaded
		 * @param index int
		 */
		public final void setIndex(int index) {
			this.index = index;
		}

		/**
		 * return index of the user column in the model's array definition
		 * @return int
		 */
		public int getModelIndex() {
			return modelIndex;
		}

		/**
		 * set the index of the user column in the model's array definition
		 * @param modelIndex int
		 */
		public void setModelIndex(int modelIndex) {
			this.modelIndex = modelIndex;
		}

		public int getIndex() {
			return index;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Integer getPreferredWidth() {
			return preferredWidth;
		}

		public void setPreferredWidth(Integer preferredWidth) {
			this.preferredWidth = preferredWidth;
		}
	}
}