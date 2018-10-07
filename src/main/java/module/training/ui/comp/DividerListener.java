// %1126721451182:hoplugins.trainingExperience.ui.component%
package module.training.ui.comp;

import core.model.UserParameter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Dividend Listener that store in the Database the position of the varous
 * SplitPane
 * 
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class DividerListener implements PropertyChangeListener {
	public static final int transferHistoryPane_splitPane = 0;
	public static final int transferTypePane_splitPane = 1;
	public static final int training_splitPane = 2;
	public static final int training_bottomSplitPane = 3;
	public static final int training_rightSplitPane = 4;
	public static final int training_mainSplitPane = 5;
	public static final int training_lowerLeftSplitPane = 6;
	public static final int teamAnalyzer_LowerLefSplitPane = 7;
	public static final int teamAnalyzer_UpperLeftSplitPane = 8;
	public static final int teamAnalyzer_MainSplitPane = 9;
	public static final int teamAnalyzer_BottomSplitPane = 10;
	public static final int training_pastFutureTrainingsSplitPane = 11;

	private int key;

	public DividerListener(int key) {
		this.key = key;
	}

	/**
	 * Method invoked when the splitpane divisor is moved Store the new position
	 * value in the DB
	 * 
	 * @param e
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		Number value = (Number) e.getNewValue();
		int newDivLoc = value.intValue();

		switch (key) {
		case transferHistoryPane_splitPane:
			UserParameter.instance().transferHistoryPane_splitPane = newDivLoc;
			break;
		case transferTypePane_splitPane:
			UserParameter.instance().transferTypePane_splitPane = newDivLoc;
			break;
		case training_splitPane:
			UserParameter.instance().training_splitPane = newDivLoc;
			break;
		case training_bottomSplitPane:
			UserParameter.instance().training_bottomSplitPane = newDivLoc;
			break;
		case training_rightSplitPane:
			UserParameter.instance().training_rightSplitPane = newDivLoc;
			break;
		case training_mainSplitPane:
			UserParameter.instance().training_mainSplitPane = newDivLoc;
			break;
		case training_lowerLeftSplitPane:
			UserParameter.instance().training_lowerLeftSplitPane = newDivLoc;
			break;
		case teamAnalyzer_LowerLefSplitPane:
			UserParameter.instance().teamAnalyzer_LowerLefSplitPane = newDivLoc;
			break;
		case teamAnalyzer_UpperLeftSplitPane:
			UserParameter.instance().teamAnalyzer_UpperLeftSplitPane = newDivLoc;
			break;
		case teamAnalyzer_MainSplitPane:
			UserParameter.instance().teamAnalyzer_MainSplitPane = newDivLoc;
			break;
		case teamAnalyzer_BottomSplitPane:
			UserParameter.instance().teamAnalyzer_BottomSplitPane = newDivLoc;
			break;
		case training_pastFutureTrainingsSplitPane:
			UserParameter.instance().training_pastFutureTrainingsSplitPane = newDivLoc;
			break;			
		}

	}
}
