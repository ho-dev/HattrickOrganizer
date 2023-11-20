package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import javax.swing.*;

import static core.util.Helper.DEFAULTDEZIMALFORMAT;
import static core.util.Helper.INTEGERFORMAT;

/**
 * Displays the rating.
 */
public class RatingTableEntry extends AbstractHOTableEntry {

    private final Color bgColor = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    private final static Icon iconStar = ImageUtilities.getStarIcon();

    private JComponent m_clComponent = new JPanel();

    private String m_sTooltip = "";
    private float m_fRating;
    private final boolean starsAligned;


    public RatingTableEntry() {
        this(false);
    }

    public RatingTableEntry(Boolean _starsAligned) {
        starsAligned = _starsAligned;
        m_fRating = 0.0f;
        createComponent();
    }

    public RatingTableEntry(Integer f) {
        this(f, false);
    }

    public RatingTableEntry(Integer f, Boolean _starsAligned) {
        starsAligned = _starsAligned;
        if ( f == null){
            m_fRating=0.f;
        }
        else {
            m_fRating = f / 2.0f;
        }
        createComponent();
    }


	public final javax.swing.JComponent getComponent(boolean isSelected) {
        m_clComponent.setBackground((isSelected)?HODefaultTableCellRenderer.SELECTION_BG:bgColor);
        return m_clComponent;
    }

    public final void setRating(float f) {
        setRating(f, false);
    }

    public final void setRating(float f, boolean forceUpdate) {
        if (f < 0) {
            f = 0;
        }

        if (forceUpdate || (f != m_fRating)) {
            m_fRating = f/2.0f;
            updateComponent();
        }

        m_clComponent.repaint();
    }

    public final float getRating() {
        return m_fRating*2.0f;
    }

    public final void setToolTipText(String text) {
        m_sTooltip = text;
        updateComponent();
    }


	public final void clear() {

        GridBagConstraints constraints = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.gridy = 0;

        m_clComponent.removeAll();
        m_clComponent.setLayout(layout);

        final JLabel jlabel = new JLabel(ImageUtilities.NOIMAGEICON);
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        constraints.gridx = 0;
        layout.setConstraints(jlabel, constraints);
        m_clComponent.add(jlabel);
    }

	public final int compareTo(@NotNull IHOTableEntry obj) {
        if (obj instanceof RatingTableEntry entry) {
            return Float.compare(getRating(), entry.getRating());
        }
        return 0;
    }

	public final void createComponent() {

        JPanel renderer = new JPanel();

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        renderer.setLayout(layout);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        JLabel starLabel = getStarsLabel(m_fRating);
        layout.setConstraints(starLabel, constraints);
        renderer.add(starLabel);

        renderer.setToolTipText(m_sTooltip);
        m_clComponent = renderer;
    }

    
	public final void updateComponent() {
        m_clComponent.removeAll();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        m_clComponent.setLayout(layout);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        JLabel starLabel = getStarsLabel(m_fRating);
        layout.setConstraints(starLabel, constraints);
        m_clComponent.add(starLabel);
        m_clComponent.setToolTipText(m_sTooltip);
        m_clComponent.repaint();
    }

    private JLabel getStarsLabel(float _rating) {
        final JLabel jlabel;
        if (_rating == 0) {
            jlabel = new JLabel(ImageUtilities.NOIMAGEICON);
        } else {
            if (_rating == (int) _rating) {
                if (starsAligned) {
                    jlabel = new JLabel("   " + INTEGERFORMAT.format(_rating));
                } else {
                    jlabel = new JLabel(INTEGERFORMAT.format(_rating));
                }
            } else {
                jlabel = new JLabel(DEFAULTDEZIMALFORMAT.format(_rating));
            }
            jlabel.setIcon(iconStar);
        }
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jlabel.setHorizontalTextPosition(SwingConstants.LEADING);
        jlabel.setHorizontalAlignment(SwingConstants.LEFT);
        return jlabel;
    }
}
