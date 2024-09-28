package core.model;

import core.db.AbstractTable;
import core.db.DBManager;

import java.util.Objects;

public class HOConfigurationParameter extends AbstractTable.Storable {

    static protected final HOProperties parameters = new HOProperties();
    static private boolean parametersChanged = false;

    private String key;
    private String value;

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

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (!Objects.equals(value, this.value)){
            this.value = value;
            parameters.setProperty(key, value);
            parametersChanged = true;
        }
    }

    static public void storeParameters() {
        if (parametersChanged){
            for (var p : parameters.entrySet()){
                DBManager.instance().saveUserParameter((String)p.getKey(), (String)p.getValue());
            }
        }
    }
}


