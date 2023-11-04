/*
 * MyHashtable.java
 *
 * Created on 13. Januar 2004, 10:45
 */
package core.file.xml

class SafeInsertMap: HashMap<String, String>() {

    /**
     *
     * Note: this method was renamed from <code>put</code> to <code>insert</code>
     * to avoid Kotlin intempestive renames attempts.
     */
    fun insert(key: String, value: String?): String {
        return if (value != null) {
            super.put(key, value)
            value
        } else {
            super.put(key, "")
            ""
        }
    }
}
