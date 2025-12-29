package core.model

import core.db.DBManager


/**
 * Configuration parameter of boolean type
 */

class HOConfigurationBooleanParameter(key : String, defaultValue: Boolean?)
    : HOConfigurationParameter(key, defaultValue as Object?) {

    /**
     * Return the value
     * @return Boolean?
     */
    fun getValue(): Boolean? {
        return parameters.get(key) as Boolean?
    }

    override fun convertToObject(storedValue: String): Object {
        return java.lang.Boolean.valueOf(storedValue) as Object
    }
}