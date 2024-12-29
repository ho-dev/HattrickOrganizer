package core.gui.comp.panel;

import core.gui.comp.entry.DoubleLabelEntries;
import core.model.UserParameter;

import javax.swing.*;
import java.awt.*;

/**
 * Panel displaying two labels that are semantically related, such as main value and comparison value.
 * This is similar to {@link DoubleLabelEntries}, except this may live outside
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
        layout.setVgap(0);
        setLayout(layout);
        // Fix right label width to avoid components moving when values change.
        var width = UserParameter.instance().fontSize * 5;
        var height = UserParameter.instance().fontSize;
        rightLabel.setMinimumSize(new Dimension(width, height));
        rightLabel.setMaximumSize(new Dimension(width, height));
        rightLabel.setPreferredSize(new Dimension(width, height));

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
