// %1280579671:de.hattrickorganizer.gui.statistic%
package module.statistics;

import core.gui.comp.panel.LazyImagePanel;
import core.model.HOVerwaltung;

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
	private FinanzStatistikPanel finanzStatistikPanel;
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
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Spieler"),
				playerStatisticsPanel);

		// Team Panel
		teamStatisticsPanel = new TeamStatisticsPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Verein"),
				teamStatisticsPanel);

		// SpieleStatistik
		matchesStatisticsPanel = new MatchesStatisticsPanel();
		tabbedPane
				.addTab(HOVerwaltung.instance().getLanguageString("Spiele"), matchesStatisticsPanel);

		// clubStatisticsPanel
		clubStatisticsPanel = new ClubStatisticsPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("ls.module.statistics.club"),
				clubStatisticsPanel);

		// Finanzstatistik
		finanzStatistikPanel = new FinanzStatistikPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Finanzen"),
				finanzStatistikPanel);

		// Arenastatistik
		arenaStatistikPanel = new ArenaStatistikPanel();
		tabbedPane
				.addTab(HOVerwaltung.instance().getLanguageString("Stadion"), arenaStatistikPanel);
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
