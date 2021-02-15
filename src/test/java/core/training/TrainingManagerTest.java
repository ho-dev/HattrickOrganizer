package core.training;

import core.HO;
import core.model.HOVerwaltung;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TrainingManagerTest {

    public static void main(String[] args) {

        HO.setPortable_version(true);
        HOVerwaltung.instance().loadLatestHoModel();
        HOVerwaltung.instance().setResource("English");

        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(7, ChronoUnit.DAYS);

       // TrainingWeekManager trainingWeekManager = new TrainingWeekManager(startDate, endDate);

        //System.out.print(trainingWeekManager.getTrainingList());

    }


}
