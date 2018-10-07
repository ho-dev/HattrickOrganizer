package core.net.test;

import core.file.xml.XMLManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.swing.JTextArea;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Simple class to run a connection test with debug information.
 *
 * @author aik
 */
public class ConnTest {

	private static final long serialVersionUID = 8532069037729446669L;
	final static private String LS = System.getProperty("line.separator");
	final static private String URL_NORMAL = "http://ho1.sourceforge.net/onlinefiles/version.htm";
	final static private String URL_SERVERS = "http://www.hattrick.org/common/chppxml.axd?file=servers";
	//final static private String URL_HT = "http://plugins.hattrickorganizer.net/version.htm";
	final static private int steps = 3;
	final static public float VERSION = 0.1f;

	/**
	 * Constructor.
	 */
	public ConnTest() {
		Properties systemProperties = System.getProperties();
		systemProperties.setProperty("sun.net.client.defaultConnectTimeout","10000");
		systemProperties.setProperty("sun.net.client.defaultReadTimeout","10000");
		ConnTestFrame frame = new ConnTestFrame(this);
		frame.setVisible(true);
	}

	public void start(final JTextArea log) {
		log.append("Starting "+steps+" tests..." + LS);
		new Thread() {
			@Override
			public void run() {
				printSystemInfos(log);
				testNormalUrl(log);
				testHtStartUrl(log);
				testHtLogin(log);
				log.append("Finished!" + LS);
			}
		}.start();
	}

	/**
	 * Test connection the login at HT.
	 */
	static private void testHtLogin(JTextArea log) {
		try {
			log.append("Step 3/"+steps+" - testing login at Hattrick..." + LS);
			log.append("Still TODO" + LS);
		} catch (Exception e) {
			log.append("ERROR!" + LS);
			if (log != null) log.append("Error details: " + e + LS);
		}
	}

	/**
	 * Test connection to a normal web resource.
	 */
	static private void testHtStartUrl(JTextArea log) {
		try {
			log.append("Step 2/"+steps+" - testing initial Hattrick connection..." + LS);
			final URL url = new URL(URL_SERVERS);
			final HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setRequestMethod("GET");
			httpurlconnection.connect();
			InputStream is = httpurlconnection.getInputStream();
			final BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			boolean found = false;
			while ((line = br.readLine()) != null) {
				found = true;
				//log.append("\t >>> " + line + LS);
				sb.append(line);
			}
			br.close();
			if (found) {
				Document doc = null;
		        doc = XMLManager.parseString(sb.toString());
		        try {
		        	String htserver = parseDetails(doc, log);
		        	log.append("Test OK, Recommended HT server: " + htserver + LS);
		        } catch (Exception e2) {
		        	log.append("ERROR!" + LS);
		        	log.append("Error details: " + e2 + LS);
		        }
			} else {
				log.append("Test failed - no data!" + LS);
			}
		} catch (Exception e) {
			log.append("ERROR!" + LS);
			if (log != null) log.append("Error details: " + e + LS);
		}
	}

	/**
	 * Test connection to a normal web resource.
	 */
	static private void testNormalUrl(JTextArea log) {
		try {
			log.append("Step 1/"+steps+" - testing normal connection..." + LS);
			final URL url = new URL(URL_NORMAL);
			final HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setRequestMethod("GET");
			httpurlconnection.connect();
			InputStream is = httpurlconnection.getInputStream();
			final BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = null;
			boolean found = false;
			while ((line = br.readLine()) != null) {
				found = true;
				log.append("\t >>> " + line + LS);
			}
			br.close();
			if (found) {
				log.append("Test OK" + LS);
			} else {
				log.append("Test failed - no data!" + LS);
			}
		} catch (Exception e) {
			log.append("ERROR!" + LS);
			if (log != null) log.append("Error: " + e);
		}
	}

	/**
	 * Add some system infos.
	 */
	static private void printSystemInfos(JTextArea log) {
		try {
			log.append("System Information" + LS);
			Properties p = System.getProperties();
			log.append("Java version: " + p.getProperty("java.version") + " ("+p.getProperty("java.vendor")+")"+ LS);
			log.append("Java home: " + p.getProperty("java.home") + "\nUser dir: " + p.getProperty("user.dir") + LS);
			log.append("OS: " + p.getProperty("os.name") + " " + p.getProperty("os.version") + " (" + p.getProperty("os.arch") + ")" + LS);
			log.append(LS);
		} catch (Exception e) {
			log.append("ERROR!" + LS);
			if (log != null) log.append("Error details: " + e + LS);
		}
	}

	/**
	 * @see de.hattrickorganizer.logik.xml.XMLMenuParser.parseMenuFromString()
	 */
	private final static String parseDetails(Document doc, JTextArea log) throws Exception {
        Element ele = null;
        Element root = null;

        if (doc == null) {
        	log.append("Error: no valid XML data!" + LS);
            return "";
        }
        root = doc.getDocumentElement();
        ele = (Element) root.getElementsByTagName("RecommendedURL").item(0);
        String ip = ele.getFirstChild().getNodeValue();
        if (ip.startsWith("http://")) {
            ip = ip.substring(7, ip.length());
        }
        return ip;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ConnTest();
	}
}
