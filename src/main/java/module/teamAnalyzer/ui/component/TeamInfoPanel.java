package module.teamAnalyzer.ui.component;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class TeamInfoPanel extends JPanel {


    public void setTeam(Map<String, String> details) {
        removeAll();
        setBorder(BorderFactory.createTitledBorder("Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Manager: "), gbc);
        gbc.gridy++;
        add(new JLabel("Last Login: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel(details.get("Loginname")), gbc);
        gbc.gridy++;
        add(new JLabel(details.get("LastLoginDate")), gbc);

    }
}
