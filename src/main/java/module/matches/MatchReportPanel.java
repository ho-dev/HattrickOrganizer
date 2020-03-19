// %4112883594:de.hattrickorganizer.gui.matches%
package module.matches;

import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchEvent;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.CompoundBorder;

/**
 * Zeigt die Stärken eines Matches an.
 */
public class MatchReportPanel extends LazyImagePanel {

	private static final long serialVersionUID = -9014579382145462648L;
	private GridBagConstraints constraints;
	private GridBagLayout layout;
	private JLabel penaltyContestresults;
	private JPanel panel;
	JScrollPane pane;
	private List<Component> highlightLabels;
	private final MatchesModel matchesModel;
	private boolean bPenaltyContest = false;

	public enum ActionTypeCategory {
		ACTION_TYPE_NEUTRAL(1),
		ACTION_TYPE_HOME(2),
		ACTION_TYPE_AWAY(3);

		private final int value;

		ActionTypeCategory(final int newValue) {
			value = newValue;
		}

		public int getValue() { return value; }
	}

	/**
	 * Creates a new SpielHighlightPanel object.
	 */
	public MatchReportPanel(MatchesModel matchesModel) {
		this(matchesModel, false);
	}

	/**
	 * Creates a new SpielHighlightPanel object.
	 *
	 * @param print
	 *            if true: use printer version (no colored background)
	 */
	public MatchReportPanel(MatchesModel matchesModel, boolean print) {
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
		clear();
		MatchKurzInfo info = this.matchesModel.getMatch();
		if (info == null) {
			return;
		}


		if (info.getMatchStatus() == MatchKurzInfo.FINISHED) {
			clear();

			Matchdetails details = this.matchesModel.getDetails();

			JLabel timeLabel, playerlabel, matchEventPlayer;

			List<MatchEvent> matchHighlights = details.getHighlights();
			ImageIcon icon;
			Boolean bEventHighlighted;

			int homePenalitiesScored=0;
			int guestPenalitiesScored=0;

			ActionTypeCategory actionType;
			Font f;


			for (int i = 0; i < matchHighlights.size(); i++) {
				MatchEvent highlight = matchHighlights.get(i);
				bEventHighlighted = true;

				if (highlight.getEventText().isEmpty())
				{
					bEventHighlighted = false;
				}

				// Displaying the event
				if (bEventHighlighted) {

					// set action type for color code
					if (highlight.getTeamID() == 0)
					{
						actionType = ActionTypeCategory.ACTION_TYPE_NEUTRAL;
					}
					else if (highlight.isNeutralEvent())
						{
							actionType = ActionTypeCategory.ACTION_TYPE_NEUTRAL;
						}
					else if (highlight.getTeamID() == info.getHeimID())
					{
						actionType = ActionTypeCategory.ACTION_TYPE_HOME;
					}
					else
					{
						actionType = ActionTypeCategory.ACTION_TYPE_AWAY;
					}

					icon = highlight.getIcon();

//					String spielername = highlight.getSpielerName();
//					if (spielername.length() > 30) {
//						spielername = spielername.substring(0, 29);
//					}
//					spielername += (" (" + highlight.getMinute() + "')");
//
//					if (highlight.isGoalEvent()) {
//						if (homeAction) {
//							homeScore++;
//							scoreText = "<html><b>" + homeScore + "</b> - " + guestScore + "</html>";
//						}
//						else {
//							guestScore++;
//							scoreText = "<html>" + homeScore + " - <b>" + guestScore + "</b></html>";
//						}
//					}

//					if (highlight.isPenaltyContestGoalEvent()) {
//						if (homeAction) {homePenalitiesScored++;}
//						else {guestPenalitiesScored++;}
//					}
//
//					else if(highlight.isSubstitution())
//					{
//						spielername = highlight.getSpielerName();
//						if (spielername.length() > 30) {
//							spielername = spielername.substring(0, 29);
//						}
//
//						String playerEntering =  highlight.getGehilfeName();
//						if (playerEntering.length() > 30) { playerEntering = playerEntering.substring(0, 29);}
//
//						spielername = "<html>" + spielername + "<br>" + playerEntering + " (" + highlight.getMinute() + "')</html>";
//
//					}
					playerlabel = new JLabel("", icon, SwingConstants.LEFT);
//					{
//						@Override
//						public Dimension getMinimumSize() {
//							return new Dimension(48, 30);
//						}};
					playerlabel.setToolTipText(MatchEvent.getEventTextDescription(highlight.getiMatchEventID()));

					timeLabel = new JLabel("", SwingConstants.CENTER)
					{
						@Override
						public Dimension getMinimumSize() {
							return new Dimension(25, 25);
						}

						@Override
						public Dimension getMaximumSize() {
							return new Dimension(25, 250);
						}
					};

					// Match Events Label
					if (actionType == ActionTypeCategory.ACTION_TYPE_HOME) {
						playerlabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
						timeLabel.setBackground(Color.decode("#6ECDEA"));
					}
					else if (actionType == ActionTypeCategory.ACTION_TYPE_AWAY)
					{
						playerlabel.setBorder(BorderFactory.createEmptyBorder(0, 41, 0, 0));
						timeLabel.setBackground(Color.decode("#d15e5e"));
					}
					else {
						playerlabel.setBorder(BorderFactory.createEmptyBorder(0, 23, 0, 0));
						timeLabel.setBackground(Color.decode("#a6a6a6"));
					}

					timeLabel.setText(highlight.getMinute()+"'");
					timeLabel.setOpaque(true);
					f = timeLabel.getFont();
					timeLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
					constraints.anchor = GridBagConstraints.LINE_START;
					constraints.fill = GridBagConstraints.BOTH;
//					constraints.fill = GridBagConstraints.VERTICAL;

					constraints.weightx = 0.0;
					constraints.gridx = 2;
					constraints.gridy = i + 4;
					constraints.gridwidth = 1;
					constraints.insets = new Insets(10,0,0,20);
					layout.setConstraints(timeLabel, constraints);
					panel.add(timeLabel);

					constraints.anchor = GridBagConstraints.LINE_START;
					constraints.fill = GridBagConstraints.HORIZONTAL;
					constraints.fill = GridBagConstraints.VERTICAL;
					constraints.weightx = 0.0;
//					constraints.weighty = 1.0;
					constraints.gridy = i + 4;
					constraints.gridwidth = 1;
					constraints.gridx = 3;
					constraints.insets = new Insets(10,0,0,20);
					layout.setConstraints(playerlabel, constraints);
					panel.add(playerlabel);

					matchEventPlayer = new JLabel("<html>"+highlight.getEventText()+"</html>");
					matchEventPlayer.setVerticalAlignment(JLabel.CENTER);
					highlightLabels.add(matchEventPlayer);
					constraints.anchor = GridBagConstraints.LINE_START;
					constraints.fill = GridBagConstraints.HORIZONTAL;
					constraints.weightx = 1.0;
//					constraints.weighty = 2.0;
					constraints.gridy = i + 4;
					constraints.gridwidth = 1;
					constraints.gridx = 4;
					constraints.insets = new Insets(10,0,0,0);
					layout.setConstraints(matchEventPlayer, constraints);
					panel.add(matchEventPlayer);

					// Add labels to the highlight vector
					highlightLabels.add(playerlabel);
					highlightLabels.add(timeLabel);

				}

				else if (highlight.getMatchEventID() == MatchEvent.MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION)
				{
					constraints.anchor = GridBagConstraints.CENTER;
					constraints.fill = GridBagConstraints.HORIZONTAL;
					constraints.weightx = 1.0;
					constraints.gridx = 0;
					constraints.gridy = i + 4;
					constraints.gridwidth = 5;
					JLabel penaltyContestLabel = new JLabel(HOVerwaltung.instance().getLanguageString("MatchEvent_71_report"), SwingConstants.CENTER);
					f = penaltyContestLabel.getFont();
					penaltyContestLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
					penaltyContestLabel.setBackground(Color.lightGray);
					penaltyContestLabel.setOpaque(true);
					layout.setConstraints(penaltyContestLabel, constraints);
					panel.add(penaltyContestLabel);
					highlightLabels.add(penaltyContestLabel);
					bPenaltyContest = true;
				}
			}

//			String title = info.getHeimName() + "  " + homeScore + "   -   " + guestScore + "  " + info.getGastName() ;
//			matchTeamsAndScores.setText(title);

			if (bPenaltyContest) {
				String subtitle = HOVerwaltung.instance().getLanguageString("penalites") + "  (" + homePenalitiesScored + "-" + guestPenalitiesScored + ")";
				penaltyContestresults.setText(subtitle);
			}

		}
	}

