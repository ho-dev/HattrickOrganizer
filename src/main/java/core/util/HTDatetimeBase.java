package core.util;

import core.model.HOVerwaltung;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.*;
import java.time.format.DateTimeFormatter;



/**
  Singleton class used by HTDateTime
 */
public class HTDatetimeBase implements PropertyChangeListener {

    private static HTDatetimeBase cl_Instance;
    private static final LocalDate cl_HT_Birthdate = LocalDate.of(1997, 9, 22);

    // This is a bit unclear from HT side but latest discussion suggest that HTweeks are calculated as if ORIGIN_HT_DATE happened
    // at different time across the world, i.e. no timezone attached
    //private static ZonedDateTime ORIGIN_HT_DATE = ZonedDateTime.of(1997, 9, 22, 0, 0, 0, 0, m_HTzoneID);

    private static final ZoneId cl_HTzoneID = ZoneId.of("Europe/Stockholm");
    static final DateTimeFormatter cl_DatetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(cl_HTzoneID);
    static final DateTimeFormatter cl_DateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(cl_HTzoneID);
    private static final ZoneId cl_UserSystemZoneID = ZoneId.systemDefault();
    private static ZoneId cl_UserZoneID = DateTimeUtils.fromHash(core.model.UserParameter.instance().TimeZoneDifference);
    static Instant cl_LastUpdate;
    private static int cl_UserSeasonOffset;
    private PropertyChangeSupport support;

    public HTDatetimeBase() {
        cl_UserSeasonOffset = HOVerwaltung.instance().getModel().getBasics().getSeasonOffset();
        cl_LastUpdate = HOVerwaltung.instance().getModel().getBasics().getDatum().toInstant();
        if (cl_UserZoneID == null) {
            HOLogger.instance().error(getClass(), "ZoneID could not be identified, reverting to System defaults: " + cl_UserSystemZoneID);
            cl_UserZoneID = cl_UserSystemZoneID;

            // Reverting to HT timezine as a last resort.
            if (cl_UserZoneID == null) {
                HOLogger.instance().info(getClass(), "Reverting to HT default zone ID: " + cl_HTzoneID);
                cl_UserZoneID = cl_HTzoneID;
            }
        }
        support = new PropertyChangeSupport(this);
        HOVerwaltung.instance().addPropertyChangeListener(this);
    }

    protected static HTDatetimeBase instance() {
        if (cl_Instance == null) {
            cl_Instance = new HTDatetimeBase();
        }
        return cl_Instance;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    /**
     * if HOVerwaltung changed, we need to reload dependant parameters
     */
    public void propertyChange(PropertyChangeEvent evt) {
        HOLogger.instance().debug(this.getClass(), "HOVerwaltung model changed => HTDateTimeBase is reinitialized");
        HTDatetimeBase oldInstance = cl_Instance;
        cl_UserSeasonOffset = HOVerwaltung.instance().getModel().getBasics().getSeasonOffset();
        cl_LastUpdate = HOVerwaltung.instance().getModel().getBasics().getDatum().toInstant();
        support.firePropertyChange("cl_Instance", oldInstance, cl_Instance);
    }


    public static ZoneId getUserZoneID() {return cl_UserZoneID;}
    public static ZoneId getHTZoneID() {return cl_HTzoneID;}
    public static int getUserSeasonOffset() {return cl_UserSeasonOffset;}
    public static DateTimeFormatter getDateFormatter() {return cl_DateFormatter;}
    public static LocalDate getHT_Birthdate() {return cl_HT_Birthdate;}

}
