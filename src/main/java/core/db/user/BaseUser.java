package core.db.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.HO;
import core.util.HOLogger;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class BaseUser {
    protected String teamName;
    protected String dbName;
    protected String clubLogo;
    protected int backupLevel;
    protected boolean isNtTeam;

    public BaseUser(String _teamName, String _dbName, String _clubLogo, int _backupLevel, boolean _isNtTeam) {
        this.teamName = _teamName;
        this.dbName = _dbName;
        this.backupLevel = _backupLevel;
        this.isNtTeam = _isNtTeam;
        this.clubLogo = _clubLogo;
    }

    public static void serialize(List<BaseUser> baseUsers, String jsonFolder) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = Files.newBufferedWriter(Paths.get(jsonFolder, "users.json"));
            gson.toJson(baseUsers, writer);
            writer.close();
        } catch (Exception ex) {
            HOLogger.instance().error(HO.class, "users.json file file could not be saved: \n" + ex);
        }
    }

    public static BaseUser[] loadBaseUsers(String jsonFolder) {
        BaseUser[] baseUsers;
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(jsonFolder, "users.json"));
            baseUsers = gson.fromJson(reader, BaseUser[].class);
            reader.close();
        } catch (Exception ex) {
            HOLogger.instance().info(HO.class, "users.json file not found => a new one will be created");
            baseUsers = new BaseUser[]{};
        }
        return baseUsers;
    }

}
