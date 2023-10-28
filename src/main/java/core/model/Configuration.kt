package core.model

abstract class Configuration {
    fun getStringValue(values: Map<String?, String>, key: String?): String {
        return values[key].toString()
    }

    fun getBooleanValue(values: Map<String?, String>, key: String?): Boolean {
        val value = values[key].toString()
        return "true".equals(value, ignoreCase = true)
    }

    fun getIntValue(values: Map<String?, String>, key: String?): Int {
        val value = values[key].toString()
        try {
            return value.toInt()
        } catch (_: NumberFormatException) {
        }
        return 0
    }

    fun getFloatValue(values: Map<String?, String>, key: String?): Float {
        val value = values[key].toString()
        try {
            return value.toFloat()
        } catch (_: NumberFormatException) {
        }
        return 0f
    }

    fun getDoubleValue(values: Map<String?, String>, key: String?, defaultValue: Double): Double {
        val value = values[key].toString()
        try {
            return value.toDouble()
        } catch (_: NumberFormatException) {
        }
        return defaultValue
    }
    /**
     * Values for saving in db.
     *
     * @return Map – map containing key-value pairs representing configuration values.
     */
	abstract var values: Map<String?, String?>?
}
