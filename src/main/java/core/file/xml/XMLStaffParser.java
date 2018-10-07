package core.file.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLStaffParser {

	
		/**
		 * Utility class - private constructor enforces noninstantiability.
		 */
	    private XMLStaffParser() {
	    }
	    

	    public static List<MyHashtable> parseStaffFromString(String inputStream) {
	    	Document doc = null;
	        doc = XMLManager.parseString(inputStream);
	        return createList(doc);
	    }
	    
	    private static List<MyHashtable> createList(Document doc) {
	    	
	    	final List<MyHashtable> returnList = new ArrayList<MyHashtable>();
	    	NodeList nodeList;
	    	MyHashtable hash = null;
	    	Element ele = null;
	    	Element root = doc.getDocumentElement();
	    	
	    	root = (Element) root.getElementsByTagName("StaffMembers").item(0);
	    	nodeList = root.getElementsByTagName("Staff");
	            
	    	for (int i = 0; (nodeList != null) && (i < nodeList.getLength()); i++) {
	    		 hash = new MyHashtable();
	    		 root = (Element) nodeList.item(i);
	       
	    		 ele = (Element) root.getElementsByTagName("Name").item(0);
	    		 hash.put("Name", (XMLManager.getFirstChildNodeValue(ele)));
	    		 ele = (Element) root.getElementsByTagName("StaffId").item(0);
	    		 hash.put("StaffId", (XMLManager.getFirstChildNodeValue(ele)));
	    		 ele = (Element) root.getElementsByTagName("StaffType").item(0);
	    		 hash.put("StaffType", (XMLManager.getFirstChildNodeValue(ele)));
	    		 ele = (Element) root.getElementsByTagName("StaffLevel").item(0);
	    		 hash.put("StaffLevel", (XMLManager.getFirstChildNodeValue(ele)));
	    		 ele = (Element) root.getElementsByTagName("Cost").item(0);
	    		 hash.put("Cost", (XMLManager.getFirstChildNodeValue(ele)));
	                
	                
	    		 returnList.add(hash);
	
	    	 }
	    	return returnList;
	    }
}
