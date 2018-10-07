// %4025553032:de.hattrickorganizer.gui.menu.option%
package core.option;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


/**
 * OLD! Initaloptionen
 */
public final class InitOptionsDialog extends JDialog implements java.awt.event.ActionListener {

	private static final long serialVersionUID = 1L;
	//~ Instance fields ----------------------------------------------------------------------------

    private JButton m_jbOK;
    private JComboBox m_jcbSprachdatei;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new InitOptionsDialog object.
     */
    public InitOptionsDialog() {
        super(new JFrame(), "Please select your language", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(m_jbOK)) {
            if (m_jcbSprachdatei.getSelectedItem() != null) {
                core.model.UserParameter.instance().sprachDatei = ((String) m_jcbSprachdatei
                                                            .getSelectedItem());

                setVisible(false);
            }
        }
    }

    private void initComponents() {
        setContentPane(new JPanel(new BorderLayout()));

        final JPanel optionspanel = new JPanel();
        optionspanel.setLayout(new GridLayout(1, 2, 4, 4));

        optionspanel.add(new JLabel("Language"));

        final String[] sprachdateien = core.model.HOVerwaltung.getLanguageFileNames();

        try {
            java.util.Arrays.sort(sprachdateien);
        } catch (Exception e) {
        }

        m_jcbSprachdatei = new JComboBox(sprachdateien);
        m_jcbSprachdatei.setSelectedItem(core.model.UserParameter.instance().sprachDatei);
        optionspanel.add(m_jcbSprachdatei);

        getContentPane().add(optionspanel, BorderLayout.CENTER);

        final JPanel buttonpanel = new JPanel();

        m_jbOK = new JButton("OK");
        m_jbOK.addActionListener(this);
        buttonpanel.add(m_jbOK);

        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        pack();

        //Spracheinstellung unter dem Splashscreen anzeigen
        setLocation((this.getToolkit().getScreenSize().width - this.getWidth()) / 2,
                    (this.getToolkit().getScreenSize().height - this.getHeight() + 250) / 2);

        setVisible(true);
    }
}
