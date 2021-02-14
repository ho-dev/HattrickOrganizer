package core.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import core.model.HOVerwaltung;
import java.time.ZoneId;


public class HTDatetimeTest {

    @Test
    public void testWeeksAndSeasons() {

        HOVerwaltung.instance().loadLatestHoModel();
        HOVerwaltung.instance().setResource("English");

        DateTimeInfo dti = new DateTimeInfo("2021-02-14 23:11:00");
        assertEquals(dti.getHTSeasonLocalized(), 77);
        assertEquals(dti.getHTWeekLocalized(), 6);


        dti = new DateTimeInfo("2020-09-07 00:00:00");
        assertEquals(dti.getHTSeasonLocalized(), 75);
        assertEquals(dti.getHTWeekLocalized(), 10);

//
//        dti = new DateTimeInfo("2023-02-23 17:00:00", ZoneId.of("America/Belize"), -74);
//        assert dti.getHTSeasonLocalized() == 71;
//        assert dti.getHTWeekLocalized() == 15;
//
//        dti = new DateTimeInfo("2020-09-21 01:30:00", ZoneId.of("America/Belize"), -74);
//        assert dti.getHTSeasonLocalized() == 76;
//        assert dti.getHTWeekLocalized() == 1;
//
//        dti = new DateTimeInfo("2020-09-21 01:30:00", ZoneId.of("America/Bahia"), -74);
//        assert dti.getHTSeasonLocalized() == 75;
//        assert dti.getHTWeekLocalized() == 16;


    }

//    public class TestRunner {
//        public static void main(String[] args) {
//            Result result = JUnitCore.runClasses(Junit4AssertionTest.class);
//            for (Failure failure : result.getFailures()) {
//                System.out.println(failure.toString());
//            }
//            System.out.println("Result=="+result.wasSuccessful());
//        }


    }
