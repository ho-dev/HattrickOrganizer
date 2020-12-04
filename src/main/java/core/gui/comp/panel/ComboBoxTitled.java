package core.gui.comp.panel;

import core.datatype.CBItem;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class ComboBoxTitled extends JPanel {

    private final JPanel jlp = new JPanel();
    private JComboBox<CBItem> m_jcbItems;
    private JLabel m_jlTitle;
    private Boolean m_bInverseColor;
    private final String m_title;
    private final GridBagLayout layout = new GridBagLayout();

    public ComboBoxTitled(String title, JComboBox<CBItem> cb) {
        this(title, cb, false);
    }

    public ComboBoxTitled(String title, JComboBox<CBItem> cb, Boolean bInverseColor) {
        super();
        m_jcbItems = cb;
        m_title = title;
        m_bInverseColor = bInverseColor;
        initComponents();
    }

    /**
     * Create the components, don't forget the CB for the players and the listener!
     */
    private void initComponents() {

        Color bgColor = m_bInverseColor ? ThemeManager.getColor(HOColorName.TABLEENTRY_BG) : ThemeManager.getColor(HOColorName.PANEL_BG);
        Color bgCBColor = m_bInverseColor ? ThemeManager.getColor(HOColorName.PANEL_BG) : ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
        Color borderColor = m_bInverseColor ? ThemeManager.getColor(HOColorName.PANEL_BG) : ThemeManager.getColor(HOColorName.TABLEENTRY_BG);

        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, borderColor));

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;

        jlp.setLayout(layout);

        m_jlTitle = new JLabel(m_title);
        m_jlTitle.setFont(getFont().deriveFont(Font.BOLD));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 12, 0, 0);
        jlp.add(m_jlTitle, constraints);

        constraints.gridy = 1;
        constraints.insets = new Insets(5, 8, 5, 8);
        m_jcbItems.setBackground(bgCBColor);
        jlp.add(m_jcbItems, constraints);


        jlp.setBackground(bgColor);

        BorderLayout bl = new BorderLayout();
        bl.setHgap(0);
        bl.setVgap(0);
        setLayout(bl);
        add(jlp, BorderLayout.CENTER);
    }


}
