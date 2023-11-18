package core.gui.language

import core.util.HOLogger
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel
import javax.xml.parsers.SAXParserFactory

/**
 * This class represents the model for the language file drop down box
 * @author edswifa
 */
class LanguageComboBoxModel : AbstractListModel<Any?>(), ComboBoxModel<Any?> {
    private val comboBoxItemList: ArrayList<String> = ArrayList()
    private var selected: String? = null

    init {
        loadData()
    }

    override fun getSize(): Int {
        return comboBoxItemList.size
    }

    override fun getElementAt(index: Int): String? {
        return comboBoxItemList[index]
    }

    override fun setSelectedItem(anItem: Any) {
        selected = anItem as String
    }

    override fun getSelectedItem(): Any {
        return selected!!
    }

    /**
     * Load up the model data from the languages.xml file using a SAX parser
     */
    private fun loadData() {
        try {
            val factory = SAXParserFactory.newInstance()
            val saxParser = factory.newSAXParser()
            val handler: DefaultHandler = object : DefaultHandler() {
                var blname = false
                @Throws(SAXException::class)
                override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
                    if (qName.equals("datei", ignoreCase = true)) {
                        blname = true
                    }
                }

                @Throws(SAXException::class)
                override fun characters(ch: CharArray, start: Int, length: Int) {
                    if (blname) {
                        val s = String(ch, start, length)
                        if (!s.equals("languages.properties", ignoreCase = true)) {
                            comboBoxItemList.add(s.substring(0, s.indexOf(".properties")))
                        }
                        blname = false
                    }
                }
            }
            val languages = this.javaClass.getClassLoader().getResource("sprache/languages.xml")
            if (languages != null) {
                saxParser.parse(languages.path, handler)
            }
        } catch (e: Exception) {
            HOLogger.instance().error(javaClass, e.message)
        }
    }
}
