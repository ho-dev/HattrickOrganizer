package core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class HOLauncher {

	public static void main(String[] args) {
		String updateFileName = "update.piz";
		boolean updateSuccess = true;
		File file = new File(updateFileName);
		if (file.exists()) {
			var absolutePath = file.getAbsolutePath();
			var dir = new File(absolutePath).getParent();
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

	private static void update(String zipFileName, String zipFileDir) throws Exception {
		//////////////////////////////////////////////////////////////////////////////////
		// 1. backup installation dir (for rollback) -> backup/hox.y.n
		var dontDeletePaths = new ArrayList<Path>();
		var deletePaths = new ArrayList<Path>();
		var backupDir = new File(zipFileDir + File.separator + "backup");
		var backupPath = backupDir.toPath();
		var zipDir = new File(zipFileDir);
		backupDir.mkdirs();
		var backupfilename = backupDir.getPath() + File.separator + "HO" + "-" + HO.getVersionString() + ".zip";
		var backupFile = new File(backupfilename);
		var zOut = new ZipOutputStream(new FileOutputStream(backupFile));
		zOut.setMethod(ZipOutputStream.DEFLATED);
		zOut.setLevel(5);
		final int bufLength = 8*1024;
		byte[] buffer = new byte[bufLength];

		try (Stream<Path> filepath = Files.walk(Paths.get("."))) {
			filepath.forEach(p-> {
				try {
					var file = p.toFile();
					var parent = p.toAbsolutePath().normalize().getParent();
					if (file.getName().equals("database.data")){
						// register as dontDelete path
						dontDeletePaths.add(parent);
					}
					if (!file.getName().equals(zipFileName) &&
							!file.isDirectory() &&
							!pathIsPartOfDirectory(p,backupPath)
					) {
						deletePaths.add(p);
						FileInputStream tFINS = new FileInputStream(file);
						int readReturn;
						zOut.putNextEntry(new ZipEntry(file.getPath()));
						do {
							readReturn = tFINS.read(buffer);
							if (readReturn != -1) {
								zOut.write(buffer, 0, readReturn);
							}
						} while (readReturn != -1);
						zOut.closeEntry();
						tFINS.close();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
		catch (IOException e) {
			throw new IOException("Directory Not Present!");
		}
		zOut.finish();
		zOut.close();

		//////////////////////////////////////////////////////////////////////////////////
		// 2. Delete files except
		//    - database folders
		//    - img directory
		//    - logs directory
		//    - users.json
		//    - update.piz
		dontDeletePaths.add(new File(zipFileDir+ File.separator + "img").toPath());
		dontDeletePaths.add(new File(zipFileDir+ File.separator + "logs").toPath());
		dontDeletePaths.add(backupPath);
		for ( var p : deletePaths) {
			var file = p.toFile();
			if (!pathIsPartOfOneOfTheseDirectories(p, dontDeletePaths) &&
					!file.getName().equals(zipFileName) &&
					!file.getName().equals("users.json") &&
					!file.isDirectory()
			) {
				var suc = file.delete();
				if (!suc) {
					System.out.println("cannot delete file " + file.getName());
				}
			}
		}

		//////////////////////////////////////////////////////////////////////////////////
		// 3. extract update.piz
		try (ZipFile zipFile = new ZipFile(zipFileName)) {
			String destDirStr = zipDir.getAbsolutePath() + File.separatorChar;
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				String fileName = destDirStr + entry.getName();
				//saveEntry(zipFile, entry, fileName);
				File f = new File(fileName.replace('\\', '/'));
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				if (entry.isDirectory()) {
					if (!f.exists()) {
						f.mkdir();
					}
					continue;
				}

				InputStream is = zipFile.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(f);
				int len;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				fos.flush();
				fos.close();
				is.close();
			}
		}
	}

	private static boolean pathIsPartOfOneOfTheseDirectories(Path path, List<Path> paths) {
		for (var p : paths) {
			if (pathIsPartOfDirectory(path, p)) {
				return true;
			}
		}
		return false;
	}
	private static boolean pathIsPartOfDirectory (Path p, Path dir){
		return p.toAbsolutePath().normalize().startsWith(dir);
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
