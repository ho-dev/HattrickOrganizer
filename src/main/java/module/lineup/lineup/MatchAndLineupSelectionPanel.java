package module.lineup.lineup;

import com.google.gson.JsonObject;
import core.gui.Refreshable;
import core.gui.Updatable;
import core.model.HOVerwaltung;
import core.util.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MatchAndLineupSelectionPanel extends JPanel implements Refreshable {

    private JLabel m_jlPlaceHolder; //TO DO remove this one

    public MatchAndLineupSelectionPanel() {
        initComponents();
        core.gui.RefreshManager.instance().registerRefreshable(this);
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);

        m_jlPlaceHolder = new JLabel("Placeholder");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        layout.setConstraints(m_jlPlaceHolder, gbc);
        add(m_jlPlaceHolder);


        addItemListeners();
    }

    private void setComponents() {}

    private void addItemListeners() {}

    private void removeItemListeners() {}

    @Override
    public void reInit() {
        refresh();
    }

    @Override
    public void refresh() {
        removeItemListeners();
        setComponents();
        addItemListeners();
    }

}
