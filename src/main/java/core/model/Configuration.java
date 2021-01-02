package core.model;

import java.util.Map;


public abstract class Configuration {

	public String getStringValue(Map<String, String> values, String key) {
		return String.valueOf(values.get(key)); 
	}
	
	public boolean getBooleanValue(Map<String, String> values,String key) {
		String value = String.valueOf(values.get(key));
		return "true".equalsIgnoreCase(value);
	}

	public int getIntValue(Map<String, String> values, String key) {
		String value = String.valueOf(values.get(key));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public float getFloatValue(Map<String, String> values,String key) {
		String value = String.valueOf(values.get(key));
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
		}
		return 0f;
	}

	public double getDoubleValue(Map<String, String> values,String key, double defaultValue) {
		String value = String.valueOf(values.get(key));
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}

	/**
	 * Values for saving in db.
	 * 
	 * @return Map – map containing key-value pairs representing configuration values.
	 */
	public abstract Map<String, String> getValues();
	
	/**
	 * load values to set properties in object
	 * @param values
	 */
	public abstract void setValues(Map<String, String> values);

}
