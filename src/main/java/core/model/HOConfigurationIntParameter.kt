package core.model

import core.db.DBManager


/**
 * Configuration parameter of integer type
 */

class HOConfigurationIntParameter(key : String, defaultValue : Object?)
    : HOConfigurationParameter(key,defaultValue) {

    constructor(key: String, default : Int) : this(key, default as Object){}
    constructor(key: String) : this(key, null){}

    /**
     * Return the value
     * @return Integer?
     */
    fun getValue(): Integer? {
        return parameters.get(key) as Integer?
    }

    fun setValue(v: Int){
        this.setValue(Integer.valueOf(v) as Object?)
    }

    override fun convertToObject(storedValue: String) : Object {
        return Integer.valueOf(storedValue) as Object
    }
}