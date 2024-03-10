package core.file.xml;

import core.util.HOLogger;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLManager {

    /**
     * Creates a new instance of XMLManager
     */
    private XMLManager() {
    }

    /**
     * liefert den Value des attributes sonst ""
     */
    public static String getAttributeValue(Element ele, String attributeName) {
        try {
            if ((ele != null) && (attributeName != null)) {
                return ele.getAttribute(attributeName);
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }

    /////////////////////////////////////////////////////////////////////////////////
    //Helper
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * liefert den Value des ersten childes falls kein child vorhanden liefert ""
     */
    public static String getFirstChildNodeValue(Element ele) {
        try {
            if (ele != null && ele.getFirstChild() != null) {
                return ele.getFirstChild().getNodeValue();
            }
        } catch (Exception ignored) {
        }

        return "";
    }

    public static String xmlValue(Element element, String xmlKey) {
        if (element != null) {
            var ele = (Element) element.getElementsByTagName(xmlKey).item(0);
            return XMLManager.getFirstChildNodeValue(ele);
        }
        return "";
    }

    public static Integer xmlIntegerValue(Element ele, String xmlKey) {
        var value = xmlValue(ele, xmlKey);
        if (!value.isEmpty()) {
            return Integer.parseInt(value);
        }
        return null;
    }

    public static Long xmlLongValue(Element ele, String xmlKey) {
        var value = xmlValue(ele, xmlKey);
        if (!value.isEmpty()) {
            return Long.parseLong(value);
        }
        return null;
    }

    public static int xmlIntValue(Element ele, String xmlKey) {
        return xmlIntValue(ele,xmlKey,0);
    }

    public static int xmlIntValue(Element ele, String xmlKey, int def) {
        var value = xmlValue(ele, xmlKey);
        try {
            return Integer.parseInt(value);
        }
        catch (Exception exception){
            return def;
        }
    }

    public static boolean xmlBoolValue(Element ele, String xmlKey) {
        return xmlBoolValue(ele,xmlKey,false);
    }

    public static boolean xmlBoolValue(Element ele, String xmlKey, boolean def) {
        var value = xmlValue(ele, xmlKey);
        try {
            return Boolean.parseBoolean(value);
        }
        catch (Exception exception){
            return def;
        }
    }

    /**
     * Get the content of the tag <code>xmlKey</code> under the XML <code>element</code>,
     * and inserts it into the <code>hash</code>, using <code>hashKey</code> as the entry key.
     *
     * <p>If the element is not found in the XML element, an empty string is returned.</p>
     *
     * @param hash {@link Map} into which the value of the XML element is inserted.
     * @param element XML element from which the value is being read.
     * @param xmlKey Name of the XML tag for which we get the value.
     * @param hashKey Name of the key in <code>hash</code> when inserting the value.
     *
     * @return String – Value of the element <code>xmlKey</code> in <code>element</code> if present;
     * empty string otherwise.
     */
    public static String xmlValue2Hash(Map<String, String> hash, Element element, String xmlKey, String hashKey) {
        var value = xmlValue(element, xmlKey);
        hash.put(hashKey, value);
        return value;
    }

    /**
     * Get the content of the tag <code>key</code> under the XML <code>element</code>,
     * and inserts it into the <code>hash</code>, using <code>key</code> as the entry key.
     *
     * <p>If the element is not found in the XML element, an empty string is returned.</p>
     *
     * @param hash {@link Map} into which the value of the XML element is inserted.
     * @param element XML element from which the value is being read.
     * @param key Name of the XML tag for which we get the value, and name of the key in <code>hash</code>
     *            when inserting the value.
     *
     * @return String – Value of the element <code>key</code> in <code>element</code> if present;
     * empty string otherwise.
     */
    public static String xmlValue2Hash(Map<String, String> hash, Element element, String key) {
        return xmlValue2Hash(hash, element, key, key);
    }

    public static int xmlIntValue2Hash(Map<String, String> hash, Element element, String key, int def) {
        var val = xmlValue2Hash(hash, element, key, key);
        return NumberUtils.toInt(val, def);
    }

    public static void xmlAttribute2Hash(Map<String, String> hash, Element root, String xmlElementname, String xmlAttributename) {
        var ele = (Element) root.getElementsByTagName(xmlElementname).item(0);
        var value = "";
        if (ele != null) {
            value = ele.getAttribute(xmlAttributename);
        }
        hash.put(xmlElementname + xmlAttributename,value);
    }

    /**
     * Parse XM from file name.
     */
    public static Document parseFile(String fileName) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc = null;

        try {
            builder = factory.newDocumentBuilder();

            doc = builder.parse(new File(fileName));
        } catch (Exception e) {
            HOLogger.instance().log(XMLManager.class,"Parser error: " + e);
            HOLogger.instance().log(XMLManager.class,e);
        }

        return doc;
    }
    
    /**
     * Parse XML from input stream.
     */
	public static Document parseFile(InputStream xmlStream) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;

		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(xmlStream);
		} catch (Exception e) {
			HOLogger.instance().log(XMLManager.class, "Parser error: " + e);
			HOLogger.instance().log(XMLManager.class, e);
		}

		return doc;
	}

    /**
     * Parse XML fro file.
     */
    public static Document parseFile(File datei) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc = null;

        try {
            //Validierung, Namensräume einschalten
            //factory.setValidating ( false );
            //factory.setNamespaceAware ( true );
            builder = factory.newDocumentBuilder();

            doc = builder.parse(datei);
        } catch (Exception e) {
            HOLogger.instance().log(XMLManager.class,"Parser fehler: " + e);
            HOLogger.instance().log(XMLManager.class,e);
        }

        return doc;
    }

    /**
     * parsed eine übergebene Datei
     */
    public static Document parseString(String inputString) {
        //Fix to remove commented tag
        if ( inputString == null || inputString.isEmpty()) return null;
        int indexComm = inputString.indexOf("<!--");

        while (indexComm > -1) {
            final int endComm = inputString.indexOf("-->");
            final String comment = inputString.substring(indexComm, endComm + 3);
            inputString = inputString.replaceAll(comment, "");
            indexComm = inputString.indexOf("<!--");
        }

        Document doc = null;

        try {
            final java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            //Validierung, Namensräume einschalten
            //factory.setValidating ( false );
            //factory.setNamespaceAware ( true );
            builder = factory.newDocumentBuilder();

            doc = builder.parse(input);
        } catch (Exception e) {
            HOLogger.instance().log(XMLManager.class,"Parser fehler: " + e);
            HOLogger.instance().log(XMLManager.class,e);
        }

        if (doc == null || doc.getElementsByTagName("HattrickData").getLength() <= 0) {
            HOLogger.instance().error(XMLManager.class, "Cannot parse data:" + inputString);
            return null;
        }

        return doc;
    }

    /**
     * speichert das übergebene Dokument in der angegebenen Datei Datei wird überschrieben falls
     * vorhanden
     */
    public static void writeXML(Document doc, String dateiname) {
        //Transformer creation for DOM rewriting into XML file
        Transformer serializer;
        DOMSource source;
        File datei;
        StreamResult result;

        try {
            //You can do any modification to the document here
            serializer = TransformerFactory.newInstance().newTransformer();
            source = new DOMSource(doc);
            datei = new File(dateiname);
            result = new StreamResult(datei);

            serializer.transform(source, result);
        } catch (Exception e) {
            HOLogger.instance().log(XMLManager.class,"XMLManager.writeXML: " + e);
            HOLogger.instance().log(XMLManager.class,e);
        }
    }
}
