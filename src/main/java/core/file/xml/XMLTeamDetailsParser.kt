package core.file.xml

import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList


object XMLTeamDetailsParser {

    fun fetchRegionID(xmlFile: String): String {
        return fetchTeamDetail(xmlFile, "Region", "RegionID")
    }

    fun fetchLogoURI(xmlFile: String): String {
        return fetchTeamDetail(xmlFile, "LogoURL", null)
    }

    private fun fetchTeamDetail(xmlFile: String, section: String, attribute: String?): String {
        try {
            val doc: Document = XMLManager.parseString(xmlFile) ?: return "-1"
            var root:Element? = doc.documentElement

            root = root?.getElementsByTagName("Team")?.item(0) as Element?
            root = root?.getElementsByTagName(section)?.item(0) as Element?

            if (attribute != null) {
                root = root?.getElementsByTagName(attribute)?.item(0) as Element?
            }

            return XMLManager.getFirstChildNodeValue(root)

        } catch (ex: Exception) {
            HOLogger.instance().log(XMLTeamDetailsParser.javaClass, ex)
        }

        return "-1"
    }

    fun parseTeamDetailsFromString(inputStream: String, teamId: Int): Map<String, String> {
        return parseDetails(XMLManager.parseString(inputStream), teamId)
    }

    private fun parseDetails(doc: Document?, teamId: Int): Map<String, String> {
        val hash = SafeInsertMap()

        if (doc == null) {
            return hash
        }

        var root = doc.documentElement

        try {
            XMLManager.xmlValue2Hash(hash, root, "FetchedDate")

            // User
            root = root.getElementsByTagName("User").item(0) as Element
            XMLManager.xmlValue2Hash(hash, root, "Loginname")
            XMLManager.xmlValue2Hash(hash, root, "LastLoginDate")

            var supportStatus = "False"
            val supporterTier: NodeList = root.getElementsByTagName("SupporterTier")
            if (supporterTier.length > 0) {
                val ele = supporterTier.item(0) as Element
                val supportValue = XMLManager.getFirstChildNodeValue(ele)
                if (supportValue.trim().isNotEmpty()) {
                    supportStatus = "True"
                }
            }
            hash.insert("HasSupporter", supportStatus)

            // We need to find the correct team

            var team: Element? = null
            var ele:Element? = doc.documentElement?.getElementsByTagName("Teams")?.item(0) as Element?
            if (ele != null) {
                root = ele
                val list: NodeList? = root.getElementsByTagName("Team")
                if (list != null) {
                    for (i in 0..<list.length) {
                        team = list.item(i) as Element
                        ele = team.getElementsByTagName("TeamID").item(0) as Element

                        if (Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)) == teamId) {
                            break
                        }
                    }
                }
            } else {
                team = doc.documentElement?.getElementsByTagName("Team")?.item(0) as Element?
            }

            if (team == null) {
                return hash
            }

            XMLManager.xmlValue2Hash(hash, team, "TeamID")
            XMLManager.xmlValue2Hash(hash, team, "TeamName")
            XMLManager.xmlValue2Hash(hash, team, "FoundedDate", "ActivationDate")
            XMLManager.xmlValue2Hash(hash, team, "HomePage")
            XMLManager.xmlValue2Hash(hash, team, "LogoURL")
            // youth team info
            XMLManager.xmlValue2Hash(hash, team, "YouthTeamID")
            XMLManager.xmlValue2Hash(hash, team, "YouthTeamName")

            root = team.getElementsByTagName("League").item(0) as Element?
            XMLManager.xmlValue2Hash(hash, root, "LeagueID")

            try {
                XMLManager.xmlValue2Hash(hash, team, "LeagueLevel")
                XMLManager.xmlValue2Hash(hash, team, "LeagueLevelUnitName")
                XMLManager.xmlValue2Hash(hash, team, "LeagueLevelUnitID")
            } catch (ex: Exception) {
                HOLogger.instance().log(XMLTeamDetailsParser.javaClass, ex)
            }

            try {
                XMLManager.xmlValue2Hash(hash, team, "NumberOfVictories")
                XMLManager.xmlValue2Hash(hash, team, "NumberOfUndefeated")
            } catch (exp: Exception) {
                HOLogger.instance().log(XMLTeamDetailsParser.javaClass, exp)
            }

            val fanclub = team.getElementsByTagName("Fanclub").item(0) as Element?
            if (fanclub != null) {
                XMLManager.xmlValue2Hash(hash, fanclub, "FanclubSize")
            }

            root = team.getElementsByTagName("Trainer").item(0) as Element?
            XMLManager.xmlValue2Hash(hash, root, "PlayerID", "TrainerID")

            root = team.getElementsByTagName("Arena").item(0) as Element?
            XMLManager.xmlValue2Hash(hash, root, "ArenaName")
            XMLManager.xmlValue2Hash(hash, root, "ArenaID")
            root = team.getElementsByTagName("Region").item(0) as Element?
            XMLManager.xmlValue2Hash(hash, root, "RegionID")

            // Power Rating
            val powerRating = doc.documentElement.getElementsByTagName("PowerRating").item(0) as Element?
            if (powerRating != null) {
                XMLManager.xmlValue2Hash(hash, powerRating, "GlobalRanking")
                XMLManager.xmlValue2Hash(hash, powerRating, "LeagueRanking")
                XMLManager.xmlValue2Hash(hash, powerRating, "RegionRanking")
                XMLManager.xmlValue2Hash(hash, powerRating, "PowerRating")
            }

            if (team.getElementsByTagName("TeamRank").length > 0) {
                hash.insert("TeamRank", team.getElementsByTagName("TeamRank").item(0).textContent)
            }

        } catch (e: Exception) {
            HOLogger.instance().log(XMLTeamDetailsParser.javaClass, e)
        }

        return hash
    }

    fun getTeamInfoFromString(input: String): List<TeamInfo> {
        val ret = mutableListOf<TeamInfo>()
        if (input.isEmpty()) return ret

        val doc = XMLManager.parseString(input) ?: return ret

        var root = doc.documentElement
        root = root.getElementsByTagName("Teams").item(0) as Element

        val list: NodeList = root.getElementsByTagName("Team")

        for (i in 0..<list.length) {
            val team = list.item(i) as Element
            val info = TeamInfo()

            var ele = team.getElementsByTagName("TeamID").item(0) as Element
            info.teamId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele))

            ele = team.getElementsByTagName("YouthTeamID").item(0) as Element
            info.youthTeamId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele))

            ele = team.getElementsByTagName("TeamName").item(0) as Element
            info.name = XMLManager.getFirstChildNodeValue(ele)

            ele = team.getElementsByTagName("IsPrimaryClub").item(0) as Element
            info.primaryTeam = XMLManager.getFirstChildNodeValue(ele).toBoolean()

            val league = team.getElementsByTagName("League").item(0) as Element
            ele = league.getElementsByTagName("LeagueName").item(0) as Element
            info.country = XMLManager.getFirstChildNodeValue(ele)

            ele = league.getElementsByTagName("LeagueID").item(0) as Element
            info.leagueId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele))

            ele = team.getElementsByTagName("LeagueLevelUnit").item(0) as Element
            ele = ele.getElementsByTagName("LeagueLevelUnitName").item(0) as Element
            info.league = XMLManager.getFirstChildNodeValue(ele)

            ret.add(info)
        }

        return ret
    }

}
