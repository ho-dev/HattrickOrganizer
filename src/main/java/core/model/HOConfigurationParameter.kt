package core.model

import core.db.AbstractTable.Storable
import core.db.DBManager

/**
 * Configuration parameters are registered in a static property register
 * which is saved in the user configuration table at application termination
 * Remark: This class should replace usage of UserParameter an HOParameter classes
 */
open class HOConfigurationParameter(
    /**
     * Parameter key
     */
    val key: String, defaultValue: String?
) : Storable() {
    /**
     * Return the key
     * @return String
     */

    /**
     * Parameter value
     */
    private var value: String?

    /**
     * Create configuration parameter
     * If key is found in registry, the value is fetched from registry otherwise it is loaded from the database.
     * If this isn't found either the given default value is used.
     * @param key Parameter key
     * @param defaultValue Default value
     */
    init {
        this.value = parameters.getProperty(key)
        if (value == null) {
            value = DBManager.instance().loadHOConfigurationParameter(key)
            if (value == null) {
                value = defaultValue
            }
            parameters.setProperty(key, value)
        }
    }

    /**
     * Return the value
     * @return String
     */
    fun getValue(): String? {
        return value
    }

    /**
     * Set the value.
     * ParameterChanged is the to true, if new value different to previous value
     * @param value New value
     */
    fun setValue(value: String) {
        if (value != this.value) {
            this.value = value
            parameters.setProperty(key, value)
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
        private var parametersChanged = false

        /**
         * Store the current parameters of the registry in the database
         */
        @JvmStatic
        fun storeParameters() {
            if (parametersChanged) {
                for ((key, value) in parameters) {
                    DBManager.instance().saveUserParameter(key as String, value as String)
                }
            }
        }
    }
}


