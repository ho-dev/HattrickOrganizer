package module.training.ui;

import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.constants.TrainingConstants;
import core.training.TrainingManager;
import core.training.TrainingPerWeek;
import core.util.Helper;
import module.training.ui.comp.DividerListener;
import module.training.ui.comp.FutureTrainingsEditionPanel;
import module.training.ui.comp.trainingParametersEditor;
import module.training.ui.comp.TrainingComboBox;
import module.training.ui.model.FutureTrainingsTableModel;
import module.training.ui.model.ModelChange;
import module.training.ui.model.PastTrainingsTableModel;
import module.training.ui.model.TrainingModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import static core.gui.theme.HOIconName.TRAINING_ICON;
import static module.lineup.LineupPanel.TITLE_FG;

/**
 * Panel where past and future training are shown
 */
public class TrainingPanel extends JPanel implements TrainingConstants {

	/** Future trainings table model */
	private FutureTrainingsTableModel futureTrainingsTableModel;
	/** Past trainings table model */
	private PastTrainingsTableModel pastTrainingsTableModel;

	private JTable futureTrainingsTable;
	private JButton m_jbEditAllFutureTrainings;
	private JButton m_jbEditSelectedFutureTrainings;
	private JSplitPane splitPane;

	private final TrainingModel model;

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
		pastTrainingsTableModel.populate(TrainingManager.instance().getHistoricalTrainings());
		futureTrainingsTableModel.populate(model.getFutureTrainings());
	}

	private void addListeners() {
		this.m_jbEditAllFutureTrainings.addActionListener(arg0 -> {
			TableCellEditor editor = futureTrainingsTable.getCellEditor();

			if (editor != null) {
				editor.stopCellEditing();
			}

			Object[] options = {Helper.getTranslation("ls.button.close")};

			Map<Object, Object> colorMap = Map.of("trainingColor1", ThemeManager.getColor(HOColorName.TRAINING_ICON_COLOR_1),
					"trainingColor2", ThemeManager.getColor(HOColorName.TRAINING_ICON_COLOR_2));

			JOptionPane.showOptionDialog(getTopLevelAncestor(),
					new FutureTrainingsEditionPanel(model, futureTrainingsTableModel),
					Helper.getTranslation("ls.module.training.edit_future_trainings.tt"),
					JOptionPane.NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, ImageUtilities.getSvgIcon(TRAINING_ICON, colorMap, 25,25),
					options, options[0]);
		});

		this.splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.training_pastFutureTrainingsSplitPane));

		this.model.addModelChangeListener(change -> {
			if (change == ModelChange.FUTURE_TRAINING) {
				reload();
			}
		});
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
		pastTrainingsLabel.setText(HOVerwaltung.instance().getLanguageString("PastTrainings"));
		pastTrainingsLabel.setForeground(TITLE_FG);
		pastTrainingsLabel.setFont(getFont().deriveFont(Font.BOLD));
		uGbc.gridx = 0;
		uGbc.gridy = 0;
		pastTrainingsPanel.add(pastTrainingsLabel, uGbc);

		this.pastTrainingsTableModel = new PastTrainingsTableModel();
		JTable pastTrainingsTable = new TrainingTable(this.pastTrainingsTableModel){

			public Component prepareRenderer(
					TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				int modelRow = convertRowIndexToModel(row);
				var histoTraining = TrainingManager.instance().getHistoricalTrainings();
				var nbRows = histoTraining.size();
				TrainingPerWeek tpw = histoTraining.get(nbRows- modelRow- 1);
				var source = tpw.getSource();
				switch (source) {
					case MANUAL -> c.setForeground(ThemeManager.getColor(HOColorName.BLUE));
					case GUESS -> c.setForeground(ThemeManager.getColor(HOColorName.RED));
					default -> c.setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
				}
				return c;
			}

			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);

				try {
						int modelRow = convertRowIndexToModel(rowIndex);
						var histoTraining = TrainingManager.instance().getHistoricalTrainings();
						var nbRows = histoTraining.size();
						TrainingPerWeek tpw = histoTraining.get(nbRows- modelRow- 1);
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


		JScrollPane upperScrollPane = new JScrollPane(pastTrainingsTable);
		upperScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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


		futureTrainingsTableModel = new FutureTrainingsTableModel(this.model);

		futureTrainingsTable = new TrainingTable(futureTrainingsTableModel){

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
				return c;
			}

			public String getToolTipText(MouseEvent e) {
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
		JScrollPane lowerScrollPane = new JScrollPane(this.futureTrainingsTable);
		lowerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		lGbc.gridx = 0;
		lGbc.gridy = 1;
		lGbc.weighty = 1;
		lGbc.gridwidth = 3;
		lGbc.fill = GridBagConstraints.BOTH;
		futureTrainingsPanel.add(lowerScrollPane, lGbc);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pastTrainingsPanel,
				futureTrainingsPanel);
		splitPane.setDividerLocation(UserParameter.instance().training_pastFutureTrainingsSplitPane);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * JTable class for past and future trainings table
	 */
	private class TrainingTable extends JTable {


		public TrainingTable(TableModel arg0) {
			super(arg0);
			setComboBoxEditor();
		}

		/**
		 * Initiates combo box and editor
		 */
		private void setComboBoxEditor() {
			// Sets the combo box for selecting the training type
			JComboBox jcbTrainingEditor = new TrainingComboBox();
			TableColumn trainingColumn = getColumnModel().getColumn(1);
			trainingColumn.setCellEditor(new DefaultCellEditor(jcbTrainingEditor));
			trainingColumn.setPreferredWidth(120);

			// Sets the combo box for selecting the intensity
			JComboBox jcbIntensityEditor = new trainingParametersEditor(TrainingConstants.MIN_TRAINING_INTENSITY);
			TableColumn trainingIntensityColumn = getColumnModel().getColumn(2);
			trainingIntensityColumn.setCellEditor(new DefaultCellEditor(jcbIntensityEditor));
			trainingIntensityColumn.setPreferredWidth(50);

			// Sets the combo box for selecting the staminaTrainingPart
			JComboBox jcbStaminaEditor = new trainingParametersEditor(TrainingConstants.MIN_STAMINA_SHARE);
			TableColumn staminaColumn = getColumnModel().getColumn(3);
			staminaColumn.setCellEditor(new DefaultCellEditor(jcbStaminaEditor));
			staminaColumn.setPreferredWidth(50);

			// Sets the combo box for selecting the coach skill
			JComboBox jcbCoachSkillEditor = new trainingParametersEditor(TrainingConstants.MIN_COACH_SKILL, TrainingConstants.MAX_COACH_SKILL);
			TableColumn coachSkillColumn = getColumnModel().getColumn(4);
			coachSkillColumn.setCellEditor(new DefaultCellEditor(jcbCoachSkillEditor));
			coachSkillColumn.setPreferredWidth(50);

			// Sets the combo box for selecting the Assistant Coach Total Level
			JComboBox jcbAssitantsTotalLevelEditor = new trainingParametersEditor(TrainingConstants.MIN_ASSISTANTS_COACH_LEVEL, TrainingConstants.MAX_ASSISTANTS_COACH_LEVEL);
			TableColumn assitantsLevelColumn = getColumnModel().getColumn(5);
			assitantsLevelColumn.setCellEditor(new DefaultCellEditor(jcbAssitantsTotalLevelEditor));
			assitantsLevelColumn.setPreferredWidth(50);
			
		}
	}
}
