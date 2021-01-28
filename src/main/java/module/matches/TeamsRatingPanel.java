package module.matches;

import core.constants.player.PlayerAbility;
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

	private final double MIN_WIDTH_BAR = 0.2;  // minimum width of the rating bar to ensure proper visibility in case of extreme ratings ratio
	private final int RATING_BAR_WIDTH = 200;
	private final int RATING_BAR_HEIGHT = 75;
	private final int INSET = 12;
	private final double RATING_MAX = 21d;
	private final int LARGE_RATING_BAR_HEIGHT = 2*(RATING_BAR_HEIGHT + INSET);
	private JLabel m_jlGuestTeamName;
	private JLabel m_jlHomeTeamName;
	private final MatchesModel matchesModel;
	private CustomProgressBar leftDefense, centralDefense, rightDefense, midfield, leftAttack, centralAttack, rightAttack;
	private JProgressBar[] bars;
	private JPanel m_jpBottom;
	private GridBagConstraints m_jgbcBottom;
	private Font f;


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

		m_jlHomeTeamName.setText(details.getHeimName());
		m_jlGuestTeamName.setText(details.getGastName());
		setValue(leftDefense, details.getHomeLeftDef(), details.getGuestRightAtt());
		setValue(centralDefense, details.getHomeMidDef(), details.getGuestMidAtt());
		setValue(rightDefense, details.getHomeRightDef(), details.getGuestLeftAtt());
		setValue(midfield, details.getHomeMidfield(), details.getGuestMidfield());
		setValue(leftAttack, details.getHomeLeftAtt(), details.getGuestRightDef());
		setValue(centralAttack, details.getHomeMidAtt(), details.getGuestMidDef());
		setValue(rightAttack, details.getHomeRightAtt(), details.getGuestLeftDef());
		setBarValue(0, details.getHomeMidfield());
		setBarValue(1, details.getHomeRightDef());
		setBarValue(2, details.getHomeMidDef());
		setBarValue(3, details.getHomeLeftDef());
		setBarValue(4, details.getHomeRightAtt());
		setBarValue(5, details.getHomeMidAtt());
		setBarValue(6, details.getHomeLeftAtt());
		setBarValue(7, details.getGuestMidfield());
		setBarValue(8, details.getGuestRightDef());
		setBarValue(9, details.getGuestMidDef());
		setBarValue(10, details.getGuestLeftDef());
		setBarValue(11, details.getGuestRightAtt());
		setBarValue(12, details.getGuestMidAtt());
		setBarValue(13, details.getGuestLeftAtt());
	}

	private void addListeners() {
		this.matchesModel.addMatchModelChangeListener(() -> setNeedsRefresh(true));
	}

	private void initComponents() {


		f =  new JLabel("").getFont();
		f = f.deriveFont(f.getStyle() | Font.BOLD);

		bars = new JProgressBar[14];
		for (int i = 0; i < 14; i++) {
			bars[i] = new JProgressBar(0, 100);
			bars[i].setPreferredSize(new Dimension(200, 20)); //25 if nimbus
			bars[i].setStringPainted(true);
		}

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


		m_jgbcBottom.insets = new Insets(8, 8, 0, 8);

		m_jgbcBottom.gridx = 0;
		m_jgbcBottom.gridy = 0;
		m_jpBottom.add(new JLabel(""), m_jgbcBottom);

		m_jlHomeTeamName = new JLabel("");
		m_jlHomeTeamName.setFont(f);
		m_jgbcBottom.gridx = 1;
		m_jpBottom.add(m_jlHomeTeamName, m_jgbcBottom);


		m_jlGuestTeamName = new JLabel("");
		m_jlGuestTeamName.setFont(f);
		m_jgbcBottom.gridx = 2;
		m_jpBottom.add(m_jlGuestTeamName, m_jgbcBottom);


		addRow("ls.match.ratingsector.midfield", 1);

		addRow("ls.match.ratingsector.leftdefence", 2);
		addRow("ls.match.ratingsector.centraldefence", 3);
		addRow("ls.match.ratingsector.rightdefence", 4);

		addRow("ls.match.ratingsector.leftattack", 5);
		addRow("ls.match.ratingsector.centralattack", 6);
		m_jgbcBottom.insets = new Insets(8, 8, 8, 8);
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
				ThemeManager.getColor(HOColorName.BORDER_RATING_BAR), RATING_BAR_WIDTH, iHeight, f);
	}

	private void addRow(String txt, int row) {
		JLabel label = new JLabel(Helper.getTranslation(txt));
		label.setFont(f);
		add(label, 0, row);
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

		for (var bar:bars){
			bar.setValue(0);
			bar.setString("");
		}

		m_jlGuestTeamName.setText("");
		m_jlHomeTeamName.setText("");

	}

	private void setBarValue(int index, int value) {
		double htValue = 1 + (value-1d)/4;
		bars[index].setValue((int) (htValue * 100 / RATING_MAX));
		bars[index].setString(PlayerAbility.getNameForSkill(true,value));
	}


	private void setValue(CustomProgressBar rating, int val1, int val2){
		rating.setBackground(ThemeManager.getColor(HOColorName.GUEST_ACTION));
		rating.setValue(val1, val2, MIN_WIDTH_BAR);
	}

	private void resetValue(CustomProgressBar rating){
		rating.setBackground(ThemeManager.getColor(HOColorName.NEUTRAL_ACTION));
		rating.resetValue();
	}

}




