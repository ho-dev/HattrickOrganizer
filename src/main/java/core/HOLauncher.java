package core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.regex.Pattern;

public class HOLauncher {

	public static void main(String[] args) {
		String updateFileName = "update.piz";
		boolean updateSuccess = true;
		File file = new File(updateFileName);
		if (file.exists()) {
			String dir = file.getAbsolutePath();
			dir = dir.substring(0, dir.length() - 10);

			File mark_for_deletion = new File(dir + "//update_done.ini");
			if (mark_for_deletion.exists()) {
				// update.zip was not deleted after last update we are doing it now .......
				try {
					Files.delete(Path.of(file.getAbsolutePath()));
					System.out.println("zip file has been deleted");
					try {
						Files.delete(Path.of(mark_for_deletion.getAbsolutePath()));
						System.out.println("mark_for_deletion file has been deleted");
					}
					catch (IOException e) {
						System.err.print("mark_for_deletion file could not be deleted even at launch " + e);
					}
				}
				catch (IOException e) {
					System.err.print("zip file could not be deleted even at launch " + e);
				}
			}
			else {
				// update.zip exists and mark_for_deletion does not => we try to unzip ....
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
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						Files.delete(Path.of(file.getAbsolutePath()));
					}
					catch (IOException e) {
						System.err.print("zip file could not be deleted after update !: " + e);

						// it is not clear why the process does not release properly the update file, we will try to delete it at next launch ....
						try {
							mark_for_deletion.createNewFile();
							System.out.println("mark_for_deletion file has been created, zip file will be deleted at next launch");
						} catch (IOException ioExceptionError) {
							System.err.print("mark_for_deletion file could not be created neither: " + ioExceptionError);
						}
					}
				}
			}
		}
		HO.main(args);
	}

	private static void update(String zipFileName, String zipFileDir) throws RuntimeException {
		final String zipFilePath = zipFileDir + File.separator + zipFileName;

		try (ZipFile zipFile = new ZipFile(zipFilePath)) {
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();
			final int BUFFER_SIZE = 8 * 1024;
			final byte[] bytesIn = new byte[BUFFER_SIZE];
			ZipEntry entry;
			String destPath;

			while (entries.hasMoreElements()) {
				entry = entries.nextElement();

				if (!entry.isDirectory() && isValidDestPath(entry.getName())) {
					destPath = Paths.get(zipFileDir, entry.getName()).toRealPath().toString();
					try (InputStream inputStream = zipFile.getInputStream(entry);
						 OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destPath), BUFFER_SIZE)
					) {
						int data;
						while ((data = inputStream.read(bytesIn)) != -1) {
							outputStream.write(bytesIn, 0, data);
						}
					}
					System.out.println("file : " + entry.getName() + " => " + destPath + " has been unzipped");
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Error unzipping file " + zipFilePath, e);
		}
	}

	private static boolean isValidDestPath(String fileName) {
		final List<String> updateFolders = List.of("\\.\\/prediction/.*", "\\.\\/[^\\/]*\\.jar", "\\.\\/changelog\\.html", "\\.\\/truststore\\.jks");
		Pattern pattern;

		for (String regex : updateFolders) {
			pattern = Pattern.compile(regex, Pattern.MULTILINE);
			if (pattern.matcher(fileName).matches()) {
				return true;
			}
		}
		return false;
	}


}
