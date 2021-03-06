// %1884453469:de.hattrickorganizer.gui.templates%
package core.gui.comp.entry;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import javax.swing.*;


/**
 * A panel with two labels to display two values in the same column, e.g. (value, diff).
 *
 * <p>The two labels within the resulting components will have equal width, unless the layout
 * manager has been set.
 */
public class DoubleLabelEntries extends AbstractHOTableEntry {
    //~ Instance fields ----------------------------------------------------------------------------

    private DoubleLabel m_clComponent = new DoubleLabel();
    private IHOTableEntry m_clLinks;
    private IHOTableEntry m_clRechts;

    private LayoutManager layout;

    private final static Color DIFF_COLOR = ThemeManager.getColor(HOColorName.FG_INJURED);

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new DoubleLabelEntries object.
     */
    public DoubleLabelEntries() {
    }

    /**
     * Creates a new DoubleLabelEntries object.
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
     * Creates a new DoubleLabelEntries object.
     *
     */
    public DoubleLabelEntries(IHOTableEntry links, IHOTableEntry rechts) {
        this(links, rechts, null);
    }

    public DoubleLabelEntries(IHOTableEntry links, IHOTableEntry rechts, LayoutManager layout) {
        m_clLinks = links;
        m_clRechts = rechts;
        setLayoutManager(layout);
        createComponent();
    }

	public final JComponent getComponent(boolean isSelected) {
        m_clComponent.removeAll();
        m_clComponent.setOpaque(false);

        final JComponent links = m_clLinks.getComponent(isSelected);
        final JComponent rechts = m_clRechts.getComponent(isSelected);

        if (layout != null) {
            m_clComponent.setLayoutManager(layout);
        }

        // If the layout is a GridBagLayout, force the components to take
        // the full space of their respective cell.
        if (layout instanceof GridBagLayout) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;

            m_clComponent.add(links, gbc);
            m_clComponent.add(rechts, gbc);
        } else {
            m_clComponent.add(links);
            m_clComponent.add(rechts);
        }

        return m_clComponent;
    }

    public final void setLabels(IHOTableEntry links, IHOTableEntry rechts) {
        m_clLinks = links;
        m_clRechts = rechts;
        updateComponent();
    }

    /**
     * Only use if left is a {@link ColorLabelEntry}.
     */
    public final ColorLabelEntry getLeft() {
        return (ColorLabelEntry) m_clLinks;
    }

    /**
     * Only use if right is a {@link ColorLabelEntry}.
     */
    public final ColorLabelEntry getRight() {
        return (ColorLabelEntry) m_clRechts;
    }

    public final IHOTableEntry getTableEntryLeft() {
        return m_clLinks;
    }

    public final IHOTableEntry getTableEntryRight() {
        return m_clRechts;
    }

	public final void clear() {
        m_clLinks.clear();
        m_clRechts.clear();
    }

	public int compareTo(@NotNull IHOTableEntry obj) {
        if (obj instanceof DoubleLabelEntries) {
            final DoubleLabelEntries entry = (DoubleLabelEntries) obj;
            return getTableEntryLeft().compareTo(entry.getTableEntryLeft());
        }

        return 0;
    }

	public final void createComponent() {
        m_clComponent = new DoubleLabel();
    }

	public void updateComponent() {
        m_clLinks.updateComponent();
        m_clRechts.updateComponent();
    }

    public void setLayoutManager(LayoutManager layout) {
        this.layout = layout;
    }
}
