package core.training;


import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.XtraData;
import core.util.HOLogger;
import core.util.Helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TrainingManagerTest {

    public static void main(String[] args) {

        HOVerwaltung.instance().loadLatestHoModel();
        HOVerwaltung.instance().setResource("English");

        Helper.getTranslation("ls.team.trainingtype.playmaking");

        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(7, ChronoUnit.DAYS);

        TrainingWeekManager trainingWeekManager = new TrainingWeekManager(startDate, endDate);

        System.out.print(trainingWeekManager.getTrainingList());

    }


}
