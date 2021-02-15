package core.util;

import core.model.HOVerwaltung;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


/*
   Class allowing coherent representation of time between time information obtaine by CHPP ( CE(ST) ) and localized
 */
public class DateTimeInfo {


    private static ZoneId m_UserSystemZoneID = ZoneId.systemDefault();
    private static ZoneId m_UserZoneID;
    private static int m_UserSeasonOffsetDefault = HOVerwaltung.instance().getModel().getBasics().getSeasonOffset();
    private static int m_UserSeasonOffset;
    private static ZoneId m_HTzoneID = ZoneId.of("Europe/Stockholm");

    // This is a bit unclear from HT side but latest discussion suggest that HTweeks are calculated as if ORIGIN_HT_DATE happened
    // at different time accross the world, i.e. no timezone attached

    //private static ZonedDateTime ORIGIN_HT_DATE = ZonedDateTime.of(1997, 9, 22, 0, 0, 0, 0, m_HTzoneID);
    private static LocalDate ORIGIN_HT_DATE = LocalDate.of(1997, 9, 22);
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Timestamp m_tsHattrick;  // for compatibility only
    private Timestamp m_tsUserLocalized;  // for compatibility only
    private Instant m_instantHattrick;
    private Instant m_instantUserLocalized;
    private ZonedDateTime m_zdtHattrick;
    private ZonedDateTime m_zdtUserLocalized;
    private String m_sHattrick; // String as provided by Hattrick in xml files
    private int m_HTweekLocalized;
    private int m_HTseasonLocalized;



    @Deprecated
    public Timestamp getUserLocalizedTimeAsTimestamp() {
        return m_tsUserLocalized;
    }

    @Deprecated
    public Timestamp getHattrickTimeAsTimestamp() {
        return m_tsHattrick;
    }

    public ZonedDateTime getUserLocalizedTime() {
        return m_zdtUserLocalized;
    }

    public ZonedDateTime getHattrickTime() {
        return m_zdtHattrick;
    }

    public String getHattrickTimeAsString() {
        return m_sHattrick;
    }

    public int getHTSeasonLocalized() {
        return m_HTseasonLocalized;
    }

    public int getHTWeekLocalized() {
        return m_HTweekLocalized;
    }

    /**
     * By default resolves to System ZoneID
     * @param sDateTime as of "yyyy-MM-dd HH:mm:ss"
     **/
    public DateTimeInfo(String sDateTime) {
        this(sDateTime, m_UserSystemZoneID, m_UserSeasonOffsetDefault);
    }


    /**
     * @param sDateTime as of "yyyy-MM-dd HH:mm:ss"
     **/
    public DateTimeInfo(String sDateTime, ZoneId zoneID, Integer seasonOffset) {
        m_UserZoneID = zoneID;
        m_UserSeasonOffset = seasonOffset;
        m_sHattrick = cleanDateTimeString(sDateTime);
        LocalDateTime _htDateTimeNonLocalized = LocalDateTime.parse(m_sHattrick, dtf);
        m_zdtHattrick = ZonedDateTime.of(_htDateTimeNonLocalized, m_HTzoneID);
        m_zdtUserLocalized = m_zdtHattrick.withZoneSameInstant(m_UserZoneID);
        m_instantHattrick = m_zdtHattrick.toInstant();
        m_instantUserLocalized = m_zdtUserLocalized.toInstant();
        m_tsHattrick = Timestamp.from(m_instantHattrick);
        m_tsUserLocalized = Timestamp.from(m_instantUserLocalized);

        // This is a bit unclear from HT side but latest discussion suggest that HTweeks are calculated as if ORIGIN_HT_DATE happened
        // at different time accross the world, i.e. no timezone attached
//        ZonedDateTime origin_ht_date_localized = ORIGIN_HT_DATE.withZoneSameInstant(m_UserZoneID);
        ZonedDateTime origin_ht_date_localized = ORIGIN_HT_DATE.atStartOfDay(m_UserZoneID);

        long nbDays = ChronoUnit.DAYS.between(origin_ht_date_localized.toLocalDate(), m_zdtUserLocalized.toLocalDate());
        long nbWeeks = nbDays / 7;

        m_HTseasonLocalized = (int)Math.floorDiv(nbWeeks, 16) + 1 + m_UserSeasonOffset;
        m_HTweekLocalized = (int)(nbWeeks % 16) + 1;
    }


    public boolean isInTheFuture(){
        ZonedDateTime zdt = ZonedDateTime.now(m_HTzoneID);
        return m_zdtHattrick.isAfter(zdt);
    }

    public boolean isPassed(){
        return !isInTheFuture();
    }


    public DateTimeInfo(Timestamp ts) {
        this(ts.toInstant());
    }

    /**
       This constructor is supposed to be used for instant representing time in HT tZ
     */
    public DateTimeInfo(Instant instant) {
        this(DateTimeUtils.InstantToSQLtimeStamp(instant));
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


}
