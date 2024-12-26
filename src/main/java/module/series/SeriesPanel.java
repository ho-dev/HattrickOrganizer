package module.series;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.model.UserParameter;
import module.series.promotion.LeaguePromotionHandler;
import module.series.promotion.PromotionInfoPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel displaying the league table, as well as the series history graph.
 */
public class SeriesPanel extends LazyImagePanel {

	private JButton deleteButton;
	private JComboBox<MatchFixtures> seasonComboBox;
	private SeriesTablePanel seriesTable;
	private MatchDayPanel[] matchDayPanels;
	private SeriesHistoryPanel seriesHistoryPanel;
	private Model model;
	private PromotionInfoPanel promotionInfoPanel;

    @Override
	protected void initialize() {
		initPromotionHandler();
		initComponents();
		fillSaisonCB();
		addListeners();
		registerRefreshable(true);
	}

	private void initPromotionHandler() {
        LeaguePromotionHandler promotionHandler = new LeaguePromotionHandler();
		promotionInfoPanel = new PromotionInfoPanel(promotionHandler);
	}

	@Override
	protected void update() {
		fillSaisonCB();
	}

	private void delete() {
		if (seasonComboBox.getSelectedItem() != null) {
			MatchFixtures spielplan = (MatchFixtures) seasonComboBox.getSelectedItem();
			int value = JOptionPane.showConfirmDialog(this,
					TranslationFacility.tr("ls.button.delete") + " "
							+ TranslationFacility.tr("Ligatabelle") + ":\n"
							+ spielplan.toString(), TranslationFacility.tr("confirmation.title"), JOptionPane.YES_NO_OPTION);

			if (value == JOptionPane.YES_OPTION) {
				DBManager.instance().deleteSpielplanTabelle(spielplan.getSaison(), spielplan.getLigaId());
				DBManager.instance().deletePaarungTabelle(spielplan.getSaison(), spielplan.getLigaId());
				this.model.setCurrentSeries(null);
				RefreshManager.instance().doReInit();
			}
		}
	}

