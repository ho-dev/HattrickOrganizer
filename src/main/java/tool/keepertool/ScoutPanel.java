package tool.keepertool;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import module.transfer.scout.ScoutEintrag;
import module.transfer.scout.TransferScoutingTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel for selection of scouted keepers
 *
 * @author draghetto
 */
class ScoutPanel extends JPanel {

	private final JComboBox players = new JComboBox();
    private final ResultPanel target;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ScoutPanel object.
     *
     * @param panel the panel where to show results
     */
    ScoutPanel(ResultPanel panel) {
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

        final TransferScoutingTableModel model = HOMainFrame.instance().getTransferScoutPanel().getScoutPanel()
                                                                         .getTransferTable()
                                                                         .getTransferTableModel();

        for (final ScoutEintrag element : model.getScoutListe()) {
            if (element.getTorwart() > 4) {
                players.addItem(new PlayerItem(element));
            }
        }

        if (players.getItemCount() == 1) {
            players.setEnabled(false);
        }

        players.addItemListener(e -> {
            final PlayerItem selected = (PlayerItem) players.getSelectedItem();
            if (selected != null) {
                target.setPlayer(selected.getForm(), selected.getTsi(), 0,
                                 selected.toString());
            }
        });

        reset();
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
        setLayout(new BorderLayout());
        setOpaque(false);

        final JPanel buttonPanel = new ImagePanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(6, 2));
        buttonPanel.add(label());
        buttonPanel.add(label());
        buttonPanel.add(label());
        buttonPanel.add(players);
        buttonPanel.add(label());
        buttonPanel.add(label());
        buttonPanel.add(label());
        buttonPanel.add(label());
        buttonPanel.add(label());
        buttonPanel.add(label());
        buttonPanel.add(label());
        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Create a configured label
     *
     * @return the built component
     */
    private Component label() {
        final JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setOpaque(false);
        return label;
    }
}
