package module.matches;

import core.gui.comp.CustomProgressBar;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;
import core.util.Helper;

import java.awt.*;
import javax.swing.*;


/**
 * Panel showing detailed team ratings of selected match
 */
class TeamsRatingPanel extends LazyImagePanel {

	private final double MIN_WIDTH_BAR = 10d;  // minimum width of the rating bar to ensure proper visibility in case of extreme ratings ratio
	private final int RATING_BAR_WIDTH = 200;
	private final int RATING_BAR_HEIGHT = 75;
	private final int INSET = 12;

	private final int LARGE_RATING_BAR_HEIGHT = 2*(RATING_BAR_HEIGHT + INSET);

	private JLabel n_jlGuestTeamName;
	private JLabel n_jlHomeTeamName;
	private JLabel n_jlGuestTeamScore;
	private JLabel n_jlHomeTeamScore;

//	private JLabel[] homePercent;
//	private JLabel[] awayPercent;
//	private GridBagLayout layout;
//	private GridBagConstraints constraints;
	private final MatchesModel matchesModel;
	private CustomProgressBar leftDefense, centralDefense, rightDefense, midfield, leftAttack, centralAttack, rightAttack;
	private JProgressBar[] bars;
	private JPanel m_jpBottom;
	private GridBagConstraints m_jgbcBottom;


	TeamsRatingPanel(MatchesModel matchesModel) {
		this(matchesModel, false);
	}

	TeamsRatingPanel(MatchesModel matchesModel, boolean print) {
		super(print);
		this.matchesModel = matchesModel;
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {

		MatchKurzInfo matchInfo = matchesModel.getMatch();

		if ((matchInfo == null) || matchInfo.getMatchStatus() != MatchKurzInfo.FINISHED) {
			clear();
			return;
		}

		Matchdetails details = matchesModel.getDetails();

		if (details.getHomeHatStats() == 0)
		{
			clear();
			return;
		}

		setValue(leftDefense, details.getHomeLeftDef(), details.getGuestRightAtt());
		setValue(centralDefense, details.getHomeMidDef(), details.getGuestMidAtt());
		setValue(rightDefense, details.getHomeRightDef(), details.getGuestLeftAtt());
		setValue(midfield, details.getHomeMidfield(), details.getGuestMidfield());
		setValue(leftAttack, details.getHomeLeftAtt(), details.getGuestRightDef());
		setValue(centralAttack, details.getHomeMidAtt(), details.getGuestMidDef());
		setValue(rightAttack, details.getHomeRightAtt(), details.getGuestLeftDef());

	}

	private void addListeners() {
		this.matchesModel.addMatchModelChangeListener(() -> setNeedsRefresh(true));
	}

	private void initComponents() {
//		JPanel panel = new JPanel(layout);
		bars = new JProgressBar[14];
//		homePercent = new JLabel[barCount];
//		awayPercent = new JLabel[barCount];
		for (int i = 0; i < 14; i++) {
			bars[i] = new JProgressBar(0, 100);
		}
//		setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints gbc = new GridBagConstraints();

		setLayout(layout);

		leftDefense = createRatingBar();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, INSET, 0, 0);
		add(leftDefense, gbc);

		centralDefense = createRatingBar();
		gbc.gridy = 1;
		gbc.insets = new Insets(INSET, INSET, 0, 0);
		add(centralDefense, gbc);

		rightDefense = createRatingBar();
		gbc.gridy = 2;
		add(rightDefense, gbc);

		midfield = createRatingBar(true);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 3;
		gbc.insets = new Insets(0, INSET, 0, INSET);
		add(midfield, gbc);

		leftAttack = createRatingBar();
		gbc.gridx = 2;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 0, 0, INSET);
		add(leftAttack, gbc);

		centralAttack = createRatingBar();
		gbc.gridy = 1;
		gbc.insets = new Insets(INSET, 0, 0, INSET);
		add(centralAttack, gbc);

		rightAttack = createRatingBar();
		gbc.gridy = 2;
		add(rightAttack, gbc);

		m_jpBottom = new JPanel(new GridBagLayout());
		m_jgbcBottom = new GridBagConstraints();

		m_jpBottom.setBorder(BorderFactory.createLineBorder(ThemeManager
				.getColor(HOColorName.PANEL_BORDER)));



		m_jgbcBottom.gridx = 0;
		m_jgbcBottom.gridy = 0;
		m_jpBottom.add(new JLabel(""), m_jgbcBottom);

