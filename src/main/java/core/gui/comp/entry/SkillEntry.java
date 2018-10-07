// %3663921258:de.hattrickorganizer.gui.templates%
/*
 * SkillEntry.java
 *
 * Created on 4. September 2004, 14:22
 */
package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.util.Helper;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;



/**
 * Skillanzeige eines Spielers (Nachkommastellen in Grau)
 *
 * @author Pirania
 */
public class SkillEntry implements IHOTableEntry {
    //~ Instance fields ----------------------------------------------------------------------------

    private Color m_clBGColor = ColorLabelEntry.BG_STANDARD;
    private Color m_clFGColor = ColorLabelEntry.FG_STANDARD;
    private static Color m_clFGColor2 = ThemeManager.getColor(HOColorName.SKILLENTRY2_BG);
    private JComponent m_clComponent;
    private JLabel m_jlLabel1;
    private JLabel m_jlLabel2;
    private String m_sNachkomma = "";
    private String m_sText = "";

    //Für Compareto
    private double m_dZahl = Double.NEGATIVE_INFINITY;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SkillEntry object.
     */
    public SkillEntry() {
        createComponent();
    }

    /**
     * Creates a new SkillEntry object.
     */
    public SkillEntry(double zahl) {
        m_dZahl = zahl;
        createText();
        createComponent();
    }

    /**
     * Creates a new SkillEntry object.
     */
    public SkillEntry(double zahl, Color foreground, Color background) {
        m_dZahl = zahl;
        m_clFGColor = foreground;
        m_clBGColor = background;
        createText();
        createComponent();
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final void setBGColor(Color bgcolor) {
        m_clBGColor = bgcolor;
        updateComponent();
    }

    /**
     * Gibt eine passende Komponente zurück
     */
	public final JComponent getComponent(boolean isSelected) {
        m_clComponent.setBackground(isSelected?HODefaultTableCellRenderer.SELECTION_BG:m_clBGColor);
        m_jlLabel1.setForeground(isSelected?HODefaultTableCellRenderer.SELECTION_FG:m_clFGColor);
        m_jlLabel2.setForeground(isSelected?HODefaultTableCellRenderer.SELECTION_FG:m_clFGColor);
        return m_clComponent;
    }

    public final void setFGColor(Color fgcolor) {
        m_clFGColor = fgcolor;
        updateComponent();
    }

    public final void setZahl(double zahl) {
        m_dZahl = zahl;
        updateComponent();
    }

    /**
     * Gibt die Zahl zurück
     */
    public final double getZahl() {
        return m_dZahl;
    }

	public final void clear() {
        m_dZahl = 0d;
        updateComponent();
    }

    /**
     * Vergleich zum Sortieren
     */
	public final int compareTo(IHOTableEntry obj) {
        if (obj instanceof SkillEntry) {
            final SkillEntry entry = (SkillEntry) obj;

            //Zahl?
            final double zahl1 = m_dZahl;
            final double zahl2 = entry.getZahl();

            if (zahl1 < zahl2) {
                return -1;
            } else if (zahl1 > zahl2) {
                return 1;
            } else {
                return 0;
            }
        }

        return 0;
    }

    /**
     * Erstellt eine passende Komponente
     */
	public final void createComponent() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(0, 0, 0, 0);

        JPanel panel = new JPanel(layout);

        m_jlLabel1 = new JLabel(m_sText, SwingConstants.RIGHT);
        m_jlLabel1.setForeground(m_clFGColor);
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(m_jlLabel1, constraints);
        panel.add(m_jlLabel1);

        m_jlLabel2 = new JLabel(m_sNachkomma, SwingConstants.LEFT);
        m_jlLabel2.setForeground(m_clFGColor2);
        m_jlLabel2.setFont(m_jlLabel1.getFont().deriveFont(m_jlLabel1.getFont().getSize2D() - 1f));
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        layout.setConstraints(m_jlLabel2, constraints);
        panel.add(m_jlLabel2);

        m_clComponent = panel;
        m_clComponent.setOpaque(true);
    }

    /**
     * Erzeugt die beiden Texte aus der Zahl
     */
    public final void createText() {
        m_sText = Integer.toString((int) m_dZahl);

        if (core.model.UserParameter.instance().anzahlNachkommastellen == 1) {
            m_sNachkomma = Helper.DEFAULTDEZIMALFORMAT.format(Helper.round(m_dZahl - (int) m_dZahl,core.model.UserParameter.instance().anzahlNachkommastellen));
        } else {
            m_sNachkomma = Helper.DEZIMALFORMAT_2STELLEN.format(Helper.round(m_dZahl - (int) m_dZahl,  core.model.UserParameter.instance().anzahlNachkommastellen));
        }

        int index = m_sNachkomma.indexOf(',');

        if (index < 0) {
            index = m_sNachkomma.indexOf('.');
        }

        if (index >= 0) {
            m_sNachkomma = m_sNachkomma.substring(index);
        }
    }

	public final void updateComponent() {
        m_jlLabel1.setText(m_sText);
        m_jlLabel2.setText(m_sNachkomma);
        m_jlLabel1.setBackground(m_clBGColor);
        m_jlLabel1.setForeground(m_clFGColor);
        m_jlLabel2.setBackground(m_clBGColor);
        m_jlLabel2.setForeground(m_clFGColor2);
        m_jlLabel2.setFont(m_jlLabel1.getFont().deriveFont(m_jlLabel1.getFont().getSize2D() - 1f));
    }
}
