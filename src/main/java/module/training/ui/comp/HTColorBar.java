// %1542412551:hoplugins.trainingExperience.ui.bar%
package module.training.ui.comp;

import core.constants.player.PlayerSkill;

import javax.swing.*;
import java.awt.*;

/**
 * ColorBar: A color bar
 */
public final class HTColorBar extends JComponent {

    private int length;
    private Color backgroundColor;
    private Color backgroundFirstColor;
    private Color backgroundSecondColor;
    private Color backgroundThirdColor;
    private int thickness;
    private int end;
    private float value;
    private float secondValue;
    private float thirdValue;
    private int skillIndex;

    Color GREY = new Color(236, 236, 236);
    Color GREEN = new Color(89, 150, 93); //Form, Stamina >6
    Color LIGHT_ORANGE = new Color(241, 196, 10); //Form, Stamina >4 & <=6
    Color ORANGE = new Color(245, 161, 4); //Form, Stamina >2 & <=4
    Color RED = new Color(221, 65, 64); //Form, Stamina <=2

    Color LIGHT_GREEN = new Color(101, 170, 105); // Skill Decimal
    Color ULTRA_LIGHT_GREEN = new Color(120, 195, 125); // Future Training

    /**
     * ColorBar: initialize a color bar
     */
    public HTColorBar(int skillIndex, float value, int len, int thickness) {
        this.length = len;
        this.end = (int) (len * value);
        this.thickness = thickness;
        this.backgroundColor = GREY;
        this.backgroundFirstColor = GREEN;
        this.backgroundSecondColor = GREEN;
        this.backgroundThirdColor = GREEN;
        this.value = value;
        this.secondValue = 0;
        this.thirdValue = 0;
        this.skillIndex = skillIndex;

        paintImmediately(getBounds());
    }

    public void setLevel(float value, double skillValue) {
        this.end = (int) (value * length);
        this.backgroundFirstColor = GREEN;

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
                    backgroundFirstColor = LIGHT_ORANGE;
                } else if (skillValue > 2 && skillValue <= 4) {
                    backgroundFirstColor = ORANGE;
                } else if (skillValue <= 2) {
                    backgroundFirstColor = RED;
                }
                break;
        }

        paintImmediately(getBounds());
    }

    public void setSecondLevel(float secondValue) {
        this.backgroundSecondColor = LIGHT_GREEN;
        this.secondValue = secondValue;
        paintImmediately(getBounds());
    }

    public void setThirdLevel(float thirdValue) {
        this.backgroundThirdColor = ULTRA_LIGHT_GREEN;
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
        //g.clearRect(0, 0, length, thickness);
        g.setColor(backgroundFirstColor);
        g.fillRect(xStart, 0, xEnd, thickness);

        if (secondValue > 0) {
            xStart += xEnd;
            xEnd = (int) (secondValue * length);
            g.setColor(backgroundSecondColor);
            g.fillRect(xStart, 0, xEnd, thickness);
        }

        if (thirdValue > 0) {
            xStart += xEnd;
            xEnd = (int) (thirdValue * length);
            g.setColor(backgroundThirdColor);
            g.fillRect(xStart, 0, xEnd, thickness);
        }
    }
}
