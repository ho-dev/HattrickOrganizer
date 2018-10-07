// %2956927164:plugins%
package module.teamOfTheWeek.gui;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 * This is an empty panel to display a lineup
 */
class LineupPanel extends JPanel {

	private static final long serialVersionUID = -1373544624896628833L;

    private JLabel m_jlTeamName;
    private JPanel m_clLeftBack= new JPanel();
    private JPanel m_clLeftWinger= new JPanel();
    private JPanel m_clLeftCentralDefender= new JPanel();
    private JPanel m_clLeftMidfield= new JPanel();
    private JPanel m_clLeftForward= new JPanel();
    private JPanel m_clRightBack= new JPanel();
    private JPanel m_clRightWinger= new JPanel();
    private JPanel m_clRightCentralDefender= new JPanel();
    private JPanel m_clRightMidfield= new JPanel();
    private JPanel m_clRightForward= new JPanel();
    private JPanel m_clKeeper= new JPanel();

    LineupPanel() {
        super();
        setOpaque(false);
        initComponents();
    }

    final JPanel getKeeperPanel() {
        return m_clKeeper;
    }

    final JPanel getLeftCentralDefenderPanel() {
        return m_clLeftCentralDefender;
    }

    final JPanel getLeftForwardPanel() {
        return m_clLeftForward;
    }

    final JPanel getLeftMidfieldPanel() {
        return m_clLeftMidfield;
    }

    final JPanel getLeftWingPanel() {
        return m_clLeftWinger;
    }

    final JPanel getLeftWingbackPanel() {
        return m_clLeftBack;
    }

 
    final JPanel getRightCentralDefenderPanel() {
        return m_clRightCentralDefender;
    }

    final JPanel getRightForwardPanel() {
        return m_clRightForward;
    }

    final JPanel getRightMidfieldPanel() {
        return m_clRightMidfield;
    }

    final JPanel getRightWingPanel() {
        return m_clRightWinger;
    }

    final JPanel getRightWingbackPanel() {
        return m_clRightBack;
    }

    //-- Getter/Setter ----------------------------------------------------------

    final void setTeamName(String teamname) {
        m_jlTeamName.setText(teamname);
    }

    final String getTeamName() {
        return m_jlTeamName.getText();
    }

     private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);

        final javax.swing.JPanel centerPanel = new javax.swing.JPanel();
        centerPanel.setOpaque(false);

        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 2, 2, 2);

        centerPanel.setLayout(layout);


            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 4;
       

        m_jlTeamName = new JLabel();
        m_jlTeamName.setOpaque(false);
        m_jlTeamName.setForeground(ThemeManager.getColor(HOColorName.LABEL_ONGREEN_FG));
        m_jlTeamName.setFont(m_jlTeamName.getFont().deriveFont(Font.BOLD, UserParameter.instance().schriftGroesse + 3));
        layout.setConstraints(m_jlTeamName, constraints);
        centerPanel.add(m_jlTeamName);

            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 4;
        
        m_clKeeper.setOpaque(false);
        layout.setConstraints(m_clKeeper, constraints);
        centerPanel.add(m_clKeeper);
       
            constraints.gridx = 0;
            constraints.gridy = 5;
            constraints.gridwidth = 1;

        m_clRightBack.setOpaque(false);
        layout.setConstraints(m_clRightBack, constraints);
        centerPanel.add(m_clRightBack);
        
            constraints.gridx = 1;
            constraints.gridy = 5;
            constraints.gridwidth = 1;
 
        m_clRightCentralDefender.setOpaque(false);
        layout.setConstraints(m_clRightCentralDefender, constraints);
        centerPanel.add(m_clRightCentralDefender);
        
            constraints.gridx = 2;
            constraints.gridy = 5;
            constraints.gridwidth = 1;

        m_clLeftCentralDefender.setOpaque(false);
        layout.setConstraints(m_clLeftCentralDefender, constraints);
        centerPanel.add(m_clLeftCentralDefender);

            constraints.gridx = 3;
            constraints.gridy = 5;
            constraints.gridwidth = 1;
        
        m_clLeftBack.setOpaque(false);
        layout.setConstraints(m_clLeftBack, constraints);
        centerPanel.add(m_clLeftBack);
       
            constraints.gridx = 0;
            constraints.gridy = 6;
            constraints.gridwidth = 1;
        
        m_clRightWinger.setOpaque(false);
        layout.setConstraints(m_clRightWinger, constraints);
        centerPanel.add(m_clRightWinger);
        
            constraints.gridx = 1;
            constraints.gridy = 6;
            constraints.gridwidth = 1;
        
        m_clRightMidfield.setOpaque(false);
        layout.setConstraints(m_clRightMidfield, constraints);
        centerPanel.add(m_clRightMidfield);
        
            constraints.gridx = 2;
            constraints.gridy = 6;
            constraints.gridwidth = 1;
       
       m_clLeftMidfield.setOpaque(false);
        layout.setConstraints(m_clLeftMidfield, constraints);
        centerPanel.add(m_clLeftMidfield);
        
            constraints.gridx = 3;
            constraints.gridy = 6;
            constraints.gridwidth = 1;
        
        m_clLeftWinger.setOpaque(false);
        layout.setConstraints(m_clLeftWinger, constraints);
        centerPanel.add(m_clLeftWinger);
        
            constraints.gridx = 1;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
        
        m_clLeftForward.setOpaque(false);
        layout.setConstraints(m_clLeftForward, constraints);
        centerPanel.add(m_clLeftForward);
        
            constraints.gridx = 2;
            constraints.gridy = 7;
            constraints.gridwidth = 1;
        
        m_clRightForward.setOpaque(false);
        layout.setConstraints(m_clRightForward, constraints);
        centerPanel.add(m_clRightForward);
       
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
        

        final JLabel label = new JLabel();
        label.setOpaque(true);
        layout.setConstraints(label, constraints);
        centerPanel.add(label);

       
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
        

        add(centerPanel, BorderLayout.CENTER);
    }
}