	private void addListeners() {
		this.deleteButton.addActionListener(e -> delete());

		this.seasonComboBox.addActionListener(e -> {
			// Determine current match schedule
			if (seasonComboBox.getSelectedItem() instanceof MatchFixtures) {
				model.setCurrentSeries((MatchFixtures) seasonComboBox.getSelectedItem());
			} else {
				model.setCurrentSeries(null);
			}

			// Inform all panels
			informSaisonChange();
		});

		this.seriesTable.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				teamSelectionChanged();
			}
		});
	}

	private void teamSelectionChanged() {
		if (this.model.getCurrentTeam() == null && seriesTable.getSelectedTeam() == null) {
			return;
		}

		if (this.model.getCurrentTeam() == null
				|| !this.model.getCurrentTeam().equals(seriesTable.getSelectedTeam())) {
			this.model.setCurrentTeam(seriesTable.getSelectedTeam());
			markierungInfo();
		}
	}

	private void fillSaisonCB() {
		// Get the match schedules as objects with the pairings
		var spielplaene = DBManager.instance().getAllSpielplaene(true);
		final MatchFixtures markierterPlan = (MatchFixtures) seasonComboBox.getSelectedItem();

		// Remove all old seasons
		seasonComboBox.removeAllItems();

		// Fill new
		for (var fixture : spielplaene) {
			seasonComboBox.addItem(fixture);
		}

		//  Restore old marking
		seasonComboBox.setSelectedItem(markierterPlan);

		if ((seasonComboBox.getSelectedIndex() < 0) && (seasonComboBox.getItemCount() > 0)) {
			seasonComboBox.setSelectedIndex(0);
		}

		// Aktuellen Spielplan bestimmen
		if (seasonComboBox.getSelectedItem() instanceof MatchFixtures) {
			this.model.setCurrentSeries((MatchFixtures) seasonComboBox.getSelectedItem());
		} else {
			this.model.setCurrentSeries(null);
		}

		// Alle Panels informieren
		informSaisonChange();
	}

	private void informSaisonChange() {
		seriesTable.changeSaison();
		seriesHistoryPanel.changeSaison();
		markierungInfo();
	}

	private void initComponents() {
		this.model = new Model();
		setLayout(new BorderLayout());

		// ComboBox für Saisonauswahl
		final JPanel panel = new ImagePanel(new BorderLayout());
		final JPanel toolbarPanel = new ImagePanel(null);
		var fontSize = UserParameter.instance().fontSize;
		var gap = 10;
		var xLocation = 10;
		var yLocation = 5;
		var width = 20 * fontSize;
		var height = 2 * fontSize;
		seasonComboBox = new JComboBox();
		seasonComboBox.setToolTipText(TranslationFacility.tr("tt_Ligatabelle_Saisonauswahl"));
		seasonComboBox.setSize(width, height);
		seasonComboBox.setLocation(xLocation, yLocation);
		toolbarPanel.add(seasonComboBox);

		deleteButton = new JButton(ThemeManager.getIcon(HOIconName.REMOVE));
		deleteButton.setToolTipText(TranslationFacility.tr("tt_Ligatabelle_SaisonLoeschen"));
		xLocation += width + gap;
		width = 2 * fontSize;
		deleteButton.setSize(width, height);
		deleteButton.setLocation(xLocation, yLocation);
		deleteButton.setBackground(ThemeManager.getColor(HOColorName.BUTTON_BG));
		toolbarPanel.add(deleteButton);
		toolbarPanel.setPreferredSize(new Dimension(xLocation+width, yLocation + height));
		panel.add(toolbarPanel, BorderLayout.NORTH);

		JSplitPane leagueStatsPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, initLigaTabelle(), initTabellenverlaufStatistik());
		UserParameter.instance().series_tableSplitPaneDivider.init(leagueStatsPanel);

		final JPanel tablePanel = new ImagePanel(new BorderLayout());
		tablePanel.add(leagueStatsPanel, BorderLayout.NORTH);

		final JPanel historyPanel = new ImagePanel(new BorderLayout());
		historyPanel.add(initSpielPlan(), BorderLayout.CENTER);

		tablePanel.add(historyPanel, BorderLayout.CENTER);

		panel.add(tablePanel, BorderLayout.CENTER);

		add(panel, BorderLayout.CENTER);
	}

	private Component initLigaTabelle() {
		seriesTable = new SeriesTablePanel(this.model);
		JScrollPane scrollpane = new JScrollPane(seriesTable);
		scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollpane.setPreferredSize(new Dimension((int) seriesTable.getPreferredSize().getWidth(),
				(int) seriesTable.getPreferredSize().getHeight() + 22));
		return scrollpane;
	}

	private Component initSpielPlan() {
		JLabel label;
		matchDayPanels = new MatchDayPanel[14];
		for (int i = 0; i < matchDayPanels.length; i++) {
			matchDayPanels[i] = new MatchDayPanel(this.model, i + 1);
		}

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridy = 0;
		constraints.insets = new Insets(4, 4, 4, 4);

		final JPanel panel = new ImagePanel(layout);

		label = new JLabel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);

		for (int i = 0; i < 7; i++) {
			constraints.gridx = 1;
			constraints.gridy = i;
			constraints.gridheight = 1;
			layout.setConstraints(matchDayPanels[i], constraints);
			panel.add(matchDayPanels[i]);
		}

		label = new JLabel();
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridheight = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);

		for (int i = 7; i < matchDayPanels.length; i++) {
			constraints.gridx = 3;
			constraints.gridy = i - 7;
			constraints.gridheight = 1;
			layout.setConstraints(matchDayPanels[i], constraints);
			panel.add(matchDayPanels[i]);
		}

		label = new JLabel();
		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.gridheight = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);

		final JScrollPane scrollpane = new JScrollPane(panel);
		scrollpane.getVerticalScrollBar().setBlockIncrement(100);
		scrollpane.getVerticalScrollBar().setUnitIncrement(20);
		scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		return scrollpane;
	}

	private Component initTabellenverlaufStatistik() {
		seriesHistoryPanel = new SeriesHistoryPanel(this.model);

		final JPanel panel = new ImagePanel();
		panel.add(seriesHistoryPanel);

		final JScrollPane scrollpane = new JScrollPane(panel);
		scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollpane.setPreferredSize(new Dimension((int) seriesTable.getPreferredSize().getWidth(),
				(int) seriesTable.getPreferredSize().getHeight()));

		return scrollpane;
	}

	private void markierungInfo() {
		for (MatchDayPanel matchDayPanel : matchDayPanels) {
			matchDayPanel.changeSeason();
		}
	}
}
