package core.model;

import java.awt.Color;
import java.util.HashMap;


public abstract class Configuration {

	public String getStringValue(HashMap<String, String> values,String key) {
		return String.valueOf(values.get(key)); 
	}
	
	public boolean getBooleanValue(HashMap<String, String> values,String key) {
		String value = String.valueOf(values.get(key));
		if (value.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	public int getIntValue(HashMap<String, String> values,String key) {
		String value = String.valueOf(values.get(key));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public float getFloatValue(HashMap<String, String> values,String key) {
		String value = String.valueOf(values.get(key));
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
		}
		return 0f;
	}
	
	public Color getColorValue(HashMap<String, String> values, String key) {
		String value = String.valueOf(values.get(key));
		try {
			return new Color(Integer.parseInt(value));
		} catch (NumberFormatException e) {
		}
		return new Color(0);		
	}
	
	/**
	 * Values for saving in db.
	 * 
	 * @return HashMap
	 */
	public abstract HashMap<String, String> getValues();
	
	/**
	 * load values to set properties in object
	 * @param values
	 */
	public abstract void setValues(HashMap<String, String> values);

}
