// %4175459228:de.hattrickorganizer.tools.updater%
package tool.updater;

import core.net.MyConnector;
import core.util.HOLogger;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UpdateHelper {

	/**
	 * Creates a new UpdateHelper object.
	 */
	private UpdateHelper() {
	}

	public static Document getDocument(File file) throws ParserConfigurationException, IOException,
			SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(file);
	}

	public static Document getDocument(File file, String encoding) throws SAXException,
			IOException, ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputStreamReader in = new InputStreamReader(new FileInputStream(file), encoding);
		BufferedReader reader = new BufferedReader(in);
		InputSource input = new InputSource(reader);
		return builder.parse(input);
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
