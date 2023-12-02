package core.db;

import core.model.Configuration;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

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

	UserConfigurationTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("CONFIG_KEY").setGetter((p) -> ((_Configuration) p).getKey()).setSetter((p, v) -> ((_Configuration) p).setKey((String) v)).setType(Types.VARCHAR).setLength(50).isPrimaryKey(true).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CONFIG_VALUE").setGetter((p) -> ((_Configuration) p).getValue()).setSetter((p, v) -> ((_Configuration) p).setValue((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build()
		};
	}

	public void storeConfiguration(String key, String value) {
		var _config = new _Configuration();
		_config.setKey(key);
		_config.setValue(value);
		_config.setIsStored(isStored(key));
		store(_config);
	}

	private HashMap<String, String> getAllStringValues() {
		HashMap<String, String> map = new HashMap<>();
		var _configs = load(_Configuration.class, connectionManager.executePreparedQuery(createSelectStatement("")));
		for (var _config : _configs) {
			map.put(_config.getKey(), _config.getValue());
		}
		return map;
	}

	int getDBVersion() {
		var config = loadOne(_Configuration.class, "DBVersion");
		if (config != null) return Integer.parseInt(config.getValue());
		HOLogger.instance().log(getClass(), "Old DB version.");
		try (final ResultSet rs = connectionManager.executeQuery("SELECT DBVersion FROM UserParameter")) {
			if ((rs != null) && rs.next()) {
				var ret = rs.getInt(1);
				rs.close();
				return ret;
			}
		} catch (Exception e1) {
			HOLogger.instance().log(getClass(), e1);
		}
		return 0;
	}

	/**
	 * Get the last HO release where we have completed successfully a config update
	 *
	 * @return the ho version of the last conf update
	 */
	double getLastConfUpdate() {
		var config = loadOne(_Configuration.class, "LastConfUpdate");
		if (config != null) return Double.parseDouble(config.getValue());
		return 0.;
	}

	/**
	 * update/ insert method
	 *
	 * @param obj Configuration
	 */
	void storeConfigurations(Configuration obj) {
		final Map<String, String> values = obj.getValues();
		for ( var configuration : values.entrySet()){
			var key = configuration.getKey();
			var val = configuration.getValue();
			storeConfiguration(key, (val != null) ? val : "");
		}
	}

	/**
	 * @param obj Configuration
	 */
	void loadConfigurations(Configuration obj) {
		// initialize with default value
		final Map<String, String> map = obj.getValues();
		final Map<String, String> storedValues = getAllStringValues();

		map.forEach((key, value) -> {
			final String storedValue = storedValues.get(key);

			// this will allow to detect further problems
			if (storedValue == null) {
				HOLogger.instance().info(UserConfigurationTable.class, "parameter " + key + " is not stored in UserConfigurationTable. Default is used: " + value);
			} else {
				map.put(key, storedValue); // update map with value store in DB (in UserConfiguration table)
			}
		});
		obj.setValues(map);
	}


	public static class _Configuration extends AbstractTable.Storable {
		public _Configuration(){}

		private String key;
		private String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}