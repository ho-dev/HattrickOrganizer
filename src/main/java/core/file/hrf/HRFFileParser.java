package core.file.hrf;

import core.model.HOModel;
import core.util.HOLogger;
import core.util.IOUtils;
import java.io.File;

/**
 * Imports an HRF file and creates the correponding {@link HOModel} entities.
 */
public class HRFFileParser {

	private HRFFileParser() {
	}

	/**
	 * Reads and parses the {@link File} input.  The format of the file must be HRF.
	 */
	public static HOModel parse(File file) {

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

		return null;
	}
}
