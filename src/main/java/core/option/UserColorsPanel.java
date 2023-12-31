package core.option;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.gui.theme.HOColor;
import core.gui.theme.HOIconName;
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
        tableColumnModel.getColumn(0).setMaxWidth(50);
        tableColumnModel.getColumn(0).setPreferredWidth(50);

		return new JScrollPane(table);
    }

    protected TableModel getModel(String skin) {
		var colors = HOColor.getColors(skin);
        Object[][] value = new Object[colors.size()][4];

		int i=0;
		for (var color : colors){
			value[i][0] = HOVerwaltung.instance().getLanguageString("ls.color."+ color.getName().toLowerCase());
			value[i][1] = color.getColorReference()!=null?HOVerwaltung.instance().getLanguageString("ls.color."+ color.getColorReference().toLowerCase()):null;
			value[i][2] = color.getColor();
			value[i][3] = color.getDefaultValue()!=null?color.getDefaultValue().getColor():null;
			i++;
		}

		return new TableModel(value, columnNames);
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
