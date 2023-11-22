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
