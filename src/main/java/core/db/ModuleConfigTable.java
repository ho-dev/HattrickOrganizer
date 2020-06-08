package core.db;

import core.util.HOLogger;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


final class ModuleConfigTable extends AbstractTable {
	final static String TABLENAME = "MODULE_CONFIGURATION";

	protected ModuleConfigTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[3];
		columns[0] = new ColumnDescriptor("CONFIG_KEY", Types.VARCHAR, false, true, 50);
		columns[1] = new ColumnDescriptor("CONFIG_VALUE", Types.VARCHAR, true, 256);
		columns[2] = new ColumnDescriptor("CONFIG_DATATYPE", Types.INTEGER, false );
	}

	/**
	 * update & insert method
	 */
	void saveConfig(Map<String, Object> values) {
		int updated;
		String key;

		final Set<String> keys = values.keySet();
		for (String s : keys) {
			key = s;
			updated = updateConfig(key, values.get(key));
			if (updated == 0)
				insertConfig(key, values.get(key));
		} // for		
	}

	 Map<String,Object> findAll() {
		 final HashMap<String, Object> values = new HashMap<>();
		 final StringBuilder sql = new StringBuilder(100);
		 sql.append("SELECT * FROM ").append(getTableName());
		 try {
			 final ResultSet rs = adapter.executeQuery(sql.toString());
			 if (rs != null) {
				 while (rs.next()) {
					 values.put(rs.getString(this.columns[0].getColumnName()), createObject(rs.getString(this.columns[1].getColumnName()), rs.getInt(this.columns[2].getColumnName())));
				 }
				 rs.close();
			 }
		 } catch (SQLException e) {
			 HOLogger.instance().error(this.getClass(), e);
		 }
		 return values;
	 }
	
	private int updateConfig(String key, Object value)  {
		if(key == null)
			return -1;
		StringBuilder sql = new StringBuilder(100);
		sql.append("UPDATE ").append(getTableName()).append(" SET ").append(this.columns[1].getColumnName());
		if(value != null)
			sql.append(" = '").append(value).append("'");
		else
			sql.append(" = null ");
		
		sql.append(",").append(this.columns[2].getColumnName()).append(" = ").append(getType(value));
		sql.append(" WHERE ").append(this.columns[0].getColumnName()).append(" = '").append(key).append("'");
		return adapter.executeUpdate(sql.toString());
	}
	
	private void insertConfig(String key, Object value) {
		if(key == null)
			return;
		StringBuilder sql = new StringBuilder(100);
		sql.append("INSERT INTO ").append(getTableName()).append(" VALUES ('").append(key).append("',");
		if(value == null)
			sql.append(" null ");
		else
			sql.append("'").append(value).append("',").append(getType(value)).append(")");
		adapter.executeUpdate(sql.toString());		
	}
	
	void deleteConfig(String key) {
		String sql = "DELETE FROM " + getTableName() + " WHERE " + this.columns[0].getColumnName() +
				" = '" + key + "'";
		adapter.executeUpdate(sql);
	}
	
	private int getType(Object obj){
		if(obj == null)
			return Types.NULL;
		if(obj instanceof Integer)
			return Types.INTEGER;
		if(obj instanceof BigDecimal)
			return Types.DECIMAL;
		if(obj instanceof Timestamp)
			return Types.TIMESTAMP;
		if(obj instanceof Boolean)
			return Types.BOOLEAN;
		if(obj instanceof Date)
			return Types.DATE;
		return Types.VARCHAR;
	}
	
	private Object createObject(String value, int type){
		if(value == null)
			return value;
		switch(type){
		case Types.INTEGER:
			return Integer.valueOf(value);
		case Types.DECIMAL:
			return new BigDecimal(value);
		case Types.TIMESTAMP:
			return Timestamp.valueOf(value);
		case Types.BOOLEAN:
			return Boolean.valueOf(value);
		case Types.DATE:
			return Date.valueOf(value);
		}
		return value;
	}
	
	@Override
	protected void insertDefaultValues(){
		if(findAll().size() == 0){
			HashMap<String, Object> defaults = new HashMap<>();
			defaults.put("TA_numericRating", Boolean.FALSE);
			defaults.put("TA_descriptionRating", Boolean.TRUE);
			defaults.put("TA_lineupCompare", Boolean.TRUE);
			defaults.put("TA_mixedLineup", Boolean.FALSE);
			defaults.put("TA_tacticDetail", Boolean.FALSE);
			defaults.put("TA_isStars", Boolean.TRUE);
			defaults.put("TA_isTotalStrength", Boolean.TRUE);
			defaults.put("TA_isSquad", Boolean.TRUE);
			defaults.put("TA_isSmartSquad", Boolean.TRUE);
			defaults.put("TA_isLoddarStats", Boolean.TRUE);
			defaults.put("TA_isShowPlayerInfo", Boolean.FALSE);
			defaults.put("TA_isCheckTeamName", Boolean.TRUE);
			saveConfig(defaults);
		}
	}

}
