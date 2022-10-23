// %1126721329338:hoplugins.transfers.constants%
package module.transfer;

import core.db.AbstractTable;
import core.model.HOVerwaltung;


/**
 * Transfer Types class
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public final class TransferType extends AbstractTable.Storable {

    private int playerId;
    private Integer transferType;

    //~ Static fields/initializers -----------------------------------------------------------------

    /** Number of Transfers Types */
    public static final int NUMBER = 12;

    public static final int UNDEFINED = -2;
    /** Fired Player */
    public static final int FIRED_PLAYER = -1;

    /** Player from Youth Pull Transfer */
    public static final int YOUTH_PULL = 0;

    /** Original Player Transfer */
    public static final int ORIGINAL_ROSTER = 1;

    /** Old Starter transferred old Player */
    public static final int OLD_STARTER = 2;

    /** Old Starter transferred old Player */
    public static final int OLD_BACKUP = 3;

    /** Trained transferred old Player */
    public static final int OLD_TRAINED = 4;

    /** Future Trainer Trasfer */
    public static final int FUTURE_TRAINER = 5;

    /** Skill Trading Trasfer */
    public static final int SKILL_TRADING = 6;

    /** Day Trading Trasfer */
    public static final int DAY_TRADING = 7;

    /** Untrained Starter Player currently on roster */
    public static final int STARTER_ROSTER = 8;

    /** Untrained Bencher Player currently on roster */
    public static final int BACKUP_ROSTER = 9;

    /** Trained Player currently on roster */
    public static final int TRAINED_ROSTER = 10;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new DividerDAO object.
     */
    public TransferType() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get a transfer type codebased on a string
     *
     * @param type string representation of he transfer type
     *
     * @return Transfer type
     */
    public static int getTransferCode(String type) {
        if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Trained_on_Roster"))) { //$NON-NLS-1$
            return TRAINED_ROSTER;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Starter_on_Roster"))) { //$NON-NLS-1$
            return STARTER_ROSTER;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Backup_on_Roster"))) { //$NON-NLS-1$
            return BACKUP_ROSTER;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Youth_Pull"))) { //$NON-NLS-1$
            return YOUTH_PULL;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Original_Player"))) { //$NON-NLS-1$
            return ORIGINAL_ROSTER;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Day_Trading"))) { //$NON-NLS-1$
            return DAY_TRADING;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Skill_Trading"))) { //$NON-NLS-1$
            return SKILL_TRADING;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Future_Trainer"))) { //$NON-NLS-1$
            return FUTURE_TRAINER;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Old_Trained_Player"))) { //$NON-NLS-1$
            return OLD_TRAINED;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Old_Starter_Player"))) { //$NON-NLS-1$
            return OLD_STARTER;
        } else if (type.equalsIgnoreCase(HOVerwaltung.instance().getLanguageString("TransferTypes.Old_Backup_Player"))) { //$NON-NLS-1$
            return OLD_BACKUP;
        }  else {
            return FIRED_PLAYER;
        }
    }

    /**
     * Returns the Transfer Type String description
     *
     * @param type Transfer type
     *
     * @return the descritption
     */
    public static String getTransferDesc(int type) {
        return switch (type) {
            case TRAINED_ROSTER ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Trained_on_Roster"); //$NON-NLS-1$

            case STARTER_ROSTER ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Starter_on_Roster"); //$NON-NLS-1$

            case BACKUP_ROSTER ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Backup_on_Roster"); //$NON-NLS-1$

            case YOUTH_PULL -> HOVerwaltung.instance().getLanguageString("TransferTypes.Youth_Pull"); //$NON-NLS-1$

            case ORIGINAL_ROSTER ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Original_Player"); //$NON-NLS-1$

            case DAY_TRADING -> HOVerwaltung.instance().getLanguageString("TransferTypes.Day_Trading"); //$NON-NLS-1$

            case SKILL_TRADING ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Skill_Trading"); //$NON-NLS-1$

            case FUTURE_TRAINER ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Future_Trainer"); //$NON-NLS-1$

            case OLD_TRAINED ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Old_Trained_Player"); //$NON-NLS-1$

            case OLD_STARTER ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Old_Starter_Player"); //$NON-NLS-1$

            case OLD_BACKUP ->
                    HOVerwaltung.instance().getLanguageString("TransferTypes.Old_Backup_Player"); //$NON-NLS-1$

            case FIRED_PLAYER -> HOVerwaltung.instance().getLanguageString("FiredPlayer"); //$NON-NLS-1$

            default -> ""; //$NON-NLS-1$
        };
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Integer getTransferType() {
        return transferType;
    }

    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }
}
