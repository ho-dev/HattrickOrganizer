package tool.injury;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;


/**
 * The Panel to calculate the exact number of needed updates
 *
 * @author draghetto
 */
class UpdateTSIPanel extends AbstractInjuryPanel {
	
	private static final long serialVersionUID = 1067981692979047647L;
	
    //~ Instance fields ----------------------------------------------------------------------------

	private String msg = TranslationFacility.tr("UpdatesNeeded");

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new UpdateTSIPanel object.
     *
     * @param dialog the main injury dialog
     */
    UpdateTSIPanel(InjuryDialog dialog) {
        super(dialog);
        reset();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Action to be executed when the button is pressed Calculates the result using the parameters
     */
    @Override
	public final void doAction() {
        final int tsi = getInput();

        final double updates = InjuryCalculator.getUpdateTSINumber(getDetail().getTSIPre(),
                                                                   getDetail().getTSIPost(),
                                                                   getDetail().getDesiredLevel(),
                                                                   tsi);

        if (updates > -1) {
            setOutputMsg(msg + ": " + formatNumber(updates));
        }
    }

    /**
     * Reset the panel to default data
     */
    final void reset() {
        setInputMsg(TranslationFacility.tr("Injury4"));
        setOutputMsg(msg);
        setHeader(TranslationFacility.tr("Injury3"));
        setInputValue(HOVerwaltung.instance().getModel().getClub().getAerzte() + "");
    }
}
