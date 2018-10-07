package module.ifa;

import core.gui.comp.panel.LazyPanel;
import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;
import module.ifa.config.Config;
import module.ifa.model.Country;
import module.ifa.model.IfaModel;
import module.ifa.table.IfaTableCellRenderer;
import module.ifa.table.IfaTableModel;
import module.ifa.table.SummaryTableSorter;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class PluginIfaPanel extends LazyPanel {

	private static final long serialVersionUID = 3806181337290704445L;
	private JSplitPane splitPane;
	private IfaModel model;

	@Override
	protected void initialize() {
		this.model = new IfaModel();
		initComponents();
		addListeners();
		registerRefreshable(true);
	}

	@Override
	public void update() {
		model.reload();
		
	}

	private void addListeners() {
		this.splitPane.addPropertyChangeListener("dividerLocation", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (splitPane.getSize().height > 0 && splitPane.getDividerLocation() > 0) {
					double proportionalDividerLocation = 1.0 / ((double) splitPane.getSize().height / (double) splitPane
							.getDividerLocation());
					ModuleConfig.instance().setBigDecimal(
							Config.STATS_TABLES_DIVIDER_LOCATION.toString(),
							BigDecimal.valueOf(proportionalDividerLocation));
				}
			}
		});
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		StatsPanel statsPanel = new StatsPanel(this.model);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(10, 10, 3, 10);
		add(statsPanel, gbc);

		this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.splitPane.add(createTablePanel(true), 0);
		this.splitPane.add(createTablePanel(false), 1);
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(this.splitPane, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		add(new RightPanel(this.model), gbc);

		validate();

		final double dividerLocation = ModuleConfig
				.instance()
				.getBigDecimal(Config.STATS_TABLES_DIVIDER_LOCATION.toString(),
						BigDecimal.valueOf(0.5)).doubleValue();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				splitPane.setDividerLocation(dividerLocation);

			}
		});
	}

	private JPanel createTablePanel(boolean away) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel headerLabel = new JLabel();
		if (away) {
			headerLabel.setText(HOVerwaltung.instance().getLanguageString(
					"ifa.statisticsTable.header.away"));
		} else {
			headerLabel.setText(HOVerwaltung.instance().getLanguageString(
					"ifa.statisticsTable.header.home"));
		}
		Font boldFont = headerLabel.getFont().deriveFont(
				headerLabel.getFont().getStyle() ^ Font.BOLD);
		headerLabel.setFont(boldFont);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(10, 10, 3, 10);
		panel.add(headerLabel, gbc);

		final IfaTableModel tblModel = new IfaTableModel();
		tblModel.setData(this.model, away);
		JTable table = new JTable(tblModel);
		IfaTableCellRenderer renderer = new IfaTableCellRenderer();
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		table.getColumnModel().getColumn(3).setCellRenderer(renderer);
		table.getColumnModel().getColumn(4).setCellRenderer(renderer);
		table.getColumnModel().getColumn(5).setCellRenderer(renderer);
		table.getColumnModel().getColumn(6).setCellRenderer(renderer);

		TableRowSorter<TableModel> sorter = new SummaryTableSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		sorter.setComparator(0, new Comparator<Country>() {

			@Override
			public int compare(Country o1, Country o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
		List<SortKey> list = new ArrayList<SortKey>();
		list.add(new SortKey(5, SortOrder.DESCENDING));
		sorter.setSortKeys(list);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(3, 10, 10, 10);
		panel.add(new JScrollPane(table), gbc);

		return panel;
	}
}
