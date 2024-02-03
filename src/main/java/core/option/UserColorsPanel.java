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
import java.util.ArrayList;
import java.util.List;

import static core.gui.theme.HOColor.areDifferentColors;
import static java.awt.event.ItemEvent.*;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class UserColorsPanel extends JPanel {

	private JComboBox skins = null;
	private final DefaultTableModel tableModel = new DefaultTableModel() {
		/**
		 * 0: The color name column is not editable
		 * 1: The color reference name column is editable
		 * 2: The color value column is only editable if reference is not set
		 * 3: The default color column is only editable if set
		 * @param row             the row whose value is to be queried
		 * @param column          the column whose value is to be queried
		 * @return true if cell is editable
		 */
		@Override
		public boolean isCellEditable(int row, int column) {
			return switch (column) {
				case 1 -> true; // colorReference is editable
				case 2 -> {    // color is only editable if colorReference is not set
					var colorReference = this.getDataVector().get(row).get(1);
					yield colorReference == null;
				}
				case 3 -> this.getDataVector().get(row).get(3) != null; // default reset is only possible if not null
				default -> false; // others are not editable
			};
		}
	};
	private final JTable colorTable = new JTable(tableModel);

	private final String[] columnNames = new String[]{
			HOVerwaltung.instance().getLanguageString("Name"),
			HOVerwaltung.instance().getLanguageString("Reference"),
			HOVerwaltung.instance().getLanguageString("Value"),
			HOVerwaltung.instance().getLanguageString("Default"),
	};
	private JPanel tablePanel;
	private List<HOColor> colors = new ArrayList<>();

	/**
	 * Create user color setting panel
	 */
	protected UserColorsPanel() {
		initComponents();
	}

	/**
	 * Create label component with colored background, used to render the color columns
	 * @param table Table
	 * @param value Value
	 * @param isSelected Is selected
	 * @param hasFocus Has focus
	 * @param row Row number
	 * @param column Column number
	 * @return JLabel
	 */
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

	/**
	 * Create panels to edit theme selection and color settings
	 */
	private void initComponents() {
		setLayout(new BorderLayout());
		add(getTopPanel(), BorderLayout.NORTH);
		add(getTablePanel(), BorderLayout.CENTER);
	}

	/**
	 * Create panel with combo box for theme selection
	 * @return Image Panel
	 */
	private JPanel getTopPanel() {
		JPanel panel = new ImagePanel();
		var themes = ThemeManager.instance().getRegisteredThemes();
		var names = themes.stream().map(Theme::getName).toArray();
		skins = new JComboBox(names);
		var selected = ThemeManager.getCurrentThemeName();
		skins.setSelectedItem(selected);
		skins.addActionListener(e->{
			if (e.getSource() == skins) {
				UserParameter.temp().skin = (String) skins.getSelectedItem();
				initData(UserParameter.instance().skin);
				// TODO change the look and feel dynamically
				OptionManager.instance().setRestartNeeded();
			}
		});
		panel.add(skins);
		return panel;
	}

	/**
	 * Create panel with color settings table
	 * @return JPanel with color table
	 */
	private JPanel getTablePanel() {
		if (tablePanel == null) {
			tablePanel = new JPanel();
			tablePanel.setLayout(new BorderLayout());
			tablePanel.add(createTable());
		}
		return tablePanel;
	}

	/**
	 * Create the color settings table
	 * @return JScrollPane with the color table
	 */
	protected JScrollPane createTable() {
		String skin = (String)skins.getSelectedItem();
		initData(skin);
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

	/**
	 * Initialize the color settings table
	 * The settings are copied to a internal color list.
	 * @param skin Theme name
	 */
	protected void initData(String skin) {
		// Clone the static color list for the editor
		colors = new ArrayList<>();
		for (var c : HOColor.getColors(skin)) colors.add(c.clone());
		Object[][] value = new Object[colors.size()][4];
		tableModel.setDataVector(value, columnNames);
		int i = 0;
		for (var color : colors) {
			updateRow(i++, color);
		}
	}

	/**
	 * Create name chooser combo box used by the color reference column
	 * @return ComboBox
	 */
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
							OptionManager.instance().setOptionsChanged(true);
						}
					}
				}
			}
		});
		return box;
	}

	/**
	 * Update the current row of the color settings table
	 * @param color Color setting
	 */
	public void updateRow(HOColor color) {
		updateRow(colorTable.getEditingRow(), color);
		OptionManager.instance().setOptionsChanged(true);
	}

	/**
	 * Update color settings row
	 * @param row Row number
	 * @param color Color setting
	 */
	private void updateRow(int row, HOColor color) {
		var tableModel = (DefaultTableModel) colorTable.getModel();
		tableModel.setValueAt(createNameLabel(color.getHOColorName()), row, 0);
		tableModel.setValueAt(color.colorReference(), row, 1);
		tableModel.setValueAt(color, row, 2);
		tableModel.setValueAt(color.getDefaultValue(), row, 3);
	}

	/**
	 * Reset color setting to given default setting
	 * @param color Default color setting
	 */
	public void resetRow(HOColor color) {
		colors.set(colorTable.getEditingRow(), color);
		updateRow(color);
	}

	/**
	 * Create label displaying the color name (1st column of the settings table)
	 * The provided technical color name is translated to the select language.
	 * This value is used as tool tip too.
	 * @param colorName Color name
	 * @return JLabel
	 */
	private JLabel createNameLabel(HOColorName colorName) {
		String text = colorName!=null?colorName.toString():"";
		var label = new JLabel(text);
		label.setToolTipText(text);
		return label;
	}

	/**
	 * Get selected theme name
	 * @return Theme name
	 */
	public String getSelectedTheme(){
		return UserParameter.temp().skin;
	}

	/**
	 * Store the edited color settings
	 */
	public void storeChangedColorSettings() {
		for (var color : colors) {
			var theme = getSelectedTheme();
			var origValue = HOColor.getHOColor(color.getHOColorName(), theme);
			assert origValue != null;
			if (areDifferentColors(origValue, color) ||
					areDifferentColors(origValue.getDefaultValue(), color.getDefaultValue())) {
				if (!color.getTheme().equals("default")) {
					color.setTheme(theme);
					DBManager.instance().storeHOColor(color);
				}
				else {
					// reset default
					DBManager.instance().deleteHOColor(origValue);
				}
				HOColor.addColor(color);
				OptionManager.instance().setRestartNeeded();
			}
		}
	}

	/**
	 * Get the color value of the color setting
	 * If no value is set, the corresponding reference is searched in the internal setting list.
	 * @param currentColor Color setting
	 * @return Color value
	 */
	public Color getColor(HOColor currentColor) {
		var ret = currentColor.getColor();
		if ( ret == null){
			var colorReference = currentColor.getColorReference();
			if (colorReference != null){
				var hoColor = colors.stream().filter(i->i.getName().equals(currentColor.getName())).findFirst();
				if ( hoColor.isPresent()){
					ret = getColor(hoColor.get());
				}
			}
		}
		return ret;
	}
}