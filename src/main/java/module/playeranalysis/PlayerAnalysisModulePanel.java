package module.playeranalysis;

import core.gui.comp.panel.LazyImagePanel;
import core.model.TranslationFacility;
import core.module.config.ModuleConfig;
import module.playeranalysis.skillcompare.PlayerComparePanel;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

public class PlayerAnalysisModulePanel extends LazyImagePanel {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private PlayerAnalyseMainPanel playerAnalyseMainPanel;
	private PlayerComparePanel playerComparePanel;
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
					TranslationFacility.tr("Spiele"));
			if (ModuleConfig.instance().getBoolean(PlayerAnalysisModule.SHOW_PLAYERCOMPARE))
				tabbedPane.add(getPlayerComparePanel(),
						TranslationFacility.tr("PlayerCompare"));
		}
		return tabbedPane;
	}

	private PlayerAnalyseMainPanel getSpielerAnalyseMainPanel() {
		if (playerAnalyseMainPanel == null)
			playerAnalyseMainPanel = new PlayerAnalyseMainPanel();
		return playerAnalyseMainPanel;
	}

	private PlayerComparePanel getPlayerComparePanel() {
		if (playerComparePanel == null)
			playerComparePanel = new PlayerComparePanel();
		return playerComparePanel;
	}

}
