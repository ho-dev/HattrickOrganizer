package core.file.xml;

import core.model.News;
import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLNewsParser {
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of XMLArenaParser
     */
    public XMLNewsParser() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final News parseNews(String dateiname) {
        return parseDetails(XMLManager.parseString(dateiname));
    }

    public final News parseNews(java.io.File datei) {
        return parseDetails(XMLManager.parseFile(datei));
    }

    /////////////////////////////////////////////////////////////////////////////////
    //parse public
    ////////////////////////////////////////////////////////////////////////////////
    public final News parseNewsFromString(String inputStream) {
        return parseDetails(XMLManager.parseString(inputStream));
    }

    /////////////////////////////////////////////////////////////////////////////////
    //Parser Helper private
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * erstellt das MAtchlineup Objekt
     */
    protected final News parseDetails(Document doc) {
		News news = new News();	
        if (doc == null) {
            return news;
        }

        //Tabelle erstellen
		Element root = doc.getDocumentElement();

        try {
            //Fetchdate
			news.setId(getIntTagValue(root, "id"));
			news.setType(getIntTagValue(root, "type"));			
			news.setLink(getTagValue(root, "link"));    
			news.setVersion(getFloatTagValue(root, "version"));
			news.setMinimumHOVersion(getFloatTagValue(root, "hoNeeded"));
			NodeList list = root.getElementsByTagName("text");
			for (int i = 0; (list != null) && (i < list.getLength()); i++) {
				root = (Element) list.item(i);								
				news.addMessage(XMLManager.getFirstChildNodeValue(root));					
			}						     
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"XMLExonom<Parser.parseDetails Exception gefangen: " + e);            
        }
        return news;
    }

	private String getTagValue(Element root, String tag) {
		final Element ele = (Element) root.getElementsByTagName(tag).item(0);
		return (XMLManager.getFirstChildNodeValue(ele));
	}
	
	private int getIntTagValue(Element root, String tag) {
		try {
			return Integer.parseInt(getTagValue(root, tag));
		} catch (NumberFormatException e) {
			return 0;
 		}
	}

	private float getFloatTagValue(Element root, String tag) {
		try {
			return Float.parseFloat(getTagValue(root, tag));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
}
