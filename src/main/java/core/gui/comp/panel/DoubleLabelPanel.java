package core.gui.comp.panel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel displaying two labels that are semantically related, such as main value and comparison value.
 * This is similar to {@link core.gui.comp.entry.DoppelLabelEntry}, except this may live outside
 * a table.
 */
public class DoubleLabelPanel extends JPanel {

    private JLabel leftLabel = new JLabel();
    private JLabel rightLabel = new JLabel();

    public DoubleLabelPanel() {
        setOpaque(false);

        setPreferredSize(new Dimension(90, 10));
        setMinimumSize(new Dimension(90, 40));
        setMinimumSize(new Dimension(90, 10));

        final GridLayout layout = new GridLayout(1, 2);
        setLayout(layout);

        add(leftLabel);
        add(rightLabel);
    }

    public JLabel getLeftLabel() {
        return leftLabel;
    }

    public void setLeftLabel(JLabel leftLabel) {
        this.leftLabel = leftLabel;
        updateComponent();
    }

    public JLabel getRightLabel() {
        return rightLabel;
    }

    public void setRightLabel(JLabel rightLabel) {
        this.rightLabel = rightLabel;
    }

    public void updateComponent() {
        this.removeAll();
        add(leftLabel);
        add(rightLabel);
        revalidate();
        repaint();
    }
}
