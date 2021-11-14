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
	public static final int training_lowerLeftSplitPane = 6;
	public static final int teamAnalyzer_SimButtonSplitPane = 7;
	public static final int teamAnalyzer_RatingPanelSplitPane = 8;
	public static final int teamAnalyzer_FilterPanelSplitPane = 9;
	public static final int teamAnalyzer_MainPanelSplitPane = 10;
	public static final int teamAnalyzer_BottomSplitPane = 11;

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
			case transferHistoryPane_splitPane -> UserParameter.instance().transferHistoryPane_splitPane = newDivLoc;
			case transferTypePane_splitPane -> UserParameter.instance().transferTypePane_splitPane = newDivLoc;
			case training_splitPane -> UserParameter.instance().training_splitPane = newDivLoc;
			case training_lowerLeftSplitPane -> UserParameter.instance().training_lowerLeftSplitPane = newDivLoc;
			case teamAnalyzer_SimButtonSplitPane -> UserParameter.instance().teamAnalyzer_SimButtonSplitPane = newDivLoc;
			case teamAnalyzer_RatingPanelSplitPane -> UserParameter.instance().teamAnalyzer_RatingPanelSplitPane = newDivLoc;
			case teamAnalyzer_FilterPanelSplitPane -> UserParameter.instance().teamAnalyzer_FilterPanelSplitPane = newDivLoc;
			case teamAnalyzer_MainPanelSplitPane -> UserParameter.instance().teamAnalyzer_MainPanelSplitPane = newDivLoc;
			case teamAnalyzer_BottomSplitPane -> UserParameter.instance().teamAnalyzer_BottomSplitPane = newDivLoc;
		}

	}
}
