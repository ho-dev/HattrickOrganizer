package module.teamanalyzer.vo;

import core.model.match.MatchLineupPosition;
import lombok.Getter;
import module.teamanalyzer.manager.PlayerDataManager;

/**
 * This is a wrapper around IMatchLineupPlayer
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class PlayerPerformance {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Wrapped object */
    @Getter
    private final MatchLineupPosition matchLineupPosition;

    /** Status of the player on the team. injured, sold etc */
    @Getter
    private int status;
    private int injuryStatus = 0;
    private int bookingStatus = 0;
    private int transferListedStatus = 0;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new PlayerPerformance object around the loaded from HO
     *
     * @param matchLineupPosition The IMatchLineupPlayer object to be wrapped
     */
    public PlayerPerformance(MatchLineupPosition matchLineupPosition) {
        this.matchLineupPosition = matchLineupPosition;
    }

    //~ Methods ------------------------------------------------------------------------------------
    public int getRoleId() {
        return matchLineupPosition.getRoleId();
    }

    public String getNickName() {
        return matchLineupPosition.getNickName();
    }

    public byte getPosition() {
        return matchLineupPosition.getPosition();
    }

    public double getRating() {
        return matchLineupPosition.getRating();
    }

    public double getRatingEnd(){
        return matchLineupPosition.getRatingStarsEndOfMatch();
    }

    public int getSortId() {
        return matchLineupPosition.getSortId();
    }

    /**
     * Gets the ID of the player.
     *
     * @deprecated Please use {@link #getPlayerId()} instead.
     */
    @Deprecated(since = "10.0", forRemoval = true)
    public int getSpielerId() {
        return getPlayerId();
    }

    public int getPlayerId() {
        return matchLineupPosition.getPlayerId();
    }

    /**
     * Gets the last name of the player.
     *
     * @deprecated Please use {@link #getLastName()} instead.
     */
    @Deprecated(since = "10.0", forRemoval = true)
    public String getSpielerName() {
        return getLastName();
    }

    public String getLastName() {
        return matchLineupPosition.getSpielerName();
    }

    /**
     * Gets the first name of the player.
     *
     * @deprecated Please use {@link #getFirstName()} instead.
     */
    @Deprecated(since = "10.0", forRemoval = true)
    public String getSpielerVName() {
        return getFirstName();
    }

    public String getFirstName() {
        return matchLineupPosition.getSpielerVName();
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
        return matchLineupPosition.getBehaviour();
    }
}
