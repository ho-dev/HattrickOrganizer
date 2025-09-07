package core.model

import com.sun.jdi.IntegerValue

/**
 * Configuration parameter of integer type
 */
class HOConfigurationIntParameter : HOConfigurationParameter{

    constructor(key: String, defaultValue: Int) : super(key, defaultValue.toString())
    constructor(key: String) : super(key, null)

    /**
     * Parameter value as integer
     */
    private var intValue: Int?

    /**
     * Constructor calls string constructor
     */
    init {
        val stringVal = this.getValue();
        if (stringVal != null && !stringVal.isEmpty()) this.intValue = Integer.valueOf(stringVal)
        else this.intValue = null
    }

    /**
     * Return the parameter integer value
     * @return int
     */
    fun getIntValue(): Int? {
        return this.intValue
    }

    /**
     * Set the new parameter integer value
     * ParameterChanged is set true if new value is different to previous parameter value
     * @param newValue New integer value
     */
    fun setIntValue(newValue: Int?) {
        if (this.intValue != newValue) {
            this.intValue = newValue
            if (newValue != null) setValue(newValue.toString())
            else setValue(null)
        }
    }
}