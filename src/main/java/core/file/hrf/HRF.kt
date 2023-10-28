package core.file.hrf

import core.db.AbstractTable.Storable
import core.util.HODateTime

/**
 * hattrick/HO file information
 */
class HRF(var hrfId:Int = -1, var datum: HODateTime = HODateTime.now()) : Storable() {

    val isOK: Boolean
        get() = hrfId >= 0
    val name: String
        get() = datum.toLocaleDateTime()
}
