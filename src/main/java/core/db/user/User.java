package core.db.user;

import core.HO;
import core.util.HOLogger;
import core.util.OSUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class User{



	private String dbURL;
	private String DbUsername = "sa";
	private String DbPwd = "";
	private String dbFolder;

	public BaseUser getBaseUser() {
		return baseUser;
	}

	private BaseUser baseUser;
//	private String name = "singleUser";

//
//
//    // getters and setters
	public String getDbName() {return baseUser.dbName;}
	public void setDbName(String _dbName) {this.baseUser.dbName = _dbName; }
//	public final String getDriver() {return driver;	}
	public String getDbFolder() {return dbFolder;}
	public final String getTeamName() {return baseUser.teamName;}
	public final void setName(String _teamName) {this.baseUser.teamName = _teamName;}
//	public final String getPwd() {return pwd;}
//	public final String getUser() {return user;}
	public int getBackupLevel() {return baseUser.backupLevel;}
	public boolean isNtTeam() {return baseUser.isNtTeam;}
//	public final void setName(String name) {this.name = name;}
	public final void setBackupLevel(int level) {baseUser.backupLevel = level;}
	public void setNtTeam(boolean _isNtTeam) {baseUser.isNtTeam = _isNtTeam;}

	public String getDbPwd() {
		return DbPwd;
	}

	public String getDbUsername() {
		return DbUsername;
	}

	public final @NotNull String getDbURL() {return dbURL;}

//	public void setURL() {
//		if (HO.isPortableVersion()) dbURL = "jdbc:hsqldb:file:" + dbName + "/database";
//		else dbURL = "jdbc:hsqldb:file:" + dbFolder + "/database";
//	}
//
//

	public User(BaseUser bu){
		baseUser = bu;
		this.fillUserInfos();
	}

	private User(String _teamName, String _dbName) {
		this(_teamName, _dbName, 3, false);
	}

	private User(String _teamName, String _dbName, int _backupLevel, boolean _isNtTeam) {
		baseUser = new BaseUser(_teamName, _dbName, _backupLevel, _isNtTeam);
		this.fillUserInfos();
	}

	public void fillUserInfos(){
		this.dbFolder = Paths.get(UserManager.instance().getDbParentFolder() , this.baseUser.dbName).toString();

		if (HO.isPortableVersion()) this.dbURL = "jdbc:hsqldb:file:" + this.baseUser.dbName + "/database";
		else this.dbURL = "jdbc:hsqldb:file:" + dbFolder + "/database";
	}



	public static User createDefaultUser() {
		int _id = UserManager.instance().getAllUser().size() + 1;
		final String sID = _id > 1 ? String.valueOf(_id) : "";
		return new User( "user" + sID, "db" + sID);
	}

//	public static void addNewUser() {
//		User newUser = createDefaultUser();
//		users.add(newUser);
//	}




//
//
//
//




//		users = new ArrayList<>();
//
//		File file = getUserXMLfile(usersFilename);
//
//		if (file.exists()) {
//			try {
//				Document doc = UpdateHelper.getDocument(file, ENCODING);
//				parseFile(doc.getChildNodes());
//			}
//			catch (Exception e){
//				HOLogger.instance().error(User.class, "error while reading user.xml file  " + e);
//			}
//
//		}
//

//
//		// complete user infos for elements not stored in the XML
//		String _dbname;
//		for (User this_user: users){
//			_dbname = this_user.dbURL.substring(17, this_user.dbURL.length()-9);
//			_dbname =_dbname.replace("\\", "/");
//			String[] splittedFileName = _dbname.split("/");
//			_dbname = splittedFileName[splittedFileName.length-1];
//			this_user.setDbName(_dbname);
//			this_user.dbFolder = Paths.get(dbParentFolder, this_user.dbName).toString();
//		}



//

//
//

//
//	private static void parseFile(NodeList elements) {
//		for (int i = 0; i < elements.getLength(); i++) {
//			if (elements.item(i) instanceof Element) {
//				Element element = (Element) elements.item(i);
//				Text txt = (Text) element.getFirstChild();
//
//				if (txt != null) {
//					if (element.getTagName().equals("HoUsers")) {
//						parseFile(element.getChildNodes());
//					}
//
//					if (element.getTagName().equals("User")) {
//						User tmp = new User();
//						tmp.parseUser(element.getChildNodes());
//						users.add(tmp);
//					}
//				}
//			}
//		}
//	}
//
//	private void parseUser(NodeList elements) {
//		for (int i = 0; i < elements.getLength(); i++) {
//			if (elements.item(i) instanceof Element) {
//				Element element = (Element) elements.item(i);
//				Text txt = (Text) element.getFirstChild();
//
//				if (txt != null) {
//					if (element.getTagName().equals("Name")) {
//						name = txt.getData().trim();
//					}
//
//					if (element.getTagName().equals("Url")) {
//						dbURL = txt.getData().trim();
//					}
//
//					if (element.getTagName().equals("User")) {
//						user = txt.getData().trim();
//					}
//
//					if (element.getTagName().equals("Password")) {
//						pwd = txt.getData().trim();
//					}
//
//					if (element.getTagName().equals("Driver")) {
//						driver = txt.getData().trim();
//					}
//
//					if (element.getTagName().equals("BackupLevel")) {
//						backupLevel = Integer.parseInt(txt.getData().trim());
//					}
//
//					if (element.getTagName().equals("NtTeam")) {
//						isNtTeam = txt.getData().trim().equals("true");
//					}
//				}
//			}
//		}
//	}


}
