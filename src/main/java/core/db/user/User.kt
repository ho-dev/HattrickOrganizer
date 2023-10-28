package core.db.user

import core.HO
import java.nio.file.Paths

class User {
    private lateinit var dbURL: String
    lateinit var dbFolder: String
        private set

    val baseUser: BaseUser
    var dbName: String
        get() = baseUser.dbName
        set(value) {
            baseUser.dbName = value
            fillUserInfos()
        }
    var teamName: String
        get() = baseUser.teamName
        set(value) {
            baseUser.teamName = value
        }
    var numberOfBackups: Int
        get() = baseUser.backupLevel
        set(n) {
            baseUser.backupLevel = n
        }
    var isNtTeam: Boolean
        get() = baseUser.isNtTeam
        set(b) {
            baseUser.isNtTeam = b
        }
    var clubLogo: String
        get() = baseUser.clubLogo
        set(logo) {
            baseUser.clubLogo = logo
        }
    val dbPwd: String
        get() = ""
    val dbUsername: String
        get() = "sa"

    fun getDbURL(): String {
        return dbURL
    }

    constructor(bu: BaseUser) {
        baseUser = bu
        fillUserInfos()
    }

    private constructor(teamName: String, dbName: String) : this(teamName, dbName, 3, false)
    constructor(teamName: String, dbName: String, backupLevel: Int, isNtTeam: Boolean) {
        baseUser = BaseUser(teamName, dbName, "", backupLevel, isNtTeam)
        fillUserInfos()
    }

    private fun fillUserInfos() {
        dbFolder = Paths.get(UserManager.dbParentFolder, baseUser.dbName).toString()
        dbURL = if (HO.isPortableVersion()) "jdbc:hsqldb:file:" + baseUser.dbName + "/database" else "jdbc:hsqldb:file:$dbFolder/database"
    }

    companion object {
        @JvmStatic
        fun createDefaultUser(): User {
            val id = UserManager.users.size + 1
            val sID = if (id > 1) id.toString() else ""
            return User("user$sID", "db$sID")
        }
    }
}
