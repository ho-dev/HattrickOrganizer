package core.file;

import core.util.HOLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


/**
 * Utility class for handling ZipFiles.
 * 
 */
public class ZipHelper {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private ZipHelper() {
		// do nothing
	}

	/**
	 * Extracts the file with the given entryName to the specified directory. If
	 * not existing, the destination directory is created.
	 * 
	 * @param zipFile
	 *            the zip file to extract a file from.
	 * @param entryName
	 *            the name of the entry to extract.
	 * @param destDir
	 *            the destination directory.
	 * @throws IOException
	 *             if an io error occurs while extracting.
	 */
	public static void extractFile(ZipFile zipFile, String entryName, String destDir) throws IOException {

		File file = new File(destDir);
		file.mkdirs();
		Enumeration<? extends ZipEntry> e = zipFile.entries();

		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			String fileName = destDir + File.separatorChar + entry.getName();
			if (fileName.toUpperCase(java.util.Locale.ENGLISH).endsWith(
					entryName.toUpperCase(java.util.Locale.ENGLISH))) {
				saveEntry(zipFile, entry, fileName);
			}
		}
	}

	/**
	 * Closes a zip file. This method is null safe, if the given zipFile is
	 * null, this methos does nothing. The method will not throw an exception.
	 * If an exception occurs, it will be logged by HOLogger.
	 * 
	 * @param zipFile
	 *            the zip file to close.
	 */
	public static void close(ZipFile zipFile) {
		if (zipFile != null) {
			try {
				zipFile.close();
			} catch (Exception ex) {
				HOLogger.instance().error(ZipHelper.class, ex);
			}
		}
	}

	/**
	 * Extracts a zip file to a directory. If the destination directory does not
	 * exist, it will be created.
	 * 
	 * @param file
	 *            the file to extract.
	 * @param destDir
	 *            the destination directory.
	 * @throws ZipException
	 *             if a ZIP error has occurred
	 * @throws IOException
	 *             if an I/O error has occurred
	 */
	public static void unzip(File file, File destDir) throws ZipException, IOException {
		destDir.mkdirs();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			String destDirStr = destDir.getAbsolutePath() + File.separatorChar;
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				String fileName = destDirStr + entry.getName();
				if (HOLogger.instance().getLogLevel() == HOLogger.DEBUG) {
					HOLogger.instance().debug(ZipHelper.class,
							zipFile.getName() + ": " + "extracting " + entry.getName() + " to " + fileName);
				}
				saveEntry(zipFile, entry, fileName);
			}
		} finally {
			close(zipFile);
		}
	}

	private static void saveEntry(ZipFile zipFile, ZipEntry entry, String fileName) throws IOException,
			FileNotFoundException {
		File f = new File(getSystemIndependentPath(fileName));

		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		if (entry.isDirectory()) {
			if (!f.exists()) {
				f.mkdir();
			}
			return;
		}

		InputStream is = zipFile.getInputStream(entry);
		byte[] buffer = new byte[2048];

		FileOutputStream fos = new FileOutputStream(f);
		int len = 0;

		while ((len = is.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
		}

		fos.flush();
		fos.close();
		is.close();
	}

	private static String getSystemIndependentPath(String str) {
		return str.replace('\\', '/');
	}
}
