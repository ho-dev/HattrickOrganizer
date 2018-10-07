// %1126721330760:hoplugins.transfers.ui%
package module.transfer.history;


import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import module.transfer.ui.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;



/**
 * Pane to show totals for transfers of your own team.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
class TotalsPanel extends JPanel {
    //~ Static fields/initializers -----------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = 957113178669933183L;

	private static final NumberFormat FORMAT = NumberFormat.getIntegerInstance();

    //~ Instance fields ----------------------------------------------------------------------------

    private JLabel buyAvgPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$
    private JLabel buyTotPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$
    private JLabel diffTotPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$
    private JLabel sellAvgPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$
    private JLabel sellTotPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a TotalsPanel.
     *
     * @param title Name for the type of transfers.
     */
    TotalsPanel(String title) {
        this(title, "");
    }

    /**
     * Creates a TotalsPanel.
     *
     * @param titel Name for the type of transfers.
     * @param currency Currency symbol
     */
    public TotalsPanel(String titel, String currency) {
        super(new BorderLayout());

        FORMAT.setGroupingUsed(true);
        FORMAT.setMaximumFractionDigits(0);

        final double[][] sizes = {
                               {10, 75, 20, 75, 20, 75, 10},
                               {20, 20, 20, 10, 20}
                           };

        final JPanel panel = new ImagePanel();
        panel.setOpaque(false);

        final TableLayout layout = new TableLayout(sizes);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                                                         titel));

        panel.add(new JLabel(HOVerwaltung.instance().getLanguageString("Total"), SwingConstants.CENTER), "3, 0"); //$NON-NLS-1$ //$NON-NLS-2$
        panel.add(new JLabel(HOVerwaltung.instance().getLanguageString("Durchschnitt"),
                             SwingConstants.CENTER), "5, 0"); //$NON-NLS-1$ 

        panel.add(new JLabel(HOVerwaltung.instance().getLanguageString("Purchases"), SwingConstants.LEFT), "1, 1"); //$NON-NLS-1$ //$NON-NLS-2$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "2, 1"); //$NON-NLS-1$
        panel.add(buyTotPrice, "3, 1"); //$NON-NLS-1$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "4, 1"); //$NON-NLS-1$
        panel.add(buyAvgPrice, "5, 1"); //$NON-NLS-1$

        panel.add(new JLabel(HOVerwaltung.instance().getLanguageString("Sales"), SwingConstants.LEFT), "1, 2"); //$NON-NLS-1$ //$NON-NLS-2$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "2, 2"); //$NON-NLS-1$
        panel.add(sellTotPrice, "3, 2"); //$NON-NLS-1$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "4, 2"); //$NON-NLS-1$
        panel.add(sellAvgPrice, "5, 2"); //$NON-NLS-1$

        panel.add(new JLabel(HOVerwaltung.instance().getLanguageString("Difference"), SwingConstants.LEFT), "1, 4"); //$NON-NLS-1$ //$NON-NLS-2$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "2, 4"); //$NON-NLS-1$
        panel.add(diffTotPrice, "3, 4"); //$NON-NLS-1$

        add(panel, BorderLayout.CENTER);
        setOpaque(false);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the total values.
     *
     * @param buyTot Total value for BUY transfers.
     * @param buyAvg Average value for BUY transfers.
     * @param sellTot Total value for SELL transfers.
     * @param sellAvg Average value for SELL transfers.
     */
    public final void setValues(int buyTot, double buyAvg, int sellTot, double sellAvg) {
        buyTotPrice.setText(FORMAT.format(buyTot));
        buyAvgPrice.setText(FORMAT.format(buyAvg));
        sellTotPrice.setText(FORMAT.format(sellTot));
        sellAvgPrice.setText(FORMAT.format(sellAvg));

        final int diffTot = sellTot - buyTot;
        diffTotPrice.setText(FORMAT.format(diffTot));
    }
}
