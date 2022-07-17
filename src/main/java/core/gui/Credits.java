package core.gui;

import core.HO;
import core.db.user.UserManager;
import core.gui.comp.HyperLinkLabel;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.BrowserLauncher;
import core.util.HOLogger;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Credits {

	public static void showCredits(Component parent) {

		JPanel creditsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		creditsPanel.add(new JLabel("Hattrick Organizer " + HO.getVersionString()), gbc);
		gbc.gridy = 1;
		creditsPanel.add(new JLabel(" "), gbc);

		gbc.gridy = 2;
		String[] text = HOVerwaltung.instance().getLanguageString("window.about.text").split("\\n");
		for (String line : text) {
			creditsPanel.add(new JLabel(line), gbc);
			gbc.gridy++;
		}
		creditsPanel.add(new JLabel(" "), gbc);
		gbc.gridy++;

		int lines = gbc.gridy + 1;
		gbc.gridy = 0;
		gbc.gridx = 2;
		gbc.gridheight = lines;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.insets = new Insets(0, 15, 0, 0);
		creditsPanel.add(new JLabel(ThemeManager.getIcon(HOIconName.CHPP_WHITE_BG)), gbc);

		JPanel hoPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		JLabel hoLabel = new JLabel("HO website: ");
		hoPanel.add(hoLabel, gbc);

		gbc.gridx = 1;
		JLabel linkLabel = new HyperLinkLabel("https://akasolace.github.io/HO/");
		hoPanel.add(linkLabel, gbc);

		gbc.gridy = 4;
		gbc.gridx = 0;
		hoPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.about.database.folder") + ": "), gbc);
		gbc.gridx = 1;
		hoPanel.add(new HyperLinkLabel(new File(UserManager.instance().getDbParentFolder() + "\\" + UserManager.instance().getCurrentUser().getDbName()).toURI().toString()), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		hoPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.about.logs.folder") + ": "), gbc);
		gbc.gridx = 1;
		hoPanel.add(new HyperLinkLabel(new File(HOLogger.instance().getLogsFolderName()).toURI().toString()), gbc);

		creditsPanel.add(hoPanel, gbc);

		Object[] options1 = {HOVerwaltung.instance().getLanguageString("window.about.licence"), HOVerwaltung.instance().getLanguageString("ls.button.ok")};

		int result = JOptionPane.showOptionDialog(parent, creditsPanel, HOVerwaltung.instance().getLanguageString("window.about.title"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, null);
		if (result == JOptionPane.YES_OPTION) {
			try {
				BrowserLauncher.openURL("https://raw.githubusercontent.com/akasolace/HO/master/LICENSE");
			} catch (Exception ex) {
				HOLogger.instance().log(HOMainFrame.class, ex);
			}
		}
	}
}
