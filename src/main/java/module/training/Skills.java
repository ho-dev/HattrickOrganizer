package module.training;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.player.Player;

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


    public enum HTSkillID {
        Keeper(1),
        Stamina(2),
        SetPieces(3),
        Defender(4),
        Scorer(5),
        Winger(6),
        Passing(7),
        Playmaker(8),
        Trainer(9),
        Leadership(10),
        Experience(11);

        private int value;
        private static HashMap<Integer, HTSkillID> map = new HashMap<>();

        HTSkillID(int value) {
            this.value = value;
        }

        // Init mapping
        static {
            for (HTSkillID skill : HTSkillID.values()) {
                map.put(skill.value, skill);
            }
        }

        public static HTSkillID valueOf(int skill) {
            return map.get(skill);
        }

        public int getValue() {
            return value;
        }

        public int convertToPlayerSkill(){
            return switch (this) {
                case Keeper -> PlayerSkill.KEEPER;
                case Stamina -> PlayerSkill.STAMINA;
                case SetPieces -> PlayerSkill.SET_PIECES;
                case Defender -> PlayerSkill.DEFENDING;
                case Scorer -> PlayerSkill.SCORING;
                case Winger -> PlayerSkill.WINGER;
                case Passing -> PlayerSkill.PASSING;
                case Playmaker -> PlayerSkill.PLAYMAKING;
                case Leadership -> PlayerSkill.LEADERSHIP;
                case Experience -> PlayerSkill.EXPERIENCE;
                case Trainer -> -1;
            };
        }

    }

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

        private int value;
        private static HashMap<Integer, ScoutCommentSkillTypeID> map = new HashMap<>();

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

        public int getValue() {
            return value;
        }

        final static private EnumMap<ScoutCommentSkillTypeID, HTSkillID> hTskillIdmap = new EnumMap<>(ScoutCommentSkillTypeID.class) {{
            put(KEEPER, HTSkillID.Keeper);
            put(DEFENDING, HTSkillID.Defender);
            put(PLAYMAKER, HTSkillID.Playmaker);
            put(WINGER, HTSkillID.Winger);
            put(PASSING, HTSkillID.Passing);
            put(SCORER, HTSkillID.Scorer);
            put(SET_PIECES, HTSkillID.SetPieces);
        }};

        public HTSkillID toHTSkillId() {
            return hTskillIdmap.get(this);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------
    public static int getSkillAtPosition(int position) {
        return switch (position) {
            case 0 -> PlayerSkill.FORM;
            case 1 -> PlayerSkill.STAMINA;
            case 2 -> PlayerSkill.KEEPER;
            case 3 -> PlayerSkill.DEFENDING;
            case 4 -> PlayerSkill.PLAYMAKING;
            case 5 -> PlayerSkill.WINGER;
            case 6 -> PlayerSkill.PASSING;
            case 7 -> PlayerSkill.SCORING;
            case 8 -> PlayerSkill.SET_PIECES;
            case 9 -> PlayerSkill.EXPERIENCE;
            default -> 0;
        };

    }

    /**
     * Returns the base training type for that skill
     *
     * @param skillIndex skill to train
     * @return base training type for that skill
     */
    public static Color getSkillColor(int skillIndex) {
        return switch (skillIndex) {
            case PlayerSkill.KEEPER -> Color.BLACK;
            case PlayerSkill.PLAYMAKING -> Color.ORANGE.darker();
            case PlayerSkill.PASSING -> Color.GREEN.darker();
            case PlayerSkill.WINGER -> Color.MAGENTA;
            case PlayerSkill.DEFENDING -> Color.BLUE;
            case PlayerSkill.SCORING -> Color.RED;
            case PlayerSkill.SET_PIECES -> Color.CYAN.darker();
            case PlayerSkill.STAMINA -> new Color(85, 26, 139);
            default -> Color.BLACK;
        };

    }

    /**
     * Returns the Skill value for the player
     *
     * @param player player which should be inspected
     * @param skillIndex constant index value of the skill we want to see
     * @return The Skill value or 0 if the index is incorrect
     */
    public static float getSkillValue(Player player, int skillIndex) {
        return switch (skillIndex) {
            case PlayerSkill.KEEPER -> player.getGKskill() + player.getSub4Skill(skillIndex);
            case PlayerSkill.PLAYMAKING -> player.getPMskill() + player.getSub4Skill(skillIndex);
            case PlayerSkill.PASSING -> player.getPSskill() + player.getSub4Skill(skillIndex);
            case PlayerSkill.WINGER -> player.getWIskill() + player.getSub4Skill(skillIndex);
            case PlayerSkill.DEFENDING -> player.getDEFskill() + player.getSub4Skill(skillIndex);
            case PlayerSkill.SCORING -> player.getSCskill() + player.getSub4Skill(skillIndex);
            case PlayerSkill.SET_PIECES -> player.getSPskill() + player.getSub4Skill(skillIndex);
            case PlayerSkill.STAMINA -> player.getStamina() + player.getSub4Skill(skillIndex);
            case PlayerSkill.FORM -> player.getForm() + player.getSub4Skill(skillIndex);
            case PlayerSkill.EXPERIENCE -> player.getExperience() + player.getSub4Skill(skillIndex);
            default -> 0;
        };
    }

    /**
     * Returns the base training type for that skill
     *
     * @param skillIndex skill to train
     * @return base training type for that skill
     */
    public static int getTrainingTypeForSkill(int skillIndex) {
        return switch (skillIndex) {
            case PlayerSkill.KEEPER -> TrainingType.GOALKEEPING;
            case PlayerSkill.PLAYMAKING -> TrainingType.PLAYMAKING;
            case PlayerSkill.PASSING -> TrainingType.SHORT_PASSES;
            case PlayerSkill.WINGER -> TrainingType.CROSSING_WINGER;
            case PlayerSkill.DEFENDING -> TrainingType.DEFENDING;
            case PlayerSkill.SCORING -> TrainingType.SCORING;
            case PlayerSkill.SET_PIECES -> TrainingType.SET_PIECES;
            default -> 0;
        };

    }
}
