package core.file.xml;

import core.util.HOLogger;

import java.io.File;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.ByteArrayInputStream
import java.io.InputStream

object XMLManager {
    /**
     * Returns the value of the attribute, "" otherwise.
     */
    fun getAttributeValue(ele: Element?, attributeName: String?): String {
        try {
            if ((ele != null) && (attributeName != null)) {
                return ele.getAttribute(attributeName)
            }
        } catch (e: Exception) {
            return ""
        }

        return ""
    }

    /**
     * Returns the value of the first child, "" if no child exists.
     */
    fun getFirstChildNodeValue(ele: Element?): String {
        return if (ele?.firstChild != null) {
            ele.firstChild.nodeValue
        } else {
            ""
        }
    }

    fun xmlValue(element: Element?, xmlKey: String): String {
        if (element != null) {
            val ele = element.getElementsByTagName(xmlKey).item(0) as Element
            return getFirstChildNodeValue(ele)
        }
        return ""
    }

    fun xmlIntegerValue(ele: Element, xmlKey: String):Int? {
        val value = xmlValue(ele, xmlKey)
        if (value.isNotEmpty()) {
            return value.toInt()
        }
        return null
    }

    fun xmlIntValue(ele: Element, xmlKey: String):Int {
        return xmlIntValue(ele,xmlKey, 0)
    }

    private fun xmlIntValue(ele: Element, xmlKey: String, def: Int):Int {
        val value = xmlValue(ele, xmlKey)
        return try {
            value.toInt()
        } catch (exception: Exception) {
            def
        }
    }

    fun xmlBoolValue(ele: Element, xmlKey: String): Boolean {
        return xmlBoolValue(ele,xmlKey,false)
    }

    fun xmlBoolValue(ele: Element, xmlKey: String, def: Boolean): Boolean {
        val value = xmlValue(ele, xmlKey)
        return try {
            value.toBoolean()
        } catch (exception: Exception) {
            def
        }
    }

    fun xmlBooleanValue(ele: Element, xmlKey: String):Boolean? {
        val value = xmlValue(ele, xmlKey)
        if (value.isNotEmpty()) {
            return value.toBoolean()
        }
        return null
    }

    fun xmlValue2Hash(hash: MutableMap<String, String>, element: Element, xmlKey: String, hashKey: String): String {
        val value = xmlValue(element, xmlKey)
        hash[hashKey] = value
        return value
    }

    fun xmlValue2Hash(hash: MutableMap<String, String>, element: Element, key: String): String {
        return xmlValue2Hash(hash, element, key, key)
    }

    fun xmlIntValue2Hash(hash: MutableMap<String, String>, element:Element, key: String, def:Int): Int {
        val value = xmlValue2Hash(hash, element, key, key)
        return NumberUtils.toInt(value, def)
    }

    fun xmlAttribute2Hash(hash: MutableMap<String, String>, root: Element, xmlElementname: String, xmlAttributeName: String) {
        val ele = root.getElementsByTagName(xmlElementname).item(0) as Element
        hash[xmlElementname+xmlAttributeName] = ele.getAttribute(xmlAttributeName)
    }

    /**
     * Parse XML from file name.
     */
    fun parseFile(fileName: String):Document? {
        val factory:DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        var doc: Document? = null;

        try {
            val builder = factory.newDocumentBuilder();

            doc = builder.parse(File(fileName));
        } catch (e: Exception) {
            HOLogger.instance().log(XMLManager.javaClass, "Parser error: $e")
            HOLogger.instance().log(XMLManager.javaClass, e)
        }

        return doc
    }
    
    /**
     * Parse XML from input stream.
     */
	fun parseFile(xmlStream: InputStream): Document? {
		val factory:DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
		var doc:Document? = null

		try {
			val builder = factory.newDocumentBuilder();
			doc = builder.parse(xmlStream);
		} catch (e: Exception) {
			HOLogger.instance().log(XMLManager.javaClass, "Parser error: $e")
			HOLogger.instance().log(XMLManager.javaClass, e)
		}

		return doc
	}

    /**
     * Parse XML from file.
     */
    fun parseFile(file: File):Document? {
        val factory = DocumentBuilderFactory.newInstance()
        var doc: Document? = null

        try {
            val builder = factory.newDocumentBuilder()
            doc = builder.parse(file)
        } catch (e: Exception) {
            HOLogger.instance().log(XMLManager.javaClass,"Parser error: $e")
            HOLogger.instance().log(XMLManager.javaClass, e)
        }

        return doc
    }

    /**
     * Parses an XML string.
     */
    fun parseString(inputString: String): Document? {
        var tmpInputString = inputString
        //Fix to remove commented tag
        if (tmpInputString.isEmpty()) return null
        var indexComm = tmpInputString.indexOf("<!--")

        while (indexComm > -1) {
            val endComm = tmpInputString.indexOf("-->")
            val comment = tmpInputString.substring(indexComm, endComm + 3)
            tmpInputString = tmpInputString.replace(comment, "")
            indexComm = tmpInputString.indexOf("<!--")
        }

        var doc: Document? = null

        try {
            val input = ByteArrayInputStream(tmpInputString.toByteArray(Charsets.UTF_8))
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()

            doc = builder.parse(input)
        } catch (e: Exception) {
            HOLogger.instance().log(XMLManager.javaClass,"Parser error: $e")
            HOLogger.instance().log(XMLManager.javaClass, e)
        }

        if (doc == null || doc.getElementsByTagName("HattrickData").length <= 0) {
            HOLogger.instance().error(XMLManager.javaClass, "Cannot parse data:$tmpInputString");
            return null;
        }

        return doc;
    }

    /**
     * Saves the document into the <code>fileName</code> file.  This overwrites the file if it already exists.
     */
    fun writeXML(doc: Document, fileName: String) {
        try {
            val serializer = TransformerFactory.newInstance().newTransformer()
            val source = DOMSource(doc)
            val file = File(fileName)
            val result = StreamResult(file)

            serializer.transform(source, result)
        } catch (e: Exception) {
            HOLogger.instance().log(XMLManager.javaClass, "XMLManager.writeXML: $e")
            HOLogger.instance().log(XMLManager.javaClass, e)
        }
    }

	/**
	 * Returns the content of an XML [Document] into a String.
	 */
	fun getXML(doc: Document): String {
		var xml = ""
        
		try {
			val serializer = TransformerFactory.newInstance().newTransformer()
			val source = DOMSource(doc)
			val sw = StringWriter()
			val result = StreamResult(sw)
			serializer.transform(source, result)
			xml = sw.toString()
		} catch (e: Exception) {
			HOLogger.instance().log(XMLManager.javaClass,"XMLManager.getXML: $e")
			HOLogger.instance().log(XMLManager.javaClass, e)
		}
		return xml
	}
}
