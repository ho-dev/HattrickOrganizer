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
    private int thickness;
    private int end;
    private float value;
    private int skillIndex;

    Color GREEN = new Color(89, 150, 93); //Form >6
    Color LIGHT_ORANGE = new Color(241, 196, 10); //Form >4 & <=6
    Color ORANGE = new Color(245, 161, 4); //Form >2 & <=4
    Color RED = new Color(221, 65, 64); //Form <=2

    /**
     * ColorBar: initialize a color bar
     */
    public HTColorBar(int skillIndex, float value, int len, int thickness) {
        this.length = len;
        this.end = (int) (len * value);
        this.thickness = thickness;
        this.backgroundColor = GREEN;
        this.value = value;
        this.skillIndex = skillIndex;

        paintImmediately(getBounds());
    }

    public void setLevel(float value, double skillValue) {
        this.end = (int) (value * length);
        this.backgroundColor = GREEN;

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
                break;
            case PlayerSkill.EXPERIENCE:
                break;
            case PlayerSkill.FORM:
                if (skillValue > 4 && skillValue <= 6) {
                    backgroundColor = LIGHT_ORANGE;
                } else if (skillValue > 2 && skillValue <= 4) {
                    backgroundColor = ORANGE;
                } else if (skillValue <= 2) {
                    backgroundColor = RED;
                }
                break;
        }

        paintImmediately(getBounds());
    }

    /* draw a color bar */
    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, length, thickness);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, end, thickness);
        //g.drawLine(0, 0, end, thickness);
    }
}
