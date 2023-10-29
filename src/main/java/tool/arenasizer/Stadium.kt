package tool.arenasizer

import core.db.AbstractTable.Storable
import org.apache.commons.lang3.math.NumberUtils
import java.util.*

/**
 * Contains the Stadium data.
 */
class Stadium : Storable {

    var name = ""
    var arenaId = 0
    var expansion = false
    var expansionCosts = 0

    var expansionVip = 0
    var expansionBasicSeating = 0
    var expansionStanding = 0 // aka Terrace
    var expansionSeatingUnderRoof = 0

    var vip = 0
    var basicSeating = 0
    var standing = 0 // aka Terrace
    var seatingUnderRoof = 0

    @JvmField
    var hrfId = 0

    fun totalSize(): Int = standing + basicSeating + seatingUnderRoof + vip

    constructor()

    /**
     * Creates a new Stadium object.
     */
    constructor(properties: Properties) {
        name = properties.getProperty("arenaname", "")
        arenaId = NumberUtils.toInt(properties.getProperty("arenaid"), 0)
        standing = NumberUtils.toInt(properties.getProperty("antalstaplats"), 0)
        basicSeating = NumberUtils.toInt(properties.getProperty("antalsitt"), 0)
        seatingUnderRoof = NumberUtils.toInt(properties.getProperty("antaltak"), 0)
        vip = NumberUtils.toInt(properties.getProperty("antalvip"), 0)
        expansionStanding = NumberUtils.toInt(properties.getProperty("expandingstaplats"), 0)
        expansionBasicSeating = NumberUtils.toInt(properties.getProperty("expandingsitt"), 0)
        expansionSeatingUnderRoof = NumberUtils.toInt(properties.getProperty("expandingtak"), 0)
        expansionVip = NumberUtils.toInt(properties.getProperty("expandingvip"), 0)
        expansion = NumberUtils.toInt(properties.getProperty("isexpanding"), 0) > 0
        if (expansion) {
            expansionCosts = NumberUtils.toInt(properties.getProperty("expandcost"), 0)
        }
    }
}
