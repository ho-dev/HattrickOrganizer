package module.youth;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.util.HODateTime;

import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class YouthTrainingContext {
    public int age;
    public int days;
    private HODateTime birthday;

    public long numberOfKnownTop3Skills = 0;
    public int minimumTop3SkillPotential = 8;

    public SortedMap<HODateTime, AbstractMap.SimpleEntry<PlayerSkill, Double>> futureTrainings = new TreeMap<>();

    public YouthTrainingContext(YouthPlayer youthPlayer) {
        this.age = youthPlayer.getAgeYears();
        this.days = youthPlayer.getAgeDays();
        var hrfTime = HOVerwaltung.instance().getModel().getBasics().getDatum();
        birthday = hrfTime.minus(age*112+days, ChronoUnit.DAYS);

        for (var s : youthPlayer.getCurrentSkills().values()) {
            if (s.isTop3() != null) {
                if (s.isTop3()) {
                    numberOfKnownTop3Skills++;
                    if (s.isMaxAvailable() && s.getMax() < minimumTop3SkillPotential) {
                        minimumTop3SkillPotential = s.getMax();
                    }
                }
            }
        }
    }

    public void addFutureTraining(PlayerSkill s, double max) {
        var t = birthday.plus(age*112+days, ChronoUnit.DAYS);
        futureTrainings.put(t, new AbstractMap.SimpleEntry<>(s, max));
    }
}
