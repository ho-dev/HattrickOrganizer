// %2077366746:plugins%
/*
 * IMatchDetails.java
 *
 * Created on 18. Oktober 2004, 07:18
 */
package core.model.match;

/**
 * Interface for severy match details.
 *
 * @author thomas.werth
 */
public interface IMatchDetails {
    //~ Static fields/initializers -----------------------------------------------------------------

    public static final int EINSTELLUNG_UNBEKANNT = -1000;
    /** Play it cool */
    public static final int EINSTELLUNG_PIC = -1;
    /** Normal */
    public static final int EINSTELLUNG_NORMAL = 0;
    /** Match of the Season */
    public static final int EINSTELLUNG_MOTS = 1;

    /** Normal tactic */
    public static final int TAKTIK_NORMAL = 0;
    /** Pressing tactic */
    public static final int TAKTIK_PRESSING = 1;
    /** Counter attack tactic */
    public static final int TAKTIK_KONTER = 2;
    /** AiM - Attack On Middle */
    public static final int TAKTIK_MIDDLE = 3;
    /** AoW - Attack On Wings */
    public static final int TAKTIK_WINGS = 4;
    /** Play creatively */
    public static final int TAKTIK_CREATIVE = 7;
    /** Long shots */
    public static final int TAKTIK_LONGSHOTS = 8;

    /** away match */
    public static final short LOCATION_AWAY = 0;
    /** home match */
    public static final short LOCATION_HOME = 1;
    /** away derby */
    public static final short LOCATION_AWAYDERBY = 2;
    /** Tournament */
    public static final short LOCATION_TOURNAMENT = 3;



}
