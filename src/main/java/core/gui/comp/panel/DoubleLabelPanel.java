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
        addLabels();
    }

    private void addLabels() {
        final FlowLayout layout = new FlowLayout();
        setLayout(layout);
        // Fix right label width to avoid components moving when values change.
        rightLabel.setMinimumSize(new Dimension(90, 10));
        rightLabel.setMaximumSize(new Dimension(90, 10));
        rightLabel.setPreferredSize(new Dimension(90, 10));

        add(leftLabel);
        add(rightLabel);
    }

    public void setLeftLabel(JLabel leftLabel) {
        this.leftLabel = leftLabel;
        updateComponent();
    }

    public void setRightLabel(JLabel rightLabel) {
        this.rightLabel = rightLabel;
    }

    public void updateComponent() {
        this.removeAll();
        addLabels();
        revalidate();
        repaint();
    }
}
