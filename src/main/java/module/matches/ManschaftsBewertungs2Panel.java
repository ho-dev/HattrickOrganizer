// %2517784300:de.hattrickorganizer.gui.matches%
package module.matches;

import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * Zeigt die Stärken eines Matches an
 */
class ManschaftsBewertungs2Panel extends LazyImagePanel {

	private static final long serialVersionUID = 1835093736247065469L;
	private JLabel gastTeamNameLabel;
	private JLabel heimTeamNameLabel;
	private JLabel heimTeamToreLabel;
	private JLabel gastTeamToreLabel;
	private JProgressBar[] bars;
	private JLabel[] homePercent;
	private JLabel[] awayPercent;
	private GridBagLayout layout;
	private GridBagConstraints constraints;
	private final MatchesModel matchesModel;

	/**
	 * Creates a new ManschaftsBewertungsPanel object.
	 */
	ManschaftsBewertungs2Panel(MatchesModel matchesModel) {
		this(matchesModel, false);
	}

	ManschaftsBewertungs2Panel(MatchesModel matchesModel, boolean print) {
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
		MatchKurzInfo info = this.matchesModel.getMatch();
		if (info == null) {
			clear();
			return;
		}

		if (info.getMatchStatus() != MatchKurzInfo.FINISHED) {
			clear();
		}

		heimTeamNameLabel.setText(info.getHeimName());
		gastTeamNameLabel.setText(info.getGastName());

		int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		if (info.getHeimID() == teamid) {
			heimTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.TEAM_FG));
		} else {
			heimTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
		}

		if (info.getGastID() == teamid) {
			gastTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.TEAM_FG));
		} else {
			gastTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
		}

		if (info.getMatchStatus() == MatchKurzInfo.FINISHED) {
			// Sterne für Sieger!
			if (info.getMatchStatus() != MatchKurzInfo.FINISHED) {
				heimTeamNameLabel.setIcon(null);
				gastTeamNameLabel.setIcon(null);
			} else if (info.getHeimTore() > info.getGastTore()) {
				heimTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR,
						Color.WHITE));
				gastTeamNameLabel.setIcon(null);
			} else if (info.getHeimTore() < info.getGastTore()) {
				heimTeamNameLabel.setIcon(null);
				gastTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR,
						Color.WHITE));
			} else {
				heimTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR,
						Color.WHITE));
				gastTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR,
						Color.WHITE));
			}

			Matchdetails details = this.matchesModel.getDetails();
			heimTeamToreLabel.setText(info.getHeimTore() + " (" + details.getHomeHalfTimeGoals()
					+ ") ");
			gastTeamToreLabel.setText(info.getGastTore() + " (" + details.getGuestHalfTimeGoals()
					+ ") ");

			setBarValue(0, details.getHomeGesamtstaerke(false),
					details.getGuestGesamtstaerke(false));
			setBarValue(1, details.getHomeMidfield(), details.getGuestMidfield());
			setBarValue(2, details.getHomeRightDef(), details.getGuestLeftAtt());
			setBarValue(3, details.getHomeMidDef(), details.getGuestMidAtt());
			setBarValue(4, details.getHomeLeftDef(), details.getGuestRightAtt());
			setBarValue(5, details.getHomeRightAtt(), details.getGuestLeftDef());
			setBarValue(6, details.getHomeMidAtt(), details.getGuestMidDef());
			setBarValue(7, details.getHomeLeftAtt(), details.getGuestRightDef());
		}
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
		int barCount = 8;
		bars = new JProgressBar[barCount];
		homePercent = new JLabel[barCount];
		awayPercent = new JLabel[barCount];
		for (int i = 0; i < barCount; i++) {
			bars[i] = new JProgressBar(0, 100);
			homePercent[i] = new JLabel(" ");
			awayPercent[i] = new JLabel(" ");
		}
		setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

		GridBagLayout mainlayout = new GridBagLayout();
		GridBagConstraints mainconstraints = new GridBagConstraints();
		mainconstraints.anchor = GridBagConstraints.NORTH;
		mainconstraints.fill = GridBagConstraints.HORIZONTAL;
		mainconstraints.weighty = 0.1;
		mainconstraints.weightx = 1.0;
		mainconstraints.insets = new Insets(4, 6, 4, 6);

		setLayout(mainlayout);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.weighty = 0.0;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 3, 2, 2);

		layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager
				.getColor(HOColorName.PANEL_BORDER)));
		panel.setBackground(getBackground());

		JLabel label = new JLabel(HOVerwaltung.instance().getLanguageString("Heim"));
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Gast"));
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 5;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 1;
		constraints.gridy = 4;
		heimTeamNameLabel = new JLabel();
		heimTeamNameLabel.setPreferredSize(new Dimension(140, 14));
		heimTeamNameLabel.setFont(heimTeamNameLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimTeamNameLabel, constraints);
		panel.add(heimTeamNameLabel);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 2;
		constraints.gridy = 4;
		heimTeamToreLabel = new JLabel();
		heimTeamToreLabel.setFont(heimTeamToreLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimTeamToreLabel, constraints);
		panel.add(heimTeamToreLabel);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 5;
		constraints.gridy = 4;
		gastTeamNameLabel = new JLabel();
		gastTeamNameLabel.setPreferredSize(new Dimension(140, 14));
		gastTeamNameLabel.setFont(gastTeamNameLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastTeamNameLabel, constraints);
		panel.add(gastTeamNameLabel);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 4;
		constraints.gridy = 4;
		gastTeamToreLabel = new JLabel();
		gastTeamToreLabel.setFont(gastTeamToreLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastTeamToreLabel, constraints);
		panel.add(gastTeamToreLabel);

		// Platzhalter
		label = new JLabel(" ");
		add(panel, label, layout, constraints, 0, 5);

		// Bewertungen
		// Mittelfeld
		initRow(panel, "Gesamt", "Gesamt", 0, 6);

		// Platzhalter
		label = new JLabel(" ");
		add(panel, label, layout, constraints, 0, 7);

		initRow(panel, "ls.match.ratingsector.midfield", "ls.match.ratingsector.midfield", 1, 8);
		initRow(panel, "ls.match.ratingsector.rightdefence", "ls.match.ratingsector.leftattack", 2, 9);
		initRow(panel, "ls.match.ratingsector.centraldefence", "ls.match.ratingsector.centralattack", 3, 10);
		initRow(panel, "ls.match.ratingsector.leftdefence", "ls.match.ratingsector.rightattack", 4, 11);
		initRow(panel, "ls.match.ratingsector.rightattack", "ls.match.ratingsector.leftdefence", 5, 12);
		initRow(panel, "ls.match.ratingsector.centralattack", "ls.match.ratingsector.centraldefence", 6, 13);
		initRow(panel, "ls.match.ratingsector.leftattack", "ls.match.ratingsector.rightdefence", 7, 14);

		mainconstraints.gridx = 0;
		mainconstraints.gridy = 0;
		mainlayout.setConstraints(panel, mainconstraints);
		add(panel);

		clear();
	}

	private void initRow(JPanel panel, String txt1, String txt2, int index, int row) {
		add(panel, new JLabel(HOVerwaltung.instance().getLanguageString(txt1)), layout,
				constraints, 1, row);
		add(panel, homePercent[index], layout, constraints, 2, row);
		add(panel, bars[index], layout, constraints, 3, row);
		add(panel, awayPercent[index], layout, constraints, 4, row);
		add(panel, new JLabel(HOVerwaltung.instance().getLanguageString(txt2)), layout,
				constraints, 5, row);

	}

	private void add(JPanel panel, JComponent label, GridBagLayout layout,
			GridBagConstraints constraints, int x, int y) {
		if (x == 0) {
			constraints.weightx = 0.0;
			constraints.gridwidth = 1;
		} else {
			constraints.weightx = 1.0;
			constraints.gridwidth = 1;
		}

		constraints.gridx = x;
		constraints.gridy = y;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);
	}

	private void clear() {
		heimTeamNameLabel.setText(" ");
		gastTeamNameLabel.setText(" ");
		heimTeamToreLabel.setText(" ");
		gastTeamToreLabel.setText(" ");
		heimTeamNameLabel.setIcon(null);
		gastTeamNameLabel.setIcon(null);

		for (int i = 0; i < bars.length; i++) {
			bars[i].setValue(0);
			homePercent[i].setText(" ");
			awayPercent[i].setText(" ");
		}
	}

	private void setBarValue(int index, float home, float away) {
		bars[index].setValue((int) getPercent(home, away));
		bars[index].setToolTipText(bars[index].getValue() + " %" + " -- "
				+ (100 - bars[index].getValue()) + " %");
		homePercent[index].setText(bars[index].getValue() + " %");
		awayPercent[index].setText((100 - bars[index].getValue()) + " %");
		bars[index].setForeground(bars[index].getValue() < 50 ? ThemeManager
				.getColor(HOColorName.MATCHDETAILS_PROGRESSBAR_RED) : ThemeManager
				.getColor(HOColorName.MATCHDETAILS_PROGRESSBAR_GREEN));
		bars[index].setBackground(bars[index].getValue() < 50 ? ThemeManager
				.getColor(HOColorName.MATCHDETAILS_PROGRESSBAR_GREEN) : ThemeManager
				.getColor(HOColorName.MATCHDETAILS_PROGRESSBAR_RED));
	}

	private float getPercent(float home, float opponnent) {
		return home * 100 / (home + opponnent);
	}
}
