package module.matches;

import core.gui.comp.CustomProgressBar;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel showing detailed team ratings of selected match
 */
class TeamsRatingPanel extends LazyImagePanel {

	private final Color homeTeamColor = Color.BLUE; // TODO: get this from theme manager and inline with rest of the module (should be already defined)
	private final double MIN_WIDTH_BAR = 10d;  // minimum width of the rating bar to ensure proper visibility in case of extreme ratings ratio

//	private JLabel n_jlGuestTeamName;
//	private JLabel n_jlHomeTeamName;
//	private JLabel n_jlGuestTeamScore;
//	private JLabel n_jlHomeTeamScore;
//	private CustomProgressBar[] bars;
//	private JLabel[] homePercent;
//	private JLabel[] awayPercent;
//	private GridBagLayout layout;
//	private GridBagConstraints constraints;
	private final MatchesModel matchesModel;
	private CustomProgressBar leftDefense, centralDefense, rightDefense, midfield, leftAttack, centralAttack, rightAttack;


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
		setValue(leftDefense, details.getHomeLeftDef(), details.getGuestRightAtt());
		setValue(centralDefense, details.getHomeMidDef(), details.getGuestMidAtt());
		setValue(rightDefense, details.getHomeRightDef(), details.getGuestLeftAtt());
		setValue(midfield, details.getHomeMidfield(), details.getGuestMidfield());
		setValue(leftAttack, details.getHomeLeftAtt(), details.getGuestRightDef());
		setValue(centralAttack, details.getHomeMidAtt(), details.getGuestMidDef());
		setValue(rightAttack, details.getHomeRightAtt(), details.getGuestLeftDef());

	}

	private void addListeners() {
		this.matchesModel.addMatchModelChangeListener(new MatchModelChangeListener() {

			@Override
			public void matchChanged() {
				setNeedsRefresh(true);
			}
		});
	}

	private void initComponents() {
//		JPanel panel = new JPanel(layout);
//		int barCount = 8;
//		bars = new CustomProgressBar[barCount];
//		homePercent = new JLabel[barCount];
//		awayPercent = new JLabel[barCount];
//		for (int i = 0; i < barCount; i++) {
//			bars[i] = new CustomProgressBar(Color.RED); //TODO: fix that part
//			homePercent[i] = new JLabel(" ");
//			awayPercent[i] = new JLabel(" ");
//		}
//		setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints gbc = new GridBagConstraints();

		setLayout(layout);

		leftDefense = new CustomProgressBar(homeTeamColor, 100, 30);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(leftDefense, gbc);

		centralDefense = new CustomProgressBar(homeTeamColor, 100, 30);
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 0, 0, 0);
		add(centralDefense, gbc);

		rightDefense = new CustomProgressBar(homeTeamColor, 100, 30);
		gbc.gridy = 2;
		add(rightDefense, gbc);

		midfield = new CustomProgressBar(homeTeamColor, 100, 70);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 3;
		gbc.insets = new Insets(0, 5, 0, 0);
		add(midfield, gbc);

		leftAttack = new CustomProgressBar(homeTeamColor, 100, 30);
		gbc.gridx = 2;
		add(leftAttack, gbc);

		centralAttack = new CustomProgressBar(homeTeamColor, 100, 30);
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 0, 0, 0);
		add(centralAttack, gbc);

		rightAttack = new CustomProgressBar(homeTeamColor, 100, 30);
		gbc.gridy = 2;
		add(rightAttack, gbc);


//		mainconstraints.anchor = GridBagConstraints.NORTH;
//		mainconstraints.fill = GridBagConstraints.HORIZONTAL;
//		mainconstraints.weighty = 0.1;
//		mainconstraints.weightx = 1.0;
//		mainconstraints.insets = new Insets(4, 6, 4, 6);

