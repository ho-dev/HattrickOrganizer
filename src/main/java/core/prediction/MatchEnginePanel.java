// %127697663:de.hattrickorganizer.gui.matchprediction%
package core.prediction;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.prediction.engine.MatchPredictionManager;
import core.prediction.engine.MatchResult;
import core.prediction.engine.TeamData;
import core.prediction.engine.TeamRatings;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.vo.MatchRating;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MatchEnginePanel extends ImagePanel implements	 ActionListener {
	private static final long serialVersionUID = 4911590394636764762L;

	JButton m_jbButton = new JButton(HOVerwaltung.instance().getLanguageString("Simulate"));

	private JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
	private MatchResultTable m_jtMatchResultTable;
	private MatchScoreDiffTable m_jtMatchScoreDiffTable;
	private MatchScoreTable m_jtMatchScoreTable;
	private PredictPanel predictPanel;
	private TeamRatingPanel homeTeamPanel;
	private TeamRatingPanel guestTeamPanel;
	private boolean isHomeMatch = true;

	public MatchEnginePanel(TeamData homeTeamValues,
			TeamData guestTeamValues) {
		if (guestTeamValues.getTeamName().startsWith(
				HOVerwaltung.instance().getModel().getBasics().getTeamName())) {
			isHomeMatch = false;
		}
		homeTeamPanel = new TeamRatingPanel(homeTeamValues);
		guestTeamPanel = new TeamRatingPanel(guestTeamValues);
		predictPanel = new PredictPanel(homeTeamValues.getTeamName(),
				guestTeamValues.getTeamName());

		m_jtMatchResultTable = new MatchResultTable(new MatchResult(),isHomeMatch);
		m_jtMatchScoreTable = new MatchScoreTable(new MatchResult(),isHomeMatch);
		m_jtMatchScoreDiffTable = new MatchScoreDiffTable(new MatchResult(),isHomeMatch);

		jbInit();
	}

	public final void setGuestteam(TeamData guestteam) {
		guestTeamPanel.setTeamData(guestteam);
		predictPanel.setGuestTeamName(guestteam.getTeamName());
	}

	public final void setHometeam(TeamData hometeam) {
		homeTeamPanel.setTeamData(hometeam);
		predictPanel.setHomeTeamName(hometeam.getTeamName());
	}

	public final int getNumberOfMatches() {
		return slider.getValue();
	}

	public final void actionPerformed(ActionEvent e) {
		calculateNMatches(getNumberOfMatches());
		TeamRatingPanel opponentPanel = getOpponentPanel();
		if ( opponentPanel.isRatingsChanged()){
			// user has changed the match ratings
			SystemManager.adjustRatingsLineup(opponentPanel.getTeamData());
		}
	}

	private TeamRatingPanel getOpponentPanel() {
		if ( isHomeMatch ){
			return guestTeamPanel;
		}
		return homeTeamPanel;
	}

	/**
	 * Calculates numberOfMatches matches and show the results uses
	 * getNumberOfMatches() as parameter
	 */
	public final void calculateNMatches(int numberOfMatches) {
		int match = (1 + numberOfMatches) * 1000;
		var mainframe = HOMainFrame.instance();
		mainframe.resetInformation();
		MatchResult result = new MatchResult();
		final TeamData team1 = homeTeamPanel.getTeamData();
		final TeamData team2 = guestTeamPanel.getTeamData();
		for (int i = 0; i < match; i++) {
			result.addMatchResult(MatchPredictionManager.instance().calculateMatchResult(team1, team2));
			if (i%1000==0)mainframe.updateProgress((int) ((i * 100d) / match));
		}
		mainframe.resetInformation();
		refresh(result);
	}

	/**
	 * Use this methode, if you have created your own matchresults. To calculate
	 * n matches use calculateNMatches
	 * 
	 */
	public final void refresh(MatchResult matchresults) {

		// Beide Tabellen anpassen
		m_jtMatchResultTable.refresh(matchresults, isHomeMatch);

		// Leeren, wird gefüllt durch Klick auf ein Spiel in der Anderen Tabelle
		m_jtMatchScoreTable.refresh(matchresults, isHomeMatch);
		m_jtMatchScoreDiffTable.refresh(matchresults, isHomeMatch);

		// Ergebnisauswertung
		predictPanel.refresh(matchresults);
	}

	private void jbInit() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridwidth = 2;

		final JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.setLayout(layout);

		final JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(predictPanel, BorderLayout.CENTER);

		final JPanel actionpanel = new JPanel(new BorderLayout());
		actionpanel.setOpaque(false);
		m_jbButton.addActionListener(this);
		actionpanel.add(m_jbButton, BorderLayout.EAST);

		// Turn on labels at major tick marks.
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setValue(UserParameter.instance().simulatorMatches);
		slider.addChangeListener(arg0 -> UserParameter.instance().simulatorMatches = slider.getValue());
		actionpanel.add(slider, BorderLayout.CENTER);

		panel.add(actionpanel, BorderLayout.SOUTH);

		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(panel, constraints);
		topPanel.add(panel);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weighty = 0.0;
		constraints.weightx = 0.0;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(homeTeamPanel, constraints);
		topPanel.add(homeTeamPanel);

		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.weighty = 0.0;
		constraints.weightx = 0.0;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(guestTeamPanel, constraints);
		topPanel.add(guestTeamPanel);

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);

		final JScrollPane scrollpane2 = new JScrollPane(m_jtMatchResultTable);
		layout.setConstraints(scrollpane2, constraints);
		add(scrollpane2, BorderLayout.WEST);

		final JScrollPane scrollPane = new JScrollPane(m_jtMatchScoreTable);
		final JScrollPane scrollPane1 = new JScrollPane(m_jtMatchScoreDiffTable);
		final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				scrollPane, scrollPane1);
		split.setDividerLocation(150);
		add(split, BorderLayout.CENTER);
	}
}
