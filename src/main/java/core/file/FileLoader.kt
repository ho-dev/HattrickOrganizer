package core.file

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import core.util.HOLogger
import java.io.FileNotFoundException

/**
 * @author cira
 *
 * This class should be used to load any external file.
 * Other classes using this utility won't need to know if the requested is located inside the
 * HO.jar file, or at the same level of HO.jar in the directory tree.
 */

object FileLoader {

    enum class FileLoadingStatus {
        OUTSIDE_JAR,
        INSIDE_JAR,
        NOT_FOUND
    }

    private val fileStatusesCache = mutableMapOf<String, FileLoadingStatus>()

    /**
     * Returns the lastModified of the requested file. <br />
     * This can be used in order to check if a file was modified since its last access, in order to decide whether it needs to be parsed again.
     * <strong>Please note</strong> that this property is not accessible when the file is loaded from a Jar. This shouldn't represent an issue,
     * because those files shouldn't be reloaded.
     * @param fileName The name of the file to be used to get the lastModified
     * @return <em>.lastModified()</em> of the File object (if accessible outside the Jar). <br />
     * <em>1</em> if the file is accessed from the Jar (this will allow to the current implementation of the reload-if-needed system to run once and only once)<br />
     * <em>-1</em> if the file has not been found
     */
    fun getFileLastModified(fileName: String): Long {
        val tmpFileName = if (!fileName.startsWith("/")) "/${fileName}" else fileName

        if (!fileStatusesCache.containsKey(tmpFileName)) {
            // inserts the file in the cache if not yet available
            this.getFileInputStream(tmpFileName)
        }
        return when (fileStatusesCache[tmpFileName]) {
            FileLoadingStatus.OUTSIDE_JAR -> {
                File(tmpFileName).lastModified()
            }

            FileLoadingStatus.INSIDE_JAR -> 1
            else -> -1
        }
    }


    /**
     * Provides access to the InputStream of a requested file
     * @param fileName The name of the file to be returned
     * @return the InputStream related to the fileName or <em>null</em> if the file doesn't exist
     */
    fun getFileInputStream(fileName: String): InputStream? {
        val tmpFileName = if (!fileName.startsWith("/")) "/${fileName}" else fileName

        val cachedFileStatus = fileStatusesCache[tmpFileName]
        if (cachedFileStatus == FileLoadingStatus.NOT_FOUND) return null

        val fileUnknown = (cachedFileStatus == null)

        if (fileUnknown || cachedFileStatus == FileLoadingStatus.OUTSIDE_JAR) {
            val returnFile = File(tmpFileName)
            try {
                val inputStream = FileInputStream(returnFile)
                if (fileUnknown) {
                    fileStatusesCache[tmpFileName] = FileLoadingStatus.OUTSIDE_JAR
                    HOLogger.instance().debug(javaClass, "File will loaded from outside the JAR: $tmpFileName")
                }
                return inputStream
            } catch (e: FileNotFoundException) {
                if (!fileUnknown) {
                    // Well... something's wrong here. This should never happen. Cache is updates!
                    fileStatusesCache[tmpFileName] = FileLoadingStatus.NOT_FOUND
                    HOLogger.instance().debug(
                        javaClass,
                        "File that was outside the jar will not be searched anymore: $tmpFileName"
                    )
                    return null
                }
                // ...else... it continues
            }
        }

        if (fileUnknown || fileStatusesCache[tmpFileName] == FileLoadingStatus.INSIDE_JAR) {
            val inputStream = FileLoader::class.java.getResourceAsStream(tmpFileName)
            if (inputStream != null) {
                if (fileUnknown) {
                    fileStatusesCache[tmpFileName] = FileLoadingStatus.INSIDE_JAR
                    HOLogger.instance().debug(javaClass, "File will be loaded from inside the JAR: $tmpFileName")
                }
                return inputStream
            } else {
                fileStatusesCache[tmpFileName] = FileLoadingStatus.NOT_FOUND
            }
        }
        HOLogger.instance().debug(javaClass, "File will not be searched anymore: $tmpFileName")
        return null

    }

    /**
     * Provides access to the InputStream of the first requested file found in the list.
     * This can be useful in order to provide one (or more) alternative file(s) to be searched.
     * @param fileNames Ordered list of file names to be returned
     * @return the InputStream related to the fileName or <em>null</em> if the file doesn't exist
     */
    fun getFileInputStream(fileNames: Array<String>): InputStream? {
        for (fileName in fileNames) {
            val stream = this.getFileInputStream(fileName)
            if (stream != null) return stream
        }
        return null
    }
}
