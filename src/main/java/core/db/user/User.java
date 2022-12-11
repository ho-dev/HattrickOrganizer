package core.db.user;

import core.HO;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Paths;


public class User{

	private String dbURL;
	private String dbFolder;
	public BaseUser getBaseUser() {
		return baseUser;
	}
	private final BaseUser baseUser;

//    // getters and setters
	public String getDbName() {return baseUser.dbName;}
	public void setDbName(String _dbName) {
		this.baseUser.dbName = _dbName;
		fillUserInfos();
	}
	public String getDbFolder() {return dbFolder;}
	public final String getTeamName() {return baseUser.teamName;}
	public final void setTeamName(String _teamName) {this.baseUser.teamName = _teamName;}
	public int getNumberOfBackups() {return baseUser.backupLevel;}
	public void setNumberOfBackups(int n) { baseUser.backupLevel = n;}
	public boolean isNtTeam() {return baseUser.isNtTeam;}
	public void setIsNtTeam(boolean b){baseUser.isNtTeam=b;}
	public void setClubLogo(String logo){baseUser.clubLogo=logo;}
	public String getClubLogo(){return baseUser.clubLogo;}

	public String getDbPwd() {
		return "";
	}
	public String getDbUsername() {
		return "sa";
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
		baseUser = new BaseUser(_teamName, _dbName, "", _backupLevel, _isNtTeam);
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
