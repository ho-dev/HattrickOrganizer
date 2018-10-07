package module.teamAnalyzer.vo;

import core.model.match.MatchLineupPlayer;


/**
 * This is a wrapper around IMatchLineupPlayer
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class PlayerPerformance {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Wrapped object */
    private MatchLineupPlayer mlp;

    /** Status of the player on the team. injured, sold etc */
    private int status;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new PlayerPerformance object around the loaded from HO
     *
     * @param _mlp The IMatchLineupPlayer object to be wrapped
     */
    public PlayerPerformance(MatchLineupPlayer _mlp) {
        mlp = _mlp;
    }

    //~ Methods ------------------------------------------------------------------------------------
    public int getId() {
        return mlp.getId();
    }

    public String getNickName() {
        return mlp.getNickName();
    }

    public byte getPosition() {
        return mlp.getPosition();
    }

    @SuppressWarnings("deprecation")
	public int getPositionCode() {
        return mlp.getPositionCode();
    }

    public double getRating() {
        return mlp.getRating();
    }

    public int getSortId() {
        return mlp.getSortId();
    }

    public int getSpielerId() {
        return mlp.getSpielerId();
    }

    public String getSpielerName() {
        return mlp.getSpielerName();
    }

    public String getSpielerVName() {
        return mlp.getSpielerVName();
    }

    public void setStatus(int i) {
        status = i;
    }

    public int getStatus() {
        return status;
    }

    public byte getTaktik() {
        return mlp.getTaktik();
    }
}
