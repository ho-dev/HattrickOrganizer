// %119160480:de.hattrickorganizer.gui%
package core.gui;

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JProgressBar;
import javax.swing.JTextField;



/**
 * Panel als Information unter dem MainFrame
 */
public class InfoPanel extends ImagePanel {
    //~ Static fields/initializers -----------------------------------------------------------------

	private static final long serialVersionUID = 4902186890362152556L;

	// color for error messages
    public static final Color FEHLERFARBE = ThemeManager.getColor(HOColorName.LABEL_ERROR_FG); // Color.red
    // color for info messages
    public static final Color INFOFARBE = ThemeManager.getColor(HOColorName.LABEL_FG);//Color.black;
    // color for success messages
    public static final Color ERFOLGSFARBE = ThemeManager.getColor(HOColorName.LABEL_SUCCESS_FG);//Color.green;

    //~ Instance fields ----------------------------------------------------------------------------

    private JProgressBar m_jpbProgressBar = new JProgressBar(0, 100);
    private JTextField m_jlInfoLabel = new JTextField();
 //   private JTextField m_jtfKurzInfoLabel = new JTextField();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new InfoPanel object.
     */
    public InfoPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setzt den kurzen Infotext
     */
    public final void setKurzInfoText(String text) {
       // m_jtfKurzInfoLabel.setText(text);
    }

    /**
     * Setzt den langen Infotext
     */
    public final void setLangInfoText(String text) {
        m_jlInfoLabel.setText(text);
        m_jlInfoLabel.setForeground(INFOFARBE);
    }

    /**
     * Setzt den langen Infotext und die Zeichenfarbe
     */
    public final void setLangInfoText(String text, Color zeichenfarbe) {
        m_jlInfoLabel.setText(text);
        m_jlInfoLabel.setForeground(zeichenfarbe);
    }

    /**
     * Setzt den Wert des Fortschrittsbalken
     *
     * @param value min=0, max=100
     */
    public final void changeProgressbarValue(int value) {
        m_jpbProgressBar.setValue(value);
        m_jpbProgressBar.paintImmediately(m_jpbProgressBar.getBounds());
    }

    /**
     * löscht alle Informationen
     */
    public final void clearAll() {
        clearLangInfo();
        clearKurzInfo();
        clearProgressbar();
    }

    /**
     * löscht den kurzen Infotext
     */
    public final void clearKurzInfo() {
       // m_jtfKurzInfoLabel.setText("");
    }

    /**
     * löscht den langen Infotext und setzt die Farbe auf INFO
     */
    public final void clearLangInfo() {
        m_jlInfoLabel.setText("");
        m_jlInfoLabel.setForeground(INFOFARBE);
    }

    /**
     * setzt den Fortschrittsbalken zurück
     */
    public final void clearProgressbar() {
        m_jpbProgressBar.setValue(0);
    }

    /**
     * Erzeugen der Komponenten
     */
    public final void initComponents() {
        this.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));

        //Constraints erzeugen
        final GridBagConstraints constraint = new GridBagConstraints();
        constraint.insets = new Insets(4, 9, 4, 4);

        //Layout erstellen und MainPanel hinzufügen
        final GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        m_jlInfoLabel.setEditable(false);
        m_jlInfoLabel.setOpaque(false);
        constraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraint.weightx = 6.0;
        constraint.weighty = 1.0;
        constraint.gridx = 0;
        constraint.gridy = 0;
        layout.setConstraints(m_jlInfoLabel, constraint);
        add(m_jlInfoLabel);

//        m_jtfKurzInfoLabel.setPreferredSize(new Dimension(40, 20));
//        m_jtfKurzInfoLabel.setBackground(Color.lightGray);
//        m_jtfKurzInfoLabel.setEditable(false);
//        constraint.fill = java.awt.GridBagConstraints.NONE;
//        constraint.weightx = 0.0;
//        constraint.weighty = 1.0;
//        constraint.gridx = 1;
//        constraint.gridy = 0;
//        layout.setConstraints(m_jtfKurzInfoLabel, constraint);
//        add(m_jtfKurzInfoLabel);

        constraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraint.weightx = 2.0;
        constraint.weighty = 1.0;
        constraint.gridx = 2;
        constraint.gridy = 0;
        layout.setConstraints(m_jpbProgressBar, constraint);
        add(m_jpbProgressBar);

    }
}
