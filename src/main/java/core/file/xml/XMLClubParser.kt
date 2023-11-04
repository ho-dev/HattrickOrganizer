/*
 * XMLClubParser.java
 *
 * Created on 4. November 2003, 14:37
 */
package core.file.xml

import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 *
 * @author thomas.werth
 */
object XMLClubParser {

    fun parseClubFromString(inputStream: String): Map<String, String> {
        return parseDetails(XMLManager.parseString(inputStream))
    }

    private fun parseDetails(doc: Document?): Map<String, String> {
        val map = SafeInsertMap()

        if (doc == null) {
            return map
        }

        try {
        	var root = doc.documentElement
            
        	var ele = root.getElementsByTagName("FetchedDate").item(0) as Element
            map.insert("FetchedDate", XMLManager.getFirstChildNodeValue(ele))

            root = root.getElementsByTagName("Team").item(0) as Element
            ele = root.getElementsByTagName("TeamID").item(0) as Element
            map.insert("TeamID", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("TeamName").item(0) as Element
            map.insert("TeamName", XMLManager.getFirstChildNodeValue(ele))

            root = doc.documentElement.getElementsByTagName("Staff").item(0) as Element
            ele = root.getElementsByTagName("MedicLevels").item(0) as Element
            map.insert("Doctors", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("SpokespersonLevels").item(0) as Element
            map.insert("PressSpokesmen", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("AssistantTrainerLevels").item(0) as Element
            map.insert("AssistantTrainers", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("SportPsychologistLevels").item(0) as Element
            map.insert("Psychologists", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("FinancialDirectorLevels").item(0) as Element
            map.insert("FinancialDirectorLevels", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("FormCoachLevels").item(0) as Element
            map.insert("FormCoachLevels", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("TacticalAssistantLevels").item(0) as Element
            map.insert("TacticalAssistantLevels", XMLManager.getFirstChildNodeValue(ele))

            root = doc.documentElement.getElementsByTagName("YouthSquad").item(0) as Element
            ele = root.getElementsByTagName("Investment").item(0) as Element
            map.insert("Investment", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("HasPromoted").item(0) as Element
            map.insert("HasPromoted", XMLManager.getFirstChildNodeValue(ele))
            ele = root.getElementsByTagName("YouthLevel").item(0) as Element
            map.insert("YouthLevel", XMLManager.getFirstChildNodeValue(ele))
        } catch (e: Exception) {
            HOLogger.instance().log(XMLClubParser.javaClass, e)
        }

        return map
    }
}
