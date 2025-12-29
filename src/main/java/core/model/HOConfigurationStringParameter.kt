package core.model

import core.db.DBManager

class HOConfigurationStringParameter(key : String, defaultValue: String?)
    : HOConfigurationParameter(key, defaultValue as Object?) {

    /**
     * Return the value
     * @return String
     */
    fun getValue(): String? {
        return parameters.get(key) as String?
    }

    override fun convertToObject(storedValue: String): Object {
        return storedValue as Object
    }

}
