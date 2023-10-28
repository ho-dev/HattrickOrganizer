package core.gui.comp;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * Checkbox mit einem Bild
 */
public class ImageCheckbox extends JPanel {
	
	private static final long serialVersionUID = -1973860107178938746L;
	
    //~ Instance fields ----------------------------------------------------------------------------

	private JCheckBox m_jchCheckbox = new JCheckBox();
    private JLabel m_jlLabel = new JLabel();


    /**
     * Creates a new ImageCheckbox object.
     *
     */
    public ImageCheckbox(String text, Color color, boolean selected) {
        this(text, color, selected, SwingConstants.RIGHT);
    }

    /**
     * Creates a new ImageCheckbox object.
     *
     */
    ImageCheckbox(String text, Color color, boolean selected, int alignment) {
    	setOpaque(false);
        final GridBagLayout layout2 = new GridBagLayout();
        final GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.weightx = 0.0;
        constraints2.weighty = 0.0;
        constraints2.insets = new Insets(0, 0, 0, 0);

        setLayout(layout2);

        constraints2.gridx = 0;
        constraints2.gridy = 0;
        constraints2.weightx = 0.0;
        m_jchCheckbox.setSelected(selected);
        m_jchCheckbox.setOpaque(false);
        layout2.setConstraints(m_jchCheckbox, constraints2);
        add(m_jchCheckbox);

        constraints2.gridx = 1;
        constraints2.gridy = 0;
        constraints2.weightx = 1.0;
        m_jlLabel.setHorizontalTextPosition(alignment);
        m_jlLabel.setText(text);
        m_jlLabel.setIcon(getImageIcon4Color(color));
        layout2.setConstraints(m_jlLabel, constraints2);
        add(m_jlLabel);
    }


    public final JCheckBox getCheckbox() {
        return m_jchCheckbox;
    }


    public final void setSelected(boolean selected) {
        m_jchCheckbox.setSelected(selected);
    }


    public final boolean isSelected() {
        return m_jchCheckbox.isSelected();
    }

 
    public final void setText(String text) {
        m_jlLabel.setText(text);
    }


    public final void addActionListener(ActionListener listener) {
        m_jchCheckbox.addActionListener(listener);
    }
    
    private ImageIcon getImageIcon4Color(Color color) {
        final BufferedImage bufferedImage = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);

        final java.awt.Graphics2D g2d = (java.awt.Graphics2D) bufferedImage.getGraphics();

        g2d.setColor(color);
        g2d.fillRect(0, 0, 13, 13);

        return new ImageIcon(bufferedImage);
    }
}
