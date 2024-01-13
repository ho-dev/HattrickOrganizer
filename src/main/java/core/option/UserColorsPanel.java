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
import static java.awt.event.ItemEvent.*;
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
	private JTable colorTable = new JTable(tableModel);

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
		colorTable.getTableHeader().setReorderingAllowed(false);
		colorTable.setSelectionMode(SINGLE_SELECTION);
		colorTable.setRowSelectionAllowed(false);
		colorTable.setCellSelectionEnabled(true);
		colorTable.setDefaultRenderer(Object.class, new UpdaterCellRenderer());

        final TableColumnModel tableColumnModel = colorTable.getColumnModel();
		tableColumnModel.getColumn(0).setMaxWidth(200);
		tableColumnModel.getColumn(0).setPreferredWidth(200);
		tableColumnModel.getColumn(1).setMaxWidth(200);
		tableColumnModel.getColumn(1).setPreferredWidth(200);
		tableColumnModel.getColumn(1).setCellEditor(new DefaultCellEditor(createNameCombobox()));

		return new JScrollPane(colorTable);
    }

	protected void initData(String skin) {
		var colors = HOColor.getColors(skin);
        Object[][] value = new Object[colors.size()][4];
		tableModel.setDataVector(value, columnNames);
		int i=0;
		for (var color : colors){
			updateRow(i++, color);
		}
	}

	private JComboBox<HOColorName> createNameCombobox() {
		var box = new JComboBox<>(HOColorName.values());
		box.insertItemAt(null, 0);
		box.addItemListener(e -> {
            var tableSelection = colorTable.getEditingRow();
            if (tableSelection >= 0 && tableSelection < HOColorName.values().length) {
				if (e != null) {
					var box1 = (JComboBox<HOColorName>) e.getSource();
					var colorName = HOColorName.values()[tableSelection];
					var hoColor = HOColor.getHOColor(colorName, (String) skins.getSelectedItem());
					if (hoColor != null) {
						if (e.getStateChange() == DESELECTED || hoColor.colorReference() == null) {
							hoColor.setDefaultValueIfNotSet();
							var currentColor = HOColor.getColor(hoColor.getHOColorName(), hoColor.getTheme());
							var selection = (HOColorName) box1.getSelectedItem();
							if (selection != null) {
								hoColor.setColorReference(selection.name());
							} else {
								hoColor.setColor(currentColor);
							}

							updateRow(tableSelection, hoColor);

						}
					}
				}
			}
        });
		return box;
	}

	private void updateRow(int row, HOColor color) {
		var tableModel = (DefaultTableModel)colorTable.getModel();
		tableModel.setValueAt(createNameLabel(color.getName()), row, 0);
		tableModel.setValueAt(color.colorReference(), row, 1);
		tableModel.setValueAt(createColorLabel(HOColor.getColor(color.getHOColorName(), color.getTheme())), row, 2);
		Color defaultColor = null;
		var value = color.getDefaultValue();
		if ( value != null){
			if (value.getColorReference() != null){
				defaultColor = HOColor.getColor(value.getHOColorName(), value.getTheme());
			}
			else {
				defaultColor = value.getColor();
			}
		}
		tableModel.setValueAt(createColorLabel(defaultColor), row, 3);
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
