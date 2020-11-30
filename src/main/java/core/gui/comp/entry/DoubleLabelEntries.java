// %1884453469:de.hattrickorganizer.gui.templates%
package core.gui.comp.entry;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.SwingConstants;


/**
 * A panel with two labels to display two values in the same column, e.g. (value, diff).
 */
public class DoubleLabelEntries extends AbstractHOTableEntry {
    //~ Instance fields ----------------------------------------------------------------------------

    private DoppelLabel m_clComponent = new DoppelLabel();
    private IHOTableEntry m_clLinks;
    private IHOTableEntry m_clRechts;

    private final static Color DIFF_COLOR = ThemeManager.getColor(HOColorName.FG_INJURED);

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new DoppelLabelEntry object.
     */
    public DoubleLabelEntries() {
    }

    /**
     * Creates a new DoppelLabelEntry object.
     *
     */
    public DoubleLabelEntries(Color color) {
        super();
        m_clLinks = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, color,
                                        SwingConstants.RIGHT);
        m_clRechts = new ColorLabelEntry("", DIFF_COLOR, color,
                                         SwingConstants.CENTER);
        createComponent();
    }

    /**
     * Creates a new DoppelLabelEntry object.
     *
     */
    public DoubleLabelEntries(IHOTableEntry links, IHOTableEntry rechts) {
        m_clLinks = links;
        m_clRechts = rechts;
        createComponent();
    }

	public final JComponent getComponent(boolean isSelected) {
        m_clComponent.removeAll();
        m_clComponent.setOpaque(false);

        final JComponent links = m_clLinks.getComponent(isSelected);
        final JComponent rechts = m_clRechts.getComponent(isSelected);

        m_clComponent.add(links);
        m_clComponent.add(rechts);

        return m_clComponent;
    }

    public final void setLabels(IHOTableEntry links, IHOTableEntry rechts) {
        m_clLinks = links;
        m_clRechts = rechts;
        updateComponent();
    }

    /**
     * Nur benutzen, wenn es auch ein ColorLabelEntry ist!
     *
     */
    public final ColorLabelEntry getLinks() {
        return (ColorLabelEntry) m_clLinks;
    }

    /**
     * Nur benutzen, wenn es auch ein ColorLabelEntry ist!
     *
     */
    public final ColorLabelEntry getRechts() {
        return (ColorLabelEntry) m_clRechts;
    }

    public final IHOTableEntry getTableEntryLinks() {
        return m_clLinks;
    }

	public final void clear() {
        m_clLinks.clear();
        m_clRechts.clear();
    }

	public int compareTo(IHOTableEntry obj) {
        if (obj instanceof DoubleLabelEntries) {
            final DoubleLabelEntries entry = (DoubleLabelEntries) obj;
            return getTableEntryLinks().compareTo(entry.getTableEntryLinks());
        }

        return 0;
    }


	public final void createComponent() {
        m_clComponent = new DoppelLabel();
    }


	public void updateComponent() {
        m_clLinks.updateComponent();
        m_clRechts.updateComponent();
    }
}
