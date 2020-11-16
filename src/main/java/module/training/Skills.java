package module.training;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.player.Player;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that manages all the relation of Skills
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class Skills {

    /*
        Value	Description
        1	Goaltending
        2	Stamina
        3	Set pieces
        4	Defending
        5	Scoring
        6	Winger
        7	Passing
        8	Playmaking
        9	Trainer
        10	Leadership
        11	Experience
     */


    public enum HTSkillID {
        GOALKEEPER(1),
        STAMINA(2),
        SET_PIECES(3),
        DEFENDING(4),
        SCORING(5),
        WINGER(6),
        PASSING(7),
        PLAYMAKING(8),
        TRAINER(9),
        LEADERSHIP(10),
        EXPERIENCE(11);

        private int value;
        private static Map map = new HashMap<>();

        private HTSkillID(int value) {
            this.value = value;
        }

        // Init mapping
        static {
            for (HTSkillID skill : HTSkillID.values()) {
                map.put(skill.value, skill);
            }
        }

        public static HTSkillID valueOf(int skill) {
            return (HTSkillID) map.get(skill);
        }

        public int getValue() {
            return value;
        }

    };

    /*
        ScoutCommentSkillTypeID
    Value	Description
    1	Keeper
    2	(not in use)
    3	Defending
    4	Playmaker
    5	Winger
    6	Scorer
    7	Set Pieces
    8	Passing
     */
    public enum ScoutCommentSkillTypeID {
        KEEPER(1),
        NOT_USED(2),
        DEFENDING(3),
        PLAYMAKER(4),
        WINGER(5),
        SCORER(6),
        SET_PIECES(7),
        PASSING(8);

        private int value;
        private static Map map = new HashMap<>();

        private ScoutCommentSkillTypeID(int value) {
            this.value = value;
        }

        // Init mapping
        static {
            for (ScoutCommentSkillTypeID skill : ScoutCommentSkillTypeID.values()) {
                map.put(skill.value, skill);
            }
        }

        public static ScoutCommentSkillTypeID valueOf(Integer skill) {
            if ( skill != null) {
                return (ScoutCommentSkillTypeID) map.get(skill);
            }
            return null;
        }

        public int getValue() {
            return value;
        }

    };

    //~ Methods ------------------------------------------------------------------------------------
    public static int getSkillAtPosition(int position) {
        switch (position) {
            case 0:
                return PlayerSkill.FORM;

            case 1:
                return PlayerSkill.STAMINA;

            case 2:
                return PlayerSkill.KEEPER;

            case 3:
                return PlayerSkill.DEFENDING;

            case 4:
                return PlayerSkill.PLAYMAKING;

            case 5:
                return PlayerSkill.WINGER;

            case 6:
                return PlayerSkill.PASSING;

            case 7:
                return PlayerSkill.SCORING;

            case 8:
                return PlayerSkill.SET_PIECES;

            case 9:
                return PlayerSkill.EXPERIENCE;
        }

        return 0;
    }

    /**
     * Returns the base training type for that skill
     *
     * @param skillIndex skill to train
     * @return base training type for that skill
     */
    public static Color getSkillColor(int skillIndex) {
        switch (skillIndex) {
            case PlayerSkill.KEEPER:
                return Color.BLACK;

            case PlayerSkill.PLAYMAKING:
                return Color.ORANGE.darker();

            case PlayerSkill.PASSING:
                return Color.GREEN.darker();

            case PlayerSkill.WINGER:
                return Color.MAGENTA;

            case PlayerSkill.DEFENDING:
                return Color.BLUE;

            case PlayerSkill.SCORING:
                return Color.RED;

            case PlayerSkill.SET_PIECES:
                return Color.CYAN.darker();

            case PlayerSkill.STAMINA:
                return new Color(85, 26, 139);
        }

        return Color.BLACK;
    }

    /**
     * Returns the Skill value for the player
     *
     * @param player
     * @param skillIndex constant index value of the skill we want to see
     * @return The Skill value or 0 if the index is incorrect
     */
    public static float getSkillValue(Player player, int skillIndex) {
        switch (skillIndex) {
            case PlayerSkill.KEEPER:
                return player.getGKskill() + player.getSub4Skill(skillIndex);

            case PlayerSkill.PLAYMAKING:
                return player.getPMskill() + player.getSub4Skill(skillIndex);

            case PlayerSkill.PASSING:
                return player.getPSskill() + player.getSub4Skill(skillIndex);

            case PlayerSkill.WINGER:
                return player.getWIskill() + player.getSub4Skill(skillIndex);

            case PlayerSkill.DEFENDING:
                return player.getDEFskill() + player.getSub4Skill(skillIndex);

            case PlayerSkill.SCORING:
                return player.getSCskill() + player.getSub4Skill(skillIndex);

            case PlayerSkill.SET_PIECES:
                return player.getSPskill() + player.getSub4Skill(skillIndex);

            case PlayerSkill.STAMINA:
                return player.getKondition() + player.getSub4Skill(skillIndex);

            case PlayerSkill.FORM:
                return player.getForm() + player.getSub4Skill(skillIndex);

            case PlayerSkill.EXPERIENCE:
                return player.getErfahrung() + player.getSub4Skill(skillIndex);
        }

        return 0;
    }

    /**
     * Returns the base training type for that skill
     *
     * @param skillIndex skill to train
     * @return base training type for that skill
     */
    public static int getTrainedSkillCode(int skillIndex) {
        switch (skillIndex) {
            case PlayerSkill.KEEPER:
                return TrainingType.GOALKEEPING;

            case PlayerSkill.PLAYMAKING:
                return TrainingType.PLAYMAKING;

            case PlayerSkill.PASSING:
                return TrainingType.SHORT_PASSES;

            case PlayerSkill.WINGER:
                return TrainingType.CROSSING_WINGER;

            case PlayerSkill.DEFENDING:
                return TrainingType.DEFENDING;

            case PlayerSkill.SCORING:
                return TrainingType.SCORING;

            case PlayerSkill.SET_PIECES:
                return TrainingType.SET_PIECES;

        }

        return 0;
    }
}
