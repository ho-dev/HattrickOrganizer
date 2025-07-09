// %1126721330760:hoplugins.transfers.ui%
package module.transfer.history;


import core.gui.comp.panel.ImagePanel;
import core.model.TranslationFacility;
import core.model.UserParameter;
import module.transfer.ui.layout.TableLayout;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;



/**
 * Pane to show totals for transfers of your own team.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
class TotalsPanel extends JPanel {

	private static final NumberFormat FORMAT = NumberFormat.getIntegerInstance();

    private final JLabel buyAvgPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$
    private final JLabel buyTotPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$
    private final JLabel diffTotPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$
    private final JLabel sellAvgPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$
    private final JLabel sellTotPrice = new JLabel("", SwingConstants.RIGHT); //$NON-NLS-1$

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
     * @param title Name for the type of transfers.
     * @param currency Currency symbol
     */
    public TotalsPanel(String title, String currency) {
        super(new BorderLayout());

        FORMAT.setGroupingUsed(true);
        FORMAT.setMaximumFractionDigits(0);

        var fontSize = UserParameter.instance().fontSize;
        final double[][] sizes = {
                               {fontSize, 6*fontSize, 2*fontSize, 6*fontSize, 2*fontSize, 6*fontSize, fontSize},
                               {2*fontSize, 2*fontSize, 2*fontSize, fontSize, 2*fontSize}
                           };

        final JPanel panel = new ImagePanel();
        panel.setOpaque(false);

        final TableLayout layout = new TableLayout(sizes);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title));

        panel.add(new JLabel(TranslationFacility.tr("Total"), SwingConstants.CENTER), "3, 0"); //$NON-NLS-1$ //$NON-NLS-2$
        panel.add(new JLabel(TranslationFacility.tr("Durchschnitt"), SwingConstants.CENTER), "5, 0"); //$NON-NLS-1$

        panel.add(new JLabel(TranslationFacility.tr("Purchases"), SwingConstants.LEFT), "1, 1"); //$NON-NLS-1$ //$NON-NLS-2$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "2, 1"); //$NON-NLS-1$
        panel.add(buyTotPrice, "3, 1"); //$NON-NLS-1$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "4, 1"); //$NON-NLS-1$
        panel.add(buyAvgPrice, "5, 1"); //$NON-NLS-1$

        panel.add(new JLabel(TranslationFacility.tr("Sales"), SwingConstants.LEFT), "1, 2"); //$NON-NLS-1$ //$NON-NLS-2$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "2, 2"); //$NON-NLS-1$
        panel.add(sellTotPrice, "3, 2"); //$NON-NLS-1$
        panel.add(new JLabel(currency, SwingConstants.RIGHT), "4, 2"); //$NON-NLS-1$
        panel.add(sellAvgPrice, "5, 2"); //$NON-NLS-1$

        panel.add(new JLabel(TranslationFacility.tr("Difference"), SwingConstants.LEFT), "1, 4"); //$NON-NLS-1$ //$NON-NLS-2$
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

    public final void setValues(String buyTot, String buyAvg, String sellTot, String sellAvg, String diffTot) {
        buyTotPrice.setText(buyTot);
        buyAvgPrice.setText(buyAvg);
        sellTotPrice.setText(sellTot);
        sellAvgPrice.setText(sellAvg);

        diffTotPrice.setText(diffTot);
    }

}
