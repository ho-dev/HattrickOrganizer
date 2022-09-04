package core.db;

import core.model.Configuration;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * The Table UserConfiguration contain all User properties.
 * CONFIG_KEY = Primary Key, fieldname of the class
 * CONFIG_VALUE = value of the field, save as VARCHAR. Convert to right datatype if loaded
 * 
 * @since 1.36
 *
 */
final class UserConfigurationTable extends AbstractTable {
	final static String TABLENAME = "USERCONFIGURATION";

	protected UserConfigurationTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[2];
		columns[0] = new ColumnDescriptor("CONFIG_KEY", Types.VARCHAR, false, true, 50);
		columns[1] = new ColumnDescriptor("CONFIG_VALUE", Types.VARCHAR, true, 256);
	}


	private void insert(String key, String value) {
		executePreparedInsert(key, value);
	}

	/**
	 * Update a key in the user configuration
	 * if the key does not exist yet, insert it
	 * @param key String
	 * @param value String
	 */
	void update(String key, String value) {
		int updated = executePreparedUpdate(value, key);
		if (updated == 0)
			// Key not yet in DB -> insert key/value
			insert(key, value);
	}

	/**
	 * Removes a <code>key</code> from the user configuration tablr
	 * @param key Key to be removed.
	 */
	void remove(String key) {
		executePreparedDelete(key);
	}

	private PreparedSelectStatementBuilder getAllStringValuesStatementBuilder = new PreparedSelectStatementBuilder(this, "");
	private HashMap<String, String> getAllStringValues() {
		HashMap<String, String> map = new HashMap<>();
		final ResultSet rs = adapter.executePreparedQuery(getAllStringValuesStatementBuilder.getStatement());
		try {
			while (rs != null && rs.next()) {
				map.put(rs.getString("CONFIG_KEY"), rs.getString("CONFIG_VALUE"));
			}
			rs.close();
		} catch (SQLException e) {
			HOLogger.instance().log(getClass(), e);
		}
		return map;
	}

	int getDBVersion() {
		int version = 0;
		try {
			final ResultSet rs = executePreparedSelect("DBVersion");
			if (rs != null && rs.next()) {
				version = rs.getInt(2);
				rs.close();
			}
		} catch (Exception e) {
			try {
				HOLogger.instance().log(getClass(), "Old DB version.");
				final ResultSet rs = adapter.executeQuery("SELECT DBVersion FROM UserParameter");
				if ((rs != null) && rs.next()) {
					version = rs.getInt(1);
					rs.close();
				}
			} catch (Exception e1) {
				HOLogger.instance().log(getClass(), e1);
			}
		}
		return version;
	}

	/**
	 * Get the last HO release where we have completed successfully a config update
	 * @return	the ho version of the last conf update
	 */
	double getLastConfUpdate() {
		double version = 0;
		try {
			final ResultSet rs = executePreparedSelect("LastConfUpdate");

			if ((rs != null) && rs.next()) {
				version = rs.getDouble(2);
				rs.close();
			}
			
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
		return version;
	}

	/**
	 * update/ insert method
	 * @param obj Configuration
	 */
	void store(Configuration obj) {
		final Map<String, String> values = obj.getValues();
		final Set<String> keys = values.keySet();
		for (String key : keys) {
			update(key, (values.get(key) != null) ? values.get(key) : "");
		}		
	}

	/**
	 * 
	 * @param obj Configuration
	 */
	void load(Configuration obj) {
		// initialize with default value
		final Map<String,String> map = obj.getValues();
		final Map<String,String> storedValues = getAllStringValues();

		map.forEach((key, value) -> {
			final String storedValue = storedValues.get(key);

			// this will allow to detect further problems
			if (storedValue == null) {
				HOLogger.instance().info(UserConfigurationTable.class, "parameter " + key + " is not stored in UserConfigurationTable. Default is used: " + value);
			}
			else {
				map.put(key, storedValue); // update map with value store in DB (in UserConfiguration table)
			}
		});
		obj.setValues(map);
	}

}
