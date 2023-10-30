package core.db

import core.model.misc.Verein
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class VereinTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getHrfId() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setHrfId(v as Int) })
            ).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("COTrainer")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getCoTrainer() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setCoTrainer(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Pschyologen")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getPsychologen() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setPsychologen(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Finanzberater")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getFinancialDirectorLevels() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setFinancialDirectorLevels(v as Int) })
                ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PRManager")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getPRManager() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setPRManager(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Aerzte")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getAerzte() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setAerzte(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Jugend")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getJugend() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setJugend(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Siege")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getSiege() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setSiege(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Ungeschlagen")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getUngeschlagen() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setUngeschlagen(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Fans")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getFans() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setFans(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TacticAssist")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getTacticalAssistantLevels() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setTacticalAssistantLevels(v as Int) })
                ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FormAssist")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getFormCoachLevels() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setFormCoachLevels(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GlobalRanking")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getGlobalRanking() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setGlobalRanking(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LeagueRanking")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getLeagueRanking() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setLeagueRanking(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RegionRanking")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getRegionRanking() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setRegionRanking(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PowerRating")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as Verein?)!!.getPowerRating() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as Verein?)!!.setPowerRating(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build()
        )
    }

    /**
     * store team info
     */
    fun saveVerein(hrfId: Int, verein: Verein?) {
        if (verein != null) {
            verein.setHrfId(hrfId)
            verein.stored = isStored(hrfId)
            store(verein)
        }
    }

    /**
     * load team basic information
     */
    fun loadVerein(hrfID: Int): Verein {
        var ret: Verein? = loadOne(Verein::class.java, hrfID)
        if (ret == null) ret = Verein()
        return ret
    }

    companion object {
        val TABLENAME: String = "VEREIN"
    }
}