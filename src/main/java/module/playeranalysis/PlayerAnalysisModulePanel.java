package module.playeranalysis;

import core.gui.comp.panel.LazyImagePanel;
import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;
import module.playeranalysis.experience.ExperienceViewer;
import module.playeranalysis.skillCompare.PlayerComparePanel;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

public class PlayerAnalysisModulePanel extends LazyImagePanel {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private SpielerAnalyseMainPanel spielerAnalyseMainPanel;
	private PlayerComparePanel playerComparePanel;
	private ExperienceViewer experienceViewer;
	private boolean initialized = false;

	public final void setSpieler4Bottom(int spielerid) {
		getSpielerAnalyseMainPanel().setSpieler4Bottom(spielerid);
	}

	public final void setSpieler4Top(int spielerid) {
		getSpielerAnalyseMainPanel().setSpieler4Top(spielerid);
	}
	
	@Override
	protected void initialize() {
		setLayout(new BorderLayout());
		add(getTabbedPane(), BorderLayout.CENTER);
	}

	@Override
	protected void update() {
		// nothing to do here, data is updated in the tabs
	}
	
	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.add(getSpielerAnalyseMainPanel(),
					HOVerwaltung.instance().getLanguageString("Spiele"));
			if (ModuleConfig.instance().getBoolean(PlayerAnalysisModule.SHOW_PLAYERCOMPARE))
				tabbedPane.add(getPlayerComparePanel(),
						HOVerwaltung.instance().getLanguageString("PlayerCompare"));
			if (ModuleConfig.instance().getBoolean(PlayerAnalysisModule.SHOW_EXPERIENCE))
				tabbedPane.add(getExperienceViewer(),
						HOVerwaltung.instance().getLanguageString("ExperienceViewer"));
		}
		return tabbedPane;
	}

	private SpielerAnalyseMainPanel getSpielerAnalyseMainPanel() {
		if (spielerAnalyseMainPanel == null)
			spielerAnalyseMainPanel = new SpielerAnalyseMainPanel();
		return spielerAnalyseMainPanel;
	}

	private PlayerComparePanel getPlayerComparePanel() {
		if (playerComparePanel == null)
			playerComparePanel = new PlayerComparePanel();
		return playerComparePanel;
	}

	private ExperienceViewer getExperienceViewer() {
		if (experienceViewer == null)
			experienceViewer = new ExperienceViewer();
		return experienceViewer;
	}
}
