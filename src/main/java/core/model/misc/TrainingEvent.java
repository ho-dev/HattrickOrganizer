package core.model.misc;

import core.util.HelperWrapper;
import module.training.Skills;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Map;

public class TrainingEvent {

    private int skillID;
    //An identifier to show which type of skill that has been affected.

    private int oldLevel;
    //An integer to show the old level of the skill.

    private int newLevel;
    // An integer to show the new level of the skill.

    private int season;
    // An integer to show which season the event was recorded. The season is related to the league of the team of the player.

    private int matchRound;
    // An integer to show which matchround the event was recorded. The season is related to the league of the team of the player.

    private int dayNumber;
    // An integer to show which season the event was recorded. The season is related to the league of the team of the player. Ranges from 1 to 7

    private Timestamp eventDate;
    // undocumented feature

    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setEventDate(Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    public TrainingEvent(Map<String, String> trainingEvent) {
        this.skillID = Integer.parseInt(trainingEvent.get("SkillID"));
        this.oldLevel = Integer.parseInt(trainingEvent.get("OldLevel"));
        this.newLevel = Integer.parseInt(trainingEvent.get("NewLevel"));
        this.season = Integer.parseInt(trainingEvent.get("Season"));
        this.matchRound = Integer.parseInt(trainingEvent.get("MatchRound"));
        this.dayNumber = Integer.parseInt(trainingEvent.get("DayNumber"));

        String eventdatestr = trainingEvent.get("EventDate");
        try {
            this.eventDate = new Timestamp(HelperWrapper.instance().getHattrickDate(eventdatestr).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public void setOldLevel(int oldLevel) {
        this.oldLevel = oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getMatchRound() {
        return matchRound;
    }

    public void setMatchRound(int matchRound) {
        this.matchRound = matchRound;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getPlayerSkill() {
        return Skills.HTSkillID.valueOf(this.skillID).getValue();
    }
}
