package core.option;


import core.gui.comp.panel.ImagePanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


/**
 * Panel mit Slider und Textfield
 */
class ComboBoxPanel extends ImagePanel {
    //~ Instance fields ----------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	private JComboBox m_jcbComboBox;
    private JLabel m_jlLabel;
    private int m_iTextbreite = 80;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * @param text Text des Labels
     * @param items Einträge in der ComboBox
     * @param textbreite Breite, die für das Label vorgesehen ist.
     */
    protected ComboBoxPanel(String text, Object[] items, int textbreite) {
        m_iTextbreite = textbreite;
        initComponents(text, items);
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final void setSelectedId(int id) {
        core.util.Helper.setComboBoxFromID(m_jcbComboBox, id);
    }

    public final void setSelectedItem(Object obj) {
        m_jcbComboBox.setSelectedItem(obj);
    }

    public final Object getSelectedItem() {
        return m_jcbComboBox.getSelectedItem();
    }

    public final void addItemListener(ItemListener listener) {
        m_jcbComboBox.addItemListener(listener);
    }

    public final void removeChangeListener(ItemListener listener) {
        m_jcbComboBox.removeItemListener(listener);
    }

    private void initComponents(String text, Object[] items) {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        setLayout(layout);

        m_jlLabel = new JLabel(text, SwingConstants.LEFT);
        m_jlLabel.setPreferredSize(new Dimension(m_iTextbreite, 35));
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        layout.setConstraints(m_jlLabel, constraints);
        add(m_jlLabel);

        if (items != null) {
        	m_jcbComboBox = new JComboBox(items);
        } else {
        	m_jcbComboBox = new JComboBox();
        }
        	
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1.0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        layout.setConstraints(m_jcbComboBox, constraints);
        add(m_jcbComboBox);
    }
}
