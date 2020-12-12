package module.series;

import core.gui.comp.entry.IHOTableEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.series.LigaTabellenEintrag;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Displays the form streak by drawing circles.  Only a max of 9 circles is drawn.
 */
public class FormLabel extends JLabel implements IHOTableEntry {

    public static final Color FG_STANDARD = ThemeManager.getColor(HOColorName.TABLEENTRY_FG);
    public static final Color BG_STANDARD = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);

    private static final Color WIN_COLOR = ThemeManager.getColor(HOColorName.FORM_STREAK_WIN);
    private static final Color DRAW_COLOR = ThemeManager.getColor(HOColorName.FORM_STREAK_DRAW);
    private static final Color DEFEAT_COLOR = ThemeManager.getColor(HOColorName.FORM_STREAK_DEFEAT);
    private static final Color UNKNOWN_COLOR = ThemeManager.getColor(HOColorName.FORM_STREAK_UNKNOWN);

    private static final int MAX_NUM_FORM_STREAK_ENTRIES = 9;

    private final byte[] form;
    private Color bgColor;

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public FormLabel(byte[] form) {
        this.form = form;
    }

    @Override
    public JComponent getComponent(boolean isSelected) {
        return this;
    }

    @Override
    public void clear() {
    }

    @Override
    public int compareTo(@NotNull IHOTableEntry obj) {
        return 0;
    }

    @Override
    public int compareToThird(IHOTableEntry obj) {
        return 0;
    }

    @Override
    public void createComponent() {
        setForeground(FG_STANDARD);
        setBackground(BG_STANDARD);
    }

    @Override
    public void updateComponent() {
        setForeground(FG_STANDARD);
        setBackground(BG_STANDARD);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(bgColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        byte[] formDisplayed = getStreakToDisplay();

        for (int i = 0; i < formDisplayed.length; i++) {
            selectResultColour(g2, formDisplayed[i]);
            g2.fillOval(10+12*i, 10, 10, 10);
        }
    }

    private byte[] getStreakToDisplay() {
        byte[] formDisplayed;
        if (form.length > MAX_NUM_FORM_STREAK_ENTRIES) {
            // Find the first non-unknown result, starting from the end.
            int index = form.length-1;
            while (index >= 0 && form[index] == 0) {
                index--;
            }

            if (index >= 0) {
                // pick the last 9 non-unknown results, unless there are fewer.
                int lengthStreak = Math.min(index+1, MAX_NUM_FORM_STREAK_ENTRIES);
                formDisplayed = new byte[lengthStreak];

                int start = Math.max(index-MAX_NUM_FORM_STREAK_ENTRIES+1, 0);
                System.arraycopy(form, start, formDisplayed, 0, lengthStreak);
            } else {
                formDisplayed = new byte[0];
            }
        } else {
            // This should never get here—form always contains all 14 matches, incl. the ones yet to play.
            formDisplayed = form;
        }
        return formDisplayed;
    }

    private void selectResultColour(Graphics2D g2, byte cur) {
        switch (cur) {
            case LigaTabellenEintrag.H_SIEG:
            case LigaTabellenEintrag.A_SIEG:
                g2.setColor(WIN_COLOR);
                break;
            case LigaTabellenEintrag.H_UN:
            case LigaTabellenEintrag.A_UN:
                g2.setColor(DRAW_COLOR);
                break;
            case LigaTabellenEintrag.H_NIED:
            case LigaTabellenEintrag.A_NIED:
                g2.setColor(DEFEAT_COLOR);
                break;
            default:
                g2.setColor(UNKNOWN_COLOR);
        }
    }
}
