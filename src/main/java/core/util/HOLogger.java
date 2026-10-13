package core.util;

import core.file.ExampleFileFilter;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is the HO logger
 *
 * @author Marco Senn
 */
public class HOLogger {

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static HOLogger clLogger;
	private static String logsFolderName;
	public static final int DEBUG = 0;
	public static final int INFORMATION = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	private FileWriter logWriter;
	private int logLevel = INFORMATION;

    private record LogEntry(int level, String message) {
    }
    private final List<LogEntry> bufferedLogs = new ArrayList<>();

	/**
	 * Creates a new instance of Logger
	 */
	private HOLogger() {
	}

    public synchronized void initializeFileLogging(Path logsPath) {
        if (logWriter != null) {
            throw new IllegalStateException("File logging is already initialized");
        }

        String errorMsg;
        boolean logFolderExist = true;

        logsFolderName = logsPath.toString();
        File logsFolder = new File(logsFolderName);

        if (!logsFolder.exists()) {
            logFolderExist = logsFolder.mkdirs();
            if (!logFolderExist) {
                errorMsg = "Could not initialize the log folder: " + logsFolderName + "\n";
                System.err.println(errorMsg);
            }
        }

        if (logFolderExist) {
            String fileName = "";
            try {
                deleteOldLogs(logsFolder);
                File logFile;
                do{
                    fileName = "HO-" + HODateTime.toEpochSecond(HODateTime.now()) + ".log";
                    logFile = new File(logsFolder, fileName);
                } while (logFile.exists());
                logWriter = new FileWriter(logFile);
                flushBufferedLogs();
            } catch (Exception e) {
                errorMsg = "Unable to create logfile: " + logsFolder + "/" + fileName;
                System.err.println(errorMsg);
                e.printStackTrace();
            }
        }
    }

	private void deleteOldLogs(File dir) {
		ExampleFileFilter filter = new ExampleFileFilter("log");
		filter.setIgnoreDirectories(true);
		File[] files = dir.listFiles(filter);
		if (files != null) {
			for (var file : files) {
				long diff = System.currentTimeMillis() - file.lastModified();
				long days = (diff / (1000 * 60 * 60 * 24));
				if (days > 90)
					if (!file.delete()) {
						System.err.println("Unable to delete " + file);
					}
			}
		}
	}

	public static HOLogger instance() {
		if (clLogger == null) {
			clLogger = new HOLogger();
		}

		return clLogger;
	}

	public static String getLogsFolderName(){ return logsFolderName;}

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

	private synchronized void logMessage(Class<?> caller, Object obj, int level) {
		String msg;
		String text;

		if (obj instanceof Throwable t) {
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

        final Date d = new Date();
        final String txt = (sdf.format(d) + msg + ((caller != null) ? caller.getName() : "?") + ": " + text + "\r\n");

        final var logEntry = new LogEntry(level, txt);
        if (logWriter == null) {
            bufferedLogs.add(logEntry);
            return;
        }

        writeToFile(logEntry);
	}

    private void writeToFile(LogEntry logEntry) {
        if (logWriter == null) {
            throw new IllegalStateException("logWriter should have been initialized");
        }

        if (logEntry.level() < logLevel) {
            return;
        }

        try {
            logWriter.write(logEntry.message());
            logWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flushBufferedLogs() {
        bufferedLogs.forEach(this::writeToFile);
        bufferedLogs.clear();
    }
}
