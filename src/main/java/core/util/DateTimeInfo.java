package core.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/*
   Class allowing coherent representation of time between time information obtaine by CHPP ( CE(ST) ) and localized
 */
public class DateTimeInfo {

    private static ZoneId m_UserZoneID = ZoneId.systemDefault();  // TODO: allow to have this set from user preference (specified or system default)  #884
    private static ZoneId m_HTzoneID = ZoneId.of("Europe/Stockholm");
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Timestamp m_tsHattrick;  // for compatibility only
    private Timestamp m_tsUserLocalized;  // for compatibility only
    private Instant m_instantHattrick;
    private Instant m_instantUserLocalized;
    private ZonedDateTime m_zdtHattrick;
    private ZonedDateTime m_zdtUserLocalized;
    private String m_sHattrick; // String as provided by Hattrick in xml files


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

    /**
     * @param sDateTime as of "yyyy-MM-dd HH:mm:ss"
     **/
    public DateTimeInfo(String sDateTime) {
        m_sHattrick = cleanDateTimeString(sDateTime);
        LocalDateTime _htDateTimeNonLocalized = LocalDateTime.parse(m_sHattrick, dtf);
        m_zdtHattrick = ZonedDateTime.of(_htDateTimeNonLocalized, m_HTzoneID);
        m_zdtUserLocalized = m_zdtHattrick.withZoneSameInstant(m_UserZoneID);
        m_instantHattrick = m_zdtHattrick.toInstant();
        m_instantUserLocalized = m_zdtUserLocalized.toInstant();
        m_tsHattrick = Timestamp.from(m_instantHattrick);
        m_tsUserLocalized = Timestamp.from(m_instantUserLocalized);
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

    public DateTimeInfo(Instant instant) {
        String sDateTime = DateTimeUtils.InstantToSQLtimeStamp(instant);
        new DateTimeInfo(sDateTime);
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
