package core.model.player;

/**
 * Type of recovery estimation.
 */
public enum TypeOfRecoveryEstimation {

    /**
     * The estimated recovery date should match the expected actual recovery date.
     */
    REALISTIC_ESTIMATE,

    /**
     * The actual recovery is most likely to occur after the estimated recovery date.
     */
    OPTIMISTIC_ESTIMATE,

    /**
     * The actual recovery is most likely to occur before the estimated recovery date.
     */
    PESSIMISTIC_ESTIMATE,
}
