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
	private AlleSpielerStatistikPanel alleSpielerStatistikPanel;
	private ArenaStatistikPanel arenaStatistikPanel;
	private FinanzStatistikPanel finanzStatistikPanel;
	private JTabbedPane tabbedPane;
	private SpieleStatistikPanel spieleStatistikPanel;
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

		// clubStatisticsPanel
		clubStatisticsPanel = new ClubStatisticsPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Club"),
				clubStatisticsPanel);

		// Spielerstatistik
		playerStatisticsPanel = new PlayerStatisticsPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Spieler"),
				playerStatisticsPanel);
		// SpieleStatistik
		spieleStatistikPanel = new SpieleStatistikPanel();
		tabbedPane
				.addTab(HOVerwaltung.instance().getLanguageString("Spiele"), spieleStatistikPanel);
		// DurchschnittlicheSpielerstatistik
		alleSpielerStatistikPanel = new AlleSpielerStatistikPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Verein"),
				alleSpielerStatistikPanel);
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
