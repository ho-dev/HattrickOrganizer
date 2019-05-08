// %2077366746:plugins%
/*
 * IMatchDetails.java
 *
 * Created on 18. Oktober 2004, 07:18
 */
package core.model.match;

public interface IMatchDetails {
    //~ Static fields/initializers -----------------------------------------------------------------

    int EINSTELLUNG_UNBEKANNT = -1000;
    /** Play it cool */
    int EINSTELLUNG_PIC = -1;
    /** Normal */
    int EINSTELLUNG_NORMAL = 0;
    /** Match of the Season */
    int EINSTELLUNG_MOTS = 1;

    /** Normal tactic */
    int TAKTIK_NORMAL = 0;
    /** Pressing tactic */
    int TAKTIK_PRESSING = 1;
    /** Counter attack tactic */
    int TAKTIK_KONTER = 2;
    /** AiM - Attack On Middle */
    int TAKTIK_MIDDLE = 3;
    /** AoW - Attack On Wings */
    int TAKTIK_WINGS = 4;
    /** Play creatively */
    int TAKTIK_CREATIVE = 7;
    /** Long shots */
    int TAKTIK_LONGSHOTS = 8;

    /** away match */
    short LOCATION_AWAY = 0;
    /** home match */
    short LOCATION_HOME = 1;
    /** away derby */
    short LOCATION_AWAYDERBY = 2;
    /** Tournament */
    short LOCATION_TOURNAMENT = 3;

}
