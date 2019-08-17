package core.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import core.util.HOLogger;

/**
 * @author cira
 * 
 * This class should be used to load any external file.
 * Other classes using this utility won't need to know if the requested is located inside the
 * HO.jar file, or at the same level of HO.jar in the directory tree.  
 */

public class FileLoader {
	
	private enum FileLoadingStatus {
		OUTISDE_JAR,
		INSIDE_JAR,
		NOT_FOUND
	}

	private static FileLoader _instance = null;
	Map<String, FileLoadingStatus> fileStatusesCache = null;
	
	private FileLoader() {
		this.fileStatusesCache = new HashMap<String, FileLoadingStatus>();
	}
	
	/**
	 * Static method to be used in order to get an instance of the FileLoader
	 * @return
	 */
	public static FileLoader instance() {
		if (_instance==null) {
			_instance = new FileLoader();
		}
		return _instance;
	}
	
	/**
	 * Provides access to the InputStream of a requested file 
	 * @param fileName The name of the file to be returned
	 * @return the InputStream related to the fileName or <em>null</em> if the file doesn't exist
	 */
	public InputStream getFileInputStream(String fileName) {
		if (fileStatusesCache.get(fileName)==FileLoadingStatus.NOT_FOUND) return null;
		boolean fileUnknown = fileStatusesCache.get(fileName)==null;
		
		HOLogger.instance().setLogLevel(0);
		
		if (fileUnknown || fileStatusesCache.get(fileName)==FileLoadingStatus.OUTISDE_JAR) {
			File returnFile = new File(fileName);
			try {
				InputStream is = new FileInputStream(returnFile);
				if (fileUnknown) {
					fileStatusesCache.put(fileName, FileLoadingStatus.OUTISDE_JAR);
					HOLogger.instance().debug(getClass(), "File will loaded from outside the JAR: " + fileName);
				}
				return is;
			} catch (FileNotFoundException e) {
				if (!fileUnknown) {
					// Well... someting's wrong here. This should never happen. Cache is updates!
					fileStatusesCache.put(fileName, FileLoadingStatus.NOT_FOUND);
					HOLogger.instance().debug(getClass(), "File that was outside the jar will not be searched anymore: " + fileName);
					return null;
				}
				// ...else... it continues
			}
		}
		
		if (fileUnknown || fileStatusesCache.get(fileName)==FileLoadingStatus.INSIDE_JAR) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
			if (is!=null) {
				if (fileUnknown) {
					fileStatusesCache.put(fileName, FileLoadingStatus.INSIDE_JAR);
					HOLogger.instance().debug(getClass(), "File will be loaded from inside the JAR: " + fileName);
				}
				return is;
			} else {
				fileStatusesCache.put(fileName, FileLoadingStatus.NOT_FOUND);
			}
		}
		HOLogger.instance().debug(getClass(), "File will not be searched anymore: " + fileName);
		return null;
		
	}
	
	/**
	 * Provides access to the InputStream of the first requested file found in the list.
	 * This can be useful in order to provide one (or more) alternative file(s) to be searched. 
	 * @param fileNames Ordered list of file names to be returned
	 * @return the InputStream related to the fileName or <em>null</em> if the file doesn't exist
	 */
	public InputStream getFileInputStream(String[] fileNames) {
		InputStream returnValue = null;
		for (String fileName : fileNames) {
			returnValue = this.getFileInputStream(fileName);
			if (returnValue!=null) break;
		}
		return returnValue;
	}
	
}
