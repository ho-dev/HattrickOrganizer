package core.db;

import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnFactory;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;



class UserColumnsTable extends AbstractTable {
	final static String TABLENAME = "USERCOLUMNS";

	protected UserColumnsTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}


	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[4];
		columns[0] = new ColumnDescriptor("COLUMN_ID", Types.INTEGER, false, true);
		columns[1] = new ColumnDescriptor("MODELL_INDEX", Types.INTEGER, false);
		columns[2] = new ColumnDescriptor("TABLE_INDEX", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("COLUMN_WIDTH", Types.INTEGER, true);
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
				executePreparedInsert(
						model.getId() * 1000 + dbcolumns[i].getId(),
						i,
						dbcolumns[i].getIndex(),
						dbcolumns[i].getPreferredWidth()
				);
			}
		}
	}

	void insertDefault(HOTableModel model) {

		deleteModel(model.getId());

		UserColumn[] dbcolumns = model.getColumns();
		for (int i = 0; i < dbcolumns.length; i++) {
			dbcolumns[i].setIndex(i);
			executePreparedInsert(
					model.getId() * 1000 + dbcolumns[i].getId(),
					i,
					dbcolumns[i].getIndex(),
					dbcolumns[i].getPreferredWidth()
			);
		}
	}

	void loadModel(HOTableModel model) {
		int modelIndex;
		int tableIndex;
		int width;

		int count = 0;
		var rs = executePreparedSelect(
				model.getId() * 1000,
				model.getId() * 1000 + 999);

		UserColumn[] dbcolumns = model.getColumns();
		try {

			while (true) {
				assert rs != null;
				if (!rs.next()) break;
				modelIndex = rs.getInt(columns[1].getColumnName());
				if (modelIndex < dbcolumns.length) {
					tableIndex = rs.getInt(columns[2].getColumnName());
					width = rs.getInt(columns[3].getColumnName());
					dbcolumns[modelIndex].setIndex(tableIndex);
					dbcolumns[modelIndex].setDisplay(true);
					dbcolumns[modelIndex].setPreferredWidth(width);
					count++;
				}
			}

			if (count == 0) {
				insertDefault(model);
				loadModel(model);
			}
			rs.close();

		} catch (SQLException e) {
			HOLogger.instance().log(getClass(), e);
		}
	}
}
