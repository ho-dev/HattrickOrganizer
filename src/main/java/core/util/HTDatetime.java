package core.util;


import core.model.HOVerwaltung;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static core.util.HTDatetimeBase.cl_DateFormatter;
import static core.util.HTDatetimeBase.cl_DatetimeFormatter;
import static core.util.HTDatetimeBase.cl_LastUpdate;


/**
   Class allowing coherent representation of time between HT (CET) and user localized value
   This class also have method to convert a datetime into HTWeek and HTSeason
   Timestamps and Instants within the are all localized in CET (UTC would have be a better choice but we have to deal with HO history and with the fact that CHPP datetime refers to CET)
   ZonedDateTime should only be used for GUI display and not in code logic
 */
public class HTDatetime implements PropertyChangeListener{

    private HTDatetimeBase m_HTDatetimeBase;
    private Timestamp m_tsHT_CET;  // for compatibility only
    private Timestamp m_tsLocalized;  // for compatibility only
    private Instant m_iHT_CET;
    private Instant m_iLocalized;
    private ZonedDateTime m_zdtHT_CET;
    private ZonedDateTime m_zdtLocalized;
    private String m_sHT_CET; // String as provided by Hattrick in xml files
    private int m_HTweekLocalized;
    private int m_HTseasonLocalized;
    private int m_HTseason;



    // constructors ===================================================================
    /**
     * @param sDateTime as of "yyyy-MM-dd HH:mm:ss"
     **/
    public HTDatetime(String sDateTime) {
        m_HTDatetimeBase = HTDatetimeBase.instance();
        m_sHT_CET = cleanDateTimeString(sDateTime);
        LocalDateTime _htDateTimeNonLocalized = LocalDateTime.parse(m_sHT_CET, cl_DatetimeFormatter);
        m_zdtHT_CET = ZonedDateTime.of(_htDateTimeNonLocalized, HTDatetimeBase.getHTZoneID());
        m_zdtLocalized = m_zdtHT_CET.withZoneSameInstant(HTDatetimeBase.getUserZoneID());
        m_iHT_CET = m_zdtHT_CET.toInstant();
        m_iLocalized = m_zdtLocalized.toInstant();
        m_tsHT_CET = Timestamp.from(m_iHT_CET);
        m_tsLocalized = Timestamp.from(m_iLocalized);

        // This is a bit unclear from HT side but latest discussion suggest that HTweeks are calculated as if ORIGIN_HT_DATE happened
        // at different time accross the world, i.e. no timezone attached
//        ZonedDateTime origin_ht_date_localized = ORIGIN_HT_DATE.withZoneSameInstant(m_UserZoneID);
        ZonedDateTime origin_ht_date_localized = HTDatetimeBase.getHT_Birthdate().atStartOfDay(HTDatetimeBase.getUserZoneID());

        long nbDays = ChronoUnit.DAYS.between(origin_ht_date_localized.toLocalDate(), m_zdtLocalized.toLocalDate());
        long nbWeeks = nbDays / 7;

        m_HTseason =  (int)Math.floorDiv(nbWeeks, 16) + 1;
        m_HTseasonLocalized = m_HTseason + HTDatetimeBase.getUserSeasonOffset();
        m_HTweekLocalized = (int)(nbWeeks % 16) + 1;

        HTDatetimeBase.instance().addPropertyChangeListener(this);
    }


    public HTDatetime(Timestamp ts) {
        this(ts.toInstant());
    }

    /**
     This constructor is supposed to be used for instant representing time in HT tZ
     */
    public HTDatetime(Instant instant) {
        this(DateTimeUtils.InstantToSQLtimeStamp(instant));
    }

    static public HTDatetime now(){
        return  new HTDatetime(Instant.now());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        HOLogger.instance().debug(this.getClass(), "HTDateTimeBase model changed => HTDateTime is reinitialized");
        m_HTDatetimeBase = HTDatetimeBase.instance();
    }


    // static method           =====================
    public static String getLocalizedDateString(Instant instant, boolean includeWeeksSeasons){
        HTDatetime o_temp = new HTDatetime(instant);
        String res = cl_DateFormatter.format(instant);
        if (includeWeeksSeasons) {
            res += String.format(" (%s/%s)", o_temp.m_HTweekLocalized, o_temp.m_HTseasonLocalized);
        }
        return res;
    }

    public static boolean isAfterLastUpdate(ZonedDateTime zdt){
        return zdt.toInstant().isAfter(cl_LastUpdate);
    }


    // non-static method           =====================
    public Timestamp getUserLocalizedTimeAsTimestamp() {
        return m_tsLocalized;
    }

    public Timestamp getHattrickTimeAsTimestamp() {
        return m_tsHT_CET;
    }

    public ZonedDateTime getUserLocalizedTime() {
        return m_zdtLocalized;
    }

    public ZonedDateTime getHattrickTime() {
        return m_zdtHT_CET;
    }

    public String getHattrickTimeAsString() {
        return m_sHT_CET;
    }

    public int getHTSeasonLocalized() {
        return m_HTseasonLocalized;
    }

    public int getHTSeason(){
        return m_HTseason;
    }

    public int getHTWeekLocalized() {
        return m_HTweekLocalized;
    }

    public boolean isInTheFuture(){
        ZonedDateTime zdt = ZonedDateTime.now(HTDatetimeBase.getHTZoneID());
        return m_zdtHT_CET.isAfter(zdt);
    }

    public boolean isAfter(Instant refTimne){
        return m_iHT_CET.isAfter(refTimne);
    }

    public boolean isPassed(){
        return !isInTheFuture();
    }

    private String cleanDateTimeString(String sDateTime) {

            int dateInputSize = sDateTime.length();

            if (dateInputSize < 10){
                throw new IllegalArgumentException("The provided string is not recognized as a valid DateTime: " + sDateTime);
            }

            if(sDateTime.length()>19){
                return sDateTime.substring(0, 19);
            }

            if (sDateTime.length()==19){
                return sDateTime;
            }

            return sDateTime.substring(0, 10) + " 00:00:00";

    }

    public static Date resetDay(Date date) {
        final Calendar cal = new GregorianCalendar();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

}
