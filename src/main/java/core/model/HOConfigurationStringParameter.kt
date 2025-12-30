package core.model

class HOConfigurationStringParameter(key : String, defaultValue: String?)
    : HOConfigurationParameter(key, defaultValue as Object?) {

    /**
     * Return the value
     * @return String
     */
    fun getValue(): String? {
        return parameters.get(key) as String?
    }

    /**
     * Convert stored string value to object type (noop here)
     */
    override fun convertToObject(storedValue: String): Object {
        return storedValue as Object
    }

}
