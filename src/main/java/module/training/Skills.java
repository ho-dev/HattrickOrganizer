package module.training;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import java.awt.Color;
import java.util.EnumMap;
import java.util.HashMap;

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
        AVERAGE(0),
        KEEPER(1),
        NOT_USED(2),
        DEFENDING(3),
        PLAYMAKER(4),
        WINGER(5),
        SCORER(6),
        SET_PIECES(7),
        PASSING(8);

        private final int value;
        private static final HashMap<Integer, ScoutCommentSkillTypeID> map = new HashMap<>();

        ScoutCommentSkillTypeID(int value) {
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
                return map.get(skill);
            }
            return null;
        }

        public static Integer value(ScoutCommentSkillTypeID typeID){
            if ( typeID != null){
                return typeID.getValue();
            }
            return null;
        }

        public int getValue() {
            return value;
        }

        final static private EnumMap<ScoutCommentSkillTypeID, PlayerSkill> hTskillIdmap = new EnumMap<>(ScoutCommentSkillTypeID.class) {{
            put(KEEPER, PlayerSkill.KEEPER);
            put(DEFENDING, PlayerSkill.DEFENDING);
            put(PLAYMAKER, PlayerSkill.PLAYMAKING);
            put(WINGER, PlayerSkill.WINGER);
            put(PASSING, PlayerSkill.PASSING);
            put(SCORER, PlayerSkill.SCORING);
            put(SET_PIECES, PlayerSkill.SETPIECES);
        }};

        public PlayerSkill toHTSkillId() {
            return hTskillIdmap.get(this);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------
    public static PlayerSkill getSkillAtPosition(int position) {
        return switch (position) {
            case 0 -> PlayerSkill.FORM;
            case 1 -> PlayerSkill.STAMINA;
            case 2 -> PlayerSkill.KEEPER;
            case 3 -> PlayerSkill.DEFENDING;
            case 4 -> PlayerSkill.PLAYMAKING;
            case 5 -> PlayerSkill.WINGER;
            case 6 -> PlayerSkill.PASSING;
            case 7 -> PlayerSkill.SCORING;
            case 8 -> PlayerSkill.SETPIECES;
            case 9 -> PlayerSkill.EXPERIENCE;
            default -> null;
        };

    }

    /**
     * Returns the base training type for that skill
     *
     * @param skillIndex skill to train
     * @return base training type for that skill
     */
    public static Color getSkillColor(PlayerSkill skillIndex) {
        if ( skillIndex != null ) {
            return switch (skillIndex) {
                case KEEPER -> Color.BLACK;
                case PLAYMAKING -> Color.ORANGE.darker();
                case PASSING -> Color.GREEN.darker();
                case WINGER -> Color.MAGENTA;
                case DEFENDING -> Color.BLUE;
                case SCORING -> Color.RED;
                case SETPIECES -> Color.CYAN.darker();
                case STAMINA -> new Color(85, 26, 139);
                default -> Color.BLACK;
            };
        }
        return Color.BLACK;
    }

    /**
     * Returns the base training type for that skill
     *
     * @param skillIndex skill to train
     * @return base training type for that skill
     */
    public static int getTrainingTypeForSkill(PlayerSkill skillIndex) {
        return switch (skillIndex) {
            case KEEPER -> TrainingType.GOALKEEPING;
            case PLAYMAKING -> TrainingType.PLAYMAKING;
            case PASSING -> TrainingType.SHORT_PASSES;
            case WINGER -> TrainingType.CROSSING_WINGER;
            case DEFENDING -> TrainingType.DEFENDING;
            case SCORING -> TrainingType.SCORING;
            case SETPIECES -> TrainingType.SET_PIECES;
            default -> 0;
        };

    }
}