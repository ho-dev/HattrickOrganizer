package core.model.match;

import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.player.MatchRoleID;
import core.model.player.Player;

public class MatchLineupPosition  extends MatchRoleID {
    //~ Instance fields ----------------------------------------------------------------------------

    private String m_sNickName = "";
    private String m_sSpielerName;
    private String m_sSpielerVName = "";
    private double m_dRating;
    private double m_dRatingStarsEndOfMatch;

    private int m_iStatus;

    // starting lineup match role information
    private int m_iStartPosition = -1;
    private int m_iStartBehavior = -1;
    private boolean startSetPiecesTaker = false;
    private int matchId;
    private MatchType matchType;
    private int teamId;
    private Integer hoPosCode;
    private int startMinute=0;
    private Player player = null;

    public MatchLineupPosition(int roleId, int playerId, byte behaviour, int startMinute) {
        super(roleId, playerId,behaviour);
        this.startMinute = startMinute;
    }

    /**
     * Creates a new instance of MatchLineupPosition
     */
    public MatchLineupPosition() {}
    public MatchLineupPosition(int roleID, int spielerID, int behavior) {
        super(roleID, spielerID, (byte) behavior);
    }

    public MatchLineupPosition(int roleId, byte behaviour, Player player){
        super(roleId, player.getPlayerId(), behaviour);
        this.player = player;
    }

    public MatchLineupPosition(int roleID, int spielerID, int behavior, double rating, String name, int status) {
        super(roleID, spielerID, (byte) behavior);
        m_sSpielerName = name;
        m_dRating = rating;
        m_iStatus = status;
    }

    /**
     * Creates a new instance of MatchLineupPosition
     */
    public MatchLineupPosition(int roleID,
                               int spielerID,
                               int behavior,
                               double rating,
                               String vname,
                               String nickName,
                               String name,
                               int status,
                               double ratingStarsEndOfMatch,
                               int startPos,
                               int startBeh,
                               boolean startSetPieces) {
        super(roleID, spielerID, (byte) behavior);

        m_sSpielerName = name;
        m_sNickName = nickName;
        m_sSpielerVName = vname;
        m_dRating = rating;
        m_iStatus = status;
        m_dRatingStarsEndOfMatch = ratingStarsEndOfMatch;
        m_iStartBehavior = startBeh;
        m_iStartPosition = startPos;
        setStartSetPiecesTaker(startSetPieces);
    }

    public final int getRoleId() {
        return this.getId();
    }

    public  void setRoleId(int roleId) {
        this.setId(roleId);
    }

    public final byte getBehaviour(){
        return  this.getTactic();
    }

    /**
     * Setter for property m_sNickName.
     *
     * @param m_sNickName New value of property m_sNickName.
     */
    public final void setNickName(String m_sNickName) {
        this.m_sNickName = m_sNickName;
    }

    /**
     * Getter for property m_sNickName.
     *
     * @return Value of property m_sNickName.
     */
    public final String getNickName() {
        if ( m_sNickName != null ) return m_sNickName;
        return "";
    }

    /**
     * Setter for property m_dRating.
     *
     * @param m_dRating New value of property m_dRating.
     */
    public final void setRating(double m_dRating) {
        this.m_dRating = m_dRating;
    }
    
    /**
     * Setter for property m_dRatingStarsEndOfMatch.
     *
     * @param m_dRatingStarsEndOfMatch New value of property m_dRatingStarsEndOfMatch.
     */
    public final void setRatingStarsEndOfMatch(double m_dRatingStarsEndOfMatch) {
        this.m_dRatingStarsEndOfMatch = m_dRatingStarsEndOfMatch;
    }
    

    /////////////////////////////////////////////////////////////////////////////////
    //ACCESSOR
    //////////////////////////////////////////////////////////////////////////////////    

    /**
     * Getter for property RatingStarsEndOfMatch.
     *
     * @return Value of property RatingStarsEndOfMatch.
     */
    public final double getRatingStarsEndOfMatch() {
        return m_dRatingStarsEndOfMatch;
    }
    
    /**
     * Getter for property m_dRating.
     *
     * @return Value of property m_dRating.
     */
    public final double getRating() {
        return m_dRating;
    }

    /**
     * Setter for property m_sSpielerName.
     *
     * @param m_sSpielerName New value of property m_sSpielerName.
     */
    public final void setSpielerName(String m_sSpielerName) {
        this.m_sSpielerName = m_sSpielerName;
    }

    /**
     * Getter for property m_sSpielerName.
     *
     * @return Value of property m_sSpielerName.
     */
    public final String getSpielerName() {
        if ( m_sSpielerName != null ) return m_sSpielerName;
        return "";
    }

    /**
     * Setter for property m_sSpielerVName.
     *
     * @param m_sSpielerVName New value of property m_sSpielerVName.
     */
    public final void setSpielerVName(String m_sSpielerVName) {
        this.m_sSpielerVName = m_sSpielerVName;
    }

    /**
     * Getter for property m_sSpielerVName.
     *
     * @return Value of property m_sSpielerVName.
     */
    public final String getSpielerVName() {
        if ( m_sSpielerVName != null )return m_sSpielerVName;
        return "";
    }

    /**
     * Setter for property m_iStatus.
     *
     * @param m_iStatus New value of property m_iStatus.
     */
    public final void setStatus(int m_iStatus) {
        this.m_iStatus = m_iStatus;
    }

    /**
     * Getter for property m_iStatus.
     *
     * @return Value of property m_iStatus.
     */
    public final int getStatus() {
        return m_iStatus;
    }

    /**
     * @return the startPosition
     */
    public int getStartPosition() {
    	return m_iStartPosition;
    }
    
    /**
     * @param startPosition the startPosition to set
     */
    public void setStartPosition(int startPosition) {
    	this.m_iStartPosition = startPosition;
    }
    
    /**
     * @return the startBehavior
     */
    public int getStartBehavior() {
    	return m_iStartBehavior;
    }
    
    /**
     * @param startBehavior the startBehavior to set
     */
    public void setStartBehavior(int startBehavior) {
    	this.m_iStartBehavior = startBehavior;
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    //Helper
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * bricht den Namen auf
     */
    protected final void setName(String name) {
        final int index = name.indexOf("  ");

        if (index > -1) {
            m_sSpielerVName = name.substring(0, index);
            m_sSpielerName = name.substring(index + 2);
        }
    }

    public boolean isStartSetPiecesTaker(){
        return this.startSetPiecesTaker;
    }

    public void setStartSetPiecesTaker(Boolean b) {
        if ( b != null ) this.startSetPiecesTaker = b;
     }

    public int getMatchId() {
        return this.matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public MatchType getMatchType() {
        return this.matchType;
    }

    public void setMatchType(MatchType matchTyp) {
        this.matchType = matchTyp;
    }

    public int getTeamId() {
        return this.teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public Integer getHoPosCode() {
        if ( hoPosCode == null){
            hoPosCode = (int)getPosition();
        }
        return hoPosCode;
    }

    public void setHoPosCode(Integer hoPosCode) {
        this.hoPosCode = hoPosCode;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public Player getPlayer() {

        if ( player == null || this.getPlayerId() != player.getPlayerId()){
            if (this.getPlayerId() != 0 ) {
                player = HOVerwaltung.instance().getModel().getCurrentPlayer(this.getPlayerId());
            }
            else {
                player = null;
            }
        }
        return player;
    }

}
