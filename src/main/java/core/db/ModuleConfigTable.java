package core.db;

import core.util.HOLogger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
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
		 try {
			 final ResultSet rs = executePreparedSelect();
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

	 @Override
	 protected PreparedStatement createUpdateStatement(){
		return createUpdateStatement("SET CONFIG_VALUE=?, CONFIG_DATATYPE=? WHERE CONFIG_KEY=?");
	 }
	private int updateConfig(String key, Object value)  {
		return executePreparedUpdate(
				value,
				getType(value),
				key
		);
	}
	
	private void insertConfig(String key, Object value) {
		if(key == null)
			return;
		executePreparedInsert(
				key,
				value,
				getType(value)
		);
	}

	@Override
	protected PreparedStatement createDeleteStatement(){
		return createDeleteStatement("WHERE CONFIG_KEY=?");
	}
	void deleteConfig(String key) {
		executePreparedDelete(key);
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
		return switch (type) {
			case Types.INTEGER -> Integer.valueOf(value);
			case Types.DECIMAL -> new BigDecimal(value);
			case Types.TIMESTAMP -> Timestamp.valueOf(value);
			case Types.BOOLEAN -> Boolean.valueOf(value);
			case Types.DATE -> Date.valueOf(value);
			default -> value;
		};
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
