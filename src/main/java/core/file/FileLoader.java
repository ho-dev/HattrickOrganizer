package core.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import core.util.HOLogger;

/**
 * @author cira
 * 
 * This class should be used to load any external file.
 * Other classes using this utility won't need to know if the requested is located inside the
 * HO.jar file, or at the same level of HO.jar in the directory tree.  
 */

public class FileLoader {

	private static FileLoader _instance = null;
	boolean loadFromJar = false;
	
	private FileLoader() {
		//URL testUrl = this.getClass().getClassLoader().getResource("prediction/defaults.xml");
		//if (testUrl!=null) {
		File testFile = new File("prediction/defaults.xml");
		if (testFile.exists()) {
			HOLogger.instance().error(getClass(), "Files will be searched outside the HO.jar");
        } else {
        	loadFromJar = true;
        	HOLogger.instance().error(getClass(), "Files will be searched into the HO.jar");
        }
	}
	
	/**
	 * Static method to be used in order to get an instance of the FileLoader
	 * @return
	 */
	public static FileLoader getInstance() {
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
		if (!loadFromJar) {
			File returnFile = new File(fileName);
			try {
				return new FileInputStream(returnFile);
			} catch (FileNotFoundException e) {
				return null;
			}
		} else {
			return this.getClass().getClassLoader().getResourceAsStream(fileName);
		}
	}
	
	/**
	 * Provides access to the InputStream of the first requested file found in the list.
	 * This can be useful in order to provide one (or more) alternative file(s) to be searched. 
	 * @param fileNames Ordered list of file names to be returned. 
	 * @return the InputStream related to the fileName or <em>null</em> if the file doesn't exist
	 */
	public InputStream getFileInputStream(List<String> fileNames) {
		InputStream returnValue = null;
		for (String fileName : fileNames) {
			returnValue = this.getFileInputStream(fileName);
			if (returnValue!=null) break;
		}
		return returnValue;
	}
	
}
