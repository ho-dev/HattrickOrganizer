// %3066638262:de.hattrickorganizer.tools%
package core.util;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowserLauncher {

	/**
	 * This class should be never be instantiated; this just ensures so.
	 */
	private BrowserLauncher() {
	}

	/**
	 * Attempts to open the default web browser to the given URL.
	 * 
	 * @param url
	 *            The URL to open
	 * 
	 * @throws IOException
	 *             If the web browser could not be located or does not run
	 * @throws URISyntaxException
	 *             if the given string could not be parsed as a URI reference.
	 */
	public static void openURL(String url) throws IOException, URISyntaxException, Exception {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
			Desktop.getDesktop().browse(new URI(url));
		} else {
			// taken from
			// http://www.mkyong.com/java/open-browser-in-java-windows-or-linux/
			String os = System.getProperty("os.name").toLowerCase();
			Runtime rt = Runtime.getRuntime();
			if (os.indexOf("win") >= 0) {
				// this doesn't support showing urls in the form
				// of "page.html#nameLink"
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (os.indexOf("mac") >= 0) {
				Class.forName("com.apple.eio.FileManager")
				.getDeclaredMethod("openURL",
						new Class[] { String.class })
				.invoke(null, new Object[] { url });
			} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
				// Do a best guess on unix until we get a platform
				// independent way.
				// Build a list of browsers to try, in this order.
				String[] browsers = { "xdg-open", "google-chrome", "firefox", "opera",
						"epiphany", "konqueror", "conkeror", "midori", "kazehakase",
						"mozilla" };

				// Build a command string which looks like
				// "browser1 "url" || browser2 "url" ||..."
				StringBuffer cmd = new StringBuffer();
				for (int i = 0; i < browsers.length; i++)
					cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");

				rt.exec(new String[] { "sh", "-c", cmd.toString() });
			}
		}
	}
}
