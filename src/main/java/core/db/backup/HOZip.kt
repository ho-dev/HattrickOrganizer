package core.db.backup

import core.util.HOLogger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class HOZip(filename: String) : File(filename) {
    private val zOut: ZipOutputStream?
    private var fileCount = 0

    init {
        HOLogger.instance().info(javaClass, "Create Backup: $filename")

        // Open the ZipOutputStream
        zOut = ZipOutputStream(FileOutputStream(this))
        zOut.setMethod(ZipOutputStream.DEFLATED)
        zOut.setLevel(5)
    }

    @Throws(Exception::class)
    fun addFile(file: File) {
        val tFINS = FileInputStream(file)
        val bufLength = 1024
        val buffer = ByteArray(bufLength)
        var readReturn: Int

        // Set next Entry
        zOut!!.putNextEntry(ZipEntry(file.getName()))
        do {
            readReturn = tFINS.read(buffer)
            if (readReturn != -1) {
                zOut.write(buffer, 0, readReturn)
            }
        } while (readReturn != -1)
        zOut.closeEntry()
        fileCount++
    }

    /**
     * Closes the archive if it is still open.
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    fun closeArchive() {
        if (zOut != null) {
            zOut.finish()
            zOut.close()
        }
    }

    /**
     * Deconstructor
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    protected fun finalize() {
        if (zOut != null) {
            zOut.finish()
            zOut.close()
        }
    }

    @Throws(Exception::class)
    fun addStringEntry(filename: String?, data: String) {

        // Set next Entry
        zOut!!.putNextEntry(ZipEntry(filename))
        zOut.write(data.toByteArray())
        zOut.closeEntry()
        fileCount++
    }
}
