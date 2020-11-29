package core.file.xml;

import core.util.HOLogger;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLManager  {

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
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * Parse XM from file name.
     */
    public static Document parseFile(String fileName) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
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
		DocumentBuilder builder = null;
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
        DocumentBuilder builder = null;
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
        int indexComm = inputString.indexOf("<!--");

        while (indexComm > -1) {
            final int endComm = inputString.indexOf("-->");
            final String comment = inputString.substring(indexComm, endComm + 3);
            inputString = inputString.replaceAll(comment, "");
            indexComm = inputString.indexOf("<!--");
        }

        Document doc = null;

        try {
            final java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(inputString.getBytes("UTF-8"));
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;

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
        Transformer serializer = null;
        DOMSource source = null;
        File datei = null;
        StreamResult result = null;

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

	/**
	 * speichert das übergebene Dokument in der angegebenen Datei Datei wird überschrieben falls
	 * vorhanden
	 */
	public static String getXML(Document doc) {
		//Transformer creation for DOM rewriting into XML String
		Transformer serializer = null;
		DOMSource source = null;
		StreamResult result = null;
		String xml = "";
		try {
			//You can do any modification to the document here
			serializer = TransformerFactory.newInstance().newTransformer();
			source = new DOMSource(doc);
			StringWriter sw = new StringWriter();
			result = new StreamResult(sw);
			serializer.transform(source, result);
			xml = sw.toString();
		} catch (Exception e) {
			HOLogger.instance().log(XMLManager.class,"XMLManager.writeXML: " + e);
			HOLogger.instance().log(XMLManager.class,e);
		}
		return xml;
	}
}
