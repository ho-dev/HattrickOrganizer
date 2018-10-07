package core.file.xml;

import core.util.HOLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLMenuParser {
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of XMLMenuParser
     */
    public XMLMenuParser() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /////////////////////////////////////////////////////////////////////////////////    
    //parse public
    ////////////////////////////////////////////////////////////////////////////////    
    public final String parseMenu(String dateiname) {
        return parseDetails(XMLManager.parseFile(dateiname));
    }

    public final String parseMenuFromFile(java.io.File datei) {
        return parseDetails(XMLManager.parseFile(datei));
    }

    public final String parseMenuFromString(String inputStream) {
        return parseDetails(XMLManager.parseString(inputStream));
    }

    /////////////////////////////////////////////////////////////////////////////////    
    //Parser Helper private
    ////////////////////////////////////////////////////////////////////////////////  

    /**
     * erstellt das MAtchlineup Objekt
     */
    protected final String parseDetails(Document doc) {
        Element ele = null;
        Element root = null;

        if (doc == null) {
            return "";
        }

        //Tabelle erstellen
        root = doc.getDocumentElement();

        try {
            //Daten füllen
            ele = (Element) root.getElementsByTagName("RecommendedURL").item(0);

            String ip = ele.getFirstChild().getNodeValue();

            if (ip.startsWith("http://")) {
                ip = ip.substring(7, ip.length());
            }

            return ip;
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"XMLMenuParserparseDetails  Exception gefangen: " + e);
            HOLogger.instance().log(getClass(),e);
        }

        return "";
    }
}
