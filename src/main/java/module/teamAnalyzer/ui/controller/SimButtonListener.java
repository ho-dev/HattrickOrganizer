package module.teamAnalyzer.ui.controller;

import core.constants.player.PlayerAbility;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.match.Matchdetails;
import core.prediction.MatchEnginePanel;
import core.prediction.MatchPredictionDialog;
import core.prediction.engine.MatchPredictionManager;
import core.prediction.engine.TeamData;
import core.prediction.engine.TeamRatings;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.RecapPanel;
import module.teamAnalyzer.ui.TeamLineupData;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



/**
 * Action listener for the simulation Button
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class SimButtonListener implements ActionListener {
    //~ Instance fields ----------------------------------------------------------------------------

    /** The match prediction Manager */
    MatchPredictionManager manager = MatchPredictionManager.instance();

    /** The user team */
    TeamLineupData myTeam;

    /** The opponent team */
    TeamLineupData opponentTeam;
    private final RecapPanel recapPanel;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SimButtonListener object.
     *
     */
    public SimButtonListener(TeamLineupData myTeam, TeamLineupData opponentTeam, RecapPanel recapPanel) {
        this.myTeam = myTeam;
        this.opponentTeam = opponentTeam;
        this.recapPanel = recapPanel;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Prepares the date for the 2 teams, and launches the match rating prediction panel.
     *
     * @param e the event
     */
    public void actionPerformed(ActionEvent e) {
        if ((myTeam.getMidfield() < 1) || (opponentTeam.getMidfield() < 1)) {
            JPanel pan = new JPanel();

            pan.add(new JLabel(HOVerwaltung.instance().getLanguageString("Error.SetFormation")));
            JOptionPane.showMessageDialog(SystemManager.getPlugin(), pan,
            		HOVerwaltung.instance().getLanguageString("Error"),
                                          JOptionPane.PLAIN_MESSAGE);

            return;
        }

        TeamRatings myTeamRatings = manager.generateTeamRatings(myTeam.getMidfield(),
                                                                   myTeam.getLeftDefence(),
                                                                   myTeam.getMiddleDefence(),
                                                                   myTeam.getRightDefence(),
                                                                   myTeam.getLeftAttack(),
                                                                   myTeam.getMiddleAttack(),
                                                                   myTeam.getRightAttack());

        var lineup = HOVerwaltung.instance().getModel().getCurrentLineup();
        TeamData myTeamValues = manager.generateTeamData(myTeam.getTeamPanel().getText(),
        		myTeamRatings, lineup.getTacticType(), getTacticLevel());

        TeamRatings opponentTeamRatings = manager.generateTeamRatings(opponentTeam.getMidfield(),
                                                                         opponentTeam.getLeftDefence(),
                                                                         opponentTeam.getMiddleDefence(),
                                                                         opponentTeam.getRightDefence(),
                                                                         opponentTeam.getLeftAttack(),
                                                                         opponentTeam.getMiddleAttack(),
                                                                         opponentTeam.getRightAttack());

        TeamData opponentTeamValues = manager.generateTeamData(opponentTeam.getTeamPanel().getText(),
        		opponentTeamRatings, getTacticType(recapPanel.getSelectedTacticType()),
        		getTacticSkill(recapPanel.getSelectedTacticSkill()));

        MatchEnginePanel matchPredictionPanel;
        String match;

        if (SystemManager.getSelectedTeamLocation() == IMatchDetails.LOCATION_HOME) {
            matchPredictionPanel = new MatchEnginePanel(myTeamValues, opponentTeamValues);
            match = myTeamValues.getTeamName() + " – " + opponentTeamValues.getTeamName();
        } else {
            matchPredictionPanel = 	new MatchEnginePanel(opponentTeamValues, myTeamValues);
            match = opponentTeamValues.getTeamName() + " – " + myTeamValues.getTeamName();
        }

        MatchPredictionDialog d = new MatchPredictionDialog(matchPredictionPanel, match);
    }

    /**
     * Returns the tactic level for the user team defined formation in HO Lineup
     *
     * @return the actual tactic level as shown in HO Lineup tab
     */
    private int getTacticLevel() {
        var lineup = HOVerwaltung.instance().getModel().getCurrentLineup();
        var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
        var r = ratingPredictionModel.getTacticRating(lineup, 0);
        if (r > 0) r += 1;
        return (int) r;
    }

    /**
     * Get the tactic constant for a i18n'ed string.
     */
    private int getTacticType(String strTactic) {
    	try {
	    	if (strTactic == null || RecapPanel.VALUE_NA.equals(strTactic)) {
	    		return IMatchDetails.TAKTIK_NORMAL;
	    	} else if (strTactic.equals(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_KONTER))) {
	    		return IMatchDetails.TAKTIK_KONTER;
	    	} else if (strTactic.equals(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_MIDDLE))) {
	    		return IMatchDetails.TAKTIK_MIDDLE;
	    	} else if (strTactic.equals(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_WINGS))) {
	    		return IMatchDetails.TAKTIK_WINGS;
	    	} else if (strTactic.equals(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_PRESSING))) {
	    		return IMatchDetails.TAKTIK_PRESSING;
	    	} else if (strTactic.equals(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_CREATIVE))) {
	    		return IMatchDetails.TAKTIK_CREATIVE;
	    	} else if (strTactic.equals(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_LONGSHOTS))) {
	    		return IMatchDetails.TAKTIK_LONGSHOTS;
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return IMatchDetails.TAKTIK_NORMAL;
    }

    /**
     * Get the tactic skill for a i18n'ed string.
     */
    private int getTacticSkill(String strSkill) {
    	try {
    		if (strSkill == null || RecapPanel.VALUE_NA.equals(strSkill)) {
    			return 0;
    		}
    		for (int m=0; m<21; m++) {
    			if (strSkill.equals(PlayerAbility.getNameForSkill(m, false))) {
    				return (m>0?m-1:m); // TeamRatingPanel doesnt handle 'non existant', index 0=disastrous
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return 0;
    }
}
