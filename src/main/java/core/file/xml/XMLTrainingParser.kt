/*
 * XMLTrainingParser.java
 *
 * Created on 27. Mai 2004, 07:43
 */
package core.file.xml

import core.file.xml.XMLManager.getAttributeValue
import core.file.xml.XMLManager.getFirstChildNodeValue
import core.file.xml.XMLManager.parseString
import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Parser for training xml data.
 *
 * @author thomas.werth
 */
object XMLTrainingParser {
    /**
     * Parse the training from the given xml string.
     */
    fun parseTrainingFromString(string: String): Map<String, String> {
        return parseDetails(parseString(string))
    }
    
    private fun parseDetails(doc: Document?): Map<String, String> {
        val map = SafeInsertMap()
        if (doc == null) {
            return map
        }
        try {
            var root = doc.documentElement
            var ele = root.getElementsByTagName("FetchedDate").item(0) as Element
            map.insert("FetchedDate", getFirstChildNodeValue(ele))
            root = root.getElementsByTagName("Team").item(0) as Element
            ele = root.getElementsByTagName("TeamID").item(0) as Element
            map.insert("TeamID", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("TeamName").item(0) as Element
            map.insert("TeamName", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("TrainingLevel").item(0) as Element
            map.insert("TrainingLevel", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("StaminaTrainingPart").item(0) as Element
            map.insert("StaminaTrainingPart", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("NewTrainingLevel").item(0) as Element
            if (getAttributeValue(ele, "Available").trim { it <= ' ' }.equals("true", ignoreCase = true)) {
                map.insert("NewTrainingLevel ", getFirstChildNodeValue(ele))
            }
            ele = root.getElementsByTagName("TrainingType").item(0) as Element
            map.insert("TrainingType", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("Morale").item(0) as Element
            if (getAttributeValue(ele, "Available").trim { it <= ' ' }.equals("true", ignoreCase = true)) {
                map.insert("Morale", getFirstChildNodeValue(ele))
            }
            ele = root.getElementsByTagName("SelfConfidence").item(0) as Element
            if (getAttributeValue(ele, "Available").trim { it <= ' ' }.equals("true", ignoreCase = true)) {
                map.insert("SelfConfidence", getFirstChildNodeValue(ele))
            }
            ele = root.getElementsByTagName("Experience433").item(0) as Element
            map.insert("Experience433", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("Experience451").item(0) as Element
            map.insert("Experience451", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("Experience352").item(0) as Element
            map.insert("Experience352", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("Experience532").item(0) as Element
            map.insert("Experience532", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("Experience343").item(0) as Element
            map.insert("Experience343", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("Experience541").item(0) as Element
            map.insert("Experience541", getFirstChildNodeValue(ele))

            // new formation experiences since training xml version 1.5
            try {
                ele = root.getElementsByTagName("Experience442").item(0) as Element
                map.insert("Experience442", getFirstChildNodeValue(ele))
            } catch (e: Exception) {
                HOLogger.instance().log(XMLTrainingParser::class.java, "Err(Experience442): $e")
            }
            try {
                ele = root.getElementsByTagName("Experience523").item(0) as Element
                map.insert("Experience523", getFirstChildNodeValue(ele))
            } catch (e: Exception) {
                HOLogger.instance().log(XMLTrainingParser::class.java, "Err(Experience523): $e")
            }
            try {
                ele = root.getElementsByTagName("Experience550").item(0) as Element
                map.insert("Experience550", getFirstChildNodeValue(ele))
            } catch (e: Exception) {
                HOLogger.instance().log(XMLTrainingParser::class.java, "Err(Experience550): $e")
            }
            try {
                ele = root.getElementsByTagName("Experience253").item(0) as Element
                map.insert("Experience253", getFirstChildNodeValue(ele))
            } catch (e: Exception) {
                HOLogger.instance().log(XMLTrainingParser::class.java, "Err(Experience253): $e")
            }
            root = root.getElementsByTagName("Trainer").item(0) as Element
            ele = root.getElementsByTagName("TrainerID").item(0) as Element
            map.insert("TrainerID", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("TrainerName").item(0) as Element
            map.insert("TrainerName", getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("ArrivalDate").item(0) as Element
            map.insert("ArrivalDate", getFirstChildNodeValue(ele))
        } catch (e: Exception) {
            HOLogger.instance().log(XMLTrainingParser.javaClass, e)
        }
        return map
    }
}
