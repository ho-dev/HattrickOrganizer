package core.model

/**
 * Configuration parameter of integer type
 */
class HOConfigurationIntParameter(key: String, defaultValue: Int) :
    HOConfigurationParameter(key, defaultValue.toString()) {
    /**
     * Parameter value as integer
     */
    private var intValue: Int

    /**
     * Constructor calls string constructor
     * @param key Parameter key
     * @param defaultValue Default integer value
     */
    init {
        this.intValue = parameters.getInt(key, defaultValue)
    }

    /**
     * Return the parameter integer value
     * @return int
     */
    fun getIntValue(): Int {
        return this.intValue
    }

    /**
     * Set the new parameter integer value
     * ParameterChanged is set true if new value is different to previous parameter value
     * @param newValue New integer value
     */
    fun setIntValue(newValue: Int) {
        if (this.intValue != newValue) {
            this.intValue = newValue
            setValue(newValue.toString())
        }
    }
}