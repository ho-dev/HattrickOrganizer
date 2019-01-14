// %1942107811:de.hattrickorganizer.gui.menu.option%
package core.option;


import core.gui.comp.panel.ImagePanel;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;


/**
 * Alle weiteren Optionen, die Keine Formeln sind
 */
public final class CheckOptionPanel extends ImagePanel
    implements javax.swing.event.ChangeListener, java.awt.event.ItemListener
{
    //~ Static fields/initializers -----------------------------------------------------------------

	private static final long serialVersionUID = 1L;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SonstigeOptionenPanel object.
     */
    public CheckOptionPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------


    public final void itemStateChanged(java.awt.event.ItemEvent itemEvent) {}

	public void stateChanged(ChangeEvent arg0) {}

    private void initComponents() {
        setLayout(new GridLayout(10, 1, 4, 4));

        for(int i = 0; i < 6; i++) {
        	add(new JLabel(""));
        }
    }

}
