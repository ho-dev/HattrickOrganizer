/*
 * XMLArenaParser.java
 *
 * Created on 5. Juni 2004, 15:40
 */
package core.file.xml

import core.util.HOLogger

import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * 
 * @author thetom
 */
object XMLExtensionParser {

	fun parseExtension(dateiname: String):Extension {
		return parseDetails(XMLManager.parseString(dateiname))
	}

	private fun parseDetails(doc: Document?): Extension {
		val ext = Extension()
		if (doc != null) {
			try {
				val root = doc.documentElement
				ext.release = getTagValue(root, "release").toFloat()
				ext.minimumHOVersion = getTagValue(root, "hoNeeded").toFloat()
			} catch (e: Exception) {
				HOLogger.instance().log(XMLExtensionParser.javaClass, e)
			}
		}
		return ext
	}

	private fun getTagValue(root: Element, tag: String): String {
		val ele = root.getElementsByTagName(tag).item(0) as Element
		return (XMLManager.getFirstChildNodeValue(ele))
	}
}
