// %4112883594:de.hattrickorganizer.gui.matches%
package module.matches;

import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;

/**
 * Zeigt die Stärken eines Matches an.
 */
public class SpielHighlightPanel extends LazyImagePanel {

	private static final long serialVersionUID = -6491501224900464573L;
	private GridBagConstraints constraints;
	private GridBagLayout layout;
	private JLabel matchTeamsAndScores;
	private JLabel penaltyContestresults;
	private JPanel panel;
	private List<Component> highlightLabels;
	private final MatchesModel matchesModel;
	private boolean bPenaltyContest = false;

	/**
	 * Creates a new SpielHighlightPanel object.
	 */
	public SpielHighlightPanel(MatchesModel matchesModel) {
		this(matchesModel, false);
	}

	/**
	 * Creates a new SpielHighlightPanel object.
	 * 
	 * @param print
	 *            if true: use printer version (no colored background)
	 */
	public SpielHighlightPanel(MatchesModel matchesModel, boolean print) {
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

			JLabel playerlabel, matchEventPlayer, resultlabel ;

			List<MatchEvent> matchHighlights = details.getHighlights();
			ImageIcon icon;
			Boolean bEventHighlighted;

			int homeScore = 0;
			int guestScore = 0;
			int homePenalitiesScored=0;
			int guestPenalitiesScored=0;

			String scoreText;
			boolean homeAction;


			for (int i = 0; i < matchHighlights.size(); i++) {
				scoreText = "";
				MatchEvent highlight = matchHighlights.get(i);

				if (highlight.isGoalEvent() || highlight.isNonGoalEvent() || highlight.isBruisedOrInjured() || highlight.isBooked()
						|| highlight.isSubstitution() || highlight.isPenaltyContestGoalEvent() || highlight.isPenaltyContestNoGoalEvent())
				{
					bEventHighlighted = true;
				}
				else
				{
					bEventHighlighted = false;
				}

				// Displaying the event
				if (bEventHighlighted) {

					homeAction = (highlight.getTeamID() == info.getHeimID());
					icon = highlight.getIcon();

					String spielername = highlight.getSpielerName();
					if (spielername.length() > 30) {
						spielername = spielername.substring(0, 29);
					}
					spielername += (" (" + highlight.getMinute() + "')");

					if (highlight.isGoalEvent()) {
						if (homeAction) {
							homeScore++;
							scoreText = "<html><b>" + homeScore + "</b> - " + guestScore + "</html>";
						}
						else {
							guestScore++;
							scoreText = "<html>" + homeScore + " - <b>" + guestScore + "</b></html>";
						}
					}

					if (highlight.isPenaltyContestGoalEvent()) {
						if (homeAction) {homePenalitiesScored++;}
						else {guestPenalitiesScored++;}
					}

					else if(highlight.isSubstitution())
					{
						spielername = highlight.getSpielerName();
						if (spielername.length() > 30) {
							spielername = spielername.substring(0, 29);
						}

						String playerEntering =  highlight.getGehilfeName();
						if (playerEntering.length() > 30) { playerEntering = playerEntering.substring(0, 29);}

						spielername = "<html>" + spielername + "<br>" + playerEntering + " (" + highlight.getMinute() + "')</html>";

					}
					playerlabel = new JLabel("", icon, SwingConstants.LEFT)
					{
						@Override
						public Dimension getMinimumSize() {
							return new Dimension(48, 30);
						}};
					playerlabel.setToolTipText(MatchEvent.getEventTextDescription(highlight.getiMatchEventID()));

					resultlabel = new JLabel(scoreText);

				    // Add labels to the highlight vector
					highlightLabels.add(playerlabel);
					highlightLabels.add(resultlabel);

					// Match Events Label
					if (homeAction) {
						playerlabel.setBorder(new CompoundBorder(
								BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#6ECDEA")),
								BorderFactory.createEmptyBorder(0, 5, 0, 0)));
					} else {
						playerlabel.setBorder(new CompoundBorder(
								BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#d15e5e")),
								BorderFactory.createEmptyBorder(0, 23, 0, 0)));
					}

					constraints.anchor = GridBagConstraints.WEST;
					constraints.fill = GridBagConstraints.HORIZONTAL;
					constraints.weightx = 0.0;
					constraints.gridy = i + 4;
					constraints.gridwidth = 1;
					constraints.gridx = 2;
					constraints.insets = new Insets(14,0,0,20);
					layout.setConstraints(playerlabel, constraints);
					panel.add(playerlabel);

					matchEventPlayer = new JLabel(spielername);
					highlightLabels.add(matchEventPlayer);
					constraints.anchor = GridBagConstraints.LINE_START;
					constraints.fill = GridBagConstraints.HORIZONTAL;
					constraints.weightx = 0.0;
					constraints.gridy = i + 4;
					constraints.gridwidth = 1;
					constraints.gridx = 3;
					layout.setConstraints(matchEventPlayer, constraints);
					panel.add(matchEventPlayer);

					constraints.anchor = GridBagConstraints.EAST;
					constraints.fill = GridBagConstraints.HORIZONTAL;
					constraints.weightx = 1.0;
					constraints.gridx = 4;
					constraints.gridy = i + 4;
					constraints.gridwidth = 1;
					layout.setConstraints(resultlabel, constraints);
					panel.add(resultlabel);
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
					Font f = penaltyContestLabel.getFont();
					penaltyContestLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
					penaltyContestLabel.setBackground(Color.lightGray);
					penaltyContestLabel.setOpaque(true);
					layout.setConstraints(penaltyContestLabel, constraints);
					panel.add(penaltyContestLabel);
					highlightLabels.add(penaltyContestLabel);
					bPenaltyContest = true;
				}
			}

			String title = info.getHeimName() + "  " + homeScore + "   -   " + guestScore + "  " + info.getGastName() ;
			matchTeamsAndScores.setText(title);

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
		mainconstraints.weightx = 0.0;
		mainconstraints.insets = new Insets(4, 6, 4, 6);

		setLayout(mainlayout);

		layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.weightx = 0.0;
		constraints.insets = new Insets(5, 3, 2, 2);

		panel = new JPanel(layout);
		panel.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)),
				BorderFactory.createEmptyBorder(10, 5, 10, 0)));
		panel.setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));


		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridwidth = 4;
		matchTeamsAndScores = new JLabel("", SwingConstants.CENTER);
		matchTeamsAndScores.setFont(matchTeamsAndScores.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(matchTeamsAndScores, constraints);
		panel.add(matchTeamsAndScores);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridwidth = 4;
		penaltyContestresults = new JLabel("", SwingConstants.CENTER);
		penaltyContestresults.setFont(penaltyContestresults.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(penaltyContestresults, constraints);
		panel.add(penaltyContestresults);



		mainconstraints.gridx = 0;
		mainconstraints.gridy = 0;
		mainlayout.setConstraints(panel, mainconstraints);
		add(panel);

		clear();
	}

	/**
	 * Clear all highlights.
	 */
	private void clear() {
		removeHighlights();
		matchTeamsAndScores.setText(" ");
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
