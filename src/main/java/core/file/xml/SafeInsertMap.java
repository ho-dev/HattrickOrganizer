/*
 * SafeInsertMap.java
 *
 * Created on 13. Januar 2004, 10:45
 */
package core.file.xml;

import java.util.HashMap;

public class SafeInsertMap extends HashMap<String, String> {
    @Override
	public final String put(String key, String value) {
        return (value != null) ? super.put(key, value) : super.put(key, "");
    }
}
