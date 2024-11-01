package core.model;

/**
 * Configuration parameter of integer type
 */
public class HOConfigurationIntParameter extends HOConfigurationParameter {
    /**
     * Parameter value as integer
     */
    private int intValue;

    /**
     * Constructor calls string constructor
     * @param key Parameter key
     * @param defaultValue Default integer value
     */
    public HOConfigurationIntParameter(String key, int defaultValue) {
        super(key, String.valueOf(defaultValue));
        this.intValue = parameters.getInt(key, defaultValue);
    }

    /**
     * Return the parameter integer value
     * @return int
     */
    public int getIntValue() {
        return this.intValue;
    }

    /**
     * Set the new parameter integer value
     * ParameterChanged is set true if new value is different to previous parameter value
     * @param newValue
     */
    public void setIntValue(int newValue) {
        if (this.intValue != newValue) {
            this.intValue = newValue;
            setValue(String.valueOf(newValue));
        }
    }
}