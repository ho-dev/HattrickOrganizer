package core;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HOLauncher {

	public static void main(String[] args) {
		String updateFileName = "update.zip";
		boolean updateSuccess=true;
		File file = new File(updateFileName);
		if (file.exists()) {
			String dir = file.getAbsolutePath();
			dir = dir.substring(0, dir.length() - 10);
			try {
				update(updateFileName, dir);
			}
			catch (Exception e) {
				updateSuccess = false;
				System.err.print("update failed !");
				e.printStackTrace();
			}
			if (updateSuccess) {
				if (!file.delete()) System.err.print("zip file could not be deleted after update !");
			}
		}
		HO.main(args);
	}

	private static void update(String zipFile, String _destDir) throws IOException {
		int len;
		Pattern pattern;
		Boolean file_to_be_updated;
		String fileName;
		byte[] buffer = new byte[1024];
		final List<String> updateFolders = List.of(".install4j/.*", "prediction/.*", ".*.jar", ".*changelog.html", ".*HO.exe");
		File destDir = new File(_destDir);
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			if (! zipEntry.isDirectory()) {
				file_to_be_updated = false;
				fileName = zipEntry.getName();
				if (fileName.startsWith("./")) fileName = fileName.substring(2);
				for (String regex : updateFolders) {
					pattern = Pattern.compile(regex, Pattern.MULTILINE);
					pattern.matcher(fileName).matches();
					if (pattern.matcher(fileName).matches()) {
						file_to_be_updated = true;
						break;
					}
				}
				if (file_to_be_updated) {
					File destFile = new File(destDir, fileName);
					File parentDirectory = destFile.getParentFile();
					if (! parentDirectory.exists()) parentDirectory.mkdirs();
					else {
						if (destFile.exists()) destFile.delete();
					}
					FileOutputStream fos = new FileOutputStream(destFile, false);
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
			}
			zipEntry = zis.getNextEntry();
		}
			zis.closeEntry();
			zis.close();
		}

	private static File updateFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

}
