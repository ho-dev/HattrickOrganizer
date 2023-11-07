package core.model.player

import core.db.AbstractTable.Storable
import core.util.HODateTime

class Skillup : Storable() {
    @JvmField
    var hrfId = 0
    @JvmField
    var date: HODateTime? = null
    @JvmField
    var playerId = 0
    @JvmField
    var skill = 0
    @JvmField
    var value = 0
}
