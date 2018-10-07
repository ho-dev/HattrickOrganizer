package tool.injury;
import core.model.HOVerwaltung;

/**
 * The Panel to calculate the number of needed doctors
 *
 * @author draghetto
 */
public class DoctorPanel extends AbstractInjuryPanel {
	
	private static final long serialVersionUID = 1843273716445393647L;
	
    //~ Instance fields ----------------------------------------------------------------------------

	private String msg = HOVerwaltung.instance().getLanguageString("DoctorsNeeded");

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new DoctorPanel object.
     *
     * @param dialog the main injury dialog
     */
    public DoctorPanel(InjuryDialog dialog) {
        super(dialog);
        reset();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Action to be executed when the button is pressed Calculates the result using the parameters
     */
    @Override
	public final void doAction() {
        final int updates = getInput();

        final double doctors = InjuryCalculator.getDoctorNumber(getDetail().getAge(),
                                                                getDetail().getInjury(),
                                                                getDetail().getDesiredLevel(),
                                                                updates);

        if (doctors > -1) {
            setOutputMsg(msg + ": " + formatNumber(doctors));
        }
    }

    /**
     * Reset the panel to default data
     */
    public final void reset() {
        setInputValue("");
        setInputMsg(HOVerwaltung.instance().getLanguageString("Updates"));
        setOutputMsg(msg);
        setHeader(HOVerwaltung.instance().getLanguageString("Injury1"));
    }
}
