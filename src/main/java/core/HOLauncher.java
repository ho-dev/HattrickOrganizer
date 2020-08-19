package core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.regex.Pattern;

public class HOLauncher {

	public static void main(String[] args) {

		File f = new File(".");

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
				try {
					System.err.print("Trying to delete  " + file.toPath());
					Files.delete(file.toPath());
				} catch (IOException e) {
					System.err.print("zip file could not be deleted after update !: " + e );
				}
			}
		}
		System.err.print("HOLauncher calls HO: " + Arrays.toString(args)); // TODO: to be deleted
		HO.main(args);
	}

	private static void update(String zipFile, String _destDir) throws IOException {
		int len;
		Pattern pattern;
		Boolean file_to_be_updated;
		String fileName;
		byte[] buffer = new byte[1024];
		final List<String> updateFolders = List.of("\\.\\/prediction/.*", "\\.\\/[^\\/]*\\.jar", "\\.\\/changelog\\.html");
		File destDir = new File(_destDir);
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			System.err.print("zip entry: " + zipEntry.toString()); // TODO: to be deleted
			if (! zipEntry.isDirectory()) {
				file_to_be_updated = false;
				fileName = zipEntry.getName();
				for (String regex : updateFolders) {
					pattern = Pattern.compile(regex, Pattern.MULTILINE);
					if (pattern.matcher(fileName).matches()) {
						file_to_be_updated = true;
						break;
					}
				}
				if (file_to_be_updated) {
					System.err.print("zip entry updated: " + zipEntry.toString()); // TODO: to be deleted
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
