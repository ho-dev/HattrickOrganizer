// %36155679:hoplugins.trainingExperience.ui.renderer%
package module.training.ui.renderer;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSkill;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.training.HattrickDate;
import core.util.HOLogger;
import module.training.ui.TrainingLegendPanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * Renderer for the TrainingRecap Table (Prediction)
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TrainingRecapRenderer extends DefaultTableCellRenderer {
    /**
	 *
	 */
	private static final long serialVersionUID = -4088001127909689247L;

	private static final Color TABLE_BG = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
	private static final Color SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG);
	private static final Color TABLE_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_FG);
	private static final Color BIRTHDAY_BG = ThemeManager.getColor(HOColorName.TRAINING_BIRTHDAY_BG);
	private static final Color FULL_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_FULL_BG);
	private static final Color PARTIAL_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_PARTIAL_BG);
	private static final Color OSMOSIS_TRAINING_BG = ThemeManager.getColor(HOColorName.TRAINING_OSMOSIS_BG);

    //~ Methods ------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Reset default values
        this.setForeground(TABLE_FG);
        if (isSelected)
        	this.setBackground(SELECTION_BG);
        else
        	this.setBackground(TABLE_BG);

        String text = null;
        String tooltip = null;
        Icon icon = null;

        try {
        	String s = (String) table.getValueAt(row, column);
            int playerId = 0;
            double realPlayerAge = 0;

            // fetch playerId (last column) from table
        	playerId = Integer.parseInt((String)table.getValueAt(row, table.getColumnCount()-1));
        	Player player = HOVerwaltung.instance().getModel().getCurrentPlayer(playerId);
        	realPlayerAge = player.getAlterWithAgeDays();

        	/** If there is some kind of skillup information
        	 * in the table cell (s) -> extract it
        	 * (it is in the format "SKILLTYPE SKILLLEVEL CHANGE",
        	 * e.g. "3 10.00 1"  for skillup to outstanding playmaking)
			 *      "3 10.99 -1" for skilldrop to outstanding playmaking
        	 */

        	if (s != null && s.length() > 0) {
        		String[] skills = s.split(" "); //$NON-NLS-1$
        		int skillType = Integer.parseInt(skills[0]);
        		int change = Integer.parseInt((skills[2])); // +1: skillup; -1: skilldrop
        		icon = TrainingLegendPanel.getSkillupTypeIcon(skillType, change);
        		double val = Double.parseDouble(skills[1]);
        		String skillLevelName = PlayerAbility.getNameForSkill(val, true);
        		tooltip = PlayerSkill.toString(skillType)+": " + skillLevelName;
        		text = skillLevelName;
        	}

            if (playerId > 0) {
            	if (!isSelected) {
					// Check if player has individual training plan
					var prio = player.getTrainingPriority(new HattrickDate((String) table.getColumnModel().getColumn(column).getHeaderValue()));
					if (prio != null) {
						switch (prio) {
							case FULL_TRAINING:
								this.setBackground(FULL_TRAINING_BG);
								break;
							case PARTIAL_TRAINING:
								this.setBackground(PARTIAL_TRAINING_BG);
								break;
							case OSMOSIS_TRAINING:
								this.setBackground(OSMOSIS_TRAINING_BG);
								break;
						}
					}
				}
            	// Check if player has birthday
            	// every row is an additional week
            	int calcPlayerAgePrevCol = (int) (realPlayerAge + column*7d/112d);
            	int calcPlayerAgeThisCol = (int) (realPlayerAge + (column+1)*7d/112d);
            	// Birthday in this week! Set BG color
            	if (calcPlayerAgePrevCol < calcPlayerAgeThisCol) {
            		String ageText =  HOVerwaltung.instance().getLanguageString("ls.player.age.birthday")
    								+ " (" + calcPlayerAgeThisCol + " "
    								+  HOVerwaltung.instance().getLanguageString("ls.player.age.years")
    								+ ")";

            		if (text == null || text.length() == 0) {
            			text = ageText;
            		} else {
            			tooltip = "<html>" + tooltip + "<br>" + ageText + "</html>";
            		}
            		if (!isSelected){
						this.setBackground(BIRTHDAY_BG);
					}
            	}
            }

            if (tooltip == null)
            	tooltip = text;

            this.setToolTipText(tooltip);
    		this.setText(text);
    		this.setIcon(icon);
        } catch (Exception e) {
			HOLogger.instance().error(e.getClass(), e);
        }
        return this;
    }
}
