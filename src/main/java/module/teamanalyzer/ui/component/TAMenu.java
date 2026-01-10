package module.teamanalyzer.ui.component;

import core.model.TranslationFacility;
import module.teamanalyzer.SystemManager;
import module.teamanalyzer.vo.Team;

import javax.swing.*;


public class TAMenu extends JMenu {

	public TAMenu() {
		super(TranslationFacility.tr("TeamAnalyzer"));
		initialize();
	}

	private void initialize() {
		add(getDownloadItem());
		add(new FavouriteMenu());
	}
	
	private JMenuItem getDownloadItem() {
		JMenuItem downloadItem = new JMenuItem(TranslationFacility.tr("Menu.DownloadMatch"));
		downloadItem.addActionListener(e -> {
			JOptionPane.showMessageDialog(
					SystemManager.getPlugin(),
					new DownloadPanel(),
					TranslationFacility.tr("Menu.DownloadMatch"),
					JOptionPane.PLAIN_MESSAGE
			);

			if (SystemManager.getPlugin() != null) {
				Team selectedTeam = SystemManager.getPlugin().getFilterPanel().getSelectedTeam();
				SystemManager.setActiveTeam(selectedTeam);
				SystemManager.refresh();
			}
		});
		return downloadItem;
	}
}
