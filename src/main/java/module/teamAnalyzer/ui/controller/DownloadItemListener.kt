// %2136143411:hoplugins.teamAnalyzer.ui.controller%
package module.teamAnalyzer.ui.controller;

import core.model.HOVerwaltung;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.component.DownloadPanel;
import module.teamAnalyzer.vo.Team;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * Action listener for the download menu item
 * 
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class DownloadItemListener implements ActionListener {

	/**
	 * Action performed event listener Show the download dialog
	 * 
	 * @param arg0
	 *            the event
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JOptionPane
				.showMessageDialog(SystemManager.getPlugin(),
						new DownloadPanel(), HOVerwaltung.instance()
								.getLanguageString("Menu.DownloadMatch"),
						JOptionPane.PLAIN_MESSAGE);

		if (SystemManager.getPlugin() != null) {
			Team selectedTeam = SystemManager.getPlugin().getFilterPanel()
					.getSelectedTeam();

			SystemManager.setActiveTeam(selectedTeam);
			SystemManager.refresh();
		}
	}
}
