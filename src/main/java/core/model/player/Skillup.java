package core.model.player;

import core.db.AbstractTable;
import core.util.HODateTime;

public class Skillup extends AbstractTable.Storable {

    private int hrfId;
    private HODateTime date;
    private int playerId;
    private int skill;
    private int value;

    public Skillup(){}

    public int getHrfId() {
        return this.hrfId;
    }

    public void setHrfId(int hrfId) {
        this.hrfId = hrfId;
    }

    public void setDate(HODateTime v) {
        this.date=v;
    }

    public HODateTime getDate() {
        return date;
    }

    public void setPlayerId(int v) {
        this.playerId=v;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setSkill(int v) {
        this.skill=v;
    }

    public int getSkill() {
        return skill;
    }

    public void setValue(int v) {
        this.value=v;
    }

    public int getValue() {
        return value;
    }
}
