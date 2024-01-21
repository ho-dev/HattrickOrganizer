package core.option;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColor;
import core.gui.theme.HOColorName;
import core.gui.theme.Theme;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
				case 3 -> this.getDataVector().get(row).get(3) != null; // default reset is only possible if not null
				default -> false; // others are not editable
			};
		}
	};
	private final JTable colorTable = new JTable(tableModel);

    private final String [] columnNames = new String[]{
			HOVerwaltung.instance().getLanguageString("Name"),
			HOVerwaltung.instance().getLanguageString("Reference"),
			HOVerwaltung.instance().getLanguageString("Value"),
			HOVerwaltung.instance().getLanguageString("Default"),
	};
	private JPanel tablePanel;
	private List<HOColor> colors;

	protected UserColorsPanel(){
		initComponents();
	}

	private static Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		var component = new JLabel();
		if (value != null) {
			var hoColor = (HOColor) value;
			Color color;
			if (hoColor.getColorReference() != null) {
				color = HOColor.getColor(hoColor.getHOColorName(), hoColor.getTheme());
			} else {
				color = hoColor.getColor();
			}
			component.setBackground(color);
		}
		return component;
	}

	private void initComponents(){
		setLayout(new BorderLayout());
		add(getTopPanel(),BorderLayout.NORTH);
		add(getTablePanel(),BorderLayout.CENTER);
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
	
	private JPanel getTablePanel() {
		if ( tablePanel == null) {
			tablePanel = new JPanel();
			tablePanel.setLayout(new BorderLayout());
			tablePanel.add(createTable());
		}
		return tablePanel;
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

		final TableColumnModel tableColumnModel = colorTable.getColumnModel();
		tableColumnModel.getColumn(0).setMaxWidth(200);
		tableColumnModel.getColumn(0).setPreferredWidth(200);
		tableColumnModel.getColumn(0).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> (JLabel) value);
		tableColumnModel.getColumn(1).setMaxWidth(200);
		tableColumnModel.getColumn(1).setPreferredWidth(200);
		tableColumnModel.getColumn(1).setCellEditor(new DefaultCellEditor(createNameChooser()));
		tableColumnModel.getColumn(2).setCellEditor(new ColorTableCellEditor(this, ColorTableCellEditor.EDIT));
		tableColumnModel.getColumn(2).setCellRenderer(UserColorsPanel::getTableCellRendererComponent);
		tableColumnModel.getColumn(3).setCellEditor(new ColorTableCellEditor(this, ColorTableCellEditor.RESET_DEFAULT));
		tableColumnModel.getColumn(3).setCellRenderer(UserColorsPanel::getTableCellRendererComponent);
		return new JScrollPane(colorTable);
	}

	protected void initData(String skin) {
		// Clone the static color list for the editor
		colors = new ArrayList<>();
		for ( var c : HOColor.getColors(skin) ) colors.add(c.clone());
        Object[][] value = new Object[colors.size()][4];
		tableModel.setDataVector(value, columnNames);
		int i=0;
		for (var color : colors){
			updateRow(i++, color);
		}
	}

	private JComboBox<HOColorName> createNameChooser() {
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
							hoColor.initDefaultValue();
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

	public void updateRow(HOColor color){
		updateRow(colorTable.getEditingRow(), color);
	}

	private void updateRow(int row, HOColor color) {
		var tableModel = (DefaultTableModel) colorTable.getModel();
		tableModel.setValueAt(createNameLabel(color.getName()), row, 0);
		tableModel.setValueAt(color.colorReference(), row, 1);
		tableModel.setValueAt(color, row, 2);
		tableModel.setValueAt(color.getDefaultValue(), row, 3);
//		Color defaultColor = null;
//		var value = color.getDefaultValue();
//		if (value != null) {
//			if (value.getColorReference() != null) {
//				defaultColor = HOColor.getColor(value.getHOColorName(), value.getTheme());
//			} else {
//				defaultColor = value.getColor();
//			}
//		}
//		tableModel.setValueAt(defaultColor, row, 3);
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

	private void storeChangedColorSettings(){
		for ( var color : colors){
			var origValue = HOColor.getHOColor(color.getHOColorName(), color.getTheme());
            assert origValue != null;
            if (origValue.colorReference() != color.colorReference() ||
					origValue.getColor() != color.getColor() ||
					origValue.getDefaultValue() != color.getDefaultValue()){
				DBManager.instance().storeHOColor(color);
			}
		}
	}

}
