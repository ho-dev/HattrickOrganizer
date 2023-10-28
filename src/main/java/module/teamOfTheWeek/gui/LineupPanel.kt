// %2956927164:plugins%
package module.teamOfTheWeek.gui;

import java.awt.*;
import java.io.Serial;
import javax.swing.*;


/**
 * This is an empty panel to display a lineup
 */
class LineupPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -1373544624896628833L;

    private final JComponent m_clLeftBack = new JPanel();
    private final JComponent m_clLeftWinger = new JPanel();
    private final JComponent m_clLeftCentralDefender = new JPanel();
    private final JComponent m_clLeftMidfield = new JPanel();
    private final JComponent m_clLeftForward = new JPanel();
    private final JComponent m_clRightBack = new JPanel();
    private final JComponent m_clRightWinger = new JPanel();
    private final JComponent m_clRightCentralDefender = new JPanel();
    private final JComponent m_clRightMidfield = new JPanel();
    private final JComponent m_clRightForward = new JPanel();
    private final JComponent m_clKeeper = new JPanel();

    LineupPanel() {
        super();
        setOpaque(false);
        initComponents();
    }

    final JComponent getKeeperPanel() {
        return m_clKeeper;
    }

    final JComponent getLeftCentralDefenderPanel() {
        return m_clLeftCentralDefender;
    }

    final JComponent getLeftForwardPanel() {
        return m_clLeftForward;
    }

    final JComponent getLeftMidfieldPanel() {
        return m_clLeftMidfield;
    }

    final JComponent getLeftWingPanel() {
        return m_clLeftWinger;
    }

    final JComponent getLeftWingbackPanel() {
        return m_clLeftBack;
    }


    final JComponent getRightCentralDefenderPanel() {
        return m_clRightCentralDefender;
    }

    final JComponent getRightForwardPanel() {
        return m_clRightForward;
    }

    final JComponent getRightMidfieldPanel() {
        return m_clRightMidfield;
    }

    final JComponent getRightWingPanel() {
        return m_clRightWinger;
    }

    final JComponent getRightWingbackPanel() {
        return m_clRightBack;
    }

    //-- Getter/Setter ----------------------------------------------------------

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);

        var centerPanel = new JPanel();
        centerPanel.setOpaque(false);

        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.insets = new Insets(2, 2, 2, 2);

        centerPanel.setLayout(layout);

        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 2;

        m_clKeeper.setOpaque(false);
        centerPanel.add(m_clKeeper, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;

        m_clRightBack.setOpaque(false);
        centerPanel.add(m_clRightBack, constraints);

        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 1;

        m_clRightCentralDefender.setOpaque(false);
        centerPanel.add(m_clRightCentralDefender, constraints);

        constraints.gridx = 2;
        constraints.gridy = 5;
        constraints.gridwidth = 1;

        m_clLeftCentralDefender.setOpaque(false);
        centerPanel.add(m_clLeftCentralDefender, constraints);

        constraints.gridx = 3;
        constraints.gridy = 5;
        constraints.gridwidth = 1;

        m_clLeftBack.setOpaque(false);
        centerPanel.add(m_clLeftBack, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 1;

        m_clRightWinger.setOpaque(false);
        centerPanel.add(m_clRightWinger,constraints);

        constraints.gridx = 1;
        constraints.gridy = 6;
        constraints.gridwidth = 1;

        m_clRightMidfield.setOpaque(false);
        centerPanel.add(m_clRightMidfield, constraints);

        constraints.gridx = 2;
        constraints.gridy = 6;
        constraints.gridwidth = 1;

        m_clLeftMidfield.setOpaque(false);
        centerPanel.add(m_clLeftMidfield, constraints);

        constraints.gridx = 3;
        constraints.gridy = 6;
        constraints.gridwidth = 1;

        m_clLeftWinger.setOpaque(false);
        centerPanel.add(m_clLeftWinger, constraints);

        constraints.gridx = 1;
        constraints.gridy = 7;
        constraints.gridwidth = 1;

        m_clLeftForward.setOpaque(false);
        centerPanel.add(m_clLeftForward, constraints);

        constraints.gridx = 2;
        constraints.gridy = 7;
        constraints.gridwidth = 1;

        m_clRightForward.setOpaque(false);
        centerPanel.add(m_clRightForward, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;

        final JLabel label = new JLabel();
        label.setOpaque(true);
        centerPanel.add(label, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;

        add(centerPanel, BorderLayout.CENTER);
    }

}
