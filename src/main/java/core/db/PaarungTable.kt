package core.db

import core.model.series.Paarung
import core.util.HODateTime
import module.series.Spielplan
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class PaarungTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    init {
        idColumns = 2
    }

    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LigaID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.ligaId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Paarung?)!!.ligaId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Saison")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.saison }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Paarung?)!!.saison = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.heimName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Paarung?)!!.heimName = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.gastName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Paarung?)!!.gastName = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Datum")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.datum.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Paarung?)!!.datum = v as HODateTime? })
                .setType(Types.TIMESTAMP).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Spieltag")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.spieltag }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Paarung?)!!.spieltag = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.heimId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Paarung?)!!.heimId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.gastId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Paarung?)!!.gastId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimTore")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.toreHeim }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Paarung?)!!.toreHeim = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastTore")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.toreGast }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Paarung?)!!.toreGast = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Paarung?)!!.matchId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Paarung?)!!.matchId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build()
        )
    }

    /**
     * Saves a list of games to a given game schedule, i.e. [Spielplan].
     */
    fun storePaarung(fixtures: List<Paarung>?, ligaId: Int, saison: Int) {
        if (fixtures == null) {
            return
        }
        // Remove existing fixtures for the Spielplan if any exists.
        executePreparedDelete(ligaId, saison)
        for (fixture in fixtures) {
            fixture.ligaId = ligaId
            fixture.saison = saison
            fixture.stored = false
            store(fixture)
        }
    }

    fun loadFixtures(ligaId: Int, season: Int): List<Paarung?>? {
        return load(Paarung::class.java, ligaId, season)
    }

    companion object {
        /** tablename  */
        const val TABLENAME = "PAARUNG"
    }
}
