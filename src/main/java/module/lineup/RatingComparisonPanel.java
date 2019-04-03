package module.lineup;

import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import module.teamAnalyzer.vo.MatchRating;

import java.util.HashMap;
import java.text.DecimalFormat;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class RatingComparisonPanel extends JPanel {

	MatchRating data;
	JLabel DL = new JLabel("");
	JLabel DR = new JLabel("");
	JLabel DC = new JLabel("");
	JLabel M = new JLabel("");
	JLabel FR = new JLabel("");
	JLabel FL = new JLabel("");
	JLabel FC = new JLabel("");
	JLabel loddar = new JLabel("");
	JLabel hatstats = new JLabel("");
	JLabel name = new JLabel("");

	BufferedImage background = ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.RATINGCOMPARISON_BACKGROUND).getImage());

	public RatingComparisonPanel(String source, MatchRating data) {
		this.data = data;
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(267, 217));
		name.setText(source);
		initComponents();
	}

	@Override
	public final void paint(Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;
		paintComponent(g2d);

		g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);
		paintChildren(g2d);
	}

	private void initComponents() {
		Font numFont = new Font("SansSerif", Font.BOLD, 26);
		Font txtFont = new Font("SansSerif", Font.BOLD, 18);
		DecimalFormat formater = new DecimalFormat();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 4;
		gbc.weighty = 4;
		
		formater.setMaximumFractionDigits(2);
		formater.setRoundingMode(java.math.RoundingMode.HALF_UP);

		DL.setText(formater.format(data.getLeftDefense()));
		DL.setFont(numFont);
		add(DL, gbc);
		
		gbc.gridx = 2;
		DR.setText(formater.format(data.getRightDefense()));
		DR.setFont(numFont);
		add(DR, gbc);

		gbc.gridx = 1;
		DC.setText(formater.format(data.getCentralDefense()));
		DC.setFont(numFont);
		add(DC, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		M.setText(formater.format(data.getMidfield()));
		M.setFont(numFont);
		add(M, gbc);

		gbc.gridy = 2;
		gbc.gridwidth = 1;
		FL.setText(formater.format(data.getLeftAttack()));
		FL.setFont(numFont);
		add(FL, gbc);

		gbc.gridx = 2;
		FR.setText(formater.format(data.getRightAttack()));
		FR.setFont(numFont);
		add(FR, gbc);

		gbc.gridx = 1;
		FC.setText(formater.format(data.getCentralAttack()));
		FC.setFont(numFont);
		add(FC, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 15, 0, 0);
		loddar.setText("Loddar: " + formater.format(data.getLoddarStats()));
		loddar.setFont(txtFont);
		add(loddar, gbc);

		gbc.gridy = 4;
		hatstats.setText("Hatstats: " + formater.format(data.getHatStats()));
		hatstats.setFont(txtFont);
		add(hatstats, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridheight = 2;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 0, 0, 15);
		name.setFont(txtFont);
		add(name, gbc);
	}
}
