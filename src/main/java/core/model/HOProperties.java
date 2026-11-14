package core.model;

import java.util.Properties;

public class HOProperties  extends Properties {

    public int getInt(String key, int defaultValue) {
        var result = getProperty(key);
        if (result != null && !result.isEmpty()){
            return Integer.parseInt(result);
        }
        return defaultValue;
    }

    /**
     * @return the value as an Integer, or null if the key is missing or empty
     */
    public Integer getInteger(String key) {
        var result = getProperty(key);
        if (result != null && !result.isEmpty()){
            return Integer.parseInt(result);
        }
        return null;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        var result = getProperty(key);
        if (result != null && !result.isEmpty()){
            return Boolean.parseBoolean(result);
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        var result = getProperty(key);
        if (result != null && !result.isEmpty()){
            return Double.parseDouble(result);
        }
        return defaultValue;
    }
}
