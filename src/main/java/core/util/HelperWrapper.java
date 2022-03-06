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

//    public Date getHattrickDate(String string) throws ParseException {
//        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
//        final Date d = sdf.parse(string);
//        final String hh = string.substring(11, 13);
//
//        if (hh.equalsIgnoreCase("12")) {
//            final Calendar c = Calendar.getInstance(Locale.UK);
//            c.setFirstDayOfWeek(Calendar.SUNDAY);
//            c.setTime(d);
//            c.add(Calendar.HOUR_OF_DAY, 12);
//            return c.getTime();
//        }
//
//        return d;
//    }
//
//    /**
//     * Calculate the last valid training date for a certain date (skillupDate)
//     *
//     * @param skillupDate the skillupdate or HRF date
//     * @param refTrainingDate a reference containing a valid training time and day of week
//     *
//     * @return the last valid training date for the given 'skillupDate'
//     */
//    public HODateTime getLastTrainingDate(HODateTime skillupDate, HODateTime refTrainingDate) {
//        // Calendar for TrainingDate
//        //final Calendar trDate = Calendar.getInstance(Locale.US);
//
//        // IF refTrainingDate is null first run of HO
//        if (refTrainingDate == null) {
//            return HODateTime.now();
//        }
//
//        trDate.setTime(refTrainingDate);
//        trDate.setFirstDayOfWeek(Calendar.SUNDAY);
//        trDate.setLenient(true);
//        trDate.setMinimalDaysInFirstWeek(1);
//
//        // Calendar for Skillup Date
//        final Calendar suDate = Calendar.getInstance(Locale.US);
//        suDate.setFirstDayOfWeek(Calendar.SUNDAY);
//        suDate.setMinimalDaysInFirstWeek(1);
//        suDate.setLenient(true);
//        suDate.setTime(skillupDate);
//
//        // Move TrainingDate back to skillup week
//        trDate.set(Calendar.YEAR, suDate.get(Calendar.YEAR));
//        trDate.set(Calendar.WEEK_OF_YEAR, suDate.get(Calendar.WEEK_OF_YEAR));
//
//        // Check that is fine
//        long diff = suDate.getTimeInMillis() - trDate.getTimeInMillis();
//
//        // training date must be within one week (handle end of year)
//        while (diff > WEEK) {
//        	trDate.add(Calendar.WEEK_OF_YEAR, +1);
//        	diff = suDate.getTimeInMillis() - trDate.getTimeInMillis();
//        }
//
//        // training date must not be after skillup date
//        while (diff != 0 && trDate.after(suDate)) {
//        	trDate.add(Calendar.WEEK_OF_YEAR, -1);
//        }
//
//        final Calendar c = Calendar.getInstance(Locale.UK);
//        c.setFirstDayOfWeek(Calendar.SUNDAY);
//        c.setMinimalDaysInFirstWeek(1);
//        c.setTime(trDate.getTime());
//        return c;
//    }
//
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
