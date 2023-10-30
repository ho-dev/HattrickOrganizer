package core.db

import module.teamAnalyzer.vo.Team
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

/**
 * The Table UserConfiguration contain all User properties.
 * CONFIG_KEY = Primary Key, fieldname of the class
 * CONFIG_VALUE = value of the field, save as VARCHAR. Convert to right datatype if loaded
 *
 * @since 1.36
 */
internal class TAFavoriteTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TEAMID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Team?)!!.teamId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NAME")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Team?)!!.name }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Team?)!!.name = v as String? }).setType(Types.VARCHAR)
                .isNullable(true).setLength(20).build()
        )
    }

    fun removeTeam(teamId: Int) {
        executePreparedDelete(teamId)
    }

    fun addTeam(team: Team?) {
        store(team)
    }

    fun isTAFavourite(teamId: Int): Boolean {
        return isStored(teamId)
    }

    private val getTAFavoriteTeamsBuilder = PreparedSelectStatementBuilder(this, "")

    fun getTAFavoriteTeams(): List<Team?> {
        return load(Team::class.java, adapter.executePreparedQuery(getTAFavoriteTeamsBuilder.getStatement()))
    }


    companion object {
        const val TABLENAME = "TA_FAVORITE"
    }
}
