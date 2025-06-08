package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import core.model.player.Player;
import core.model.player.SkillChange;
import core.training.FutureTrainingManager;
import core.training.TrainingPreviewPlayers;
import module.training.PlayerSkillChange;
import module.training.ui.TrainingLegendPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkillupTableModel extends HOTableModel {

	private ArrayList<PlayerSkillChange> skillChanges;
	private TrainingModel trainingModel;

	public SkillupTableModel(UserColumnController.@NotNull ColumnModelId id) {
		super(id, "TrainingSkillUps");
		columns = new ArrayList<>(List.of(

				//             case 0 -> TranslationFacility.tr("ls.player.skill");
				//            case 1 -> TranslationFacility.tr("Week");
				//            case 2 -> TranslationFacility.tr("Season");
				//            case 3 -> TranslationFacility.tr("ls.player.age");

				new TrainingColumn("ls.player.skill", 150) {
					@Override
					public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
                        var ret = new ColorLabelEntry(entry.getSkillChange().getType().toInt(), entry.getSkillChange().getType().getLanguageString(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
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
                        var ret = new ColorLabelEntry(htWeek.sinceOrigin(), String.valueOf(htWeek.week), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setToolTipText(entry.getSkillChange().getDate().toLocaleDateTime());
						return ret;
					}
				},
				new TrainingColumn("Season", 50) {
					@Override
					public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
						var date = entry.getSkillChange().getDate();
						var htWeek = date.toHTWeek();
						var ret = new ColorLabelEntry(htWeek.sinceOrigin(), String.valueOf(htWeek.season), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setToolTipText(entry.getSkillChange().getDate().toLocaleDateTime());
						return ret;

					}
				},
				new TrainingColumn("ls.player.age", 50) {
					@Override
					public IHOTableCellEntry getTableEntry(PlayerSkillChange entry) {
						var date = entry.getSkillChange().getDate();
						var htWeek = date.toHTWeek();
						var ret = new ColorLabelEntry(htWeek.sinceOrigin(), entry.getPlayer().getAgeWithDaysAsString(date), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setToolTipText(entry.getSkillChange().getDate().toLocaleDateTime());
						return ret;

					}
				}
		)).toArray(new TrainingColumn[0]);
	}

	public void setTrainingModel(TrainingModel data) {
		this.trainingModel = data;
		fireTableDataChanged();
	}

	public SkillChange getSkillup(int row) {
		return this.skillChanges.get(row).getSkillChange();
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