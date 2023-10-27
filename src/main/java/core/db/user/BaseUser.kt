package core.db.user

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import core.HO
import core.util.HOLogger
import java.io.Reader
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Paths

class BaseUser(var teamName: String, var dbName: String, var clubLogo: String,
               var backupLevel: Int, var isNtTeam: Boolean) {
    companion object {
        fun serialize(baseUsers: List<BaseUser>, jsonFolder: String) {
            try {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val writer: Writer = Files.newBufferedWriter(Paths.get(jsonFolder, "users.json"))
                gson.toJson(baseUsers, writer)
                writer.close()
            } catch (ex: Exception) {
                HOLogger.instance().error(HO::class.java, "users.json file file could not be saved: \n$ex")
            }
        }

        fun loadBaseUsers(jsonFolder: String): Array<BaseUser> {
            var baseUsers: Array<BaseUser>
            try {
                val gson = Gson()
                val reader: Reader = Files.newBufferedReader(Paths.get(jsonFolder, "users.json"))
                baseUsers = gson.fromJson(reader, Array<BaseUser>::class.java)
                reader.close()
            } catch (ex: Exception) {
                HOLogger.instance().info(HO::class.java, "users.json file not found => a new one will be created")
                baseUsers = arrayOf()
            }
            return baseUsers
        }
    }
}
