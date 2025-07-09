package module.transfer.history;

import core.util.AmountOfMoney;
import module.transfer.PlayerTransfer;

import java.math.BigDecimal;
import java.util.List;


/**
 * Value Object representing totals information for a selection of transfers.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
class TransferTotals {
    //~ Instance fields ----------------------------------------------------------------------------

    private int number_buy = 0;
    private int number_sell = 0;
    private AmountOfMoney total_buy_price = new AmountOfMoney(0);
    private int total_buy_tsi = 0;
    private AmountOfMoney total_sell_price= new AmountOfMoney(0);;
    private int total_sell_tsi = 0;

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
        for (final PlayerTransfer transfer : transfers) {
            var price = transfer.getPrice();
            if (transfer.getType() == PlayerTransfer.BUY) {
                totals.number_buy++;
                totals.total_buy_price.add(price);
                totals.total_buy_tsi += transfer.getTsi();
            } else if (transfer.getType() == PlayerTransfer.SELL) {
                totals.number_sell++;
                totals.total_sell_price.add(price);
                totals.total_sell_tsi += transfer.getTsi();
            } else {
                totals.number_sell++;
                totals.total_sell_price.add(price);
                totals.total_sell_tsi += transfer.getTsi();
                totals.number_buy++;
                totals.total_buy_price.add(price);
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
    final AmountOfMoney getBuyPriceAvg() {
        if (number_buy > 0) {
            return total_buy_price.divide(BigDecimal.valueOf(number_buy));
        } else {
            return new AmountOfMoney(0);
        }
    }

    /**
     * Gets the total price for BUY transfers
     *
     * @return Total price
     */
    final AmountOfMoney getBuyPriceTotal() {
        return total_buy_price;
    }

    /**
     * Gets the average TSI value for BUY transfers
     *
     * @return Average TSI value
     */
    final double getBuyTsiAvg() {
        if (number_buy > 0) {
            return (double) total_buy_tsi / number_buy;
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
    final AmountOfMoney getSellPriceAvg() {
        if (number_sell > 0) {
            return total_sell_price.divide(BigDecimal.valueOf(number_sell));
        } else {
            return new AmountOfMoney(0);
        }
    }

    /**
     * Gets the total price for SELL transfers
     *
     * @return Total price
     */
    final AmountOfMoney getSellPriceTotal() {
        return total_sell_price;
    }

    /**
     * Gets the average TSI value for SELL transfers
     *
     * @return Average TSI value
     */
    final double getSellTsiAvg() {
        if (number_sell > 0) {
            return (double) total_sell_tsi / number_sell;
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
