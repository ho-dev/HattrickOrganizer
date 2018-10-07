package tool.keepertool;

import core.constants.player.PlayerAbility;
import core.gui.comp.panel.ImagePanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


/**
 * Panel for manual editing of keeper data
 *
 * @author draghetto
 */
class ManualPanel extends JPanel {

	private static final long serialVersionUID = -4449286683961509922L;

    //~ Instance fields ----------------------------------------------------------------------------

	private JComboBox form = new JComboBox();
    private JTextField tsi = new JTextField(10);
    private ResultPanel target;
    private int formValue;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ManualPanel object.
     *
     * @param panel the panel where to show results
     */
    ManualPanel(ResultPanel panel) {
        target = panel;
        init();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Reset the panel to default data
     */
    public final void reset() {
        form.setSelectedIndex(0);
        tsi.setText("");
        formValue = 0;
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
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(core.model.HOVerwaltung.instance().getLanguageString("ls.player.tsi")));
        buttonPanel.add(tsi);
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(core.model.HOVerwaltung.instance().getLanguageString("ls.player.form")));
        buttonPanel.add(form);
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));
        buttonPanel.add(label(""));

        for (int i = 1; i < 9; i++) {
            form.addItem(PlayerAbility.getNameForSkill(i, false));
        }

        form.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    formValue = form.getSelectedIndex() + 1;
                }
            });

        final JButton b = new JButton(core.model.HOVerwaltung.instance().getLanguageString("Calculate"));
        b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    final int tsiValue = Integer.parseInt(tsi.getText());
                    target.setPlayer(formValue, tsiValue, 0, "");
                }
            });
        buttonPanel.add(b);
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
