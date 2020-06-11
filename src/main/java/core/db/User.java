// %1916127255:de.hattrickorganizer.model%
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
	private static List<User> users = null;
	private static final String DEFAULT_FILE = "user.xml.default";
	private static final String FILENAME = "user.xml";
	private static final String ENCODING = "UTF-8";
	public static int INDEX = 0;
	private String driver = "org.hsqldb.jdbcDriver";
	private String name = "singleUser";
	private String pwd = "";
	private String dbURL;
	private String user = "sa";
	private int backupLevel = 3;
	private boolean isNtTeam = false;
	private static String dbParentFolder;
	private String dbFolder;

	private User() {}

	public static List<User> getAllUser() {
		try {
			if (users == null) {
				load();
			}
		} catch (Exception ex) {
			HOLogger.instance().log(User.class, ex);
		}

		return users;
	}

	public static User getCurrentUser() {
		try {
			if (users == null) {
				load();
			}
		} catch (Exception ex) {
			HOLogger.instance().log(User.class, ex);
		}

		return users.get(INDEX);
	}

	public final String getDriver() {
		return driver;
	}

	public String getDbFolder() {
		if (dbFolder == null) {
			String _dbURL = this.getDbURL();
			dbFolder = _dbURL.substring(17, _dbURL.length()-8);
		}
		return dbFolder;
	}

	public final String getName() {
		return name;
	}

	public final String getPwd() {
		return pwd;
	}

	public boolean isSingleUser() {
		return (users.size() == 1) ? true : false;
	}

	public static void load() throws Exception {
		
		// If we don't find user.xml, look for info in user.xml.default before using default values.
	
		Boolean save = false;
		
		File file = getUserXMLfile(FILENAME);
		users = new ArrayList<>();

		if (!file.exists()) {
			file = getUserXMLfile(DEFAULT_FILE);
			save = true;
		}
		
		if (file.exists()) {
			Document doc = UpdateHelper.getDocument(file, ENCODING);
			parseFile(doc.getChildNodes());
		   if (save) {save();}}
		else {
			users.add(new User());
			save();
		}
		// add default user, if there was none loaded (e.g. corrupt user.xml)
		if (users.size() < 1) {
			users.add(new User());
			save();
		}
	}

	public String getDBName() {
		try {
			return dbURL.substring(dbURL.indexOf("file") + 5, dbURL.indexOf("database") - 1);
		} catch (RuntimeException e) {
			return "db";
		}
	}

	public static void save() {
		try {
			File file = getUserXMLfile(FILENAME);
			File parentFolder = file.getParentFile();
			Boolean parentFolderCreated = false;

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
			setURL("db");
		}
		return dbURL;
	}


	public static String getDbParentFolder() {
		if (dbParentFolder == null) determineDBpath();
		return dbParentFolder;
	}

	private static void determineDBpath(){
			if (! HO.isPortableVersion())
			{
				if (HO.getPlatform() == OSUtils.OS.LINUX) {
					dbParentFolder = System.getProperty("user.home") + "/.ho";}
				else if (HO.getPlatform() == OSUtils.OS.MAC) {
					dbParentFolder =  System.getProperty("user.home") + "/Library/ApplicationSupport/HO";}
				else {
					dbParentFolder = System.getenv("AppData") + "/HO";
				}
			}
			else {
				dbParentFolder = System.getProperty("user.dir");
			}

		}

	public final String getUser() {
		return user;
	}

	public int getBackupLevel() {
		return backupLevel;
	}

	public boolean isNtTeam() {
		return isNtTeam;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setBackupLevel(int level) {
		backupLevel = level;
	}

	public final void setURL(String dbName) {
		dbFolder = Paths.get(getDbParentFolder(), dbName).toString();
		dbURL = "jdbc:hsqldb:file:" + dbFolder + "/database";
	}

	public boolean setNtTeam(boolean isNtTeam) {
		return this.isNtTeam = isNtTeam;
	}


	public static User addNewUser() {
		User newUser = new User();
		newUser.setName("user" + (users.size() + 1));
		newUser.setURL("db" + (users.size() + 1));
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
