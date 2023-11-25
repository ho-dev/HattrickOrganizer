package core

import core.db.PersistenceManager
import core.file.hrf.HRF
import core.model.HOModel
import core.util.HODateTime

class HOModelBuilder {

    private var hrf:HRF? = null
    private var persistenceManager:PersistenceManager? = null

    fun hrfId(id: Int): HOModelBuilder {
        hrf = HRF(id, HODateTime.now())
        return this
    }

    fun persistenceManager(persistenceManager: PersistenceManager): HOModelBuilder {
        this.persistenceManager = persistenceManager
        return this
    }

    fun build():HOModel {
        val hoModel = HOModel(hrf?.hrfId ?: -1)
        hoModel.persistenceManager = persistenceManager
        return hoModel
    }
}
