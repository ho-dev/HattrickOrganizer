package module.youth;

public class YouthTrainingContext {
    public int age;
    public int days;
    public long numberOfKnownTop3Skills = 0;
    public int minimumTop3SkillPotential = 8;

    public YouthTrainingContext(YouthPlayer youthPlayer) {
        this.age = youthPlayer.getAgeYears();
        this.days = youthPlayer.getAgeDays();
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
}
