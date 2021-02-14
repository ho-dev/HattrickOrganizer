package core.util;


import java.time.ZoneId;

public class HTDatetimeTest {

    public static void main(String[] args) {

        DateTimeInfo dti = new DateTimeInfo("2020-09-07 00:00:00");
        assert dti.getHTSeasonLocalized() == 75;
        assert dti.getHTWeekLocalized() == 10;

        dti = new DateTimeInfo("2023-02-23 17:00:00", ZoneId.of("America/Belize"), -74);
        assert dti.getHTSeasonLocalized() == 71;
        assert dti.getHTWeekLocalized() == 15;

        dti = new DateTimeInfo("2020-09-21 01:30:00", ZoneId.of("America/Belize"), -74);
        assert dti.getHTSeasonLocalized() == 76;
        assert dti.getHTWeekLocalized() == 1;

        dti = new DateTimeInfo("2020-09-21 01:30:00", ZoneId.of("America/Bahia"), -74);
        assert dti.getHTSeasonLocalized() == 75;
        assert dti.getHTWeekLocalized() == 16;


    }


}
