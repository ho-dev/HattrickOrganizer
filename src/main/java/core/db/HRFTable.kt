package core.db

import core.file.hrf.HRF
import core.util.HODateTime
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class HRFTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    private var maxHrf = HRF()
    private var latestHrf = HRF()
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as HRF?)!!.hrfId })
                .setSetter(BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as HRF?)!!.hrfId = v as Int })
                .setType(Types.INTEGER).isNullable(false)
                .isPrimaryKey(true)
                .build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Datum")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as HRF?)!!.datum.toDbTimestamp() })
                .setSetter(BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as HRF?)!!.datum = (v as HODateTime?)!! })
                .setType(Types.TIMESTAMP)
                .isNullable(false)
                .build()
        )
    }

     override val createIndexStatement: Array<String?>
         get() = arrayOf(
            "CREATE INDEX iHRF_1 ON " + tableName + "("
                    + columns[1].columnName + ")"
        )

    fun getLatestHrf(): HRF {
        if (latestHrf.hrfId == -1) {
            val hrf = loadLatestDownloadedHRF()
            if (hrf != null) {
                latestHrf = hrf
            }
        }
        return latestHrf
    }

    fun getMaxHrf(): HRF {
        if (maxHrf.hrfId == -1) {
            val hrf = loadMaxHrf()
            if (hrf != null) {
                maxHrf = hrf
            }
        }
        return maxHrf
    }

    /**
     * Save hattrick resource file information
     */
    fun saveHRF(hrf: HRF) {
        store(hrf)
        if (hrf.hrfId > getMaxHrf().hrfId) {
            maxHrf = hrf
        }

        // reimport of latest hrf file has to set latestHrf to a new value
        if (!hrf.datum.isBefore(getLatestHrf().datum) && hrf.hrfId != latestHrf.hrfId) {
            latestHrf = hrf
        }
    }

    private val getGetHrfID4DateStatementBeforeBuilder =
        PreparedSelectStatementBuilder(this, " WHERE Datum<=? ORDER BY Datum DESC LIMIT 1")
    private val getGetHrfID4DateStatementAfterBuilder =
        PreparedSelectStatementBuilder(this, " WHERE Datum>? ORDER BY Datum LIMIT 1")

    /**
     * Load id of latest hrf downloaded before time if available, otherwise the first after time
     */
    fun getHrfIdNearDate(time: Timestamp?): Int {
        var hrfID = 0
        var rs = adapter.executePreparedQuery(getGetHrfID4DateStatementBeforeBuilder.getStatement(), time)
        try {
            if (rs != null) {
                if (rs.next()) {
                    // HRF available?
                    hrfID = rs.getInt("HRF_ID")
                } else {
                    rs = adapter.executePreparedQuery(getGetHrfID4DateStatementAfterBuilder.getStatement(), time)
                    assert(rs != null)
                    if (rs!!.next()) {
                        hrfID = rs.getInt("HRF_ID")
                    }
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DatenbankZugriff.getHRFID4Time: $e")
        }
        return hrfID
    }

    private val loadAllHrfAscendingStatementBuilder = PreparedSelectStatementBuilder(this, "ORDER BY DATUM ASC")
    private val loadAllHrfDescendingStatementBuilder = PreparedSelectStatementBuilder(this, "ORDER BY DATUM DESC")

    /**
     * Get a list of all HRFs
     *
     * @param asc
     * order ascending (descending otherwise)
     *
     * @return all matching HRFs
     */
    fun loadAllHRFs(asc: Boolean): Array<HRF?> {
        val list: List<HRF?>?
        list = if (asc) {
            load(HRF::class.java, adapter.executePreparedQuery(loadAllHrfAscendingStatementBuilder.getStatement()))
        } else {
            load(HRF::class.java, adapter.executePreparedQuery(loadAllHrfDescendingStatementBuilder.getStatement()))
        }
        // Convert to array
        return list.toTypedArray<HRF?>()
    }

    private val loadHRFOrderedStatementBuilder =
        PreparedSelectStatementBuilder(this, " WHERE Datum>=? ORDER BY Datum ASC")

    fun getHRFsSince(from: Timestamp?): List<HRF?> {
        return load(
            HRF::class.java,
            adapter.executePreparedQuery(loadHRFOrderedStatementBuilder.getStatement(), from)
        )
    }

    private val loadLatestHRFDownloadedBeforeStatementBuilder =
        PreparedSelectStatementBuilder(this, "where DATUM < ? order by DATUM desc LIMIT 1")

    fun loadLatestHRFDownloadedBefore(fetchDate: Timestamp?): HRF? {
        return loadHRF(loadLatestHRFDownloadedBeforeStatementBuilder.getStatement(), fetchDate!!)
    }

    /**
     * liefert die Maximal Vergebene Id eines HRF-Files
     */
    private val loadMaxHrfStatementBuilder = PreparedSelectStatementBuilder(this, "order by HRF_ID desc LIMIT 1")
    private fun loadMaxHrf(): HRF? {
        return loadHRF(loadMaxHrfStatementBuilder.getStatement())
    }

    fun loadHRF(id: Int): HRF? {
        return loadHRF(preparedSelectStatement, id)
    }

    private val loadLatestDownloadedHRFStatementBuilder =
        PreparedSelectStatementBuilder(this, "order by DATUM desc LIMIT 1")

    fun loadLatestDownloadedHRF(): HRF? {
        return loadHRF(loadLatestDownloadedHRFStatementBuilder.getStatement())
    }

    private val loadHRFDownloadedAtStatementBuilder = PreparedSelectStatementBuilder(this, "where DATUM =?")
    fun loadHRFDownloadedAt(fetchDate: Timestamp?): HRF? {
        return loadHRF(loadHRFDownloadedAtStatementBuilder.getStatement(), fetchDate!!)
    }

    private fun loadHRF(preparedStatement: PreparedStatement?, vararg params: Any): HRF? {
        return loadOne(HRF::class.java, adapter.executePreparedQuery(preparedStatement, *params))
    }

    fun getHrfIdPerWeekList(nWeeks: Int): List<Int> {
        val sql = "select min(hrf_id) as id from " +
                tableName +
                " group by unix_timestamp(datum)/7/86400 order by id desc limit " +
                nWeeks
        val ret = ArrayList<Int>()
        val rs = adapter.executeQuery(sql)
        try {
            if (rs != null) {
                while (rs.next()) {
                    ret.add(rs.getInt("ID"))
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DatenbankZugriff.getAllHRFs: $e")
        }
        return ret
    }

    companion object {
        /** tablename  */
        const val TABLENAME = "HRF"
    }
}
