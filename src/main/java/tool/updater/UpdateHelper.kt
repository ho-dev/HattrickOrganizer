package tool.updater;

import core.net.MyConnector;
import core.util.HOLogger;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class UpdateHelper {

	/**
	 * Creates a new UpdateHelper object.
	 */
	private UpdateHelper() {
	}

	/**
	 * Download contents of a url into a target file.
	 */
	public static boolean download(String urlName, File targetFile) {
		int data;
		try {
			FileOutputStream outStream = new FileOutputStream(targetFile);
			InputStream in = MyConnector.instance().getFileFromWeb(urlName, false);
			BufferedOutputStream out = new BufferedOutputStream(outStream);
			while (true) {
				data = in.read();
				if (data == -1) {
					break;
				}
				out.write(data);
			}
			out.flush();
			out.close();
			in.close();
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			HOLogger.instance().log(UpdateHelper.class,
					"Error downloading from '" + urlName + "': " + e);
			return false;
		}
		return true;
	}
}
