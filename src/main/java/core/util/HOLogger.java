// %3619247323:de.hattrickorganizer.tools%
package core.util;

import core.file.ExampleFileFilter;

import java.io.File;
import java.io.FileWriter;
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
		String dirName = "logs";
		String fileName = "HO-" + dateFormat.format(new Date()) + ".log";
		try {
			File dir = new File(dirName);
			dir.mkdirs();

			deleteOldLogs(dir);
			File logFile = new File(dir, fileName);

			if (logFile.exists()) {
				logFile.delete();
			}

			logWriter = new FileWriter(logFile);
		} catch (Exception e) {
			String msg = "Unable to create logfile: " + dirName + "/" + fileName;
			System.err.println(msg);
			e.printStackTrace();
		}
	}

	
	private void deleteOldLogs(File dir){
		ExampleFileFilter filter = new ExampleFileFilter("log");
		 filter.setIgnoreDirectories(true);
		 File[] files = dir.listFiles(filter);
		 for (int i = 0; i < files.length; i++) {
			 long diff = System.currentTimeMillis() - files[i].lastModified();
			 long days = (diff / (1000 * 60 * 60 * 24));
			 if( days > 90)
				 files[i].delete(); 
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

		switch (level) {
		case DEBUG:
			msg = " [Debug]   ";
			break;
		case WARNING:
			msg = " [Warning] ";
			break;
		case ERROR:
			msg = " [Error]   ";
			break;
		default:
			msg = " [Info]    ";
		}

		if (level < logLevel) {
			return;
		}

		System.out.println(msg + ((caller != null) ? caller.getSimpleName() : "?") + ": " + text);

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

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		try {
			logWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
