// %119160480:de.hattrickorganizer.gui%
package core.gui;

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;


/**
 * Information panel at the bottom of the MainFrame
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


    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new InfoPanel object.
     */
    public InfoPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * set the information text and progress bar value
     */
    public void setInformation(String information, int progress) {
        setProgressbarValue(progress);
        setInformation(information);
    }

    /**
     * set the information text and Color
     */
    public void setInformation(String text, Color color){
        m_jlInfoLabel.setText(text);
        m_jlInfoLabel.setForeground(color);
        paintComponentImmediately(m_jlInfoLabel);
    }

    /**
     * set the information text
     */
    public final void setInformation(String text) {
        setInformation(text, INFOFARBE);
    }

    /**
     * set progress bar value
     *
     * @param value min=0, max=100
     *              values outside this range will reset the progress bar (value = 0)
     */
    public final void setProgressbarValue(int value) {
        if (value < 0 || value > 100) {
            value = 0; // reset progress bar
        }
        m_jpbProgressBar.setValue(value);
        paintComponentImmediately(m_jpbProgressBar);
    }

    private void paintComponentImmediately(JComponent component){
        var rect = component.getBounds();
        rect.x=0;
        rect.y=0;
        component.paintImmediately(rect);
    }

    /**
     * create the components
     */
    public final void initComponents() {
        this.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));

        //Constraints
        final GridBagConstraints constraint = new GridBagConstraints();
        constraint.insets = new Insets(4, 9, 4, 4);

        //Layout
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

        constraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraint.weightx = 2.0;
        constraint.weighty = 1.0;
        constraint.gridx = 2;
        constraint.gridy = 0;
        layout.setConstraints(m_jpbProgressBar, constraint);
        add(m_jpbProgressBar);

    }
}
