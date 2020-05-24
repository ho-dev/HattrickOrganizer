import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import core.HO;

public class HOLauncher {

	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {		
		File file = new File("update.zip");
		if (file.exists()) {
			String dir = file.getAbsolutePath();
			dir = dir.substring(0,dir.length()-10);
			try {
				ZipFile zip = new ZipFile(file);
				unzip(zip,dir);
				file.delete();	
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		HO.main(args);
	}
	
	private static boolean unzip(ZipFile zipFile, String destDir) {
		File file = new File(destDir);
		file.mkdirs();

		try {

			Enumeration<? extends ZipEntry> e = zipFile.entries();

			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				String fileName = destDir + File.separatorChar + entry.getName();

				File f = new File(fileName);
		
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
		
				if (entry.isDirectory()) {
					f.mkdir();
				}
		
				if (!f.exists()) {
					f.createNewFile();
				}
		
				InputStream is = zipFile.getInputStream(entry);
				byte[] buffer = new byte[2048];
		
				if (!f.isDirectory()) {
					FileOutputStream fos = new FileOutputStream(f);
		
					int len = 0;
		
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
		
					fos.flush();
		
					fos.close();
					is.close();
				}

			}
			zipFile.close();
		} catch (Exception e1) {
			return false;
		}
		return true;
	}
}
