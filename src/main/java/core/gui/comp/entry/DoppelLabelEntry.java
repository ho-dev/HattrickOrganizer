// %1884453469:de.hattrickorganizer.gui.templates%
package core.gui.comp.entry;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.SwingConstants;


/**
 * Ein Panel mit zwei Labels, um zwei Werte in einer Spalte anzuzeigen ( Wert, Verbesserung )
 */
public class DoppelLabelEntry extends AbstractHOTableEntry {
    //~ Instance fields ----------------------------------------------------------------------------

    private DoppelLabel m_clComponent = new DoppelLabel();
    private IHOTableEntry m_clLinks;
    private IHOTableEntry m_clRechts;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new DoppelLabelEntry object.
     */
    public DoppelLabelEntry() {
    }

    /**
     * Creates a new DoppelLabelEntry object.
     *
     */
    public DoppelLabelEntry(Color color) {
        super();
        m_clLinks = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, color,
                                        SwingConstants.RIGHT);
        m_clRechts = new ColorLabelEntry("", core.model.UserParameter.instance().FG_VERLETZT, color,
                                         SwingConstants.CENTER);
        createComponent();
    }

    /**
     * Creates a new DoppelLabelEntry object.
     *
     */
    public DoppelLabelEntry(IHOTableEntry links, IHOTableEntry rechts) {
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

    public final IHOTableEntry getTableRechts() {
        return m_clRechts;
    }

	public final void clear() {
        m_clLinks.clear();
        m_clRechts.clear();
    }

	public int compareTo(IHOTableEntry obj) {
        if (obj instanceof DoppelLabelEntry) {
            final DoppelLabelEntry entry = (DoppelLabelEntry) obj;
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
