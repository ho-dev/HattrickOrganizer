// %2675225597:de.hattrickorganizer.logik.xml%
/*
 * XMLArenaParser.java
 *
 * Created on 5. Juni 2004, 15:40
 */
package core.file.xml;

import core.util.HOLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author thetom
 */
public class XMLExtensionParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLExtensionParser() {
	}

	public static Extension parseExtension(String dateiname) {
		return parseDetails(XMLManager.parseString(dateiname));
	}

	private static Extension parseDetails(Document doc) {
		Extension ext = new Extension();
		if (doc != null) {
			try {
				Element root = doc.getDocumentElement();
				ext.setRelease(Float.parseFloat(getTagValue(root, "release")));
				ext.setMinimumHOVersion(Float.parseFloat(getTagValue(root, "hoNeeded")));
			} catch (Exception e) {
				HOLogger.instance().log(XMLExtensionParser.class, e);
			}
		}
		return ext;
	}

	private static String getTagValue(Element root, String tag) {
		Element ele = (Element) root.getElementsByTagName(tag).item(0);
		return (XMLManager.getFirstChildNodeValue(ele));
	}
}
