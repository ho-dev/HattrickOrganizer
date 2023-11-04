package core.db

import core.db.DBManager.PreparedStatementBuilder
import core.model.misc.Basics
import core.util.HODateTime
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class BasicsTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.hrfId }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any -> (b as Basics?)!!.hrfId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Manager")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.manager }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any? -> (b as Basics?)!!.manager = v as String? })
                .setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamID")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any -> (b as Basics?)!!.teamId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamName")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.teamName }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any? -> (b as Basics?)!!.teamName = v as String? })
                .setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Land")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.land }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any -> (b as Basics?)!!.land = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Liga")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.liga }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any -> (b as Basics?)!!.liga = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Saison")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.season }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any -> (b as Basics?)!!.season = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Spieltag")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.spieltag }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any -> (b as Basics?)!!.spieltag = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Datum")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.datum.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any? -> (b as Basics?)!!.datum = v as HODateTime? })
                .setType(Types.TIMESTAMP).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Region")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.regionId }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any -> (b as Basics?)!!.regionId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HasSupporter")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.isHasSupporter }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any -> (b as Basics?)!!.isHasSupporter = v as Boolean })
                .setType(Types.BOOLEAN).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ActivationDate")
                .setGetter(Function<Any?, Any?> { b: Any? ->
                    HODateTime.toDbTimestamp(
                        (b as Basics?)!!.activationDate
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any? -> (b as Basics?)!!.activationDate = v as HODateTime? })
                .setType(
                    Types.TIMESTAMP
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SeasonOffset")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.seasonOffset }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any? -> (b as Basics?)!!.seasonOffset = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("YouthTeamName")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.youthTeamName }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any? -> (b as Basics?)!!.youthTeamName = v as String? })
                .setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("YouthTeamID")
                .setGetter(Function<Any?, Any?> { b: Any? -> (b as Basics?)!!.youthTeamId }).setSetter(
                BiConsumer<Any?, Any> { b: Any?, v: Any? -> (b as Basics?)!!.youthTeamId = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf("CREATE INDEX IBASICS_2 ON $tableName(Datum)")

    /**
     * save Basics
     */
    fun saveBasics(hrfId: Int, basics: Basics) {
        basics.hrfId = hrfId
        store(basics)
    }

    /**
     * lädt die Basics zum angegeben HRF file ein
     */
    fun loadBasics(hrfID: Int): Basics {
        var ret = loadOne(Basics::class.java, hrfID)
        if (ret == null) ret = Basics() else if (ret.seasonOffset == 0) {
            val season0 = ret.datum.toHTWeek().season
            if (season0 != ret.season) {
                ret.seasonOffset = ret.season - season0
            }
        }
        return ret
    }

    private val getHrfIDSameTrainingStatementBuilder = PreparedStatementBuilder(
        "SELECT HRF_ID, Datum FROM $tableName WHERE Datum<= ? ORDER BY Datum DESC LIMIT 1"
    )

    /**
     * Gibt die HRFId vor dem Datum zurï¿½ck, wenn mï¿½glich
     */
    fun getHrfIDSameTraining(time: Timestamp?): Int {
        var hrfID = -1
        var hrfDate: Timestamp? = null
        val rs = adapter.executePreparedQuery(getHrfIDSameTrainingStatementBuilder.getStatement(), time)
        try {
            if (rs != null) {
                //HRF vorher vorhanden?
                if (rs.next()) {
                    hrfID = rs.getInt("HRF_ID")
                    hrfDate = rs.getTimestamp("Datum")
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "XMLExporter.getHRFID4Time: $e")
        }
        if (hrfID != -1) {
            //todo sicherstellen das kein Trainingsdatum zwischen matchdate und hrfdate liegt
            val training4Hrf: Timestamp =
                DBManager.getXtraDaten(hrfID)!!.nextTrainingDate.toDbTimestamp()
            if (training4Hrf.after(hrfDate) && training4Hrf.before(time)) //wenn hrfDate vor TrainingsDate und Matchdate nach Trainigsdate ->Abbruch!
            {
                hrfID = -1
            }
        }
        return hrfID
    }

    companion object {
        const val TABLENAME = "BASICS"
    }
}