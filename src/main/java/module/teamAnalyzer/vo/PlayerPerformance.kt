package module.teamAnalyzer.vo;

import core.model.match.MatchLineupPosition;
import core.model.player.MatchRoleID;
import module.teamAnalyzer.manager.PlayerDataManager;


/**
 * This is a wrapper around IMatchLineupPlayer
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class PlayerPerformance {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Wrapped object */
    private MatchLineupPosition mlp;

    /** Status of the player on the team. injured, sold etc */
    private int status;
    private int injuryStatus = 0;
    private int bookingStatus = 0;
    private int transferListedStatus = 0;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new PlayerPerformance object around the loaded from HO
     *
     * @param _mlp The IMatchLineupPlayer object to be wrapped
     */
    public PlayerPerformance(MatchLineupPosition _mlp) {
        mlp = _mlp;
    }

    //~ Methods ------------------------------------------------------------------------------------
    public int getRoleId() {
        return mlp.getRoleId();
    }

    public String getNickName() {
        return mlp.getNickName();
    }

    public byte getPosition() {
        return mlp.getPosition();
    }

    public double getRating() {
        return mlp.getRating();
    }

    public double getRatingEnd(){
        return mlp.getRatingStarsEndOfMatch();
    }

    public int getSortId() {
        return mlp.getSortId();
    }

    public int getSpielerId() {
        return mlp.getPlayerId();
    }

    public String getSpielerName() {
        return mlp.getSpielerName();
    }

    public String getSpielerVName() {
        return mlp.getSpielerVName();
    }

    public void setStatus(int i) {
        status = i;
        int digit = i % 10;
        this.injuryStatus = digit;
        i = i/10;

        digit = i % 10;
        this.bookingStatus = digit;
        i = i/10;

        digit = i % 10;
        this.transferListedStatus= digit;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusAsText() {
        switch (status){
            default:
            case PlayerDataManager.UNKNOWN: return "Unknown";
            case PlayerDataManager.AVAILABLE: return "Available";
            case PlayerDataManager.INJURED: return "Injured";
            case PlayerDataManager.SUSPENDED: return "Suspended";
            case PlayerDataManager.TRANSFER_LISTED: return "Sold";
        }
    }

    public byte getBehaviour() {
        return mlp.getBehaviour();
    }

    public MatchLineupPosition getMatchLineupPosition() { return this.mlp;}
}
