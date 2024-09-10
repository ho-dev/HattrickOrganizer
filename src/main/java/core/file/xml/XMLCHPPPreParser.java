package core.file.xml;

import core.model.TranslationFacility;
import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public final class XMLCHPPPreParser {

	private XMLCHPPPreParser() {
    }
	
	public static String getError(String xmlIn) {
		String sReturnError = "";
		if(!xmlIn.isEmpty()) {
	        Document doc = XMLManager.parseString(xmlIn);
	        if (doc != null) {
	        	Element ele = null;
	            Element root = doc.getDocumentElement();
	            try {
	            	// See if an error is found
	            	if (root.getElementsByTagName("ErrorCode").getLength() > 0) {
	            		sReturnError = "CHPP " + TranslationFacility.tr("Fehler");
	            		ele = (Element) root.getElementsByTagName("ErrorCode").item(0);
	            		if (ele != null) {
	            			sReturnError += " - " + XMLManager.getFirstChildNodeValue(ele);
	            		}
	            		ele = (Element) root.getElementsByTagName("Error").item(0);
	            		if (ele != null) {
	            			sReturnError += " - " + XMLManager.getFirstChildNodeValue(ele);
	            		}
	            	}
	            }
	        	catch (Exception ex)
	        	{
	        		 HOLogger.instance().error(XMLCHPPPreParser.class, "XMLCHPPPreParser Exception: " + ex);
	        		 sReturnError = "XMLCHPPPreParser Exception - " + ex.getMessage();
	        	}
	                
	        } else {
	        	sReturnError = TranslationFacility.tr("XML_PARSE_ERRROR");
	        }
		} else {
			sReturnError = TranslationFacility.tr("NO_HRF_ERROR");
        }
        return sReturnError;
    }
}
