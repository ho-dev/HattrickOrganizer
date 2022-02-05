package core.db.backup;

import core.db.user.User;
import core.db.user.UserManager;
import core.file.ExampleFileFilter;
import core.util.HOLogger;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * HSQL DB zipper
 * @author Thorsten Dietz
 */
public class BackupHelper {

	// zip and delete db
	public static void backup(File dbDirectory) {
		Calendar now = Calendar.getInstance();

		if (!dbDirectory.exists()) {return;}

		File[] filesToBackup = getFilesToBackup(dbDirectory);
		if (filesToBackup.length == 0) {return;}

		HOZip zOut;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			zOut = new HOZip(dbDirectory + File.separator + "db_" + UserManager.instance().getCurrentUser().getTeamName()
					+ "-" + sdf.format(new Date()) + ".zip");

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
	 */
	private static void deleteOldFiles(File dbDirectory) {
		File toDelete = null;
		ExampleFileFilter filter = new ExampleFileFilter("zip");
		filter.setIgnoreDirectories(true);
		File[] files = dbDirectory.listFiles(filter);
		if (files.length > UserManager.instance().getCurrentUser().getBackupLevel()) {
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
		return dbDirectory.listFiles(file -> {
			String name = file.getName();
			return (name.endsWith(".script") ||
					name.endsWith(".data") ||
					name.endsWith(".backup") ||
					name.endsWith(".log") ||
					name.endsWith(".properties"));
		});
	}

}
