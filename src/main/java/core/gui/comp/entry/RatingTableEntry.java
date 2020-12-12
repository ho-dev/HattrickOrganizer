package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.match.MatchType;
import core.util.HOLogger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

import static core.util.Helper.DEFAULTDEZIMALFORMAT;
import static core.util.Helper.INTEGERFORMAT;

public class RatingTableEntry extends AbstractHOTableEntry {


    private final Color bgColor = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    private final static Icon iconStar = ImageUtilities.getStarIcon();
    private static Icon matchIcon;
    private JComponent m_clComponent = new JPanel();
    private final JLabel matchLink = new JLabel("");
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

    public RatingTableEntry(float f) {
        this(f, false);
    }

    public RatingTableEntry(float f, Boolean _starsAligned) {
        starsAligned = _starsAligned;
        m_fRating = f/2.0f;
        createComponent();
    }

    public RatingTableEntry(float f, String lastMatchDate, MatchType matchType) {
        this(f, lastMatchDate, matchType, false);
    }

    public RatingTableEntry(float f, String lastMatchDate, MatchType matchType, Boolean _starsAligned) {
        m_fRating = f/2.0f;
        starsAligned = _starsAligned;
        createComponent();
        this.setMatchInfo(lastMatchDate, matchType);
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

    public final void setMatchInfo(String t, MatchType matchType) {

        try {
            Date date=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(t);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            matchLink.setText("("+dateFormat.format(date)+")");
            matchIcon = ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()]);
            matchLink.setIcon(matchIcon);
            matchLink.setHorizontalAlignment(SwingConstants.RIGHT);
        } catch (ParseException e) {
            HOLogger.instance().log(this.getClass(), e.getMessage());
        }
        updateComponent();

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        m_clComponent.setLayout(layout);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.EAST;
        matchLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        layout.setConstraints(matchLink, constraints);
        m_clComponent.add(matchLink);

        m_clComponent.repaint();

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


        JLabel jlabel;
        jlabel = new JLabel(ImageUtilities.NOIMAGEICON);
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        constraints.gridx = 0;
        layout.setConstraints(jlabel, constraints);
        m_clComponent.add(jlabel);
    }

	public final int compareTo(@NotNull IHOTableEntry obj) {
        if (obj instanceof RatingTableEntry) {
            final RatingTableEntry entry = (RatingTableEntry) obj;

            if (getRating() < entry.getRating()) {
                return -1;
            } else if (getRating() > entry.getRating()) {
                return 1;
            } else {
                return 0;
            }
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
        constraints.anchor = GridBagConstraints.WEST;
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
        constraints.anchor = GridBagConstraints.WEST;
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
        }
		else{
            if (_rating == (int)_rating)
            {
                if(starsAligned) {
                    jlabel = new JLabel("   " + INTEGERFORMAT.format(_rating));
                }
                else{
                    jlabel = new JLabel(INTEGERFORMAT.format(_rating));
                }
            }
            else{
                jlabel = new JLabel(DEFAULTDEZIMALFORMAT.format(_rating));
            }
            jlabel.setIcon(iconStar);
        }
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jlabel.setHorizontalTextPosition(SwingConstants.LEADING);
        jlabel.setHorizontalAlignment(SwingConstants.LEFT);
        return jlabel;
    }

	public JLabel getLabelMatch() {
		return matchLink;
	}

}
