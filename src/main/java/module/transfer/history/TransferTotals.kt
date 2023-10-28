// %4154424325:hoplugins.transfers.vo%
package module.transfer.history;

import module.transfer.PlayerTransfer;

import java.util.Iterator;
import java.util.List;


/**
 * Value Object representing totals information for a selection of transfers.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
class TransferTotals {
    //~ Instance fields ----------------------------------------------------------------------------

    private int number_buy;
    private int number_sell;
    private int total_buy_price;
    private int total_buy_tsi;
    private int total_sell_price;
    private int total_sell_tsi;

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Calculates the totals based on a list of transfers.
     *
     * @param transfers List of transfers
     *
     * @return TransferTotals object containing totals for the list of transfers.
     */
    static TransferTotals calculateTotals(List<PlayerTransfer> transfers) {
        final TransferTotals totals = new TransferTotals();

        for (Iterator<PlayerTransfer> iter = transfers.iterator(); iter.hasNext();) {
            final PlayerTransfer transfer = iter.next();

            if (transfer.getType() == PlayerTransfer.BUY) {
                totals.number_buy++;
                totals.total_buy_price += transfer.getPrice();
                totals.total_buy_tsi += transfer.getTsi();
            } else if (transfer.getType() == PlayerTransfer.SELL) {
                totals.number_sell++;
                totals.total_sell_price += transfer.getPrice();
                totals.total_sell_tsi += transfer.getTsi();
            } else {
                totals.number_sell++;
                totals.total_sell_price += transfer.getPrice();
                totals.total_sell_tsi += transfer.getTsi();
                totals.number_buy++;
                totals.total_buy_price += transfer.getPrice();
                totals.total_buy_tsi += transfer.getTsi();
            }
        }

        return totals;
    }

    /**
     * Gets the number of BUY transfers
     *
     * @return Number of BUY transfers
     */
    final int getAmountBuy() {
        return number_buy;
    }

    /**
     * Gets the number of SELL transfers
     *
     * @return Number of SELL transfers
     */
    final int getAmountSell() {
        return number_sell;
    }

    /**
     * Gets the average price for BUY transfers
     *
     * @return Average price
     */
    final double getBuyPriceAvg() {
        if (number_buy > 0) {
            return total_buy_price / number_buy;
        } else {
            return 0;
        }
    }

    /**
     * Gets the total price for BUY transfers
     *
     * @return Total price
     */
    final int getBuyPriceTotal() {
        return total_buy_price;
    }

    /**
     * Gets the average TSI value for BUY transfers
     *
     * @return Average TSI value
     */
    final double getBuyTsiAvg() {
        if (number_buy > 0) {
            return total_buy_tsi / number_buy;
        } else {
            return 0;
        }
    }

    /**
     * Gets the total TSI value for BUY transfers
     *
     * @return Total TSI value
     */
    final int getBuyTsiTotal() {
        return total_buy_tsi;
    }

    /**
     * Gets the average price for SELL transfers
     *
     * @return Average price
     */
    final double getSellPriceAvg() {
        if (number_sell > 0) {
            return total_sell_price / number_sell;
        } else {
            return 0;
        }
    }

    /**
     * Gets the total price for SELL transfers
     *
     * @return Total price
     */
    final int getSellPriceTotal() {
        return total_sell_price;
    }

    /**
     * Gets the average TSI value for SELL transfers
     *
     * @return Average TSI value
     */
    final double getSellTsiAvg() {
        if (number_sell > 0) {
            return total_sell_tsi / number_sell;
        } else {
            return 0;
        }
    }

    /**
     * Gets the total TSI value for BUY transfers
     *
     * @return Total TSI value
     */
    final int getSellTsiTotal() {
        return total_sell_tsi;
    }
}
