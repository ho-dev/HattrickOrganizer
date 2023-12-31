package core.option;

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColor;
import core.gui.theme.HOColorName;
import core.gui.theme.Theme;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import tool.updater.TableEditor;
import tool.updater.TableModel;
import tool.updater.UpdaterCellRenderer;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;


/**
 * Panel to select preferred columns to display in the various HO tables.
 *
 * @author Thorsten Dietz
 * @since 1.36
 *
 */
public class UserColorsPanel extends JPanel implements ActionListener {

	@Serial
	private static final long serialVersionUID = 1L;
	private JComboBox skins 	= null;
	private JTable table 				= null;
	private final String [] columnNames = new String[]{
			HOVerwaltung.instance().getLanguageString("Name"),
			HOVerwaltung.instance().getLanguageString("Reference"),
			HOVerwaltung.instance().getLanguageString("Value"),
			HOVerwaltung.instance().getLanguageString("Default"),
	};

	protected UserColorsPanel(){
		initComponents();
	}
	
	private void initComponents(){
		setLayout(new BorderLayout());
		add(getTopPanel(),BorderLayout.NORTH);
		add(getMiddlePanel(),BorderLayout.CENTER);
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel getTopPanel(){
		JPanel panel = new ImagePanel();
		var themes = ThemeManager.instance().getRegisteredThemes();
		var names = themes.stream().map(Theme::getName).toArray();
		skins = new JComboBox(names);
		skins.addActionListener( this );
		panel.add(skins);
		return panel;
	}
	
	/**
	 * return the panel within JTable
	 * @return JPanel
	 */
	private JPanel getMiddlePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(createTable());
		panel.setPreferredSize(new Dimension(100, 300));
		return panel;
	}
	
	private String getSelectedSkin(){
		return (String)skins.getSelectedItem();
	}

    /**
     * Creates a table with checkboxes for UserColumns of the selected model
     *
     * @return JScrollPane including the table
     */
    protected JScrollPane createTable() {
        table = new JTable(getModel(getSelectedSkin()));
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new UpdaterCellRenderer());
        table.getColumn(columnNames[0]).setCellEditor(new TableEditor());

        final TableColumnModel tableColumnModel = table.getColumnModel();
		tableColumnModel.getColumn(0).setMaxWidth(200);
		tableColumnModel.getColumn(0).setPreferredWidth(200);
		tableColumnModel.getColumn(1).setMaxWidth(200);
		tableColumnModel.getColumn(1).setPreferredWidth(200);

		return new JScrollPane(table);
    }

    protected TableModel getModel(String skin) {
		var colors = HOColor.getColors(skin);
        Object[][] value = new Object[colors.size()][4];

		int i=0;
		for (var color : colors){
			value[i][0] = createNameLabel(color.getName());
			value[i][1] = createNameLabel(color.getColorReference());
			value[i][2] = createColorLabel(HOColor.getColor(color));
			value[i][3] = createColorLabel(color.getDefaultValue()!=null?HOColor.getColor(color.getDefaultValue()):null);
			i++;
		}

		return new TableModel(value, columnNames);
    }

	private JLabel createColorLabel(Color color) {
		var label = new JLabel();
		label.setBackground(color);
		return label;
	}

	private JLabel createNameLabel(String colorName) {
		String text;
		if (colorName != null) {
			text = HOVerwaltung.instance().getLanguageString("ls.color." + colorName.toLowerCase());
		}
		else {
			text = "";
		}
		var label = new JLabel(text);
		label.setToolTipText(text);
		return label;
	}

	/**
     * action
     */
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == skins){
//			table.setModel(getModel(getSelectedModel().getColumns()));
//			 table.getColumn(columnNames[0]).setCellEditor(new TableEditor());
//			final TableColumnModel tableColumnModel = table.getColumnModel();
//		        tableColumnModel.getColumn(0).setMaxWidth(50);
//		        tableColumnModel.getColumn(0).setPreferredWidth(50);
		}

	}

}
