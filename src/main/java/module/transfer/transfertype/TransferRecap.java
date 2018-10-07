// %1784694395:hoplugins.transfers.vo%
package module.transfer.transfertype;



import module.transfer.PlayerTransfer;
import module.transfer.TransferTypes;

import java.util.HashMap;
import java.util.Map;


/**
 * Recap information about transfer types.
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class TransferRecap {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Map of transfer type */
    private final Map<String,TransferTypeRecap> types = new HashMap<String,TransferTypeRecap>();

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get recap information about a transfer type
     *
     * @param type Transfer tpe
     *
     * @return Recap information
     */
    final TransferTypeRecap getRecap(int type) {
        TransferTypeRecap recap = (TransferTypeRecap) types.get("" + type);

        if (recap == null) {
            recap = new TransferTypeRecap();
            types.put("" + type, recap);
        }

        return recap;
    }

    /**
     * Register a trade transfer
     *
     * @param element Trade information
     */
    final void addTradingOperation(TransferredPlayer element) {
        final TransferTypeRecap recap = getRecap(element.getTransferType());
        recap.addOperation(element.getIncome());
    }

    /**
     * Register a 'wasted money' transfer
     *
     * @param element Trade information
     */
    final void addWastedOperation(PlayerTransfer element) {
        final TransferTypeRecap recap = getRecap(TransferTypes.FIRED_PLAYER);

        if (element.getType() == PlayerTransfer.BUY) {
            recap.addOperation(-element.getPrice());
        } else {
            recap.addOperation(element.getPrice());
        }
    }
}
