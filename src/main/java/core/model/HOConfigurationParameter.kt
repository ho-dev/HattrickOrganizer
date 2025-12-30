package core.model

import core.db.AbstractTable.Storable
import core.db.DBManager

/**
 * Configuration parameters are registered in a static property register
 * which is saved in the user configuration table at application termination
 * Remark: This class should replace usage of UserParameter an HOParameter classes
 */
abstract class HOConfigurationParameter(
    /**
     * Parameter key
     */
    val key: String, val defaultValue : Object?
) : Storable() {



    /**
     * Create configuration parameter
     * If key is found in registry, the value is fetched from registry otherwise it is loaded from the database.
     * If this isn't found either the given default value is used.
     * @param key Parameter key
     * @param defaultValue Default value
     */
    init {
        if (!parameters.contains(key)) {
            var storedValue = DBManager.instance().loadHOConfigurationParameter(key)
            if (storedValue != null){
                parameters[key] = convertToObject(storedValue)
            }
            else if (defaultValue != null){
                parameters[key] = defaultValue
            }
        }
    }

    /**
     * Convert stored string value to corresponding object type
     */
    abstract fun convertToObject(storedValue: String) : Object


    /**
     * Set the value.
     * ParameterChanged is set to true, if new value different to previous value
     * @param value New value
     */
    fun setValue(value: Object?) {
        if (value != null) {
            if (!value.equals(parameters.get(key))) {
                parameters.put(key, value)
                parametersChanged = true
            }
        } else if (parameters.contains(key)) {
            parameters.remove(key)
            parametersChanged = true
        }
    }

    companion object {
        /**
         * The parameters' registry
         */
        @JvmStatic
        protected val parameters: HOProperties = HOProperties()

        /**
         * Remember if parameters were changed
         */
        var parametersChanged = false

        /**
         * Store the current parameters of the registry in the database
         */
        @JvmStatic
        fun storeParameters() {
            if (parametersChanged) {
                for ((key, value) in parameters) {
                    DBManager.instance().saveUserParameter(key as String, value.toString())
                }
            }
        }
    }
}


