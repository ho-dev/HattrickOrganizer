package core.file.hrf

import core.model.*
import core.model.match.MatchLineupTeam
import core.model.misc.Basics
import core.model.misc.Economy
import core.model.misc.Verein
import core.model.player.MatchRoleID
import core.model.player.Player
import core.model.player.TrainerType
import core.model.series.Liga
import core.util.HODateTime
import core.util.HOLogger
import core.util.IOUtils
import core.util.StringUtils
import module.youth.YouthPlayer
import tool.arenasizer.Stadium
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

object HRFStringParser {
    private const val ENTITY = "Entity"
    private const val BASICS = "[basics]"
    private const val LEAGUE = "[league]"
    private const val CLUB = "[club]"
    private const val TEAM = "[team]"
    private const val LINEUP = "[lineup]"
    private const val ECONOMY = "[economy]"
    private const val ARENA = "[arena]"
    private const val PLAYER = "[player]"
    private const val YOUTHPLAYER = "[youthplayer]"
    private const val XTRA = "[xtra]"
    private const val LASTLINEUP = "[lastlineup]"
    private const val STAFF = "[staff]"

    @JvmStatic
	fun parse(hrf: String?): HOModel? {
        var modelReturn: HOModel? = null
        var hrfdate: HODateTime? = null
        if (hrf == null || hrf.isEmpty()) {
            HOLogger.instance().log(HRFStringParser::class.java, "HRF string is empty")
            return null
        }
        try {
            val propertiesList: MutableList<Properties> = ArrayList()
            var properties: Properties? = null

            // Load hrf string into a stream
            val bis = ByteArrayInputStream(hrf.toByteArray(StandardCharsets.UTF_8))
            val isr = InputStreamReader(bis, StandardCharsets.UTF_8)
            val hrfReader = BufferedReader(isr)
            var lineString: String?
            var entity: Any?
            var datestring: String?
            var indexEqualsSign: Int
            // While there is still data to process
            while (hrfReader.ready()) {
                // Read a line
                lineString = hrfReader.readLine()

                // Ignore empty lines
                if (lineString == null || lineString.trim { it <= ' ' } == "") {
                    continue
                }

                // New Properties
                if (lineString.startsWith("[")) {
                    // Old Property found, add to the Vector
                    if (properties != null) {
                        // HRF date
                        entity = properties[ENTITY]
                        if (entity != null && entity.toString().equals(BASICS, ignoreCase = true)) {
                            datestring = properties.getProperty("date")
                            hrfdate = HODateTime.fromHT(datestring)
                        }
                        propertiesList.add(properties)
                    }

                    // Create a new Property
                    properties = Properties()
                    // Player?
                    if (lineString.startsWith("[player")) {
                        properties.setProperty(ENTITY, PLAYER)
                        properties.setProperty("id", lineString.substring(7, lineString.lastIndexOf(']')))
                    } else if (lineString.startsWith("[youthplayer")) {
                        properties.setProperty(ENTITY, YOUTHPLAYER)
                        properties.setProperty("id", lineString.substring(12, lineString.lastIndexOf(']')))
                    } else {
                        properties.setProperty(ENTITY, lineString)
                    }
                } else {
                    indexEqualsSign = lineString.indexOf('=')
                    if (indexEqualsSign > 0) {
                        if (properties == null) {
                            properties = Properties()
                        }
                        properties.setProperty(
                            lineString.substring(0, indexEqualsSign)
                                .lowercase(), lineString
                                .substring(indexEqualsSign + 1)
                        )
                    }
                }
            }

            // Add the last property
            if (properties != null) {
                propertiesList.add(properties)
            }

            // Close the reader
            IOUtils.closeQuietly(hrfReader)

            // Create HOModel
            modelReturn = createHOModel(propertiesList, hrfdate)
        } catch (e: Exception) {
            HOLogger.instance().error(HRFStringParser::class.java, e)
        }
        return modelReturn
    }

