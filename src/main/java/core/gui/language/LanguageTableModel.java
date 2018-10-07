package core.gui.language;

import core.util.HOLogger;
import core.util.UTF8Control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * This class represents the table model for editing language resource files.
 * @author edswifa
 *
 */
public class LanguageTableModel extends AbstractTableModel implements TableModel  {

	private static final long serialVersionUID = -1926494264955036043L;
	private String[] columnNames = {"Key", "Value"};
	private Map<String, String> data;
	private List<String> keys = new ArrayList<String>();
	private boolean isDestinationFile = false;
	private String langauageName = "";
	
	/**
	 * Default constructor to create an English table model
	 */
	public LanguageTableModel() {
		this.langauageName = "English";
		
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
		URL englishPath = this.getClass().getClassLoader().getResource("sprache/English.properties");
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(englishPath.getFile());
		} catch (FileNotFoundException e) {
			HOLogger.instance().error(getClass(), e.getMessage());
		}
		 
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if(line.contains("=")) {
					String key = line.substring(0, line.indexOf("="));
					String value = line.substring(line.indexOf("=") + 1);
					map.put(key, value);
					this.keys.add(key);
				}
			}
		} catch (IOException e) {
			HOLogger.instance().error(getClass(), e.getMessage());
		}
	 
		try {
			br.close();
		} catch (IOException e) {
			HOLogger.instance().error(getClass(), e.getMessage());
		}
		
		this.data = map;
	}

	/**
	 * Constructor to create a table model for the given language name
	 * @param languageName
	 */
	public LanguageTableModel(String languageName) {
		this();
		this.isDestinationFile = true;
		this.langauageName = languageName;
		
		ResourceBundle englishBundle = ResourceBundle.getBundle("sprache.English", new UTF8Control());
		ResourceBundle destBundle = ResourceBundle.getBundle("sprache." + languageName, new UTF8Control());
		Iterator<String> rbKeys = this.keys.iterator();
		String value = null;
		
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		while(rbKeys.hasNext()) {
			String key = rbKeys.next();
			try {
				value = destBundle.getString(key);
			} catch(Exception e) {
				value = englishBundle.getString(key);
			}
			map.put(key, value);
		}
		
		this.data = map;
	}

	@Override
	public int getRowCount() {
		return this.keys.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String key = this.keys.get(rowIndex);
		
		if (columnIndex == 0) {
			return key;
		}
		
		return this.data.get(key);
	}

	@Override
	public String getColumnName(int column) {
		return this.columnNames[column];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if((this.isDestinationFile) && (columnIndex == 1) && (rowIndex > 1)) {
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(isDestinationFile && (columnIndex == 1)) {
			this.data.put(keys.get(rowIndex), (String) aValue);
			fireTableDataChanged();
		}
	}
	
	/**
	 * Save the table model back to a properties file
	 */
	public void save() {
		StringBuilder fileName = new StringBuilder("sprache/");
		fileName.append(this.langauageName);
		fileName.append(".properties");
		URL destinationPath = this.getClass().getClassLoader().getResource(fileName.toString());

		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationPath.getPath()), "UTF-8"));

			// Loop over table and put into properties
			Iterator<String> rbKeys = this.keys.iterator();
			while(rbKeys.hasNext()) {
				String key = rbKeys.next();
				StringBuffer sb = new StringBuffer(key);
				sb.append("=");
				sb.append(this.data.get(key));
				bw.write(sb.toString());
				bw.newLine();
			}
			
			String message = "Please pass the file " + destinationPath.getPath() + " to a developer who will commit it for you.";
			JOptionPane.showMessageDialog(new JFrame(), message, "Saved", JOptionPane.INFORMATION_MESSAGE);
			HOLogger.instance().info(getClass(), "Language file " + langauageName + ".properties saved.");
			
		} catch (IOException ioe) {
			HOLogger.instance().error(getClass(), ioe.getMessage());
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException ioe) {
					HOLogger.instance().error(getClass(), ioe.getMessage());
				}
			}
		}
	}

}
