package core.db.user

import core.HO
import core.util.OSUtils

import java.util.Collections

object UserManager {

    init {
        initDbParentFolder()
        load()
    }

    var index: Int = 0
    lateinit var users: MutableList<User>
    lateinit var dbParentFolder: String

    fun getDriver(): String = "org.hsqldb.jdbcDriver"
    fun addUser(newUser: User) = users.add(newUser)

    fun isSingleUser(): Boolean = users.size == 1

    fun getCurrentUser(): User = users[index]

    fun load() {
        users = mutableListOf()
        // Load BaseUsers from json file
        val baseUsers: Array<BaseUser> = BaseUser.loadBaseUsers(dbParentFolder)

        if (baseUsers.isEmpty()) {
            // in case xml file does not exist, or it is corrupted and no users have been loaded
            val newUser: User = User.createDefaultUser()
            users.add(newUser)
            save()
        } else {
            for (baseUser in baseUsers) {
                val newUser = User(baseUser)
                users.add(newUser)
            }
        }
    }

    fun save() {
        val baseUsers: List<BaseUser> = users.map { user -> user.baseUser }
        BaseUser.serialize(baseUsers, dbParentFolder)
    }

    private fun initDbParentFolder() {
        dbParentFolder = if (!HO.portableVersion) {
            when (HO.platform) {
                OSUtils.OS.LINUX -> {
                    System.getProperty("user.home") + "/.ho"
                }
                OSUtils.OS.MAC -> {
                    System.getProperty("user.home") + "/Library/Application Support/HO"
                }
                else -> {
                    System.getenv("AppData") + "/HO"
                }
            }
        } else {
            System.getProperty("user.dir")
        }
    }

    fun swapUsers(i1: Int, i2: Int) {
        Collections.swap(users, i1, i2)
        if (index == i1) {
            index = i2
        } else if (index == i2) {
            index = i1
        }
    }
}
