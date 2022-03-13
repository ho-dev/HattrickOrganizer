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

	protected UserColumnsTable(JDBCAdapter  adapter){
		super(TABLENAME, adapter);
	}


	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[4];
		columns[0]= new ColumnDescriptor("COLUMN_ID",	Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("MODELL_INDEX",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("TABLE_INDEX",	Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("COLUMN_WIDTH",Types.INTEGER,true);
	}

	void deleteModel(int modelId){
		adapter.executeUpdate("DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN "+(modelId*1000)+" AND "+((modelId*1000+999)));
	}

	void saveModel(HOTableModel model){

		deleteModel(model.getId());

		final StringBuilder sql = new StringBuilder(100);
		final StringBuilder values = new StringBuilder(20);
		sql.append("INSERT INTO ");
		sql.append(getTableName());
		sql.append("(");
		sql.append(columns[0].getColumnName());
		sql.append(",");
		sql.append(columns[1].getColumnName());
		sql.append(",");
		sql.append(columns[2].getColumnName());
		sql.append(",");
		sql.append(columns[3].getColumnName());
		sql.append(") VALUES (");

		UserColumn[] dbcolumns = model.getColumns();
		for (int i = 0; i < dbcolumns.length; i++) {
			if (model.getId()==2 && dbcolumns[i].getId() == UserColumnFactory.ID) {
				dbcolumns[i].setDisplay(true); // force ID column
			}
			if(dbcolumns[i].isDisplay()){
				values.append((model.getId()*1000)+dbcolumns[i].getId());
				values.append(",");
				values.append(i);
				values.append(",");
				values.append(dbcolumns[i].getIndex());
				values.append(",");
				values.append(dbcolumns[i].getPreferredWidth());
				values.append(")");
				adapter.executeUpdate(sql.toString()+values);
				values.delete(0,values.length());
			} // if
		}
	}

	void insertDefault(HOTableModel model){

		deleteModel(model.getId());

		final StringBuilder sql = new StringBuilder(100);
		final StringBuilder values = new StringBuilder(20);
		sql.append("INSERT INTO ");
		sql.append(getTableName());
		sql.append("(");
		sql.append(columns[0].getColumnName());
		sql.append(",");
		sql.append(columns[1].getColumnName());
		sql.append(",");
		sql.append(columns[2].getColumnName());
		sql.append(",");
		sql.append(columns[3].getColumnName());
		sql.append(") VALUES (");

		UserColumn[] dbcolumns = model.getColumns();
		for (int i = 0; i < dbcolumns.length; i++) {
				dbcolumns[i].setIndex(i);
				values.append((model.getId()*1000)+dbcolumns[i].getId());
				values.append(",");
				values.append(i);
				values.append(",");
				values.append(dbcolumns[i].getIndex());
				values.append(",");
				values.append(dbcolumns[i].getPreferredWidth());
				values.append(")");

			adapter.executeUpdate(sql.toString()+values);
			values.delete(0,values.length());
		}
	}

	void loadModel(HOTableModel model){
		int modelIndex;
		int tableIndex;
		int width;

		int count = 0;
		String sql = "SELECT * " +
				" FROM " +
				getTableName() +
				" WHERE " +
				columns[0].getColumnName() +
				" BETWEEN " +
				model.getId() * 1000 +
				" AND " +
				(model.getId() * 1000 + 999);
		ResultSet rs = adapter.executeQuery(sql);
		UserColumn[] dbcolumns = model.getColumns();
		try {

			while (true) {
				assert rs != null;
				if (!rs.next()) break;
				modelIndex 	= rs.getInt(columns[1].getColumnName());
				if ( modelIndex < dbcolumns.length) {
					tableIndex = rs.getInt(columns[2].getColumnName());
					width = rs.getInt(columns[3].getColumnName());
					dbcolumns[modelIndex].setIndex(tableIndex);
					dbcolumns[modelIndex].setDisplay(true);
					dbcolumns[modelIndex].setPreferredWidth(width);
					count++;
				}
			}

			if (count == 0){
				insertDefault(model);
				loadModel(model);
			}
			rs.close();

		} catch (SQLException e) {
			HOLogger.instance().log(getClass(),e);
		}
	}
}
