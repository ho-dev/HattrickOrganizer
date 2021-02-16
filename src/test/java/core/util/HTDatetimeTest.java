package core.util;


import core.HO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import core.model.HOVerwaltung;
import java.time.ZoneId;


public class HTDatetimeTest {



    
    @Test
    public void testWeeksAndSeasons() {

        HO.setPortable_version(true);
        HOVerwaltung.instance().loadLatestHoModel();
        HOVerwaltung.instance().setResource("English");


        HTDatetime dti = new HTDatetime("2021-02-14 23:11:00");
        Assertions.assertEquals(dti.getHTSeasonLocalized(), 77);
        Assertions.assertEquals(dti.getHTWeekLocalized(), 5);


        dti = new HTDatetime("2020-06-27 00:00:00");
        Assertions.assertEquals(dti.getHTSeasonLocalized(), 75);
        Assertions.assertEquals(dti.getHTWeekLocalized(), 4);

        dti = new HTDatetime("2018-05-10 00:00:00");
        Assertions.assertEquals(dti.getHTSeasonLocalized(), 68);
        Assertions.assertEquals(dti.getHTWeekLocalized(), 5);

        dti = new HTDatetime("2009-05-28 00:00:00");
        Assertions.assertEquals(dti.getHTSeasonLocalized(), 39);
        Assertions.assertEquals(dti.getHTWeekLocalized(), 2);

        dti = new HTDatetime("2020-09-07 00:00:00");
        Assertions.assertEquals(dti.getHTSeasonLocalized(), 75);
        Assertions.assertEquals(dti.getHTWeekLocalized(), 15);


        dti = new HTDatetime("2023-02-23 17:00:00", ZoneId.of("America/Belize"), -12);
        Assertions.assertEquals(dti.getHTSeasonLocalized(), 71);
        Assertions.assertEquals(dti.getHTWeekLocalized(), 15);

        dti = new HTDatetime("2020-09-21 01:30:00");
        Assertions.assertEquals(dti.getHTSeasonLocalized(), 76);
        Assertions.assertEquals(dti.getHTWeekLocalized(), 1);

        dti = new HTDatetime("2020-09-21 01:30:00", ZoneId.of("America/Bahia"), 0);
        Assertions.assertEquals(dti.getHTSeasonLocalized(), 75);
        Assertions.assertEquals(dti.getHTWeekLocalized(), 16);

    }


    }
