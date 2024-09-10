// %3459649550:de.hattrickorganizer.gui.statistic%
package module.statistics;

import core.datatype.CBItem;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.model.TranslationFacility;
import module.matches.MatchesPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ArenaStatistikPanel extends LazyImagePanel {
	private static final long serialVersionUID = 2679088584924124183L;
	private ArenaStatistikTable arenaStatistikTable;
	private JComboBox matchFilterComboBox;

	@Override
	protected void initialize() {
		initComponents();
		setNeedsRefresh(false);
		addListeners();
		registerRefreshable(true);
	}

	@Override
	protected void update() {
		if (matchFilterComboBox.getSelectedIndex() > -1) {
			arenaStatistikTable.refresh(((CBItem) matchFilterComboBox.getSelectedItem()).getId());
		}
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		ImagePanel panel = new ImagePanel(null);

		matchFilterComboBox = new JComboBox(getMatchFilterItems());
		matchFilterComboBox.setFont(matchFilterComboBox.getFont().deriveFont(Font.BOLD));
		matchFilterComboBox.setSize(200, 25);
		matchFilterComboBox.setLocation(10, 5);
		panel.setPreferredSize(new Dimension(240, 35));
		panel.add(matchFilterComboBox);
		add(panel, BorderLayout.NORTH);

		// Nur Pflichtspiele ist default
		matchFilterComboBox.setSelectedIndex(0);

		// to set the first element
		arenaStatistikTable = new ArenaStatistikTable(((CBItem) matchFilterComboBox.getSelectedItem()).getId());
		add(new JScrollPane(arenaStatistikTable), BorderLayout.CENTER);

	}

	private void addListeners() {
		this.matchFilterComboBox.addItemListener(new ItemListener() {

			@Override
			public final void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// Änderung der Tabelle -> Anderer Filter!
					update();
				}
			}
		});
	}

	private CBItem[] getMatchFilterItems() {
		CBItem[] matchFilterItems = {
				new CBItem(TranslationFacility.tr("NurEigeneSpiele"),
						MatchesPanel.OWN_GAMES),
				new CBItem(TranslationFacility.tr("NurEigenePflichtspiele"),
						MatchesPanel.OWN_OFFICIAL_GAMES),
				new CBItem(TranslationFacility.tr("NurEigenePokalspiele"),
						MatchesPanel.OWN_NATIONAL_CUP_GAMES),
				new CBItem(TranslationFacility.tr("OnlySecondaryCup"),
						MatchesPanel.OWN_SECONDARY_CUP_GAMES),
				new CBItem(TranslationFacility.tr("NurEigeneLigaspiele"),
						MatchesPanel.OWN_LEAGUE_GAMES),
				new CBItem(TranslationFacility.tr("OnlyQualificationMatches"),
						MatchesPanel.OWN_QUALIF_GAMES),
				new CBItem(TranslationFacility.tr("NurEigeneFreundschaftsspiele"),
						MatchesPanel.OWN_FRIENDLY_GAMES) };
		return matchFilterItems;
	}
}
