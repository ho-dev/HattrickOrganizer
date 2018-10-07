// %2388812641:de.hattrickorganizer.tools.backup%
package core.db.backup;

import core.db.User;
import core.file.ExampleFileFilter;
import core.util.HOLogger;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;

/**
 * HSQL DB zipper
 * 
 * @author Thorsten Dietz
 */
public class BackupHelper {

	/**
	 * zip and delete db
	 * 
	 * @param dbDirectory
	 */
	public static void backup(File dbDirectory) {
		Calendar now = Calendar.getInstance();

		if (!dbDirectory.exists()) {
			return;
		}

		File[] filesToBackup = getFilesToBackup(dbDirectory);
		if (filesToBackup.length == 0) {
			return;
		}

		HOZip zOut = null;
		try {
			zOut = new HOZip(dbDirectory + File.separator + "db_" + User.getCurrentUser().getName()
					+ "-" + now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DAY_OF_MONTH) + ".zip");

			for (File file : filesToBackup) {
				zOut.addFile(file);
			}

			zOut.closeArchive();
		} catch (Exception e) {
			HOLogger.instance().log(BackupHelper.class, e);
		}

		deleteOldFiles(dbDirectory);
	}

	/**
	 * delete old zip files, which are out of backuplevel
	 * 
	 * @param dbDirectory
	 *            directory of the database
	 */
	private static void deleteOldFiles(File dbDirectory) {
		File toDelete = null;
		ExampleFileFilter filter = new ExampleFileFilter("zip");
		filter.setIgnoreDirectories(true);
		File[] files = dbDirectory.listFiles(filter);
		if (files.length > User.getCurrentUser().getBackupLevel()) {
			for (int i = 0; i < files.length; i++) {
				if (i == 0
						|| (toDelete != null && toDelete.lastModified() > files[i].lastModified())) {
					toDelete = files[i];
				}
			}
			if (toDelete != null)
				toDelete.delete();
		}
	}

	private static File[] getFilesToBackup(File dbDirectory) {
		return dbDirectory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				String name = file.getName();
				return (name.endsWith(".script") || name.endsWith(".data")
						|| name.endsWith(".backup") || name.endsWith(".properties"));
			}
		});
	}

}
