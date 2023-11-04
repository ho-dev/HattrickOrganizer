package core.file.xml


import core.model.HOVerwaltung
import core.util.HOLogger

import org.w3c.dom.Element


object XMLCHPPPreParser {
	
	fun getError(xmlIn: String): String {
		var sReturnError = ""
		val hov: HOVerwaltung = HOVerwaltung.instance()
		if (xmlIn.isNotEmpty()) {
	        val doc = XMLManager.parseString(xmlIn)
	        if (doc != null) {
	        	var ele:Element?
	            val root:Element = doc.documentElement
	            try {
	            	// See if an error is found
	            	if (root.getElementsByTagName("ErrorCode").length > 0) {
	            		sReturnError = "CHPP " + hov.getLanguageString("Fehler")
	            		ele = root.getElementsByTagName("ErrorCode").item(0) as Element
						sReturnError += " - " + XMLManager.getFirstChildNodeValue(ele)
	            		ele = root.getElementsByTagName("Error").item(0) as Element
						sReturnError += " - " + XMLManager.getFirstChildNodeValue(ele)
	            	}
	            } catch (ex: Exception) {
	        		 HOLogger.instance().error(XMLCHPPPreParser.javaClass, "XMLCHPPPreParser Exception: $ex")
	        		 sReturnError = "XMLCHPPPreParser Exception - ${ex.message}"
	        	}
	                
	        } else {
	        	sReturnError = hov.getLanguageString("XML_PARSE_ERRROR")
	        }
		} else {
			sReturnError = hov.getLanguageString("NO_HRF_ERROR")
        }
        return sReturnError
    }
}
