// %1542412551:hoplugins.trainingExperience.ui.bar%
package module.training.ui.comp;

import core.constants.player.PlayerSkill;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * ColorBar: A color bar
 */
public final class HTColorBar extends JComponent {

    private int length;
    private Color backgroundColor;
    private Color firstColor;
    private Color secondColor;
    private Color thirdColor;
    private int thickness;
    private int end;
    private float secondValue;
    private float thirdValue;
    private int skillIndex;

    Color COLOR_BACKGROUND = ThemeManager.getColor(HOColorName.PANEL_BG);
    Color COLOR_SKILL_GOOD = new Color(89, 150, 93); //Form, Stamina >6
    Color COLOR_SKILL_MEDIUM = new Color(241, 196, 10); //Form, Stamina >4 & <=6
    Color COLOR_SKILL_LOW = new Color(245, 161, 4); //Form, Stamina >2 & <=4
    Color COLOR_SKILL_BAD = new Color(221, 65, 64); //Form, Stamina <=2

    Color COLOR_SKILL_DECIMAL = new Color(153, 255, 157); // Skill Decimal
    Color COLOR_FUTURE_SKILL = new Color(120, 195, 125); // Future Training

    /**
     * ColorBar: initialize a color bar
     */
    public HTColorBar(int skillIndex, float value, int len, int thickness) {
        this.length = len;
        this.end = (int) (len * value);
        this.thickness = thickness;
        this.backgroundColor = COLOR_BACKGROUND;
        this.firstColor = COLOR_SKILL_GOOD;
        this.secondColor = COLOR_SKILL_GOOD;
        this.thirdColor = COLOR_SKILL_GOOD;
        this.secondValue = 0;
        this.thirdValue = 0;
        this.skillIndex = skillIndex;

        paintImmediately(getBounds());
    }

    public void setSkillLevel(float value, double skillValue) {
        this.end = (int) (value * length);
        this.firstColor = COLOR_SKILL_GOOD;

        switch (skillIndex) {
            case PlayerSkill.KEEPER:
            case PlayerSkill.PLAYMAKING:
            case PlayerSkill.PASSING:
            case PlayerSkill.WINGER:
            case PlayerSkill.DEFENDING:
            case PlayerSkill.SCORING:
            case PlayerSkill.SET_PIECES:
                break;
            case PlayerSkill.STAMINA:
            case PlayerSkill.FORM:
                if (skillValue > 4 && skillValue <= 6) {
                    firstColor = COLOR_SKILL_MEDIUM;
                } else if (skillValue > 2 && skillValue <= 4) {
                    firstColor = COLOR_SKILL_LOW;
                } else if (skillValue <= 2) {
                    firstColor = COLOR_SKILL_BAD;
                }
                break;
        }

        paintImmediately(getBounds());
    }

    public void setSkillDecimalLevel(float secondValue) {
        this.secondColor = COLOR_SKILL_DECIMAL;
        this.secondValue = secondValue;
        paintImmediately(getBounds());
    }

    public void setFutureSkillLevel(float thirdValue) {
        this.thirdColor = COLOR_FUTURE_SKILL;
        this.thirdValue = thirdValue;
        paintImmediately(getBounds());
    }

    /* draw a color bar */
    @Override
    public void paint(Graphics g) {
        int xStart = 0;
        int xEnd = end;

        g.setColor(backgroundColor);
        g.fillRect(0, 0, length, thickness);
        g.setColor(firstColor);
        g.fillRect(xStart, 0, xEnd, thickness);

        if (secondValue > 0) {
            xStart += xEnd;
            xEnd = (int) (secondValue * length);
            g.setColor(secondColor);
            g.fillRect(xStart, 0, xEnd, thickness);
        }

        if (thirdValue > 0) {
            xStart += xEnd;
            xEnd = (int) (thirdValue * length);
            g.setColor(thirdColor);
            g.fillRect(xStart, 0, xEnd, thickness);
        }
    }
}
