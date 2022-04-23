package core.util;

import core.HO;
import core.model.HOVerwaltung;
import core.model.misc.Basics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HODateTimeTest {


        @Test
        public void test() {

            HO.setPortable_version(true);
            HOVerwaltung.instance().loadLatestHoModel();
            HOVerwaltung.instance().setResource("English");

            var nextTraining = HODateTime.fromHT("2022-03-31 08:30:00");
            var localDateTime = nextTraining.toLocaleDateTime();
            var previousTraining = nextTraining.plusDaysAtSameLocalTime(-7);
            var localPrevious = previousTraining.toLocaleDateTime();

            var fetchedDate = HODateTime.fromHT("2022-01-08 14:33:58");

            Assertions.assertEquals("2022-01-08 14:33:58", fetchedDate.toHT());

//            Assertions.assertEquals("08.01.2022", fetchedDate.toLocaleDate());
//            Assertions.assertEquals("08.01.2022, 14:33:58", fetchedDate.toLocaleDateTime());

            var ts = fetchedDate.toDbTimestamp();
            Assertions.assertEquals("2022-01-08 14:33:58", HODateTime.fromDbTimestamp(ts).toHT());


            var dti = HODateTime.fromHT("2022-02-19 23:11:00");
            Assertions.assertEquals(dti.toHTWeek().season, 80);
            Assertions.assertEquals(dti.toHTWeek().week, 10);

            dti = HODateTime.fromHT("2021-02-14 23:11:00");
            Assertions.assertEquals(dti.toHTWeek().season, 77);
            Assertions.assertEquals(dti.toHTWeek().week, 6);

            dti = HODateTime.fromHT("2020-06-27 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 75);
            Assertions.assertEquals(dti.toHTWeek().week, 4);

            dti = HODateTime.fromHT("2018-05-10 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 68);
            Assertions.assertEquals(dti.toHTWeek().week, 5);

            dti = HODateTime.fromHT("2009-05-28 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 39);
            Assertions.assertEquals(dti.toHTWeek().week, 2);

            dti = HODateTime.fromHT("2020-09-07 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 75);
            Assertions.assertEquals(dti.toHTWeek().week, 15);

            dti = HODateTime.fromHT("2020-09-14 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 75);
            Assertions.assertEquals(dti.toHTWeek().week, 16);

            dti = HODateTime.fromHT("2020-09-21 01:30:00");
            Assertions.assertEquals(dti.toHTWeek().season, 76);
            Assertions.assertEquals(dti.toHTWeek().week, 1);

        }
}
