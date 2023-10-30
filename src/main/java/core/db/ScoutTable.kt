package core.db

import core.util.HODateTime
import module.transfer.scout.ScoutEintrag
import java.sql.*
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class ScoutTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PlayerID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.playerID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.playerID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Name")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.name }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as ScoutEintrag?)!!.name = v as String? })
                .setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Info")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.info }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as ScoutEintrag?)!!.info = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Age")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.alter }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.alter = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Marktwert")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.tsi }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.tsi = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Speciality")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.speciality }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.speciality = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Kondition")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.kondition }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.kondition = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Erfahrung")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.erfahrung }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.erfahrung = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Form")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.form }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.form = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Torwart")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.torwart }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.torwart = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Verteidigung")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.verteidigung }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.verteidigung = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Spielaufbau")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.spielaufbau }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.spielaufbau = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Fluegel")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.fluegelspiel }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.fluegelspiel = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Torschuss")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.torschuss }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.torschuss = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Passpiel")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.passpiel }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.passpiel = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Standards")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.standards }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.standards = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Price")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.price }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.price = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Deadline")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.deadline }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as ScoutEintrag?)!!.deadline = (v as HODateTime).toDbTimestamp()
                }).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Wecker")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.isWecker }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.isWecker = v as Boolean })
                .setType(Types.BOOLEAN).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AgeDays")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.ageDays }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.ageDays = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Agreeability")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.agreeability }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.setAgreeability(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("baseWage")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.getbaseWage() }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.setbaseWage(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Nationality")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.nationality }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.setNationality(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Leadership")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.leadership }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.setLeadership(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Loyalty")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.loyalty }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.loyalty = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MotherClub")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as ScoutEintrag?)!!.isHomegrown }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as ScoutEintrag?)!!.isHomegrown = v as Boolean }).setType(
                Types.BOOLEAN
            ).isNullable(false).build()
        )
    }

    /**
     * Save players from TransferScout
     */
    fun saveScoutList(list: Vector<ScoutEintrag>?) {
        executePreparedDelete()
        if (list != null) {
            for (scout in list) {
                scout.stored = false
                store(scout)
            }
        }
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder? {
        return PreparedSelectStatementBuilder(this, "")
    }

    override fun createPreparedDeleteStatementBuilder(): PreparedDeleteStatementBuilder? {
        return PreparedDeleteStatementBuilder(this, "")
    }

    val scoutList: List<ScoutEintrag?>?
        /**
         * Load player list for insertion into TransferScout
         */
        get() = load(ScoutEintrag::class.java)

    companion object {
        /** tablename  */
        const val TABLENAME = "SCOUT"
    }
}
