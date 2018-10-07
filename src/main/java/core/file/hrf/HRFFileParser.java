// %3512883119:de.hattrickorganizer.tools%
package core.file.hrf;

import core.model.HOModel;
import core.util.HOLogger;
import core.util.IOUtils;

import java.io.File;

/**
 * Importiert ein HRF-File und stellt die Werte bereit
 */
public class HRFFileParser {

	private HRFFileParser() {
	}

	/**
	 * Datei einlesen und parsen
	 */
	public static HOModel parse(File file) {
		java.sql.Timestamp hrfdate = null;

		if (!file.exists() || !file.canRead()) {
			HOLogger.instance().log(HRFFileParser.class, "Could not read file " + file.getPath());
			return null;
		}

		try {
			return HRFStringParser.parse(IOUtils.readFromFile(file, "UTF-8"));
		} catch (Exception e) {
			HOLogger.instance().log(HRFFileParser.class, "Error parsing file " + file.getPath());
			HOLogger.instance().log(HRFFileParser.class, e);
		}

		// Nur im Fehlerfall
		return null;
	}
}
