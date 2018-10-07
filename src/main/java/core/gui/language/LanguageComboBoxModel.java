package core.gui.language;

import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import core.util.HOLogger;

/**
 * This class represents the model for the language file drop down box
 * @author edswifa
 *
 */
public class LanguageComboBoxModel extends AbstractListModel implements ComboBoxModel {
	
	private static final long serialVersionUID = -8499599592907864510L;
	private ArrayList<String> comboBoxItemList;
	private String selected = null;

	public LanguageComboBoxModel() {
		comboBoxItemList = new ArrayList<String>();
		loadData();
	}

	@Override
	public int getSize() {
		return comboBoxItemList.size();
	}

	@Override
	public String getElementAt(int index) {
		return comboBoxItemList.get(index);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.selected = (String) anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}

	/**
	 * Load up the model data from the languages.xml file using a SAX parser
	 */
	private void loadData() {
		
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				boolean blname = false;
	
				@Override
				public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {

					if (qName.equalsIgnoreCase("datei")) {
						blname = true;
					}
				}
	
				@Override
				public void characters(char ch[], int start, int length) throws SAXException {

					if (blname) {
						String s = new String(ch, start, length);
						if(!s.equalsIgnoreCase("languages.properties")) {
							comboBoxItemList.add(s.substring(0, s.indexOf(".properties")));
						}
						blname = false;
					}
				}

		     };
		     
		     URL languages = this.getClass().getClassLoader().getResource("sprache/languages.xml");
		     saxParser.parse(languages.getPath(), handler);
		     
		 } catch (Exception e) {
			 HOLogger.instance().error(getClass(), e.getMessage());
		 }
	}
}
