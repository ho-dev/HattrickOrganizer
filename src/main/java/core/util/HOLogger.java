package core.util;

import core.db.user.UserManager;
import core.file.ExampleFileFilter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the HO logger
 * 
 * @author Marco Senn
 */
public class HOLogger {

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private static HOLogger clLogger;
	private static File logsFolder;
	private static String logsFolderName;
	public static final int DEBUG = 0;
	public static final int INFORMATION = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	private FileWriter logWriter;
	private int logLevel = INFORMATION;

	/**
	 * Creates a new instance of Logger
	 */
	private HOLogger() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = "HO-" + dateFormat.format(new Date()) + ".log";
		String errorMsg;
		boolean logFolderExist = true;

		logsFolderName = Paths.get(UserManager.instance().getDbParentFolder() , "logs").toString();
		logsFolder = new File(logsFolderName);
		
		if (!logsFolder.exists()) {
			logFolderExist = logsFolder.mkdirs();
			if (!logFolderExist) {
				errorMsg = "Could not initialize the log folder: " + logsFolderName + "\n";
				System.err.println(errorMsg);
			}
		}

		if(logFolderExist){
		try {
			deleteOldLogs(logsFolder);
			File logFile = new File(logsFolder, fileName);

			if (logFile.exists()) {
				if (! logFile.delete()) {System.err.println("Unable to delete " + logFile);}
			}

			logWriter = new FileWriter(logFile);
		} catch (Exception e) {
			errorMsg = "Unable to create logfile: " + logsFolder + "/" + fileName;
			System.err.println(errorMsg);
			e.printStackTrace();
		}
		}
	}

	
	private void deleteOldLogs(File dir){
		ExampleFileFilter filter = new ExampleFileFilter("log");
		filter.setIgnoreDirectories(true);
		File[] files = dir.listFiles(filter);
		for (var file : files) {
			long diff = System.currentTimeMillis() - file.lastModified();
			long days = (diff / (1000 * 60 * 60 * 24));
			if (days > 90)
				if (! file.delete()) {System.err.println("Unable to delete " + file);}
		}
	}
	
	public static HOLogger instance() {
		if (clLogger == null) {
			clLogger = new HOLogger();
		}

		return clLogger;
	}

	public void setLogLevel(int i) {
		logLevel = i;
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void log(Class<?> caller, Object obj) {
		logMessage(caller, obj, DEBUG);
	}

	public void info(Class<?> caller, Object obj) {
		logMessage(caller, obj, INFORMATION);
	}

	public void warning(Class<?> caller, Object obj) {
		logMessage(caller, obj, WARNING);
	}

	public void error(Class<?> caller, Object obj) {
		logMessage(caller, obj, ERROR);
	}

	public void debug(Class<?> caller, Object obj) {
		logMessage(caller, obj, DEBUG);
	}

	public void log(Class<?> caller, Throwable e) {
		logMessage(caller, e.toString(), ERROR);

		for (int i = 0; i < e.getStackTrace().length; i++) {
			StackTraceElement array_element = e.getStackTrace()[i];
			logMessage(caller, array_element.toString(), ERROR);
		}
	}

	private void logMessage(Class<?> caller, Object obj, int level) {

		String msg;
		String text;
		
		if (obj instanceof Throwable) {
			Throwable t = (Throwable) obj;
			text = t.getMessage() + "\n" + ExceptionUtils.getStackTrace(t);
		} else {
			text = String.valueOf(obj);
		}

		msg = switch (level) {
			case DEBUG -> " [Debug]   ";
			case WARNING -> " [Warning] ";
			case ERROR -> " [Error]   ";
			default -> " [Info]    ";
		};

		System.out.println(msg + ((caller != null) ? caller.getSimpleName() : "?") + ": " + text);

		if (level < logLevel) {
			return;
		}

		if (logWriter != null) {
			try {
				Date d = new Date();
				String txt = (sdf.format(d) + msg + ((caller != null) ? caller.getName() : "?") + ": " + text + "\r\n");
				logWriter.write(txt);
				logWriter.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
