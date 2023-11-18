package core.gui.language

import core.util.HOLogger
import core.util.UTF8Control
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableModel

/**
 * This class represents the table model for editing language resource files.
 * @author edswifa
 */
class LanguageTableModel() : AbstractTableModel(), TableModel {
    private val columnNames = arrayOf("Key", "Value")
    private var data: MutableMap<String, String?>
    private val keys: MutableList<String> = ArrayList()
    private var isDestinationFile = false
    private var langauageName = ""

    /**
     * Default constructor to create an English table model
     */
    init {
        langauageName = "English"
        val map = LinkedHashMap<String, String?>()
        val englishPath = this.javaClass.getClassLoader().getResource("sprache/English.properties")
        var fis: FileInputStream? = null
        try {
            if (englishPath != null) {
                fis = FileInputStream(englishPath.file)
            }
        } catch (e: FileNotFoundException) {
            HOLogger.instance().error(javaClass, e.message)
        }
        val br = fis?.let { InputStreamReader(it) }?.let { BufferedReader(it) }
        var line: String?
        try {
            if (br != null) {
                while (br.readLine().also { line = it } != null) {
                    if (line!!.contains("=")) {
                        val key = line!!.substring(0, line!!.indexOf("="))
                        val value = line!!.substring(line!!.indexOf("=") + 1)
                        map[key] = value
                        keys.add(key)
                    }
                }
            }
        } catch (e: IOException) {
            HOLogger.instance().error(javaClass, e.message)
        }
        try {
            br?.close()
        } catch (e: IOException) {
            HOLogger.instance().error(javaClass, e.message)
        }
        data = map
    }

    /**
     * Constructor to create a table model for the given language name
     */
    constructor(languageName: String) : this() {
        isDestinationFile = true
        langauageName = languageName
        val englishBundle = ResourceBundle.getBundle("sprache.English", UTF8Control())
        val destBundle = ResourceBundle.getBundle("sprache.$languageName", UTF8Control())
        val rbKeys: Iterator<String> = keys.iterator()

        val map = LinkedHashMap<String, String?>()
        while (rbKeys.hasNext()) {
            val key = rbKeys.next()
            val value = try {
                destBundle.getString(key)
            } catch (e: Exception) {
                englishBundle.getString(key)
            }
            map[key] = value
        }
        data = map
    }

    override fun getRowCount(): Int {
        return keys.size
    }

    override fun getColumnCount(): Int {
        return columnNames.size
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val key = keys[rowIndex]
        return if (columnIndex == 0) {
            key
        } else data[key]!!
    }

    override fun getColumnName(column: Int): String {
        return columnNames[column]
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return isDestinationFile && columnIndex == 1 && rowIndex > 1
    }

    /* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
    override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {
        if (isDestinationFile && columnIndex == 1) {
            data[keys[rowIndex]] = aValue as String
            fireTableDataChanged()
        }
    }

    /**
     * Save the table model back to a properties file
     */
    fun save() {
        val fileName = "sprache/$langauageName.properties"
        val destinationPath = this.javaClass.getClassLoader().getResource(fileName)
        var bw: BufferedWriter? = null
        try {
            if (destinationPath != null) {
                bw = BufferedWriter(OutputStreamWriter(FileOutputStream(destinationPath.path), StandardCharsets.UTF_8))
            }

            // Loop over table and put into properties
            for (key in keys) {
                val sb = key + "=" + data[key]
                bw?.write(sb)
                bw?.newLine()
            }
            val message =
                "Please pass the file ${destinationPath?.path ?: ""} to a developer who will commit it for you."
            JOptionPane.showMessageDialog(JFrame(), message, "Saved", JOptionPane.INFORMATION_MESSAGE)
            HOLogger.instance().info(javaClass, "Language file $langauageName.properties saved.")
        } catch (ioe: IOException) {
            HOLogger.instance().error(javaClass, ioe.message)
        } finally {
            if (bw != null) {
                try {
                    bw.close()
                } catch (ioe: IOException) {
                    HOLogger.instance().error(javaClass, ioe.message)
                }
            }
        }
    }
}
