package core.training;

import core.constants.player.PlayerSkill;
import core.db.AbstractTable;
import core.training.FuturePlayerTraining.Priority;

public class FuturePlayerSkillTraining extends AbstractTable.Storable {

    /**
     * Player Id
     */
    private int playerId;

    public static Integer getPriorityAsInteger(FuturePlayerSkillTraining o) {
        if (o != null && o.getPriority() != null) {
            return o.getPriority().getValue();
        }
        return null;
    }

    public PlayerSkill getSkillId() {
        return skillId;
    }

    public void setSkillId(PlayerSkill skillId) {
        this.skillId = skillId;
    }

    private PlayerSkill skillId;

    /**
     * priority of the training
     */
    private Priority priority;

    public FuturePlayerSkillTraining(int playerId, Priority prio, PlayerSkill skill) {
        this.playerId = playerId;
        this.priority = prio;
        this.skillId = skill;
    }

    /**
     * constructor is used by AbstractTable.load
     */
    public FuturePlayerSkillTraining(){}

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority prio) {
        this.priority = prio;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

}