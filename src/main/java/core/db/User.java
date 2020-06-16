package core.db;

import core.HO;
import core.util.HOLogger;
import core.util.OSUtils;
import tool.updater.UpdateHelper;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class User {
	// Class variables
	private static List<User> users = null;
	private static final String FILENAME = "user.xml";
	private static final String ENCODING = "UTF-8";
	private static String driver = "org.hsqldb.jdbcDriver";
	private static String dbParentFolder;

	private String dbName;
	private String dbURL;
	private String user = "sa";
	private int backupLevel = 3;
	private boolean isNtTeam = false;
	private String dbFolder;
	public static int INDEX = 0;
	private String name = "singleUser";
	private String pwd = "";


    // getters and setters
	public String getDbName() {return dbName;}
	public void setDbName(String dbName) {this.dbName = dbName; }
	public final String getDriver() {return driver;	}
	public String getDbFolder() {return dbFolder;}
	public final String getTeamName() {return name;}
	public final String getPwd() {return pwd;}
	public final String getUser() {return user;}
	public int getBackupLevel() {return backupLevel;}
	public boolean isNtTeam() {return isNtTeam;}
	public final void setName(String name) {this.name = name;}
	public final void setBackupLevel(int level) {backupLevel = level;}
	public boolean setNtTeam(boolean isNtTeam) {return this.isNtTeam = isNtTeam;}

	public void setURL() {
		if (HO.isPortableVersion()) dbURL = "jdbc:hsqldb:file:" + dbName + "/database";
		else dbURL = "jdbc:hsqldb:file:" + dbFolder + "/database";
	}

	public static String getDbParentFolder() {return dbParentFolder;}

	private static void setDBParentFolder(){
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

	private User() {}

	public boolean isSingleUser() { return users.size() == 1;}

	public static List<User> getAllUser() {
		try {
			if (users == null) load();
		}
		catch (Exception ex) {
			HOLogger.instance().error(User.class, ex);
		}
		return users;
	}

	public static User getCurrentUser() {return getAllUser().get(INDEX);}


	public static void load(){

		users = new ArrayList<>();

		setDBParentFolder(); // this determine the location of the db folder base on os and installation type
		File file = getUserXMLfile(FILENAME);

		if (file.exists()) {
			try {
				Document doc = UpdateHelper.getDocument(file, ENCODING);
				parseFile(doc.getChildNodes());
			}
			catch (Exception e){
				HOLogger.instance().error(User.class, "error while reading user.xml file  " + e);
			}

		}

		if (users.size() < 1) {
		// in case xml file does not exist or it is corrupted and no users have been loaded
		users.add(new User());
		save();
		}

		// complete user infos for elements not stored in the XML
		String _dbname;
		for (User this_user: users){
			_dbname = this_user.dbURL.substring(17, this_user.dbURL.length()-9);
			this_user.setDbName(_dbname);
			this_user.dbFolder = Paths.get(dbParentFolder, this_user.dbName).toString();
		}
	}

	public static void save() {
		try {
			File file = getUserXMLfile(FILENAME);
			File parentFolder = file.getParentFile();
			Boolean parentFolderCreated;

			if(! parentFolder.exists()) parentFolderCreated = parentFolder.mkdirs();
			else parentFolderCreated = true;
			file.createNewFile();

			if (! parentFolderCreated) {
				HOLogger.instance().error(DBManager.class, "Could not initialize the xml file: " +  file);
			}

		PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODING));

			writer.println("<?xml version='1.0' encoding='" + ENCODING + "' ?>");
			writer.println("<HoUsers>");

			for (int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				writer.println(" <User>");
				writer.println("   <Name><![CDATA[" + user.name + "]]></Name>");
				writer.println("   <Url><![CDATA[" + user.getDbURL() + "]]></Url>");
				writer.println("   <User><![CDATA[" + user.user + "]]></User>");
				writer.println("   <Password><![CDATA[" + user.pwd + "]]></Password>");
				writer.println("   <Driver><![CDATA[" + user.driver + "]]></Driver>");
				writer.println("   <BackupLevel>" + user.backupLevel + "</BackupLevel>");
				writer.println("   <NtTeam><![CDATA[" + user.isNtTeam + "]]></NtTeam>>");
				writer.println(" </User>");
			}

			writer.println("</HoUsers>");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			HOLogger.instance().log(User.class, e);
		}
	}

	public final String getDbURL() {
		if (dbURL == null) {
			dbName = "db";
			setURL();
		}
		return dbURL;
	}

	public static User addNewUser() {
		User newUser = new User();
		newUser.setName("user" + (users.size() + 1));
		newUser.dbName = "db" + (users.size() + 1);
		newUser.setURL();
		users.add(newUser);
		return newUser;
	}

	private static File getUserXMLfile(String fileName) {
		Path filePath = Paths.get(getDbParentFolder(), fileName);
		return new File(String.valueOf(filePath));
	}

	private static void parseFile(NodeList elements) {
		for (int i = 0; i < elements.getLength(); i++) {
			if (elements.item(i) instanceof Element) {
				Element element = (Element) elements.item(i);
				Text txt = (Text) element.getFirstChild();

				if (txt != null) {
					if (element.getTagName().equals("HoUsers")) {
						parseFile(element.getChildNodes());
					}

					if (element.getTagName().equals("User")) {
						User tmp = new User();
						tmp.parseUser(element.getChildNodes());
						users.add(tmp);
					}
				}
			}
		}
	}

	private void parseUser(NodeList elements) {
		for (int i = 0; i < elements.getLength(); i++) {
			if (elements.item(i) instanceof Element) {
				Element element = (Element) elements.item(i);
				Text txt = (Text) element.getFirstChild();

				if (txt != null) {
					if (element.getTagName().equals("Name")) {
						name = txt.getData().trim();
					}

					if (element.getTagName().equals("Url")) {
						dbURL = txt.getData().trim();
					}

					if (element.getTagName().equals("User")) {
						user = txt.getData().trim();
					}

					if (element.getTagName().equals("Password")) {
						pwd = txt.getData().trim();
					}

					if (element.getTagName().equals("Driver")) {
						driver = txt.getData().trim();
					}

					if (element.getTagName().equals("BackupLevel")) {
						backupLevel = Integer.parseInt(txt.getData().trim());
					}

					if (element.getTagName().equals("NtTeam")) {
						isNtTeam = txt.getData().trim().equals("true");
					}
				}
			}
		}
	}
}
