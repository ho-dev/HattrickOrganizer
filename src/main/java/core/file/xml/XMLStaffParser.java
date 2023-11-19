package core.file.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static core.file.xml.XMLManager.xmlValue2Hash;

public class XMLStaffParser {


	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLStaffParser() {
	}


	public static List<MyHashtable> parseStaffFromString(String inputStream) {
		Document doc;
		doc = XMLManager.parseString(inputStream);
		return createList(doc);
	}

	private static List<MyHashtable> createList(Document doc) {

		final List<MyHashtable> returnList = new ArrayList<>();
		NodeList nodeList;
		MyHashtable hash;
		Element root = doc.getDocumentElement();
		var trainer = (Element) root.getElementsByTagName("Trainer").item(0);
		hash = new MyHashtable();
		xmlValue2Hash(hash, trainer, "TrainerId");
		xmlValue2Hash(hash, trainer, "Name");
		xmlValue2Hash(hash, trainer, "Age");
		xmlValue2Hash(hash, trainer, "AgeDays");
		xmlValue2Hash(hash, trainer, "ContractDate");
		xmlValue2Hash(hash, trainer, "Cost");
		xmlValue2Hash(hash, trainer, "CountryID");
		xmlValue2Hash(hash, trainer, "TrainerType");
		xmlValue2Hash(hash, trainer, "Leadership");
		xmlValue2Hash(hash, trainer, "TrainerSkillLevel");
		xmlValue2Hash(hash, trainer, "TrainerStatus");
		returnList.add(hash);

		root = (Element) root.getElementsByTagName("StaffMembers").item(0);
		nodeList = root.getElementsByTagName("Staff");
		for (int i = 0; i < nodeList.getLength(); i++) {
			hash = new MyHashtable();
			root = (Element) nodeList.item(i);

			xmlValue2Hash(hash, root, "Name");
			xmlValue2Hash(hash, root, "StaffId");
			xmlValue2Hash(hash, root, "StaffType");
			xmlValue2Hash(hash, root, "StaffLevel");
			xmlValue2Hash(hash, root, "Cost");
			returnList.add(hash);
		}
		return returnList;
	}
}
