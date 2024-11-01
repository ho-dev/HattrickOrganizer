package core.model;

import core.db.AbstractTable;
import core.db.DBManager;

import java.util.Objects;

/**
 * Configuration parameters are registered in a static property register
 * which is saved in the user configuration table at application termination
 * Remark: This class should replace usage of UserParameter an HOParameter classes
 */
public class HOConfigurationParameter extends AbstractTable.Storable {

    /**
     * The parameters' registry
     */
    static protected final HOProperties parameters = new HOProperties();

    /**
     * Remember if parameters were changed
     */
    static private boolean parametersChanged = false;

    /**
     * Parameter key
     */
    private String key;

    /**
     * Parameter value
     */
    private String value;

    /**
     * Create configuration parameter
     * If key is found in registry, the value is fetched from registry otherwise it is loaded from the database.
     * If this isn't found either the given default value is used.
     * @param key Parameter key
     * @param defaultValue Default value
     */
    public HOConfigurationParameter(String key, String defaultValue){
        this.key = key;
        this.value = parameters.getProperty(key);
        if (value == null){
            value = DBManager.instance().loadHOConfigurationParameter(key);
            if (value == null) {
                value = defaultValue;
            }
            parameters.setProperty(key, value);
        }
    }

    /**
     * Return the key
     * @return String
     */
    public String getKey() {
        return key;
    }

    /**
     * Return the value
     * @return String
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value.
     * ParameterChanged is the to true, if new value different to previous value
     * @param value New value
     */
    public void setValue(String value) {
        if (!Objects.equals(value, this.value)){
            this.value = value;
            parameters.setProperty(key, value);
            parametersChanged = true;
        }
    }

    /**
     * Store the current parameters of the registry in the database
     */
    static public void storeParameters() {
        if (parametersChanged){
            for (var p : parameters.entrySet()){
                DBManager.instance().saveUserParameter((String)p.getKey(), (String)p.getValue());
            }
        }
    }
}


