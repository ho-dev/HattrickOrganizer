package module.lineup;

import core.model.HOVerwaltung;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JCheckBox;

public final class CombinedRatingChartPanel extends JPanel {

	private final class Datum {
		private JCheckBox checkbox;
		private Color bg;

		public Datum(String text, Color background) {
			checkbox = new JCheckBox(text);
			bg = background;
			init();
		}

		private void init() {
			checkbox.setEnabled(false);
			checkbox.setOpaque(true);
			checkbox.setBackground(bg);
			if(getRelativeLuminance(bg) < 0.179) checkbox.setForeground(Color.WHITE);
		}

		double getRelativeLuminance(Color color) {
			double[] rgb = {color.getRed(), color.getGreen(), color.getBlue()};
			for(int i = 0; i < 3; i++) {
				rgb[i] /= 255.0;
				if(rgb[i] <= 0.03928) rgb[i] /= 12.92;
				else rgb[i] = Math.pow( (rgb[i] + 0.055) / 1.055, 2.4);
			}
			return (0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2]);
		}

		JCheckBox getCheckbox() {
			return checkbox;
		}
	}

	private HOVerwaltung hov = HOVerwaltung.instance();
	private JPanel controlsPanel = new JPanel();
	private JCheckBox showHelpLines = new JCheckBox(hov.getLanguageString("Hilflinien"));
	private JCheckBox showValues = new JCheckBox(hov.getLanguageString("Beschriftung"));
	private Datum leftDefense;
	private Datum centralDefense;
	private Datum rightDefense;
	private Datum midfield;
	private Datum leftAttack;
	private Datum centralAttack;
	private Datum rightAttack;
	private Datum hatStats;
	private Datum loddar;

	public CombinedRatingChartPanel() {
		setLayout(new BorderLayout());
		initComponents();
	}

	private void initComponents() {
		controlsPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill =  GridBagConstraints.HORIZONTAL;
		gbc.gridy = 0;

		showHelpLines.setEnabled(false);
		controlsPanel.add(showHelpLines, gbc);

		gbc.gridy++;
		showValues.setEnabled(false);
		controlsPanel.add(showValues, gbc);

		gbc.gridy++;
		leftDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.leftdefence"),
								ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).brighter());
		controlsPanel.add(leftDefense.getCheckbox(), gbc);

		gbc.gridy++;
		centralDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.centraldefence"),
									ThemeManager.getColor(HOColorName.SHIRT_CENTRALDEFENCE));
		controlsPanel.add(centralDefense.getCheckbox(), gbc);

		gbc.gridy++;
		rightDefense = new Datum(hov.getLanguageString("ls.match.ratingsector.rightdefence"),
								ThemeManager.getColor(HOColorName.SHIRT_WINGBACK).darker());
		controlsPanel.add(rightDefense.getCheckbox(), gbc);

		gbc.gridy++;
		midfield = new Datum(hov.getLanguageString("ls.match.ratingsector.midfield"),
							ThemeManager.getColor(HOColorName.SHIRT_MIDFIELD));
		controlsPanel.add(midfield.getCheckbox(), gbc);

		gbc.gridy++;
		leftAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.leftattack"),
								ThemeManager.getColor(HOColorName.SHIRT_WING).brighter());
		controlsPanel.add(leftAttack.getCheckbox(), gbc);

		gbc.gridy++;
		centralAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.centralattack"),
									ThemeManager.getColor(HOColorName.SHIRT_FORWARD));
		controlsPanel.add(centralAttack.getCheckbox(), gbc);

		gbc.gridy++;
		rightAttack = new Datum(hov.getLanguageString("ls.match.ratingsector.rightattack"),
								ThemeManager.getColor(HOColorName.SHIRT_WING).darker());
		controlsPanel.add(rightAttack.getCheckbox(), gbc);

		gbc.gridy++;
		hatStats = new Datum(hov.getLanguageString("ls.match.ratingtype.hatstats"),
							ThemeManager.getColor(HOColorName.STAT_HATSTATS));
		controlsPanel.add(hatStats.getCheckbox(), gbc);

		gbc.gridy++;
		loddar = new Datum(hov.getLanguageString("ls.match.ratingtype.loddarstats"),
							ThemeManager.getColor(HOColorName.STAT_LODDAR));
		controlsPanel.add(loddar.getCheckbox(), gbc);

		add(controlsPanel, BorderLayout.WEST);
	}
}
