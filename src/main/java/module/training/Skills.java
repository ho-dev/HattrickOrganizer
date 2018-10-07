package module.training;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.player.Spieler;
import java.awt.Color;

/**
 * Class that manages all the relation of Skills
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class Skills {
    //~ Methods ------------------------------------------------------------------------------------
    public static int getSkillAtPosition(int position) {
        switch (position) {
            case 0:
                return PlayerSkill.KEEPER;

            case 1:
                return PlayerSkill.PLAYMAKING;

            case 2:
                return PlayerSkill.PASSING;

            case 3:
                return PlayerSkill.WINGER;

            case 4:
                return PlayerSkill.DEFENDING;

            case 5:
                return PlayerSkill.SCORING;

            case 6:
                return PlayerSkill.SET_PIECES;

            case 7:
                return PlayerSkill.STAMINA;

            case 8:
                return PlayerSkill.EXPERIENCE;
        }

        return 0;
    }

    /**
     * Returns the base training type for that skill
     *
     * @param skillIndex skill to train
     *
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
     * @param spieler
     * @param skillIndex constant index value of the skill we want to see
     *
     * @return The Skill value or 0 if the index is incorrect
     */
    public static int getSkillValue(Spieler spieler, int skillIndex) {
        switch (skillIndex) {
            case PlayerSkill.KEEPER:
                return spieler.getTorwart();

            case PlayerSkill.PLAYMAKING:
                return spieler.getSpielaufbau();

            case PlayerSkill.PASSING:
                return spieler.getPasspiel();

            case PlayerSkill.WINGER:
                return spieler.getFluegelspiel();

            case PlayerSkill.DEFENDING:
                return spieler.getVerteidigung();

            case PlayerSkill.SCORING:
                return spieler.getTorschuss();

            case PlayerSkill.SET_PIECES:
                return spieler.getStandards();

            case PlayerSkill.STAMINA:
                return spieler.getKondition();

            case PlayerSkill.EXPERIENCE:
                return spieler.getErfahrung();
        }

        return 0;
    }

    /**
     * Returns the base training type for that skill
     *
     * @param skillIndex skill to train
     *
     * @return base training type for that skill
     */
    public static int getTrainedSkillCode(int skillIndex) {
        switch (skillIndex) {
            case PlayerSkill.KEEPER:
                return  TrainingType.GOALKEEPING;

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
