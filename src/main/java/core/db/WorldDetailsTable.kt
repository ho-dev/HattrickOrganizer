package core.db

import core.model.WorldDetailLeague
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class WorldDetailsTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LEAGUE_ID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as WorldDetailLeague?)!!.getLeagueId() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as WorldDetailLeague?)!!.setLeagueId(v as Int) })
            ).setType(
                Types.INTEGER
            ).isPrimaryKey(true).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("COUNTRY_ID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as WorldDetailLeague?)!!.getCountryId() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as WorldDetailLeague?)!!.setCountryId(v as Int) })
            ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("COUNTRYNAME")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as WorldDetailLeague?)!!.getCountryName() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as WorldDetailLeague?)!!.setCountryName(v as String?) })
            ).setType(
                Types.VARCHAR
            ).setLength(128).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ACTIVE_USER")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as WorldDetailLeague?)!!.getActiveUsers() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as WorldDetailLeague?)!!.setActiveUsers(v as Int) })
            ).setType(
                Types.INTEGER
            ).isNullable(false).build()
        )
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder {
        return PreparedSelectStatementBuilder(this, "")
    }

    fun insertWorldDetailsLeague(league: WorldDetailLeague?) {
        if (league == null) return
        store(league)
    }

    fun getAllWorldDetailLeagues(): List<WorldDetailLeague?> {
            var ret: List<WorldDetailLeague?>? = load(WorldDetailLeague::class.java)
            if (ret!!.isEmpty()) {
                insertDefaultValues()
                ret = load(WorldDetailLeague::class.java)
            }
            return ret
        }

    override fun insertDefaultValues() {
        for (league: WorldDetailLeague? in WorldDetailLeague.allLeagues) {
            insertWorldDetailsLeague(league)
        }
    }

    companion object {
        val TABLENAME: String = "HT_WORLDDETAILS"
    }
}
