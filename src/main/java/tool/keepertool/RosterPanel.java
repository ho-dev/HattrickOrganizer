package tool.keepertool;


import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.player.Player;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * Panel for selection of roster keepers
 *
 * @author draghetto
 */
class RosterPanel extends JPanel {
	
	private static final long serialVersionUID = 4174378650521941024L;
	
    //~ Instance fields ----------------------------------------------------------------------------

	private JComboBox players = new JComboBox();
    private ResultPanel target;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new RosterPanel object.
     *
     * @param panel the panel where to show results
     */
    RosterPanel(ResultPanel panel) {
        target = panel;
        init();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Reload the data and update the panel
     */
    public final void reload() {
        players.removeAllItems();
        players.addItem(new PlayerItem());

        for (Iterator<Player> iter = HOVerwaltung.instance().getModel().getCurrentPlayer().iterator();
             iter.hasNext();) {
            final Player element = iter.next();

            if (element.getGKskill() > 4) {
                players.addItem(new PlayerItem(element));
            }
        }

        players.setEnabled(true);

        if (players.getItemCount() == 1) {
            players.setEnabled(false);
        }

        reset();

        players.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    final PlayerItem selected = (PlayerItem) players.getSelectedItem();

                    if (selected != null) {
                        target.setPlayer(selected.getForm(), selected.getTsi(), selected.getId(),
                                         selected.toString());
                    }
                }
            });
    }

    /**
     * Reset the panel to default data
     */
    public final void reset() {
        players.setSelectedIndex(0);
    }

    /**
     * Initialize the GUI components
     */
    private void init() {
        reload();

        setLayout(new BorderLayout());
        setOpaque(false);

        final JPanel buttonPanel = new ImagePanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(6, 2));
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(players);
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Create a configured label
     *
     * @param string the label text
     *
     * @return the built component
     */
    private Component label(String string) {
        final JLabel label = new JLabel(string, SwingConstants.CENTER);
        label.setOpaque(false);
        return label;
    }
}
