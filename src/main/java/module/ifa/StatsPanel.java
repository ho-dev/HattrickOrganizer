package module.ifa;

import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.WorldDetailsManager;
import core.util.GUIUtils;
import module.ifa.model.IfaModel;
import module.ifa.model.ModelChangeListener;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatsPanel extends JPanel {

	private JLabel visitedValueLabel;
	private JLabel hostedValueLabel;
	private JLabel countriesTotalValueLabel;
	private JLabel countriesTotalValueLabel2;
	private JLabel visitedCoolnessValueLabel;
	private JLabel hostedCoolnessValueLabel;
	private JLabel coolnessTotalValueLabel;
	private JLabel coolnessTotalValueLabel2;
	private JLabel coolnessSumValueLabel;
	private JLabel coolnessSumTotalValueLabel;
	private static final long serialVersionUID = -263387721027188968L;
	private final IfaModel model;

	public StatsPanel(IfaModel model) {
		this.model = model;
		initComponents();
		setHeaderTexts();
		addListeners();
	}

	private void addListeners() {
		this.model.addModelChangeListener(new ModelChangeListener() {

			@Override
			public void modelChanged() {
				setHeaderTexts();
			}
		});
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.title")));
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel visitedLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.visitedCountries"));
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(4, 10, 3, 6);
		add(visitedLabel, gbc);

		this.visitedValueLabel = new JLabel();
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 3, 6);
		add(this.visitedValueLabel, gbc);

		JLabel ofLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.of"));
		gbc.gridx = 2;
		add(ofLabel, gbc);

		this.countriesTotalValueLabel = new JLabel();
		gbc.gridx = 3;
		add(this.countriesTotalValueLabel, gbc);

		JLabel visitedCoolnessLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.coolness"));
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 4;
		gbc.insets = new Insets(4, 20, 3, 6);
		add(visitedCoolnessLabel, gbc);

		this.visitedCoolnessValueLabel = new JLabel();
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.gridx = 5;
		gbc.insets = new Insets(4, 2, 3, 6);
		add(this.visitedCoolnessValueLabel, gbc);

		ofLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.of"));
		gbc.gridx = 6;
		add(ofLabel, gbc);

		this.coolnessTotalValueLabel = new JLabel();
		gbc.gridx = 7;
		add(this.coolnessTotalValueLabel, gbc);

		// ROW 2

		JLabel hostedLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.hostedCountries"));
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(4, 10, 3, 6);
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(hostedLabel, gbc);

		this.hostedValueLabel = new JLabel();
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 3, 6);
		add(this.hostedValueLabel, gbc);

		ofLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.of"));
		gbc.gridx = 2;
		add(ofLabel, gbc);

		this.countriesTotalValueLabel2 = new JLabel();
		gbc.gridx = 3;
		add(this.countriesTotalValueLabel2, gbc);

		JLabel hostedCoolnessLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.coolness"));
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 4;
		gbc.insets = new Insets(4, 20, 3, 6);
		add(hostedCoolnessLabel, gbc);

		this.hostedCoolnessValueLabel = new JLabel();
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.gridx = 5;
		gbc.insets = new Insets(4, 2, 3, 6);
		add(this.hostedCoolnessValueLabel, gbc);

		ofLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.of"));
		gbc.gridx = 6;
		add(ofLabel, gbc);

		this.coolnessTotalValueLabel2 = new JLabel();
		gbc.gridx = 7;
		add(this.coolnessTotalValueLabel2, gbc);

		// ROW 3

		JLabel coolnessSumTotalLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.coolnessTotal"));
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(4, 20, 3, 6);
		gbc.gridx = 4;
		gbc.gridy = 2;
		add(coolnessSumTotalLabel, gbc);

		this.coolnessSumValueLabel = new JLabel();
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.gridx = 5;
		gbc.insets = new Insets(4, 2, 3, 6);
		add(this.coolnessSumValueLabel, gbc);

		ofLabel = new JLabel(HOVerwaltung.instance().getLanguageString(
				"ifa.statisticsTable.stats.of"));
		gbc.gridx = 6;
		add(ofLabel, gbc);

		this.coolnessSumTotalValueLabel = new JLabel();
		gbc.gridx = 7;
		add(this.coolnessSumTotalValueLabel, gbc);

		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.insets = new Insets(4, 20, 3, 6);
		gbc.gridheight = 3;
		gbc.gridx = 8;
		gbc.gridy = 0;
		gbc.weighty = 1.0;

	    JButton infoButton = new JButton(ThemeManager.getIcon(HOIconName.INFORMATION));
	    infoButton.setPreferredSize(new Dimension(28, 28));
	    add(infoButton, gbc);

		infoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				IfaOverviewDialog dlg = new IfaOverviewDialog(model, HOMainFrame.instance());
				GUIUtils.decorateWithDisposeOnESC(dlg);
				GUIUtils.setLocationCenteredToComponent(dlg, HOMainFrame.instance());
				dlg.setVisible(true);
			}
		});
	}

	private void setHeaderTexts() {
		NumberFormat doubleFormat = NumberFormat.getInstance();
		doubleFormat.setMaximumFractionDigits(2);
		doubleFormat.setMinimumFractionDigits(2);

		int totalCountries = WorldDetailsManager.instance().size();
		String txt = "";
		this.visitedValueLabel.setText(String.valueOf(this.model.getVistedCountriesCount()));
		this.hostedValueLabel.setText(String.valueOf(this.model.getHostedCountriesCount()));
		this.countriesTotalValueLabel.setText(String.valueOf(totalCountries));
		this.countriesTotalValueLabel2.setText(String.valueOf(totalCountries));
		double currentVisitedCoolness = this.model.getVisitedSummary().getCoolnessTotal();
		this.visitedCoolnessValueLabel.setText(doubleFormat.format(currentVisitedCoolness));
		double currentHostedCoolness = this.model.getHostedSummary().getCoolnessTotal();
		this.hostedCoolnessValueLabel.setText(doubleFormat.format(currentHostedCoolness));
		this.coolnessTotalValueLabel.setText(doubleFormat.format(this.model.getMaxCoolness()));
		this.coolnessTotalValueLabel2.setText(doubleFormat.format(this.model.getMaxCoolness()));
		this.coolnessSumValueLabel.setText(doubleFormat.format(currentVisitedCoolness
				+ currentHostedCoolness));
		this.coolnessSumTotalValueLabel
				.setText(doubleFormat.format(this.model.getMaxCoolness() * 2));
	}
}
