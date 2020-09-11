package core.db.user;

import core.HO;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Paths;


public class User{

	private String dbURL;
	private String DbUsername = "sa";
	private String DbPwd = "";
	private String dbFolder;
	public BaseUser getBaseUser() {
		return baseUser;
	}
	private BaseUser baseUser;

//    // getters and setters
	public String getDbName() {return baseUser.dbName;}
	public void setDbName(String _dbName) {this.baseUser.dbName = _dbName; }
	public String getDbFolder() {return dbFolder;}
	public final String getTeamName() {return baseUser.teamName;}
	public final void setName(String _teamName) {this.baseUser.teamName = _teamName;}
	public int getBackupLevel() {return baseUser.backupLevel;}
	public boolean isNtTeam() {return baseUser.isNtTeam;}
	public final void setBackupLevel(int level) {baseUser.backupLevel = level;}
	public void setNtTeam(boolean _isNtTeam) {baseUser.isNtTeam = _isNtTeam;}

	public String getDbPwd() {
		return DbPwd;
	}

	public String getDbUsername() {
		return DbUsername;
	}

	public final @NotNull String getDbURL() {return dbURL;}

	public User(BaseUser bu){
		baseUser = bu;
		this.fillUserInfos();
	}

	private User(String _teamName, String _dbName) {
		this(_teamName, _dbName, 3, false);
	}

	public User(String _teamName, String _dbName, int _backupLevel, boolean _isNtTeam) {
		baseUser = new BaseUser(_teamName, _dbName, _backupLevel, _isNtTeam);
		this.fillUserInfos();
	}

	private void fillUserInfos(){
		this.dbFolder = Paths.get(UserManager.instance().getDbParentFolder() , this.baseUser.dbName).toString();

		if (HO.isPortableVersion()) this.dbURL = "jdbc:hsqldb:file:" + this.baseUser.dbName + "/database";
		else this.dbURL = "jdbc:hsqldb:file:" + dbFolder + "/database";
	}


	public static User createDefaultUser() {
		int _id = UserManager.instance().getAllUser().size() + 1;
		final String sID = _id > 1 ? String.valueOf(_id) : "";
		return new User( "user" + sID, "db" + sID);
	}




}
