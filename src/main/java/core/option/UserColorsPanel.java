package core.option;

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColor;
import core.gui.theme.HOColorName;
import core.gui.theme.Theme;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import tool.updater.UpdaterCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class UserColorsPanel extends JPanel implements ActionListener {

	private JComboBox skins 	= null;
	private final DefaultTableModel tableModel = new DefaultTableModel(){
		@Override
		public boolean isCellEditable(int row, int column) {
            return switch (column) {
                case 1 -> true; // colorReference is editable
                case 2 -> { 	// color is only editable if colorReference is not set
                    var colorReference = this.getDataVector().get(row).get(1);
                    yield colorReference == null;
                }
				default -> false; // others are not editable
			};
		}
	};

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
	
	private JPanel getTopPanel(){
		JPanel panel = new ImagePanel();
		var themes = ThemeManager.instance().getRegisteredThemes();
		var names = themes.stream().map(Theme::getName).toArray();
		skins = new JComboBox(names);
		var selected = ThemeManager.getCurrentThemeName();
		skins.setSelectedItem(selected);
		skins.addActionListener( this );
		panel.add(skins);
		return panel;
	}
	
	private JPanel getMiddlePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(createTable());
		return panel;
	}
	
	private String getSelectedSkin(){
		return (String)skins.getSelectedItem();
	}

    protected JScrollPane createTable() {

		initData(getSelectedSkin());
        JTable table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
		table.setSelectionMode(SINGLE_SELECTION);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(true);
        table.setDefaultRenderer(Object.class, new UpdaterCellRenderer());

        final TableColumnModel tableColumnModel = table.getColumnModel();
		tableColumnModel.getColumn(0).setMaxWidth(200);
		tableColumnModel.getColumn(0).setPreferredWidth(200);
		tableColumnModel.getColumn(1).setMaxWidth(200);
		tableColumnModel.getColumn(1).setPreferredWidth(200);
		tableColumnModel.getColumn(1).setCellEditor(new DefaultCellEditor(createNameCombobox()));

		return new JScrollPane(table);
    }

	protected void initData(String skin) {
		var colors = HOColor.getColors(skin);
        Object[][] value = new Object[colors.size()][4];

		int i=0;
		for (var color : colors){
			value[i][0] = createNameLabel(color.getName());
			value[i][1] = color.colorReference();
			value[i][2] = createColorLabel(HOColor.getColor(color));
			value[i][3] = createColorLabel(color.getDefaultValue()!=null?HOColor.getColor(color.getDefaultValue()):null);
			i++;
		}

		tableModel.setDataVector(value, columnNames);
	}

	private JComboBox<HOColorName> createNameCombobox() {
		var box = new JComboBox<>(HOColorName.values());
		box.insertItemAt(null, 0);
		box.addActionListener(this);
		return box;
	}

	private JLabel createColorLabel(Color color) {
		var label = new JLabel();
		label.setOpaque(true);
		if (color != null) {
			label.setBackground(color);
			label.setToolTipText(color.toString());
		}
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
			UserParameter.temp().skin = (String) skins.getSelectedItem();
//			ThemeManager.instance().setCurrentTheme();	// load theme colors
			initData(UserParameter.instance().skin);
			// TODO change the look and feel dynamically
			OptionManager.instance().setRestartNeeded();
		}

	}

}
