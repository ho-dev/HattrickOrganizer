// %1126721331041:hoplugins.transfers.vo%
package module.transfer.transfertype;

import core.util.AmountOfMoney;

/**
 * Recap Information about a single Transfer Type
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class TransferTypeRecap {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Net Income of all the transfers of this type */
    private AmountOfMoney netIncome;

    /** Number of transfers of this type */
    private int number;

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the total Net Income
     *
     * @return total gain or loss by transfers of this type
     */
    final AmountOfMoney getNetIncome() {
        return netIncome;
    }

    /**
     * Returns the number of transfers
     *
     * @return Number of transfer
     */
    final int getNumber() {
        return number;
    }

    /**
     * Add a Transfer to the collection
     *
     * @param income income of the transfer
     */
    final void addOperation(AmountOfMoney income) {
        number++;
        netIncome.add( income);
    }
}
