// %833795854:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

/**
 * Extension of the spotLineup that holds user team data, as loaded from HO Lineup tab
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class UserTeamSpotLineup extends SpotLineup {
    //~ Instance fields ----------------------------------------------------------------------------

    /** The special event code */
    private int specialEvent;

    /** The tactic code  constant for Offenive Midfielder, Winger Towards middle etc.. */
    private int tacticCode;

    //~ Methods ------------------------------------------------------------------------------------
    public void setSpecialEvent(int i) {
        specialEvent = i;
    }

    public int getSpecialEvent() {
        return specialEvent;
    }

    public void setTacticCode(int i) {
        tacticCode = i;
    }

    public int getTacticCode() {
        return tacticCode;
    }
}
