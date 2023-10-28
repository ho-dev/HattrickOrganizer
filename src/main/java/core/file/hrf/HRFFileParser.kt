package core.file.hrf

import core.model.HOModel
import core.util.HOLogger
import core.util.IOUtils
import java.io.File

/**
 * Imports an HRF file and creates the corresponding [HOModel] entities.
 */
object HRFFileParser {
    /**
     * Reads and parses the [File] input.  The format of the file must be HRF.
     */
    @JvmStatic
    fun parse(file: File): HOModel? {
        if (!file.exists() || !file.canRead()) {
            HOLogger.instance().log(HRFFileParser::class.java, "Could not read file ${file.path}")
            return null
        }
        try {
            return HRFStringParser.parse(IOUtils.readFromFile(file, "UTF-8"))
        } catch (e: Exception) {
            HOLogger.instance().log(HRFFileParser::class.java, "Error parsing file ${file.path}")
            HOLogger.instance().log(HRFFileParser::class.java, e)
        }
        return null
    }
}
