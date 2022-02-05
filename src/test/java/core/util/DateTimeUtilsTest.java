package core.util;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Test
    void getCESTTimestampToInstant() {
        Timestamp ts = new Timestamp(1639944000000L);
        Instant instant = DateTimeUtils.getCESTTimestampToInstant(ts);

        assertEquals("19-12-2021", format.format(instant.atZone(ZoneId.of("America/Montreal")).toLocalDateTime()));
    }
}
