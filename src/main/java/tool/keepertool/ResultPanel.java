package tool.keepertool;

import core.constants.player.PlayerSkill;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.player.Player;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;


/**
 * Panel where results are shown
 *
 * @author draghetto
 */
class ResultPanel extends JPanel {

	private static final long serialVersionUID = 272383166131665396L;

    //~ Instance fields ----------------------------------------------------------------------------

	private DecimalFormat df = new DecimalFormat("#.00");
    private JButton set = new JButton();
    private JDialog parent;
    private JTextArea result = new JTextArea();
    private double average;
    private int id;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ResultPanel object.
     *
     * @param dialog the main KeeperTool dialog
     */
    ResultPanel(KeeperToolDialog dialog) {
        parent = dialog;
        init();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Reset the panel to default data
     */
    public final void reset() {
        id = 0;
        average = 0;
        set.setEnabled(false);
        result.setText(core.model.HOVerwaltung.instance().getLanguageString("ls.player.skill"));
        set.setText(core.model.HOVerwaltung.instance().getLanguageString("Disabled"));
    }

    /**
     * Methods that calculates the player's keeper subskill. show them and if a roster player
     * enable the button to update it
     *
     * @param form keeper form
     * @param tsi keeper tsi
     * @param playerId keeper id, 0 if not real
     * @param name player name if scout or roster, empty if manual
     */
    protected final void setPlayer(int form, int tsi, int playerId, String name) {
        if (tsi == 0) {
            reset();
            return;
        }

        final KeeperTool kt = new KeeperTool(form, tsi);
        id = playerId;
        result.setText(core.model.HOVerwaltung.instance().getLanguageString("ls.player.skill") + ": " + df.format(kt.getMin()) + " - " + df.format(kt.getMax()));
        average = kt.getAvg();

        if (playerId > 0) {
            set.setText(core.model.HOVerwaltung.instance().getLanguageString("OffsetTitle") + " "
                        + name);
            set.setEnabled(true);
        } else {
            set.setText(core.model.HOVerwaltung.instance().getLanguageString("Disabled"));
            set.setEnabled(false);
        }
    }

    /**
     * Initialize the GUI components
     */
    private void init() {
        setOpaque(false);
        setLayout(new BorderLayout());

        final JPanel panel = new ImagePanel();
        add(panel, BorderLayout.CENTER);
        result.setOpaque(false);
        panel.setLayout(new GridLayout(2, 1));
        panel.add(result, BorderLayout.NORTH);
        panel.add(set, BorderLayout.SOUTH);

        set.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent arg0) {
                    final Player sp = HOVerwaltung.instance().getModel().getCurrentPlayer(id);
                    double decimals = average - sp.getGKskill()
                                      - sp.getSub4Skill(PlayerSkill.KEEPER);

                    if (decimals > 1) {
                        decimals = 0.99;
                    }

                    if (decimals < 0) {
                        decimals = 0;
                    }

                    sp.setSubskill4Pos(PlayerSkill.KEEPER, (float)decimals);
                    core.db.DBManager.instance().saveSpieler(core.model.HOVerwaltung.instance()
                                                                                                                          .getModel()
                                                                                                                          .getID(),
                                                                                   core.model.HOVerwaltung.instance()
                                                                                                                          .getModel()
                                                                                                                          .getCurrentPlayers(),
                                                                                   core.model.HOVerwaltung.instance()
                                                                                                                          .getModel()
                                                                                                                          .getBasics()
                                                                                                                          .getDatum());
                    core.gui.RefreshManager.instance().doReInit();
                    parent.setVisible(false);
                    parent.dispose();
                }
            });
        reset();
    }
}
