package module.matches;

import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.model.match.MatchEvent;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchResultFormatter;
import core.model.match.Matchdetails;
import core.util.HtmlUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Zeigt die Stärken eines Matches an.
 */
public class SpielHighlightPanel extends LazyImagePanel {

    private static final Color HOME_COLOR = new Color(110, 205, 234);
    private static final Color GUEST_COLOR = new Color(209, 94, 94);

	private GridBagConstraints constraints;
	private GridBagLayout layout;
	private JLabel matchTeamsAndScores;
	private JLabel penaltyContestresults;
	private JPanel panel;
	private List<Component> highlightLabels;
	private final MatchesModel matchesModel;

	/**
	 * Creates a new SpielHighlightPanel object.
	 */
	public SpielHighlightPanel(MatchesModel matchesModel) {
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

			List<MatchEvent> matchEvents = details.downloadHighlightsIfMissing();

            final var highlights = MatchEventsAnalyser.analyse(info.getHomeTeamID(), matchEvents);

            AtomicInteger index = new AtomicInteger();

            highlights.regularAndExtraTime().highlights().forEach(highlight -> {
                String spielername;
                if (highlight.isSubstitution()) {
                    spielername = toPlayerNameShortened(highlight.getPlayerName());
                    final String playerEntering = toPlayerNameShortened(highlight.getAssistingPlayerName());
                    spielername = "<html>" + spielername + "<br>" + playerEntering + " " + createMinuteText(highlight) + "</html>";
                } else {
                    spielername = toPlayerNameShortened(highlight.getPlayerName());
                    spielername += " " + createMinuteText(highlight);
                }

                final String scoreText = highlight.isGoalEvent() ? createScoreText(highlight) : "";

                addToList(index.get(), highlight, spielername, scoreText);

                index.incrementAndGet();
            });

            highlights.getPenaltyContest().ifPresent(penaltyContest ->
                penaltyContest.highlights().forEach(highlight -> {
                    if (highlight.isPenaltyContestStartEvent()) {
                        addPenaltyContestToList(index.get(), highlight);
                    } else if (highlight.isPenaltyContestDecisionByCoinToss()) {
                        addPenaltyContestTossingCoinToList(index.get(), info, highlight);
                    } else {
                        final String spielername = toPlayerNameShortened(highlight.getPlayerName()) + " " + createMinuteText(highlight);
                        final String scoreText = highlight.isPenaltyContestEvent() ? createScoreText(highlight) : "";

                        addToList(index.get(), highlight, spielername, scoreText);
                    }

                    index.incrementAndGet();
                }));

            final var scoreAfterRegularAndExtraTime = highlights.regularAndExtraTime().score();

            String title = info.getHomeTeamName() + "  " + resultLabelLong(scoreAfterRegularAndExtraTime) + "  " + info.getGuestTeamName();
			matchTeamsAndScores.setText(title);

            highlights.getPenaltyContest().ifPresent(contest -> {
                String subtitle = TranslationFacility.tr("penalites") + "  (" + resultLabel(contest.penalitiesScored()) + ")";
                penaltyContestresults.setText(subtitle);
            });
		}
	}

    private void addPenaltyContestToList(int index, MatchHighlight highlight) {
        final var text = TranslationFacility.tr("MatchEvent_71_report") + " " + createMinuteText(highlight);
        addMatchPartToList(index, text);
    }

    private void addPenaltyContestTossingCoinToList(int index, MatchKurzInfo info, MatchHighlight highlight) {
        final boolean homeTeamWon = highlight.isHomeAction();
        final var teamName = homeTeamWon ? info.getHomeTeamName() : info.getGuestTeamName();
        final var text =
            TranslationFacility.tr("MatchHighlightPanel.decision_by_tossing_coin", teamName) + " " + createMinuteText(highlight);

        final JLabel label = addMatchPartToList(index, text);
        if (homeTeamWon) {
            label.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, HOME_COLOR),
                BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        } else {
            label.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, GUEST_COLOR),
                BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        }
    }

    private JLabel addMatchPartToList(int index, String text) {
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.gridx = 0;
        constraints.gridy = index + 4;
        constraints.gridwidth = 5;
        JLabel matchPartLabel = new JLabel(text, SwingConstants.CENTER);
        Font f = matchPartLabel.getFont();
        matchPartLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
        matchPartLabel.setBackground(Color.lightGray);
        matchPartLabel.setOpaque(true);
        layout.setConstraints(matchPartLabel, constraints);
        panel.add(matchPartLabel);
        highlightLabels.add(matchPartLabel);
        return matchPartLabel;
    }

    private void addToList(int index, MatchHighlight highlight, String playerName, String scoreText) {
        var playerLabel = new JPanel();
        playerLabel.setLayout(new BoxLayout(playerLabel, BoxLayout.Y_AXIS));
        highlight.getIcons().forEach(icon -> playerLabel.add(new JLabel(icon)));
        playerLabel.setToolTipText(MatchEvent.getEventTextDescription(highlight.getMatchEventID()));

        var resultLabel = new JLabel(scoreText);

        // Add labels to the highlight vector
        highlightLabels.add(playerLabel);
        highlightLabels.add(resultLabel);

        // Match Events Label
        if (highlight.isHomeAction()) {
            playerLabel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, HOME_COLOR),
                BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        } else {
            playerLabel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, GUEST_COLOR),
                BorderFactory.createEmptyBorder(0, 23, 0, 0)));
        }

        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.gridy = index + 4;
        constraints.gridwidth = 1;
        constraints.gridx = 2;
        constraints.insets = new Insets(14,0,0,20);
        layout.setConstraints(playerLabel, constraints);
        panel.add(playerLabel);

        var matchEventPlayer = new JLabel(playerName);
        highlightLabels.add(matchEventPlayer);
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.gridy = index + 4;
        constraints.gridwidth = 1;
        constraints.gridx = 3;
        layout.setConstraints(matchEventPlayer, constraints);
        panel.add(matchEventPlayer);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.gridx = 4;
        constraints.gridy = index + 4;
        constraints.gridwidth = 1;
        layout.setConstraints(resultLabel, constraints);
        panel.add(resultLabel);
    }

    private static String createMinuteText(MatchHighlight highlight) {
        return "(%d')".formatted(highlight.getMinute());
    }

    private static String createScoreText(MatchHighlight highlight) {
        if (highlight.homeScored() || highlight.guestScored()) {
            final String homeScoreStr = String.valueOf(highlight.score().homeScore());
            final String guestScoreStr = String.valueOf(highlight.score().guestScore());
            if (highlight.homeScored()) {
                return HtmlUtils.toHtml(resultLabelFromStrings(HtmlUtils.toBold(homeScoreStr), guestScoreStr));
            } else {
                return HtmlUtils.toHtml(resultLabelFromStrings(homeScoreStr, HtmlUtils.toBold(guestScoreStr)));
            }
        }
        return "";
    }

    private static String resultLabelLong(MatchScore score) {
        return "%d   %s   %d".formatted(score.homeScore(), TranslationFacility.tr(MatchResultFormatter.TRANSLATION_KEY_SEPARATOR), score.guestScore());
    }

    private static String resultLabel(MatchScore score) {
        return resultLabelFromStrings(String.valueOf(score.homeScore()), String.valueOf(score.guestScore()));
    }

    private static String resultLabelFromStrings(String homeScore, String guestScore) {
        return "%s %s %s".formatted(homeScore, TranslationFacility.tr(MatchResultFormatter.TRANSLATION_KEY_SEPARATOR), guestScore);
    }

    private static String toPlayerNameShortened(String playerName) {
        if (StringUtils.length(playerName) > 30) {
            playerName = StringUtils.substring(playerName, 0, 29);
        }
        return playerName;
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
	}

	private void removeHighlights() {
		for (Component c : this.highlightLabels) {
			panel.remove(c);
		}
		this.highlightLabels.clear();
	}
}
