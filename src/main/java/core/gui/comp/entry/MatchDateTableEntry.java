package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.match.IMatchType;
import core.util.HODateTime;
import core.util.HOLogger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchDateTableEntry extends AbstractHOTableEntry {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private final Color bgColor = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    private final JLabel matchLink = new JLabel("");
    private static Icon matchIcon;
    private HODateTime matchDate;

    private JComponent m_clComponent = new JPanel();

    public final javax.swing.JComponent getComponent(boolean isSelected) {
        m_clComponent.setBackground((isSelected) ? HODefaultTableCellRenderer.SELECTION_BG : bgColor);
        return m_clComponent;
    }

    public MatchDateTableEntry(HODateTime lastMatchDate, IMatchType matchType) {
        createComponent();
        setMatchInfo(lastMatchDate, matchType);
    }

    public JLabel getMatchLink() {
        return matchLink;
    }

    public final void setMatchInfo(HODateTime t, IMatchType matchType) {

        if (t != null) {
            matchDate = t;
            matchLink.setText(matchDate.toLocaleDateTime());
            matchIcon = ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()]);
            matchLink.setIcon(matchIcon);
        }

        updateComponent();


        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        m_clComponent.setLayout(layout);
        m_clComponent.setBorder(new EmptyBorder(0,3,0,0));

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        matchLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        layout.setConstraints(matchLink, constraints);
        m_clComponent.add(matchLink);
        m_clComponent.repaint();
    }

    @Override
    public void clear() {
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

    @Override
    public int compareTo(@NotNull IHOTableEntry obj) {
        if (matchDate == null) {
            return -1;
        }

        if (obj instanceof final MatchDateTableEntry entry) {
            if (entry.matchDate == null) {
                return 1;
            }
            return matchDate.compareTo(entry.matchDate);
        }

        return 0;
    }

    @Override
    public void createComponent() {
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

        m_clComponent = renderer;
    }

    @Override
    public void updateComponent() {
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

        m_clComponent.repaint();
    }
}
