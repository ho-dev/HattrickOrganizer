package core.model;

public class HOConfigurationIntParameter extends HOConfigurationParameter {
    private int intValue;

    public HOConfigurationIntParameter(String key, int defaultValue) {
        super(key, String.valueOf(defaultValue));
        this.intValue = parameters.getInt(key, defaultValue);
    }

    public int getIntValue() {
        return this.intValue;
    }

    public void setIntValue(int newValue) {
        if (this.intValue != newValue) {
            this.intValue = newValue;
            setValue(String.valueOf(newValue));
        }
    }
}