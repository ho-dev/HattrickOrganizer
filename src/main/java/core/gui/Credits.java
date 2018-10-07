package core.gui;

import core.gui.comp.HyperLinkLabel;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import core.info.hoInfo;

public class Credits {

	public static void showCredits(Component parent) {

		JPanel creditsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		creditsPanel.add(new JLabel("Hattrick Organizer " + hoInfo.VERSION), gbc);
		gbc.gridy = 1;
		creditsPanel.add(new JLabel(" "), gbc);

		gbc.gridy = 2;
		String[] text = HOVerwaltung.instance().getLanguageString("MenuCredits").split("\\n");
		for (String line : text) {
			creditsPanel.add(new JLabel(line), gbc);
			gbc.gridy++;
		}
		creditsPanel.add(new JLabel(" "), gbc);
		gbc.gridy++;

		int lines = gbc.gridy + 1;
		gbc.gridy = 0;
		gbc.gridx = 1;
		gbc.gridheight = lines;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.insets = new Insets(0, 15, 0, 0);
		creditsPanel.add(new JLabel(ThemeManager.getIcon(HOIconName.CHPP)), gbc);

		String fugueURL = "http://p.yusukekamiyamane.com";
		JPanel fuguePanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		fuguePanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("credits.someIconsBy")
				+ " "), gbc);
		gbc.gridx = 1;
		JLabel linkLabel = new HyperLinkLabel("Yusuke Kamiyamane", fugueURL);
		fuguePanel.add(linkLabel, gbc);
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		fuguePanel.add(
				new JLabel(". "
						+ HOVerwaltung.instance().getLanguageString("credits.allRightsReserved")),
				gbc);

		String fatcowURL = "http://www.fatcow.com/free-icons";
		JPanel fatcowPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		fatcowPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("credits.someIconsBy")
				+ " "), gbc);
		gbc.gridx = 1;
		linkLabel = new HyperLinkLabel("Fatcow Hosting", fatcowURL);
		fatcowPanel.add(linkLabel, gbc);
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		fatcowPanel.add(
				new JLabel(". "
						+ HOVerwaltung.instance().getLanguageString("credits.allRightsReserved")),
				gbc);
		
		String axialisURL = "http://www.axialis.com/free/icons/";
		JPanel axialisPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		axialisPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("credits.someIconsBy")
				+ " "), gbc);
		gbc.gridx = 1;
		linkLabel = new HyperLinkLabel("Axialis Software", axialisURL);
		axialisPanel.add(linkLabel, gbc);
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		axialisPanel.add(
				new JLabel(". "
						+ HOVerwaltung.instance().getLanguageString("credits.allRightsReserved")),
				gbc);
		
		String brsevURL = "http://brsev.deviantart.com";
		JPanel brsevPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		brsevPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("credits.someIconsBy")
				+ " "), gbc);
		gbc.gridx = 1;
		linkLabel = new HyperLinkLabel("Brsev", brsevURL);
		brsevPanel.add(linkLabel, gbc);
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		brsevPanel.add(
				new JLabel(". "
						+ HOVerwaltung.instance().getLanguageString("credits.allRightsReserved")),
				gbc);

		Object[] components = { creditsPanel, fuguePanel, fatcowPanel, axialisPanel, brsevPanel };

		JOptionPane.showMessageDialog(parent, components, HOVerwaltung.instance()
				.getLanguageString("MenuCreditsChoice"), JOptionPane.INFORMATION_MESSAGE);
	}
}
