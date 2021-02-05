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
public class TimeInfo {

    private static ZoneId m_UserZoneID = ZoneId.systemDefault();  // TODO: allow to have this set from user preference (specified or system default)  #884
    private static ZoneId m_HTzoneID = ZoneId.of("Europe/Stockholm");

    private Timestamp m_tsHattrick;  // for compatibility only
    private Timestamp m_tsUserLocalized;  // for compatibility only
    private Instant m_instantHattrick;
    private Instant m_instantUserLocalized;
    private ZonedDateTime m_zdtHattrick;
    private ZonedDateTime m_zdtUserLocalized;


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


    /**
      @param sDateTime as of "yyyy-MM-dd HH:mm:ss"
     **/
    public TimeInfo (String sDateTime){
        LocalDateTime  _htDateTimeNonLocalized = LocalDateTime.parse(sDateTime,  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        m_zdtHattrick = ZonedDateTime.of(_htDateTimeNonLocalized, m_HTzoneID);
        m_zdtUserLocalized = m_zdtHattrick.withZoneSameInstant(m_UserZoneID);
        m_instantHattrick = m_zdtHattrick.toInstant();
        m_instantUserLocalized = m_zdtUserLocalized.toInstant();
        m_tsHattrick = Timestamp.from(m_instantHattrick);
        m_tsUserLocalized = Timestamp.from(m_instantUserLocalized);
    }


}
