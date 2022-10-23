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
				ColumnDescriptor.Builder.newInstance().setColumnName("COLUMN_ID").setGetter((p) -> ((UserColumn) p).getId()).setSetter((p, v) -> ((UserColumn) p).setId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MODELL_INDEX").setGetter((p) -> ((UserColumn) p).getModelIndex()).setSetter((p, v) -> ((UserColumn) p).setModelIndex((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TABLE_INDEX").setGetter((p) -> ((UserColumn) p).getIndex()).setSetter((p, v) -> ((UserColumn) p).setIndex((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COLUMN_WIDTH").setGetter((p) -> ((UserColumn) p).getPreferredWidth()).setSetter((p, v) -> ((UserColumn) p).setPreferredWidth((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
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
				dbcolumns[i].setModelIndex(i);
				dbcolumns[i].setId(model.getId() * 1000 + dbcolumns[i].getId());
				store(dbcolumns[i]);
			}
		}
	}

	void insertDefault(HOTableModel model) {
		UserColumn[] dbcolumns = model.getColumns();
		for (int i = 0; i < dbcolumns.length; i++) {
			dbcolumns[i].setIndex(i);
		}
		saveModel(model);
	}

	void loadModel(HOTableModel model) {
		int count = 0;
		var userColumns = load(UserColumn.class, model.getId() * 1000, model.getId() * 1000 + 999);
		UserColumn[] dbcolumns = model.getColumns();
		for (var userColumn : userColumns) {
			var modelIndex = userColumn.getModelIndex();
			if (modelIndex < dbcolumns.length) {
				var dbColumn = dbcolumns[modelIndex];
				dbColumn.setModelIndex(modelIndex);
				dbColumn.setPreferredWidth(userColumn.getPreferredWidth());
				dbColumn.setDisplay(true);
				dbColumn.setIndex(userColumn.getIndex());
				count++;
			}
		}

		if (count == 0) {
			insertDefault(model);
			loadModel(model);
		}
	}
}