	private void addListeners() {this.matchesModel.addMatchModelChangeListener(() -> setNeedsRefresh(true));}

	private void initComponents() {
		highlightLabels = new ArrayList<>();

		setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

		GridBagLayout mainlayout = new GridBagLayout();
		GridBagConstraints mainconstraints = new GridBagConstraints();
		mainconstraints.anchor = GridBagConstraints.NORTH;
		mainconstraints.fill = GridBagConstraints.HORIZONTAL;
		mainconstraints.weightx = 1.0;
		mainconstraints.insets = new Insets(4, 6, 4, 6);

		setLayout(mainlayout);

		layout = new GridBagLayout();
		panel = new JPanel(layout);
		panel.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)),
				BorderFactory.createEmptyBorder(10, 5, 10, 0)));
		panel.setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));


//		constraints.anchor = GridBagConstraints.EAST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 1.0;
//		constraints.gridx = 2;
//		constraints.gridy = 1;
//		constraints.gridwidth = 4;
//		matchTeamsAndScores = new JLabel("", SwingConstants.CENTER);
//		matchTeamsAndScores.setFont(matchTeamsAndScores.getFont().deriveFont(Font.BOLD));
//		layout.setConstraints(matchTeamsAndScores, constraints);
//		panel.add(matchTeamsAndScores);

		constraints = new GridBagConstraints();
//		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(4, 6, 4, 6);
//		constraints.weightx = 0.0;
//		constraints.gridx = 2;
//		constraints.gridy = 2;
//		constraints.gridwidth = 4;
		penaltyContestresults = new JLabel("", SwingConstants.CENTER);
		penaltyContestresults.setFont(penaltyContestresults.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(penaltyContestresults, constraints);
		panel.add(penaltyContestresults);

		mainconstraints.gridx = 0;
		mainconstraints.gridy = 0;

		mainlayout.setConstraints(panel, mainconstraints);
		add(panel);

//		pane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		mainlayout.setConstraints(pane, mainconstraints);
//		add(pane);

		clear();
	}

	/**
	 * Clear all highlights.
	 */
	private void clear() {
		removeHighlights();
//		matchTeamsAndScores.setText(" ");
		penaltyContestresults.setText(" ");
		bPenaltyContest = false;

	}

	private void removeHighlights() {
		for (Component c : this.highlightLabels) {
			panel.remove(c);
		}
		this.highlightLabels.clear();
	}

}
