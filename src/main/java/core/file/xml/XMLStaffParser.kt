package core.file.xml

import core.file.xml.XMLManager.parseString
import core.file.xml.XMLManager.xmlValue2Hash
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

object XMLStaffParser {
    fun parseStaffFromString(inputStream: String): List<SafeInsertMap> {
        val doc: Document? = parseString(inputStream)
        return createList(doc)
    }

    private fun createList(doc: Document?): List<SafeInsertMap> {
        val returnList: MutableList<SafeInsertMap> = ArrayList()
        var hash: SafeInsertMap
        var root:Element? = doc?.documentElement
        val trainer = root?.getElementsByTagName("Trainer")?.item(0) as Element?

        hash = SafeInsertMap()
        if (trainer != null) {
            xmlValue2Hash(hash, trainer, "TrainerId")
            xmlValue2Hash(hash, trainer, "Name")
            xmlValue2Hash(hash, trainer, "Age")
            xmlValue2Hash(hash, trainer, "AgeDays")
            xmlValue2Hash(hash, trainer, "ContractDate")
            xmlValue2Hash(hash, trainer, "Cost")
            xmlValue2Hash(hash, trainer, "CountryID")
            xmlValue2Hash(hash, trainer, "TrainerType")
            xmlValue2Hash(hash, trainer, "Leadership")
            xmlValue2Hash(hash, trainer, "TrainerSkillLevel")
            xmlValue2Hash(hash, trainer, "TrainerStatus")
            returnList.add(hash)
        }
        root = root?.getElementsByTagName("StaffMembers")?.item(0) as Element?
        val nodeList:NodeList? = root?.getElementsByTagName("Staff")
        if (nodeList != null) {
            for (i in 0 until nodeList.length) {
                hash = SafeInsertMap()
                root = nodeList.item(i) as Element?
                if (root != null) {
                    xmlValue2Hash(hash, root, "Name")
                    xmlValue2Hash(hash, root, "StaffId")
                    xmlValue2Hash(hash, root, "StaffType")
                    xmlValue2Hash(hash, root, "StaffLevel")
                    xmlValue2Hash(hash, root, "Cost")
                    returnList.add(hash)
                }
            }
        }
        return returnList
    }
}
