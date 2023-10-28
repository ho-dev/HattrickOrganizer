package module.teamAnalyzer.ui.component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * An info customizable panel, it shows the Strings passed thru constructor!!!
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class InfoPanel extends JPanel {
	private static final long serialVersionUID = -983845737480059931L;

	/**
     * Constructs a new instance.
     */
    public InfoPanel(String[] messages) {
        jbInit(messages);
    }

    /**
     * Initializes the state of this instance.
     */
    private void jbInit(String[] messages) {
        GridBagLayout gridBagLayout1 = new GridBagLayout();

        this.setLayout(gridBagLayout1);

        JLabel[] labels = new JLabel[messages.length];

        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
            labels[i].setText(messages[i]);
            add(labels[i],
                new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(5, 5, 0, 5), 0, 0));
        }
    }
}
