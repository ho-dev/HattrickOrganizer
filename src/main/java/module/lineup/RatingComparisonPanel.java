package module.lineup;

import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import module.teamAnalyzer.vo.MatchRating;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.text.DecimalFormat;

import java.awt.Font;
import java.awt.Color;
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
		Font titleFont = new Font("SansSerif", Font.BOLD, 32);
		Font numFont = new Font("SansSerif", Font.BOLD, 23);
		Font txtFont = new Font("SansSerif", Font.BOLD, 18);
		DecimalFormat formater = new DecimalFormat();
		Double value; //to check for negatives

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 4;
		gbc.weighty = 4;
		
		formater.setMaximumFractionDigits(2);
		formater.setRoundingMode(java.math.RoundingMode.HALF_UP);

		gbc.gridx = 2;
		value = data.getLeftDefense();
		DL.setText(formater.format(value));
		DL.setFont(numFont);
		if(value < 0) DL.setForeground(new Color(179, 45, 0));
		add(DL, gbc);
		
		gbc.gridx = 0;
		value = data.getRightDefense();
		DR.setText(formater.format(value));
		DR.setFont(numFont);
		if(value < 0) DR.setForeground(new Color(179, 45, 0));
		add(DR, gbc);

		gbc.gridx = 1;
		value = data.getCentralDefense();
		DC.setText(formater.format(value));
		DC.setFont(numFont);
		if(value < 0) DC.setForeground(new Color(179, 45, 0));
		add(DC, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		value = data.getMidfield();
		M.setText(formater.format(value));
		M.setFont(numFont);
		if(value < 0) M.setForeground(new Color(179, 45, 0));
		add(M, gbc);

		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		value = data.getLeftAttack();
		FL.setText(formater.format(value));
		FL.setFont(numFont);
		if(value < 0) FL.setForeground(new Color(179, 45, 0));
		add(FL, gbc);

		gbc.gridx = 0;
		value = data.getRightAttack();
		FR.setText(formater.format(value));
		FR.setFont(numFont);
		if(value < 0) FR.setForeground(new Color(179, 45, 0));
		add(FR, gbc);

		gbc.gridx = 1;
		value = data.getCentralAttack();
		FC.setText(formater.format(value));
		FC.setFont(numFont);
		if(value < 0) FC.setForeground(new Color(179, 45, 0));
		add(FC, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 15, 0, 0);
		value = data.getLoddarStats();
		loddar.setText("Loddar: " + formater.format(value));
		loddar.setFont(txtFont);
		if(value < 0) loddar.setForeground(new Color(179, 45, 0));
		add(loddar, gbc);

		gbc.gridy = 4;
		value = data.getHatStats();
		hatstats.setText("Hatstats: " + formater.format(value));
		hatstats.setFont(txtFont);
		if(value < 0) hatstats.setForeground(new Color(179, 45, 0));
		add(hatstats, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridheight = 2;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 0, 0, 15);
		name.setFont(titleFont);
		name.setForeground(Color.BLUE);
		add(name, gbc);
	}
}
