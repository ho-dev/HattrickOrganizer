package module.teamanalyzer.ui.lineup;

import module.teamanalyzer.SystemManager;
import module.teamanalyzer.ui.TeamLineupData;

import javax.swing.JPanel;


public class FormationPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------


	/** Left Attack RatingBox */
    RatingBox leftAtt = new RatingBox();

    /** Left Defence RatingBox */
    RatingBox leftDef = new RatingBox();

    /** Central Attack RatingBox */
    RatingBox midAtt = new RatingBox();

    /** Central Defence RatingBox */
    RatingBox midDef = new RatingBox();

    /** Midfield RatingBox */
    RatingBox midfield = new RatingBox();

    /** Right Attack RatingBox */
    RatingBox rightAtt = new RatingBox();

    /** Right Defence RatingBox */
    RatingBox rightDef = new RatingBox();

    /** User Team Lineup Data */
    protected TeamLineupData myTeam = new TeamLineupData();

    /** Opponent Team Lineup Data */
    protected TeamLineupData opponentTeam = new TeamLineupData();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new FormationPanel object.
     */
    public FormationPanel() {
        var compare = displayBothTeams();
        var mixed = displayMixedLineup();
        setLayout(compare, mixed);
    }

    private boolean displayMixedLineup() {
        return SystemManager.isMixedLineup.isSet();
    }

    public boolean displayBothTeams() {
        return SystemManager.isLineup.isSet();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns user team lineup data
     *
     * @return Own lineup data
     */
    public TeamLineupData getMyTeam() {
        return myTeam;
    }

    /**
     * Returns opponent team lineup data
     *
     * @return Opponents lineup data
     */
    public TeamLineupData getOpponentTeam() {
        return opponentTeam;
    }

    /**
     * Refresh the panel
     *
     * @param compare if both teams must be shown
     * @param type standard (false) or mixed layout (true)
     */
    public void reload(boolean compare, boolean type) {
        setLayout(compare, type);
        midfield.reload((int) myTeam.getMidfield(), (int) opponentTeam.getMidfield());
        rightDef.reload((int) myTeam.getLeftAttack(), (int) opponentTeam.getRightDefence());
        leftDef.reload((int) myTeam.getRightAttack(), (int) opponentTeam.getLeftDefence());
        midDef.reload((int) myTeam.getMiddleAttack(), (int) opponentTeam.getMiddleDefence());
        rightAtt.reload((int) myTeam.getLeftDefence(), (int) opponentTeam.getRightAttack());
        leftAtt.reload((int) myTeam.getRightDefence(), (int) opponentTeam.getLeftAttack());
        midAtt.reload((int) myTeam.getMiddleDefence(), (int) opponentTeam.getMiddleAttack());
    }

    /**
     * Sets the Layout of the panel, by delegating to the proper class
     *
     * @param compare true if both teams must be shown
     * @param mixedLIneup true if mixedlineup has to be used, false if not
     */
    private void setLayout(boolean compare, boolean mixedLIneup) {
        LineupStylePanel panel;

        if (mixedLIneup) {
            panel = new MixedLineupPanel(this);
        } else {
            panel = new StandardLineupPanel(this);
        }

        if (compare) {
            panel.initCompare();
        } else {
            panel.initSingle();
        }

        removeAll();
        setOpaque(false);
        add(panel);
    }
}
