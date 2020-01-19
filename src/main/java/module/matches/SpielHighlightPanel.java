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
	private JLabel gastTeamNameLabel;
	private JLabel gastTeamToreLabel;
	private JLabel heimTeamNameLabel;
	private JLabel heimTeamToreLabel;
	private JPanel panel;
	private List<Component> highlightLabels;
	private final MatchesModel matchesModel;

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
			removeHighlights(); 
			
			Matchdetails details = this.matchesModel.getDetails();

			JLabel playerlabel, matchEventPlayer, resultlabel ;

			List<MatchEvent> matchHighlights = details.getHighlights();
			ImageIcon icon;
			Boolean bEventHighlighted;

			int homeScore = 0;
			int guestScore = 0;
			int homeScore_ht = 0;
			int guestScore_ht = 0;

			String scoreText;
			boolean homeAction = false;

			for (int i = 0; i < matchHighlights.size(); i++) {
				scoreText = "";
				MatchEvent highlight = matchHighlights.get(i);

				if (highlight.isGoalEvent() || highlight.isNonGoalEvent() || highlight.isBruisedOrInjured() || highlight.isBooked()
						|| (highlight.getMatchEventID() == MatchEvent.MatchEventID.INJURED_PLAYER_REPLACED))
				{
					bEventHighlighted = true;
				}
				else
				{
					bEventHighlighted = false;
					if (highlight.getMatchEventID() == MatchEvent.MatchEventID.SECOND_HALF_STARTED)
					{
						homeScore_ht = homeScore;
						guestScore_ht = guestScore;
					}
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

					else if(highlight.getMatchEventID() == MatchEvent.MatchEventID.INJURED_PLAYER_REPLACED)
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
//					playerlabel.setForeground(MatchesHelper.getColor4SpielHighlight(highlight));
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
			}

			// stars for winner !
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
				heimTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR_GRAY,
						Color.WHITE));
				gastTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR_GRAY,
						Color.WHITE));
			}

			heimTeamToreLabel.setText(homeScore + " (" + homeScore_ht + ") ");
			gastTeamToreLabel.setText(guestScore + " (" + guestScore_ht + ") ");

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
//		mainconstraints.weighty = 0.1;
		mainconstraints.weightx = 0.0;
		mainconstraints.insets = new Insets(4, 6, 4, 6);

		setLayout(mainlayout);

		layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
//		constraints.weighty = 0.0;
		constraints.weightx = 0.0;
		constraints.insets = new Insets(5, 3, 2, 2);

		panel = new JPanel(layout);
		panel.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)),
				BorderFactory.createEmptyBorder(10, 5, 10, 0)));
		panel.setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

//		// Platzhalter
		JLabel label = new JLabel("   ");
//		constraints.anchor = GridBagConstraints.WEST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 0.0;
//		constraints.gridx = 4;
//		constraints.gridy = 1;
//		constraints.gridheight = 30;
//		constraints.gridwidth = 1;
//		layout.setConstraints(label, constraints);
//		panel.add(label);

//		label = new JLabel(HOVerwaltung.instance().getLanguageString("Heim"));
//		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
//		label.setHorizontalAlignment(SwingConstants.CENTER);
//		constraints.anchor = GridBagConstraints.CENTER;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 0.0;
//		constraints.gridx = 2;
//		constraints.gridy = 1;
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
//		constraints.gridy = 1;
//		constraints.gridwidth = 2;
//		layout.setConstraints(label, constraints);
//		panel.add(label);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 3;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		heimTeamNameLabel = new JLabel();
		heimTeamNameLabel.setPreferredSize(new Dimension(140, 14));
		heimTeamNameLabel.setFont(heimTeamNameLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimTeamNameLabel, constraints);
		panel.add(heimTeamNameLabel);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 3;
		constraints.gridy = 2;
		heimTeamToreLabel = new JLabel();
		heimTeamToreLabel.setFont(heimTeamToreLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimTeamToreLabel, constraints);
		panel.add(heimTeamToreLabel);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 4;
		constraints.gridy = 1;
		gastTeamNameLabel = new JLabel();
		gastTeamNameLabel.setPreferredSize(new Dimension(140, 14));
		gastTeamNameLabel.setFont(gastTeamNameLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastTeamNameLabel, constraints);
		panel.add(gastTeamNameLabel);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 4;
		constraints.gridy = 2;
		gastTeamToreLabel = new JLabel();
		gastTeamToreLabel.setFont(gastTeamToreLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastTeamToreLabel, constraints);
		panel.add(gastTeamToreLabel);

//		// Platzhalter
//		constraints.anchor = GridBagConstraints.EAST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 0.0;
//		constraints.gridx = 7;
//		constraints.gridy = 2;
//		label = new JLabel("    ");
//		layout.setConstraints(label, constraints);
//		panel.add(label);

//		constraints.anchor = GridBagConstraints.EAST;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.weightx = 0.0;
//		constraints.gridx = 0;
//		constraints.gridy = 3;
//		label = new JLabel(" ");
//		layout.setConstraints(label, constraints);
//		panel.add(label);

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

		heimTeamNameLabel.setText(" ");
		gastTeamNameLabel.setText(" ");
		heimTeamToreLabel.setText(" ");
		gastTeamToreLabel.setText(" ");
		heimTeamNameLabel.setIcon(null);
		gastTeamNameLabel.setIcon(null);
	}

	private void removeHighlights() {
		for (Component c : this.highlightLabels) {
			panel.remove(c);
		}
		this.highlightLabels.clear();
	}

}
