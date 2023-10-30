package core.db

import core.gui.theme.TeamLogoInfo
import core.util.HODateTime
import core.util.HOLogger
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class TeamsLogoTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TEAM_ID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TeamLogoInfo?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TeamLogoInfo?)!!.teamId = v as Int })
                .setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("URL")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TeamLogoInfo?)!!.url }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as TeamLogoInfo?)!!.url = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FILENAME")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TeamLogoInfo?)!!.filename }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as TeamLogoInfo?)!!.filename = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LAST_ACCESS")
                .setGetter(Function<Any?, Any?> { p: Any? ->
                    HODateTime.toDbTimestamp(
                        (p as TeamLogoInfo?)!!.lastAccess
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as TeamLogoInfo?)!!.lastAccess = v as HODateTime? })
                .setType(
                    Types.TIMESTAMP
                ).isNullable(true).build()
        )
    }

    /**
     * Gets team logo file name BUT it will triggers download of the logo from internet if it is not yet available.
     * It will also update LAST_ACCESS field
     *
     * @param teamID             the team id
     * @return the team logo file name
     */
    fun loadTeamLogoInfo(teamID: Int): TeamLogoInfo? {
        return loadOne(TeamLogoInfo::class.java, teamID)
    }

    fun storeTeamLogoInfo(info: TeamLogoInfo?) {
        if (info == null) return
        var logoURL: String? = null
        var fileName: String? = null
        if (info.url == null) {
            // case of bot team ?
            HOLogger.instance().debug(this.javaClass, "storeTeamLogoInfo: logo URI was null for team " + info.teamId)
        } else {
            val logoURI = info.url
            if (logoURI.contains(".")) {
                logoURL = if (!logoURI.startsWith("http")) {
                    "http:$logoURI"
                } else {
                    logoURI
                }
                if (logoURL != null) {
                    val url = logoURL.toHttpUrlOrNull()
                    if (url != null) {
                        fileName = url.pathSegments[url.pathSize - 1]
                    }
                }
            }
            if (fileName == null) {
                HOLogger.instance().error(this.javaClass, "storeTeamLogoInfo: logo URI not recognized $logoURI")
                return
            }
            info.filename = fileName
        }
        info.url = logoURL
        info.stored = isStored(info.teamId)
        store(info)
    }

    companion object {
        /**
         * tablename
         */
        const val TABLENAME = "CLUBS_LOGO"
    }
}
