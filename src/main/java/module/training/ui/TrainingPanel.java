package module.training.ui;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.constants.TrainingConstants;
import core.training.TrainingManager;
import core.training.TrainingPerWeek;
import core.util.Helper;
import module.training.ui.comp.FutureTrainingsEditionPanel;
import module.training.ui.comp.TrainingComboBox;
import module.training.ui.comp.TrainingParametersEditor;
import module.training.ui.model.ModelChange;
import module.training.ui.model.TrainingModel;
import module.training.ui.model.TrainingSettingsTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;

import static core.gui.theme.HOIconName.TRAINING_ICON;
import static javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION;
import static module.lineup.LineupPanel.TITLE_FG;

/**
 * Panel where past and future training are shown
 */
public class TrainingPanel extends JPanel implements TrainingConstants {

private TrainingSettingsTable futureTrainingsTable;
    private TrainingSettingsTable pastTrainingsTable;
	private JButton m_jbEditAllFutureTrainings;
	private JButton m_jbEditSelectedFutureTrainings;

    private ListSelectionModel m_lsm;

	private final TrainingModel model;

	private static final Color TABLE_BG = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
	private static final Color SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG);

	/**
	 * Creates a new TrainingPanel object.
	 */
	public TrainingPanel(TrainingModel _model) {
		super();
		model = _model;
		initComponents();
		addListeners();
		reload();
	}

	/**
	 * Populate the table is called everytime a refresh command is issued
	 */
	public void reload() {
        var pastTrainingsTableModel = UserColumnController.instance().getTrainingSettingsPastTableModel();
        var futureTrainingsTableModel = UserColumnController.instance().getTrainingSettingsFutureTableModel();
		pastTrainingsTableModel.setTrainingSettings(TrainingManager.instance().getHistoricalTrainings());
		futureTrainingsTableModel.setTrainingSettings(model.getFutureTrainings());
	}

	private void addListeners() {

		Map<Object, Object> colorMap = Map.of(
				"trainingColor1", ThemeManager.getColor(HOColorName.TRAINING_ICON_COLOR_1),
				"trainingColor2", ThemeManager.getColor(HOColorName.TRAINING_ICON_COLOR_2));

		Object[] options = {Helper.getTranslation("ls.button.close")};

        var futureTrainingsTableModel = UserColumnController.instance().getTrainingSettingsFutureTableModel();
        futureTrainingsTableModel.addTableModelListener(this::saveFutureTrainingSetting);

        var pastTrainingTableModel = UserColumnController.instance().getTrainingSettingsPastTableModel();
        pastTrainingTableModel.addTableModelListener(this::savePastTrainingSetting);

		m_jbEditSelectedFutureTrainings.addActionListener(arg0 -> {

			TableCellEditor editor = futureTrainingsTable.getCellEditor();
			if (editor != null) {
				editor.stopCellEditing();
			}

			JOptionPane.showOptionDialog(getTopLevelAncestor(),
					new FutureTrainingsEditionPanel(model, futureTrainingsTableModel, m_lsm),
					Helper.getTranslation("ls.module.training.edit_selected_future_trainings.tt"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, ImageUtilities.getSvgIcon(TRAINING_ICON, colorMap, 25,25),
					options, options[0]);
		});

		m_jbEditAllFutureTrainings.addActionListener(arg0 -> {
			TableCellEditor editor = futureTrainingsTable.getCellEditor();

			if (editor != null) {
				editor.stopCellEditing();
			}

			JOptionPane.showOptionDialog(getTopLevelAncestor(),
					new FutureTrainingsEditionPanel(model, futureTrainingsTableModel),
					Helper.getTranslation("ls.module.training.edit_all_future_trainings.tt"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, ImageUtilities.getSvgIcon(TRAINING_ICON, colorMap, 25,25),
					options, options[0]);
		});

		this.model.addModelChangeListener(change -> {
			if (change == ModelChange.FUTURE_TRAINING) {
				reload();
			}
		});
	}

    /**
     * Save past training settings
     * Model class is called to move the edited values to the model object
     * If successful, the changed entry is stored in database.
     * @param e TableModelEvent
     */
    private void savePastTrainingSetting(TableModelEvent e) {
        if ( e.getColumn() > -1) {
            var model = (TrainingSettingsTableModel) e.getSource();
            var modelColumnIndex = this.pastTrainingsTable.convertColumnIndexToModel(e.getColumn());
            var modelRowIndex = this.pastTrainingsTable.convertRowIndexToModel(e.getFirstRow());
            var entry = model.getEditedEntry(modelRowIndex, modelColumnIndex);
            if (entry != null) {
                DBManager.instance().saveTraining(entry, entry.getTrainingDate());
            }
        }
    }

    /**
     * Save future training settings
     * Model class is called to move the edited values to the model object
     * If successful, the changed entry is stored in database and the
     * refresh is triggered to update for instance the training prediction table
     * @param e TableModelEvent
     */
    private void saveFutureTrainingSetting(TableModelEvent e) {
        if (e.getColumn() > -1) {
            var model = (TrainingSettingsTableModel) e.getSource();
            var modelColumnIndex = this.futureTrainingsTable.convertColumnIndexToModel(e.getColumn());
            var modelRowIndex = this.futureTrainingsTable.convertRowIndexToModel(e.getFirstRow());
            var entry = model.getEditedEntry(modelRowIndex, modelColumnIndex);
            if (entry != null) {
                this.model.saveFutureTraining(entry);
                RefreshManager.instance().doRefresh();
            }
        }
    }

    /**
	 * Initialize the object layout
	 */
	private void initComponents() {
		JPanel pastTrainingsPanel = new JPanel(new GridBagLayout()){};
		GridBagConstraints uGbc = new GridBagConstraints();
		uGbc.anchor = GridBagConstraints.WEST;
		uGbc.insets = new Insets(3, 3, 3, 3);

		JLabel pastTrainingsLabel = new JLabel();
		pastTrainingsLabel.setText(TranslationFacility.tr("PastTrainings"));
		pastTrainingsLabel.setForeground(TITLE_FG);
		pastTrainingsLabel.setFont(getFont().deriveFont(Font.BOLD));
		uGbc.gridx = 0;
		uGbc.gridy = 0;
		pastTrainingsPanel.add(pastTrainingsLabel, uGbc);

		var pastTrainingsTableModel = UserColumnController.instance().getTrainingSettingsPastTableModel();
		this.pastTrainingsTable = new TrainingSettingsTable(pastTrainingsTableModel) {

            public Component prepareRenderer(
                    TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int modelRow = convertRowIndexToModel(row);
                var histoTraining = TrainingManager.instance().getHistoricalTrainings();
                TrainingPerWeek tpw = histoTraining.get(modelRow);
                var source = tpw.getSource();
                switch (source) {
                    case MANUAL -> c.setForeground(ThemeManager.getColor(HOColorName.BLUE));
                    case GUESS -> c.setForeground(ThemeManager.getColor(HOColorName.RED));
                    default -> c.setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
                }
                if (super.isRowSelected(modelRow)) {
                    c.setBackground(SELECTION_BG);
                } else {
                    c.setBackground(TABLE_BG);
                }
                return c;
            }

            public String getToolTipText(@NotNull MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);

                try {
                    int modelRow = convertRowIndexToModel(rowIndex);
                    var histoTraining = TrainingManager.instance().getHistoricalTrainings();
                    TrainingPerWeek tpw = histoTraining.get(modelRow);
                    var source = tpw.getSource();
                    tip = switch (source) {
                        case MANUAL -> Helper.getTranslation("ls.module.training.manual_entry.tt");
                        case GUESS -> Helper.getTranslation("ls.module.training.guess_entry.tt");
                        default -> Helper.getTranslation("ls.module.training.hrf_entry.tt");
                    };

                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }
                return tip;
            }
        };

        var upperScrollPane = pastTrainingsTable.getContainerComponent();
		uGbc.gridy = 1;
		uGbc.weightx = 1.0;
		uGbc.weighty = 1.0;
		uGbc.fill = GridBagConstraints.BOTH;
		pastTrainingsPanel.add(upperScrollPane, uGbc);

		JPanel futureTrainingsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints lGbc = new GridBagConstraints();
		lGbc.anchor = GridBagConstraints.WEST;
		lGbc.insets = new Insets(3, 3, 3, 3);

		JLabel futureTrainingLabel = new JLabel();
		futureTrainingLabel.setText(Helper.getTranslation("FutureTrainings"));
		futureTrainingLabel.setForeground(TITLE_FG);
		futureTrainingLabel.setFont(getFont().deriveFont(Font.BOLD));
		lGbc.gridx = 0;
		lGbc.gridy = 0;
		futureTrainingsPanel.add(futureTrainingLabel, lGbc);

		m_jbEditSelectedFutureTrainings = new JButton(Helper.getTranslation("ls.button.edit_selected"));
		m_jbEditSelectedFutureTrainings.setToolTipText(Helper.getTranslation("ls.module.training.edit_selected_future_trainings.tt"));
		m_jbEditSelectedFutureTrainings.setEnabled(false);
		lGbc.gridx = 1;
		lGbc.anchor = GridBagConstraints.EAST;
		lGbc.weightx = 1;
		futureTrainingsPanel.add(this.m_jbEditSelectedFutureTrainings, lGbc);

		m_jbEditAllFutureTrainings = new JButton(Helper.getTranslation("ls.button.edit_all"));
		m_jbEditAllFutureTrainings.setToolTipText(Helper.getTranslation("ls.module.training.edit_all_future_trainings.tt"));
		lGbc.gridx = 2;
		lGbc.weightx = 0;
		futureTrainingsPanel.add(this.m_jbEditAllFutureTrainings, lGbc);

        var futureTrainingsTableModel = UserColumnController.instance().getTrainingSettingsFutureTableModel();
        futureTrainingsTableModel.setTrainingModel(this.model);
		futureTrainingsTable = new TrainingSettingsTable(futureTrainingsTableModel){

			public Component prepareRenderer(
					TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				int modelRow = convertRowIndexToModel(row);
				TrainingPerWeek tpw = model.getFutureTrainings().get(modelRow);
				var source = tpw.getSource();
				switch (source) {
					case MANUAL -> c.setForeground(ThemeManager.getColor(HOColorName.BLUE));
					case GUESS -> c.setForeground(ThemeManager.getColor(HOColorName.RED));
					default -> c.setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
				}
				if (super.isRowSelected(modelRow)) {
					c.setBackground(SELECTION_BG);
				}
				else {
					c.setBackground(TABLE_BG);
				}
				return c;
			}

			public String getToolTipText(@NotNull MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);

				try {
					int modelRow = convertRowIndexToModel(rowIndex);
					TrainingPerWeek tpw = model.getFutureTrainings().get(modelRow);
					var source = tpw.getSource();
					tip = switch (source) {
						case MANUAL -> Helper.getTranslation("ls.module.training.manual_entry.tt");
						case GUESS -> Helper.getTranslation("ls.module.training.guess_entry.tt");
						default -> Helper.getTranslation("ls.module.training.hrf_entry.tt");
					};

				} catch (RuntimeException e1) {
					//catch null pointer exception if mouse is over an empty line
				}

				return tip;
			}

        };

        futureTrainingsTable.setSelectionMode(SINGLE_INTERVAL_SELECTION);
        ListSelectionModel listSelectionModel = futureTrainingsTable.getSelectionModel();
		listSelectionModel.addListSelectionListener(e -> {
			m_lsm= (ListSelectionModel)e.getSource();
			m_jbEditSelectedFutureTrainings.setEnabled(!m_lsm.isSelectionEmpty());
		});

        var lowerScrollPane = this.futureTrainingsTable.getContainerComponent();
		lGbc.gridx = 0;
		lGbc.gridy = 1;
		lGbc.weighty = 1;
		lGbc.gridwidth = 3;
		lGbc.fill = GridBagConstraints.BOTH;
		futureTrainingsPanel.add(lowerScrollPane, lGbc);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pastTrainingsPanel, futureTrainingsPanel);
		UserParameter.instance().training_pastFutureTrainingsSplitPane.init(splitPane);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * JTable class for past and future trainings table
	 */
	private static class TrainingSettingsTable extends FixedColumnsTable {

		public TrainingSettingsTable(TrainingSettingsTableModel arg0) {
			super(arg0,3);

			// Sets the combo box for selecting the training type
			var jcbTrainingEditor = new TrainingComboBox();
			TableColumn trainingColumn = this.getTableColumn(3);
            var cellEditor = new DefaultCellEditor(jcbTrainingEditor);
            cellEditor.addCellEditorListener(this);
			trainingColumn.setCellEditor(cellEditor);

			// Sets the combo box for selecting the intensity
			var jcbIntensityEditor = new TrainingParametersEditor(TrainingConstants.MIN_TRAINING_INTENSITY);
			TableColumn trainingIntensityColumn =this.getTableColumn(4);
			trainingIntensityColumn.setCellEditor(new DefaultCellEditor(jcbIntensityEditor));

			// Sets the combo box for selecting the staminaTrainingPart
			var jcbStaminaEditor = new TrainingParametersEditor(TrainingConstants.MIN_STAMINA_SHARE);
			TableColumn staminaColumn = this.getTableColumn(5);
			staminaColumn.setCellEditor(new DefaultCellEditor(jcbStaminaEditor));

			// Sets the combo box for selecting the coach skill
			var jcbCoachSkillEditor = new TrainingParametersEditor(TrainingConstants.MIN_COACH_SKILL, TrainingConstants.MAX_COACH_SKILL);
			TableColumn coachSkillColumn = this.getTableColumn(6);
			coachSkillColumn.setCellEditor(new DefaultCellEditor(jcbCoachSkillEditor));

			// Sets the combo box for selecting the Assistant Coach Total Level
			var assistantsTotalLevelEditor = new TrainingParametersEditor(TrainingConstants.MIN_ASSISTANTS_COACH_LEVEL, TrainingConstants.MAX_ASSISTANTS_COACH_LEVEL);
			TableColumn assistantsTotalLevelColumn = this.getTableColumn(7);
			assistantsTotalLevelColumn.setCellEditor(new DefaultCellEditor(assistantsTotalLevelEditor));
		}
	}
}