		n_jlHomeTeamName = new JLabel("BB");
		m_jgbcBottom.gridx = 1;
		m_jpBottom.add(n_jlHomeTeamName, m_jgbcBottom);


		n_jlGuestTeamName = new JLabel("CC");
		m_jgbcBottom.gridx = 2;
		m_jpBottom.add(n_jlGuestTeamName, m_jgbcBottom);


		addRow("ls.match.ratingsector.midfield", 1);

		addRow("ls.match.ratingsector.leftdefence", 2);
		addRow("ls.match.ratingsector.centraldefence", 3);
		addRow("ls.match.ratingsector.rightdefence", 4);

		addRow("ls.match.ratingsector.leftattack", 5);
		addRow("ls.match.ratingsector.centralattack", 6);
		addRow("ls.match.ratingsector.rightattack", 7);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(m_jpBottom, gbc);

	}

	private CustomProgressBar createRatingBar() {
				return createRatingBar(false);
	}

	private CustomProgressBar createRatingBar(boolean isLarge) {
		int iHeight = isLarge ? LARGE_RATING_BAR_HEIGHT : RATING_BAR_HEIGHT;
		return new CustomProgressBar(ThemeManager.getColor(HOColorName.GUEST_ACTION), ThemeManager.getColor(HOColorName.HOME_ACTION),
				ThemeManager.getColor(HOColorName.BORDER_RATING_BAR), RATING_BAR_WIDTH, iHeight);
	}

	private void addRow(String txt, int row) {
		add(new JLabel(Helper.getTranslation(txt)), 0, row);
		add(bars[row-1],1, row);
		add(bars[row+6], 2, row);
	}

	private void add(JComponent comp, int x, int y) {
		m_jgbcBottom.gridx = x;
		m_jgbcBottom.gridy = y;
		m_jgbcBottom.anchor = GridBagConstraints.WEST;
		m_jgbcBottom.fill = GridBagConstraints.HORIZONTAL;
		m_jpBottom.add(comp, m_jgbcBottom);
	}

	private void clear() {

		resetValue(leftDefense);
		resetValue(centralDefense);
		resetValue(rightDefense);
		resetValue(midfield);
		resetValue(leftAttack);
		resetValue(centralAttack);
		resetValue(rightAttack);

//		n_jlHomeTeamName.setText(" ");
//		n_jlGuestTeamName.setText(" ");
//		n_jlHomeTeamScore.setText(" ");
//		n_jlGuestTeamScore.setText(" ");
//		n_jlHomeTeamName.setIcon(null);
//		n_jlGuestTeamName.setIcon(null);
//
//		for (int i = 0; i < bars.length; i++) {
//			bars[i].setValue(0);
//			homePercent[i].setText(" ");
//			awayPercent[i].setText(" ");
//		}
	}
//
//	private void setBarValue(int index, float home, float away) {
//		bars[index].setValue((int) getPercent(home, away));
//		bars[index].setToolTipText(bars[index].getValue() + " %" + " -- "
//				+ (100 - bars[index].getValue()) + " %");
//		homePercent[index].setText(bars[index].getValue() + " %");
//		awayPercent[index].setText((100 - bars[index].getValue()) + " %");
//		bars[index].setForeground(bars[index].getValue() < 50 ? ThemeManager
//				.getColor(HOColorName.MATCHDETAILS_PROGRESSBAR_RED) : ThemeManager
//				.getColor(HOColorName.MATCHDETAILS_PROGRESSBAR_GREEN));
//		bars[index].setBackground(bars[index].getValue() < 50 ? ThemeManager
//				.getColor(HOColorName.MATCHDETAILS_PROGRESSBAR_GREEN) : ThemeManager
//				.getColor(HOColorName.MATCHDETAILS_PROGRESSBAR_RED));
//	}
//

	private double getPercent(int val1, int val2) {
		var value = 100d * (double)val1 / (double)(val1 + val2);
		if (value < MIN_WIDTH_BAR) {
			return MIN_WIDTH_BAR;
		}
		else if (value > 100d - MIN_WIDTH_BAR) {
			return 100d - MIN_WIDTH_BAR;
		}
		return value;
	}

	private void setValue(CustomProgressBar rating, int val1, int val2){
		rating.setBackground(ThemeManager.getColor(HOColorName.GUEST_ACTION));
		rating.setValue(getPercent(val1, val2));
	}

	private void resetValue(CustomProgressBar rating){
		rating.setBackground(ThemeManager.getColor(HOColorName.NEUTRAL_ACTION));
		rating.setValue(0d);
	}

}




