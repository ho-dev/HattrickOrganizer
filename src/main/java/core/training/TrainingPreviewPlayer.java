package core.training;


import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import javax.swing.*;


/**
 *
 * Training preview of a player for the week
 *
 * @author yaute
 */

public class TrainingPreviewPlayer {

	private final int FULL_TRAIN_MIN = 90;
	private final int FULL_STAMINA_MIN = FULL_TRAIN_MIN;
	private int iFullTrain ;
	private int iPartialTrain;
	private boolean bFullEstimedTrain;
	private boolean bPartialEstimedTrain;
	private int iStamina;
	private boolean bEstimedStamina;

    //~ Constructors -------------------------------------------------------------------------------

	/**
	 * Create TrainingPreviewPlayer object
	 *
	 * @param iFullTrain:			number of minutes played in 100% train position
	 * @param iPartialTrain:		number of minutes played in 50% train position
	 * @param bFullEstimedTrain:		will be train in a in 100% train position for the next match
	 * @param bPartialEstimedTrain:	will be train in a in 50% train position for the next match
	 */
    public TrainingPreviewPlayer(int iFullTrain, int iPartialTrain,
								 boolean bFullEstimedTrain, boolean bPartialEstimedTrain,
								 int iStamina, boolean bEstimedStamina)
	{
		this.iFullTrain = iFullTrain;
		this.iPartialTrain = iPartialTrain;
		this.bFullEstimedTrain = bFullEstimedTrain;
		this.bPartialEstimedTrain = bPartialEstimedTrain;
		this.iStamina = iStamina;
		this.bEstimedStamina = bEstimedStamina;
    }

    //~ Methods ------------------------------------------------------------------------------------

	/**
	 * Get icon corresponding to the training preview
	 *
	 * @return		icon
	 */
	public Icon getIcon() {

		if (iFullTrain >= FULL_TRAIN_MIN) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_FT);
		}
		else if (iFullTrain > 0 && bFullEstimedTrain) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_FT_FFT);
		}
		else if (bFullEstimedTrain) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_FFT);
		}
		else if (iFullTrain > 0 && ((iPartialTrain + iFullTrain) >= FULL_TRAIN_MIN)) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_FT_PT);
		}
    	else if (iFullTrain > 0 && bPartialEstimedTrain) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_FT_FPT);
		}
    	else if (iFullTrain > 0 && iPartialTrain > 0) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_FT_PT_E);
		}
    	else if (iFullTrain > 0) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_FT_E);
		}
    	else if (iPartialTrain >= FULL_TRAIN_MIN) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_PT);
		}
    	else if (iPartialTrain > 0 && bPartialEstimedTrain) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_PT_FPT);
		}
    	else if (bPartialEstimedTrain) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_FPT);
		}
    	else if (iPartialTrain > 0) {
			return ThemeManager.getIcon(HOIconName.TRAINBAR_PT_E);
		}
		else if (iStamina >= FULL_STAMINA_MIN) {
			return ThemeManager.getIcon(HOIconName.STAMINABAR_FT);
		}
		else if (bEstimedStamina) {
			return ThemeManager.getIcon(HOIconName.STAMINABAR_FFT);
		}
		else if (iStamina > 0) {
			return ThemeManager.getIcon(HOIconName.STAMINABAR_FT_E);
		}
		return ThemeManager.getIcon(HOIconName.TRAINBAR_EMPTY);
	}

	/**
	 * Get info of training preview for tooltiptext
	 *
	 * @return		training infos
	 */
	public String getText() {
		if (iFullTrain >= FULL_TRAIN_MIN) {
			return HOVerwaltung.instance().getLanguageString("trainpre.fulltrain") + ": "
					+ FULL_TRAIN_MIN + "'";
		}
		else if (iFullTrain > 0 && bFullEstimedTrain) {
			return HOVerwaltung.instance().getLanguageString("trainpre.fulltrain") + ": "
					+ iFullTrain + "'\n"
					+ HOVerwaltung.instance().getLanguageString("trainpre.fulltrain.estimated") + ": "
					+ FULL_TRAIN_MIN + "'";
		}
		else if (bFullEstimedTrain) {
			return HOVerwaltung.instance().getLanguageString("trainpre.fulltrain.estimated") + ": "
					+ FULL_TRAIN_MIN+ "'";
		}
		else if (iFullTrain > 0 && ((iPartialTrain + iFullTrain) >= FULL_TRAIN_MIN)) {
			return HOVerwaltung.instance().getLanguageString("trainpre.fulltrain") + ": "
					+ iFullTrain + "'\n"
					+ HOVerwaltung.instance().getLanguageString("trainpre.partialtrain") + ": "
					+ (FULL_TRAIN_MIN-iFullTrain) + "'";
		}
		else if (iFullTrain > 0 && bPartialEstimedTrain) {
			return HOVerwaltung.instance().getLanguageString("trainpre.fulltrain") + ": "
					+ iFullTrain + "'\n"
					+ HOVerwaltung.instance().getLanguageString("trainpre.partialtrain.estimated") + ": "
					+ (FULL_TRAIN_MIN-iFullTrain)  + "'";
		}
		else if (iFullTrain > 0 && iPartialTrain > 0) {
			return HOVerwaltung.instance().getLanguageString("trainpre.fulltrain") + ": "
					+ iFullTrain + "'\n"
					+ HOVerwaltung.instance().getLanguageString("trainpre.partialtrain") + ": "
					+ iPartialTrain + "'";
		}
		else if (iFullTrain > 0) {
			return HOVerwaltung.instance().getLanguageString("trainpre.fulltrain") + ": "
					+ iFullTrain + "'";
		}
		else if (iPartialTrain >= FULL_TRAIN_MIN) {
			return HOVerwaltung.instance().getLanguageString("trainpre.partialtrain") + ": "
					+ FULL_TRAIN_MIN + "'";
		}
		else if (iPartialTrain > 0 && bPartialEstimedTrain) {
			return HOVerwaltung.instance().getLanguageString("trainpre.partialtrain") + ": "
					+ iPartialTrain + "'\n"
					+ HOVerwaltung.instance().getLanguageString("trainpre.partialtrain.estimated") + ": "
					+ (FULL_TRAIN_MIN-iPartialTrain)  + "'";
		}
		else if (bPartialEstimedTrain) {
			return HOVerwaltung.instance().getLanguageString("trainpre.partialtrain.estimated")  + ": "
					+ FULL_TRAIN_MIN + "'";
		}
		else if (iPartialTrain > 0) {
			return HOVerwaltung.instance().getLanguageString("trainpre.partialtrain") + ": "
					+ iPartialTrain + "'";
		}
		else if (iStamina >= FULL_STAMINA_MIN) {
			return HOVerwaltung.instance().getLanguageString("stamina.train") + ": "
					+ FULL_STAMINA_MIN + "'";
		}
		else if (bEstimedStamina) {
			return HOVerwaltung.instance().getLanguageString("stamina.train.estimated") + ": "
					+ FULL_STAMINA_MIN+ "'";
		}
		else if (iStamina > 0) {
			return HOVerwaltung.instance().getLanguageString("stamina.train") + ": "
					+ iStamina + "'";
		}
		return null;
	}
}
