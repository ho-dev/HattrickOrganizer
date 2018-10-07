// %3513105810:hoplugins.trainingExperience.ui.model%
package module.training.ui.model;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.Spieler;
import core.model.player.SpielerPosition;
import core.training.WeeklyTrainingType;
import core.util.Helper;
import module.training.Skills;
import module.training.ui.comp.VerticalIndicator;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Table Model for the main table showing training results
 * 
 * @author Mag. Bernhard Hödl AH - Solutions Augsten & Hödl OEG Neubachgasse 12
 *         A - 2325 Himberg Tabellenmodel und Daten für die dargestellte Tabelle
 *         für das HO Plugin
 */
public class OutputTableModel extends AbstractTableModel {

	public final static int COL_PLAYER_ID = 11;
	private static final long serialVersionUID = -1695207352334612268L;
	private List<Spieler> data = new ArrayList<Spieler>();
	private final TrainingModel model;

	/**
	 * Constructor
	 * 
	 * @param p_IHMM_HOMiniModel
	 */
	public OutputTableModel(TrainingModel model) {
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
		case 1:
		case 2:
		case COL_PLAYER_ID:
			return String.class;
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			return VerticalIndicator.class;
		default:
			return super.getColumnClass(columnIndex);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 12;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return HOVerwaltung.instance().getLanguageString("Spieler");
		case 1:
			return HOVerwaltung.instance().getLanguageString("ls.player.age");
		case 2:
			return HOVerwaltung.instance().getLanguageString("BestePosition");
		case 3:
			return HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper");
		case 4:
			return HOVerwaltung.instance().getLanguageString("ls.player.skill.defending");
		case 5:
			return HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking");
		case 6:
			return HOVerwaltung.instance().getLanguageString("ls.player.skill.passing");
		case 7:
			return HOVerwaltung.instance().getLanguageString("ls.player.skill.winger");
		case 8:
			return HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring");
		case 9:
			return HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces");
		case 10:
			return HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina");
		case COL_PLAYER_ID:
			return HOVerwaltung.instance().getLanguageString("ls.player.id");
		default:
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return (this.data != null) ? data.size() : 0;
	}

	/**
	 * Returns as toolTip for the cell, the last skillup for the proper player
	 * and skill
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * 
	 * @return toolTip
	 */
	public Object getToolTipAt(int rowIndex, int columnIndex) {
		return ((VerticalIndicator) getValueAt(rowIndex, columnIndex)).getToolTipText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Spieler spieler = data.get(rowIndex);

		switch (columnIndex) {
		case 0:
			// Spielername
			return spieler.getName();
		case 1:
			// Spieleralter
			return spieler.getAlterWithAgeDaysAsString();
		case 2:
			// Beste Postion
			return SpielerPosition.getNameForPosition(spieler.getIdealPosition()) + " ("
					+ spieler.getIdealPosStaerke(true) + ")";
		case 3:
			return createIcon(spieler, PlayerSkill.KEEPER);
		case 4:
			return createIcon(spieler, PlayerSkill.DEFENDING);
		case 5:
			return createIcon(spieler, PlayerSkill.PLAYMAKING);
		case 6:
			return createIcon(spieler, PlayerSkill.PASSING);
		case 7:
			return createIcon(spieler, PlayerSkill.WINGER);
		case 8:
			return createIcon(spieler, PlayerSkill.SCORING);
		case 9:
			return createIcon(spieler, PlayerSkill.SET_PIECES);
		case 10:
			return createIcon(spieler, PlayerSkill.STAMINA);
		case COL_PLAYER_ID:
			return Integer.toString(spieler.getSpielerID());
		default:
			return "";
		}
	}

	/**
	 * Refill the table with the new training based on the last changes
	 */
	public void fillWithData() {
		this.data = new ArrayList<Spieler>(HOVerwaltung.instance().getModel().getAllSpieler());
		fireTableDataChanged();
	}

	/**
	 * Get the training length for a player in a specific skill
	 * 
	 * @param player
	 *            player to be considered
	 * @param skillIndex
	 *            skill trained
	 * 
	 * @return predicted training length
	 */
	private double getTrainingLength(Spieler player, int skillIndex) {
		double dReturn = 0;
		WeeklyTrainingType wt = WeeklyTrainingType.instance(Skills.getTrainedSkillCode(skillIndex));
		if (wt != null) {
			dReturn = wt.getTrainingLength(player, this.model.getNumberOfCoTrainers(),
					this.model.getTrainerLevel(), HOVerwaltung.instance().getModel().getTeam()
							.getTrainingslevel(), HOVerwaltung.instance().getModel().getTeam()
							.getStaminaTrainingPart(), HOVerwaltung.instance().getModel().getStaff());
		}
		return dReturn;
	}

	/**
	 * Method that returns the offset in Training point
	 * 
	 * @param player
	 *            player to be considered
	 * @param skill
	 *            skill trained
	 * 
	 * @return training point offset, if any
	 */
	private double getOffset(Spieler player, int skill) {
		double offset = player.getSubskill4Pos(skill);
		double length = getTrainingLength(player, skill);
		return offset * length;
	}

	/**
	 * Create a VerticalIndicator object
	 * 
	 * @param spieler
	 *            object from which create the indicator
	 * @param skillIndex
	 *            points to skillup
	 * 
	 * @return the VerticalIndicator object
	 */
	private VerticalIndicator createIcon(Spieler spieler, int skillIndex) {
		double point = getOffset(spieler, skillIndex);
		double trainingLength = getTrainingLength(spieler, skillIndex);

		VerticalIndicator vi = new VerticalIndicator(Helper.round(point, 1), Helper.round(
				trainingLength, 1));

		return vi;
	}
}
