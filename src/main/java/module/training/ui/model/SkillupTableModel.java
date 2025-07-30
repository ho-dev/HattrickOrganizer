package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.player.SkillChange;
import module.training.PlayerSkillChange;
import module.training.Skills;
import module.training.ui.TrainingLegendPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkillupTableModel extends HOTableModel {

	private ArrayList<PlayerSkillChange> skillChanges;
	private TrainingModel trainingModel;

	public SkillupTableModel(UserColumnController.@NotNull ColumnModelId id) {
		super(id, "TrainingSkillUps");
		columns = new ArrayList<>(List.of(
				new TrainingColumn("ls.player.skill", 150) {
					@Override
					public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
						var skillChange = entry.getSkillChange();
						var text = skillChange.getType().getLanguageString() + ":  " + PlayerAbility.getNameForSkill(skillChange.getValue(), true);
						var ret = new ColorLabelEntry(entry.getSkillChange().getType().toInt(), text, getForegroundColor(entry), ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setToolTipText(entry.getSkillChange().getDate().toLocaleDateTime());
						ret.setIcon(TrainingLegendPanel.getSkillupTypeIcon(entry.getSkillChange().getType(), entry.getSkillChange().getChange()));
						return ret;
					}
				},
				new TrainingColumn("Week", 50) {
					@Override
					public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
						var date = entry.getSkillChange().getDate();
						var htWeek = date.toHTWeek();
                        var ret = new ColorLabelEntry(htWeek.sinceOrigin(), String.valueOf(htWeek.week), getForegroundColor(entry), ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setToolTipText(entry.getSkillChange().getDate().toLocaleDateTime());
						return ret;
					}
				},
				new TrainingColumn("Season", 50) {
					@Override
					public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
						var date = entry.getSkillChange().getDate();
						var htWeek = date.toHTWeek();
						var ret = new ColorLabelEntry(htWeek.sinceOrigin(), String.valueOf(htWeek.season), getForegroundColor(entry), ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setToolTipText(entry.getSkillChange().getDate().toLocaleDateTime());
						return ret;

					}
				},
				new TrainingColumn("ls.player.age", 50) {
					@Override
					public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
						var date = entry.getSkillChange().getDate();
						var htWeek = date.toHTWeek();
						var ret = new ColorLabelEntry(htWeek.sinceOrigin(), entry.getPlayer().getAgeWithDaysAsString(date), getForegroundColor(entry), ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setToolTipText(entry.getSkillChange().getDate().toLocaleDateTime());
						return ret;

					}
				}
		)).toArray(new TrainingColumn[0]);
	}

	private Color getForegroundColor(PlayerSkillChange entry) {
		if (entry.getSkillChange().getDate().isAfter(HOVerwaltung.instance().getModel().getBasics().getDatum()) ) {
			return Skills.getSkillColor(entry.getSkillChange().getType());
		}
		return Color.BLACK;
	}

	public void setTrainingModel(TrainingModel data) {
		this.trainingModel = data;
		initData();
	}

	public SkillChange getSkillup(int row) {
		if ( data != null && row > - 1 && row < data.size() ) {
			return this.data.get(row);
		}
		return null;
	}

	@Override
	protected void initData() {
		this.skillChanges = new ArrayList<>();
		if (this.trainingModel.getActivePlayer() != null) {
			for ( var skillUp : this.trainingModel.getSkillupManager().getTrainedSkillups()){
				skillChanges.add(new PlayerSkillChange(this.trainingModel.getActivePlayer(), skillUp));
			}
			for ( var skillUp : this.trainingModel.getFutureTrainingManager().getFutureSkillups()){
				skillChanges.add(new PlayerSkillChange(this.trainingModel.getActivePlayer(), skillUp));
			}
			Collections.reverse(skillChanges);
		}

		m_clData = new Object[skillChanges.size()][getDisplayedColumns().length];
		int rownum = 0;
		for (var skillChange : skillChanges) {
			int colnum = 0;
			for ( var col : getDisplayedColumns()){
				m_clData[rownum][colnum] = ((TrainingColumn)col).getTableEntry(skillChange);
				colnum++;
			}
			rownum++;
		}
		fireTableDataChanged();
	}
}