//		setLayout(mainlayout);
//
//		constraints = new GridBagConstraints();
//		constraints.anchor = GridBagConstraints.NORTH;
//		constraints.weighty = 0.0;
//		constraints.weightx = 1.0;
//		constraints.insets = new Insets(5, 3, 2, 2);
//
//		layout = new GridBagLayout();
//		JPanel panel = new JPanel(layout);
//		panel.setBorder(BorderFactory.createLineBorder(ThemeManager
//				.getColor(HOColorName.PANEL_BORDER)));
//		panel.setBackground(getBackground());
//
//		JLabel label = new JLabel(HOVerwaltung.instance().getLanguageString("Heim"));
//		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
//		label.setHorizontalAlignment(SwingConstants.CENTER);
//		constraints.anchor = GridBagConstraints.CENTER;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 0.0;
//		constraints.gridx = 1;
//		constraints.gridy = 3;
//		constraints.gridwidth = 2;
//		constraints.gridheight = 1;
//		layout.setConstraints(label, constraints);
//		panel.add(label);
//
//		label = new JLabel(HOVerwaltung.instance().getLanguageString("Gast"));
//		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
//		label.setHorizontalAlignment(SwingConstants.CENTER);
//		constraints.anchor = GridBagConstraints.CENTER;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 0.0;
//		constraints.gridx = 5;
//		constraints.gridy = 3;
//		constraints.gridwidth = 1;
//		layout.setConstraints(label, constraints);
//		panel.add(label);
//
//		constraints.anchor = GridBagConstraints.WEST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 1.0;
//		constraints.gridx = 1;
//		constraints.gridy = 4;
//		n_jlHomeTeamName = new JLabel();
//		n_jlHomeTeamName.setPreferredSize(new Dimension(140, 14));
//		n_jlHomeTeamName.setFont(n_jlHomeTeamName.getFont().deriveFont(Font.BOLD));
//		layout.setConstraints(n_jlHomeTeamName, constraints);
//		panel.add(n_jlHomeTeamName);
//
//		constraints.anchor = GridBagConstraints.EAST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 1.0;
//		constraints.gridx = 2;
//		constraints.gridy = 4;
//		n_jlHomeTeamScore = new JLabel();
//		n_jlHomeTeamScore.setFont(n_jlHomeTeamScore.getFont().deriveFont(Font.BOLD));
//		layout.setConstraints(n_jlHomeTeamScore, constraints);
//		panel.add(n_jlHomeTeamScore);
//
//		constraints.anchor = GridBagConstraints.WEST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 1.0;
//		constraints.gridx = 5;
//		constraints.gridy = 4;
//		n_jlGuestTeamName = new JLabel();
//		n_jlGuestTeamName.setPreferredSize(new Dimension(140, 14));
//		n_jlGuestTeamName.setFont(n_jlGuestTeamName.getFont().deriveFont(Font.BOLD));
//		layout.setConstraints(n_jlGuestTeamName, constraints);
//		panel.add(n_jlGuestTeamName);
//
//		constraints.anchor = GridBagConstraints.EAST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 1.0;
//		constraints.gridx = 4;
//		constraints.gridy = 4;
//		n_jlGuestTeamScore = new JLabel();
//		n_jlGuestTeamScore.setFont(n_jlGuestTeamScore.getFont().deriveFont(Font.BOLD));
//		layout.setConstraints(n_jlGuestTeamScore, constraints);
//		panel.add(n_jlGuestTeamScore);
//
//		// Platzhalter
//		label = new JLabel(" ");
//		add(panel, label, layout, constraints, 0, 5);
//
//		// Bewertungen
//		// Mittelfeld
//		initRow(panel, "Gesamt", "Gesamt", 0, 6);
//
//		// Platzhalter
//		label = new JLabel(" ");
//		add(panel, label, layout, constraints, 0, 7);
//
//		initRow(panel, "ls.match.ratingsector.midfield", "ls.match.ratingsector.midfield", 1, 8);
//		initRow(panel, "ls.match.ratingsector.rightdefence", "ls.match.ratingsector.leftattack", 2, 9);
//		initRow(panel, "ls.match.ratingsector.centraldefence", "ls.match.ratingsector.centralattack", 3, 10);
//		initRow(panel, "ls.match.ratingsector.leftdefence", "ls.match.ratingsector.rightattack", 4, 11);
//		initRow(panel, "ls.match.ratingsector.rightattack", "ls.match.ratingsector.leftdefence", 5, 12);
//		initRow(panel, "ls.match.ratingsector.centralattack", "ls.match.ratingsector.centraldefence", 6, 13);
//		initRow(panel, "ls.match.ratingsector.leftattack", "ls.match.ratingsector.rightdefence", 7, 14);
//
//		mainconstraints.gridx = 0;
//		mainconstraints.gridy = 0;
//		mainlayout.setConstraints(panel, mainconstraints);
//		add(panel);
//
//		clear();
	}

//	private void initRow(JPanel panel, String txt1, String txt2, int index, int row) {
//		add(panel, new JLabel(HOVerwaltung.instance().getLanguageString(txt1)), layout,
//				constraints, 1, row);
//		add(panel, homePercent[index], layout, constraints, 2, row);
//		add(panel, bars[index], layout, constraints, 3, row);
//		add(panel, awayPercent[index], layout, constraints, 4, row);
//		add(panel, new JLabel(HOVerwaltung.instance().getLanguageString(txt2)), layout,
//				constraints, 5, row);
//
//	}

//	private void add(JPanel panel, JComponent label, GridBagLayout layout,
//			GridBagConstraints constraints, int x, int y) {
//		if (x == 0) {
//			constraints.weightx = 0.0;
//			constraints.gridwidth = 1;
//		} else {
//			constraints.weightx = 1.0;
//			constraints.gridwidth = 1;
//		}
//
//		constraints.gridx = x;
//		constraints.gridy = y;
//		constraints.anchor = GridBagConstraints.WEST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		layout.setConstraints(label, constraints);
//		panel.add(label);
//	}

	private void clear() {
		// TODO: repair this one
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
		rating.setValue(getPercent(val1, val2));
	}
}




