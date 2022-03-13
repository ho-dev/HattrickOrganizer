package core.util;

import core.file.xml.XMLMatchdetailsParser;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.net.MyConnector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author thomas.werth
 */
public class HelperWrapper {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static HelperWrapper m_clInstance;
    final static long WEEK = 24 * 7 * 3600 * 1000L;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of HelperWrapper
     */
    private HelperWrapper() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    public static HelperWrapper instance() {
        if (m_clInstance == null) {
            m_clInstance = new HelperWrapper();
        }

        return m_clInstance;
    }

     /**
     * Utility Method that returns the field position from the HO Position Code (hoposcode) It is
     * impossible to make difference between left and right so always the left position is
     * returned
     *
     * @param hoposcode
     *
     * @return the field position
     */
    public int getPosition(int hoposcode) {
        if (hoposcode == 0) {
            return IMatchRoleID.keeper;
        }

        if (hoposcode < 4) {
            return IMatchRoleID.rightCentralDefender;
        }

        if ((hoposcode < 8)) {
            return IMatchRoleID.leftBack;
        }

        if ((hoposcode < 12)) {
            return IMatchRoleID.rightInnerMidfield;
        }

        if ((hoposcode < 16)) {
            return IMatchRoleID.leftWinger;
        }

        if ((hoposcode < 18) || (hoposcode == 21)) {
            return IMatchRoleID.rightForward;
        }

        return hoposcode;
    }

    @Deprecated
    public boolean isUserMatch(String matchID, MatchType matchType) {
    	try {
          String input = MyConnector.instance().downloadMatchdetails(Integer.parseInt(matchID), matchType);
          Matchdetails mdetails = XMLMatchdetailsParser.parseMatchdetailsFromString(input, null);
          int teamID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
          return ((mdetails.getHomeTeamId() == teamID) || (mdetails.getGuestTeamId() == teamID));
      } catch (Exception e) {
      	HOLogger.instance().warning(Helper.class, "Err: " + e);
      }

      return false;
    }
}
