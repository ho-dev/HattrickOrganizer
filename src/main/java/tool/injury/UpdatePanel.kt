package tool.injury;
import core.model.HOVerwaltung;

/**
 * The Panel to calculate the number of needed updates
 *
 * @author draghetto
 */
class UpdatePanel extends AbstractInjuryPanel {

	private static final long serialVersionUID = -7495655025085036037L;

    //~ Instance fields ----------------------------------------------------------------------------

	private String msg = HOVerwaltung.instance().getLanguageString("UpdatesNeeded");

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new UpdatePanel object.
     *
     * @param dialog the main injury dialog
     */
    UpdatePanel(InjuryDialog dialog) {
        super(dialog);
        reset();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Action to be executed when the button is pressed Calculates the result using the parameters
     */
    @Override
	public final void doAction() {
        final int doctors = getInput();

        final double updates = InjuryCalculator.getUpdateNumber(getDetail().getAge(),
                                                                getDetail().getInjury(),
                                                                getDetail().getDesiredLevel(),
                                                                doctors);

        if (updates > -1) {
            setOutputMsg(msg + ": " + formatNumber(updates));
        }
    }

    /**
     * Reset the panel to default data
     */
    final void reset() {
        setInputMsg(HOVerwaltung.instance().getLanguageString("ls.club.staff.medic"));
        setOutputMsg(msg);
        setHeader(HOVerwaltung.instance().getLanguageString("Injury2"));
        setInputValue(HOVerwaltung.instance().getModel().getClub().getAerzte() + "");
    }
}