    /**
     * Creates a [HOModel] instance from list of properties.
     *
     * @param propertiesList  List of [Properties] representing various HT entities.
     * @param hrfDate Date of the HRF file.
     * @return HOModel – Model built from the properties.
     */
    @Throws(Exception::class)
    private fun createHOModel(propertiesList: List<Properties>, hrfDate: HODateTime?): HOModel? {
        val hoModel = HOModel(hrfDate)
        var trainerID = -1
        for (properties in propertiesList) {
            val entity = properties[ENTITY]
            if (entity != null) {
                // basics
                if (entity.toString().equals(BASICS, ignoreCase = true)) {
                    hoModel.basics = Basics(properties)
                    val ownTeamId = HOVerwaltung.instance().model.getBasics().teamId
                    if (hoModel.getBasics().teamId != ownTeamId && ownTeamId != 0) {
                        HOLogger.instance().error(
                            HOModel::class.java,
                            "properties of other team can not be imported: " + hoModel.getBasics().teamName
                        )
                        return null // properties of foreign team
                    }
                } else if (entity.toString().equals(LEAGUE, ignoreCase = true)) {
                    hoModel.league = Liga(properties)
                } else if (entity.toString().equals(CLUB, ignoreCase = true)) {
                    hoModel.club = Verein(properties)
                } else if (entity.toString().equals(TEAM, ignoreCase = true)) {
                    hoModel.team = Team(properties)
                } else if (entity.toString().equals(LINEUP, ignoreCase = true)) {
                    hoModel.storeLineup(MatchLineupTeam(MatchRoleID.convertOldRoleToNew(properties)))
                } else if (entity.toString().equals(ECONOMY, ignoreCase = true)) {
                    hoModel.economy = Economy(properties)
                } else if (entity.toString().equals(ARENA, ignoreCase = true)) {
                    hoModel.stadium = Stadium(properties)
                } else if (entity.toString().equals(PLAYER, ignoreCase = true)) {
                    hoModel.addPlayer(Player(properties, hrfDate, hoModel.id))
                } else if (entity.toString().equals(YOUTHPLAYER, ignoreCase = true)) {
                    hoModel.addYouthPlayer(YouthPlayer(properties))
                } else if (entity.toString().equals(XTRA, ignoreCase = true)) {
                    hoModel.xtraDaten = XtraData(properties)
                    // Not numeric for national teams
                    trainerID = try {
                        properties.getProperty("trainerid", "-1").toInt()
                    } catch (nfe: NumberFormatException) {
                        -1
                    } catch (nfe: NullPointerException) {
                        -1
                    }
                } else if (entity.toString().equals(LASTLINEUP, ignoreCase = true)) {
                    hoModel.previousLineup = MatchLineupTeam(MatchRoleID.convertOldRoleToNew(properties))
                } else if (entity.toString().equals(STAFF, ignoreCase = true)) {
                    hoModel.staff = parseStaff(properties)
                } else {
                    // Ignorieren!
                    HOLogger.instance().log(
                        HRFStringParser::class.java,
                        "Unbekannte Entity: $entity"
                    )
                }
            } else {
                HOLogger.instance().log(
                    HRFStringParser::class.java,
                    "Fehlerhafte Datei / Keine Entity gefunden"
                )
                return null
            }
        }

        // Only keep trainerinformation for player equal to trainerID, rest is
        // resetted . So later trainer could be found by searching for player
        // having trainerType != -1
        if (trainerID > -1) {
            val players = hoModel.getCurrentPlayers()
            for (player in players) {
                if (player.isTrainer && player.playerID != trainerID) {
                    player.trainerSkill = -1
                    player.trainerTyp = TrainerType.None
                }
            }
        }
        return hoModel
    }

    private fun parseStaff(props: Properties): List<StaffMember> {
        return try {
            val list = ArrayList<StaffMember>()
            var i = 0
            while (props.containsKey("staff" + i + "name")) {
                val member = StaffMember()
                member.name = props.getProperty("staff" + i + "name")

                // FIXME: Coach does not seem to have properties correctly set.
                if (!StringUtils.isEmpty(props.getProperty("staff${i}staffid"))) {
                    member.id = props.getProperty("staff${i}staffid").toInt()
                    member.staffType = StaffType.getById(props.getProperty("staff${i}stafftype").toInt())
                    member.level = props.getProperty("staff${i}stafflevel").toInt()
                    member.cost = props.getProperty("staff${i}cost").toInt()
                    list.add(member)
                }
                i++
            }

            // because it is handy...
            list.sort()
            list
        } catch (e: Exception) {
            HOLogger.instance().error(null, "HRFStringParser: Failed to parse staff members")
            ArrayList()
        }
    }
}
