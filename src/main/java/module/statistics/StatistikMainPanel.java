// %1280579671:de.hattrickorganizer.gui.statistic%
package module.statistics;

import core.gui.comp.panel.LazyImagePanel;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * TabbedPane mit Statistiken
 */
public class StatistikMainPanel extends LazyImagePanel {

    private ClubStatisticsPanel clubStatisticsPanel;
	private TeamStatisticsPanel teamStatisticsPanel;
	private ArenaStatistikPanel arenaStatistikPanel;
	private FinancesStatisticsPanel financesStatisticsPanel;
	private JTabbedPane tabbedPane;
	private MatchesStatisticsPanel matchesStatisticsPanel;
	private PlayerStatisticsPanel playerStatisticsPanel;
	private boolean initialized = false;

	@Override
	protected void initialize() {
		initComponents();
	}

	@Override
	protected void update() {
		// do nothing, handled in sub-panels
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();

		// Spielerstatistik
		playerStatisticsPanel = new PlayerStatisticsPanel();
		tabbedPane.addTab(TranslationFacility.tr("Spieler"),
				playerStatisticsPanel);

		// Team Panel
		teamStatisticsPanel = new TeamStatisticsPanel();
		tabbedPane.addTab(TranslationFacility.tr("Verein"),
				teamStatisticsPanel);

		// SpieleStatistik
		matchesStatisticsPanel = new MatchesStatisticsPanel();
		tabbedPane
				.addTab(TranslationFacility.tr("Spiele"), matchesStatisticsPanel);

		// clubStatisticsPanel
		clubStatisticsPanel = new ClubStatisticsPanel();
		tabbedPane.addTab(TranslationFacility.tr("ls.module.statistics.club"),
				clubStatisticsPanel);

		// Finanzstatistik
		financesStatisticsPanel = new FinancesStatisticsPanel();
		tabbedPane.addTab(TranslationFacility.tr("Finanzen"),
				financesStatisticsPanel);

		// Arenastatistik
		arenaStatistikPanel = new ArenaStatistikPanel();
		tabbedPane
				.addTab(TranslationFacility.tr("Stadion"), arenaStatistikPanel);
		add(tabbedPane, java.awt.BorderLayout.CENTER);
	}

	public final void setShowSpieler(final int spielerid) {
		tabbedPane.setSelectedIndex(0);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				playerStatisticsPanel.setPlayer(spielerid);
			}
		});
	}
}
