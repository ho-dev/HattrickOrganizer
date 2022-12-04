package core.db.user;

import core.HO;
import core.util.OSUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static core.db.user.BaseUser.loadBaseUsers;
import static core.db.user.BaseUser.serialize;

public class UserManager {

    private final ArrayList<User> users = new ArrayList<>();  // List of users
    private int INDEX = 0;
    private String dbParentFolder;

    public String getDbParentFolder() {return dbParentFolder;}
    public int getINDEX() {return INDEX; }
    public void setINDEX(int _INDEX) { INDEX = _INDEX;}
    public String getDriver() {
        return "org.hsqldb.jdbcDriver";
    }

    /** singleton */
    protected static UserManager m_clInstance;

    /**
     * Get the UserManager singleton instance
     */
    public static UserManager instance() {
        if (m_clInstance == null) {
            m_clInstance = new UserManager();
            m_clInstance.setDBParentFolder();
            m_clInstance.load();
        }
        return m_clInstance;
    }

    public @NotNull ArrayList<User> getAllUser() {
        return users;
    }

    public void addUser(User newUser) {
        users.add(newUser);
    }


    public boolean isSingleUser() { return users.size() == 1;}

    private void load() {

        // Load BaseUsers from json file
        BaseUser[] baseusers = loadBaseUsers(dbParentFolder);

        if (baseusers.length < 1) {
            // in case xml file does not exist or it is corrupted and no users have been loaded
            User newUser = User.createDefaultUser();
		    users.add(newUser);
            save();
        }
        else {
            for (BaseUser _baseUser : baseusers) {
                User newUser = new User(_baseUser);
                users.add(newUser);
            }
        }

    }


    public void save() {
        List<BaseUser> lBaseUsers = users.stream().map(User::getBaseUser).collect(Collectors.toList());
        serialize(lBaseUsers, dbParentFolder);
    }


    public User getCurrentUser() {return getAllUser().get(INDEX);}



    private void setDBParentFolder(){
        if (! HO.isPortableVersion()) {
            if (HO.getPlatform() == OSUtils.OS.LINUX) {
                dbParentFolder = System.getProperty("user.home") + "/.ho";}
            else if (HO.getPlatform() == OSUtils.OS.MAC) {
                dbParentFolder =  System.getProperty("user.home") + "/Library/Application Support/HO";
            }
            else {
                dbParentFolder = System.getenv("AppData") + "/HO";
            }
        }
        else {
            dbParentFolder = System.getProperty("user.dir");
        }
    }

    public void swapUsers(int i1, int i2) {
        Collections.swap(users, i1, i2);
        if ( INDEX == i1){
            INDEX = i2;
        }
        else if ( INDEX == i2){
            INDEX = i1;
        }
    }
}
